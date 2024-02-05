package servent.handler.mutex.suzuki;

import app.AppConfig;
import app.ServentInfo;
import mutex.DistributedMutex;
import mutex.suzuki.SuzukiMutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.mutex.suzuki.SuzukiReplyMessage;
import servent.message.mutex.suzuki.SuzukiRequestMessage;
import servent.message.util.MessageUtil;

import java.util.HashMap;
import java.util.LinkedList;

public class SuzukiRequestHandler implements MessageHandler {
    private SuzukiMutex mutex;
    private SuzukiRequestMessage clientMessage;

    public SuzukiRequestHandler(Message clientMessage, DistributedMutex mutex){
        this.mutex = (SuzukiMutex) mutex;
        this.clientMessage = (SuzukiRequestMessage) clientMessage;
    }
    @Override
    public void run() {
        Integer max = Math.max(
                AppConfig.myServentInfo.getRequestNumbers().getOrDefault(
                        clientMessage.getOriginalSenderInfo(),
                        0),
                clientMessage.getSeqNum()
        );
        // Update request number
        AppConfig.myServentInfo.getRequestNumbers().put(
                clientMessage.getOriginalSenderInfo(),
                max
        );

        if (AppConfig.myServentInfo.isHasToken() && !AppConfig.myServentInfo.isInCriticalSection()
                && max == AppConfig.myServentInfo.getLastNumbers().getOrDefault( // RN[i] == LN[i]+1
                        clientMessage.getOriginalSenderInfo(), 0) + 1) {
            sendTokenTo(clientMessage.getOriginalSenderInfo());
        }
    }

    public void sendTokenTo(ServentInfo serventInfo) {

        AppConfig.myServentInfo.setHasToken(false);
        // Send TOKEN message
        SuzukiReplyMessage message = new SuzukiReplyMessage(MessageType.SUZUKI_REPLY, AppConfig.myServentInfo, serventInfo,
                new LinkedList<>(AppConfig.myServentInfo.getRequestQueue()),
                new HashMap<>(AppConfig.myServentInfo.getRequestNumbers()));
        MessageUtil.sendMessage(message);
        // Update token information, ovo je direktno slanje, ni nema me u queue?
        // AppConfig.myServentInfo.getRequestQueue().remove(serventInfo.getId());
    }
}
