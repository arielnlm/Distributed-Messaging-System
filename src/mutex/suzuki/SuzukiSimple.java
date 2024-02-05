package mutex.suzuki;

import app.AppConfig;
import app.ServentInfo;
import mutex.DistributedMutex;
import networking.SystemState;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.mutex.suzuki.SuzukiReplyMessage;
import servent.message.mutex.suzuki.SuzukiRequestMessage;
import servent.message.mutex.suzuki.simple.SimpleSuzukiReplyMessage;
import servent.message.util.MessageUtil;

import java.util.HashMap;
import java.util.LinkedList;

public class SuzukiSimple implements DistributedMutex {
    @Override
    public void lock() {
        AppConfig.timestampedStandardPrint("New mutex...");
        AppConfig.timestampedStandardPrint("Token status is " + AppConfig.myServentInfo.getMutexToken());
        if(AppConfig.myServentInfo.getMutexToken() != null){
            AppConfig.myServentInfo.setInCriticalSection(true);
            return;
        }

        requestToken();

        while (AppConfig.myServentInfo.getMutexToken() == null) {
            Thread.onSpinWait();
        }
    }

    private void requestToken(){
        AppConfig.timestampedStandardPrint("Request mutex...");
        for(ServentInfo servent : SystemState.getInstance().getActiveNodes()){
            if(servent.getAddress().equals(AppConfig.myServentInfo.getAddress()))
                continue; // Skip myself
            Message message = new SuzukiRequestMessage(MessageType.SUZUKI_REQUEST,
                    AppConfig.myServentInfo,
                    servent,
                    0);
            MessageUtil.sendMessage(message);
        }
    }

    @Override
    public void unlock() {
        AppConfig.myServentInfo.setInCriticalSection(false);
        SuzukiToken token = new SuzukiToken(AppConfig.myServentInfo.getMutexToken().getQueue());
        if(token.getQueue().isEmpty())
            return;
        ServentInfo next = token.getQueue().poll();
        if(next == null || next.equals(AppConfig.myServentInfo))
            return;
        AppConfig.myServentInfo.setMutexToken(null);
        sendTokenTo(next, token);
    }

    public void sendTokenTo(ServentInfo serventInfo, SuzukiToken token) {
        SimpleSuzukiReplyMessage message = new SimpleSuzukiReplyMessage(
                MessageType.SUZUKI_REPLY,
                AppConfig.myServentInfo, serventInfo,
                token
        );
        MessageUtil.sendMessage(message);
    }
}
