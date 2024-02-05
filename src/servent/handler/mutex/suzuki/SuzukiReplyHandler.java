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

public class SuzukiReplyHandler implements MessageHandler {
    private SuzukiMutex mutex;
    private SuzukiReplyMessage clientMessage;

    public SuzukiReplyHandler(Message clientMessage, DistributedMutex mutex){
        this.mutex = (SuzukiMutex) mutex;
        this.clientMessage = (SuzukiReplyMessage) clientMessage;
    }
    @Override
    public void run() {
        // Update local state
        AppConfig.myServentInfo.setRequestQueue(clientMessage.getRequestQueue());
        AppConfig.myServentInfo.setHasToken(true);
    }
}
