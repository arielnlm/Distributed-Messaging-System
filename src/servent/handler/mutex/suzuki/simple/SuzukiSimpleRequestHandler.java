package servent.handler.mutex.suzuki.simple;

import app.AppConfig;
import app.ServentInfo;
import mutex.DistributedMutex;
import mutex.suzuki.SuzukiMutex;
import mutex.suzuki.SuzukiSimple;
import mutex.suzuki.SuzukiToken;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.mutex.suzuki.SuzukiReplyMessage;
import servent.message.mutex.suzuki.SuzukiRequestMessage;
import servent.message.mutex.suzuki.simple.SimpleSuzukiReplyMessage;
import servent.message.util.MessageUtil;

import java.util.HashMap;
import java.util.LinkedList;

public class SuzukiSimpleRequestHandler implements MessageHandler {
    private SuzukiSimple mutex;
    private SuzukiRequestMessage clientMessage;

    public SuzukiSimpleRequestHandler(Message clientMessage, DistributedMutex mutex){
        this.mutex = (SuzukiSimple) mutex;
        this.clientMessage = (SuzukiRequestMessage) clientMessage;
    }
    @Override
    public void run() {
        SuzukiToken mutexToken = AppConfig.myServentInfo.getMutexToken();
        if(mutexToken == null)
            return;
        mutexToken.addToQueue(clientMessage.getOriginalSenderInfo());
        AppConfig.timestampedStandardPrint("I have token!");
        if(AppConfig.myServentInfo.isInCriticalSection())
            return;
        AppConfig.timestampedStandardPrint("I'm giving token!");
        sendTokenTo(mutexToken.getQueue().poll());
    }

    public void sendTokenTo(ServentInfo serventInfo) {
        SuzukiToken copyToken = new SuzukiToken(AppConfig.myServentInfo.getMutexToken().getQueue());
        AppConfig.myServentInfo.setMutexToken(null);
        SimpleSuzukiReplyMessage message = new SimpleSuzukiReplyMessage(
                        MessageType.SUZUKI_REPLY,
                        AppConfig.myServentInfo,
                        serventInfo,
                        copyToken
        );
        MessageUtil.sendMessage(message);
    }
}
