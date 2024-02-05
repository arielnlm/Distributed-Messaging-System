package servent.handler.mutex.suzuki.simple;

import app.AppConfig;
import mutex.DistributedMutex;
import mutex.suzuki.SuzukiMutex;
import mutex.suzuki.SuzukiSimple;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.mutex.suzuki.SuzukiReplyMessage;
import servent.message.mutex.suzuki.simple.SimpleSuzukiReplyMessage;

public class SuzukiSimpleReplyHandler implements MessageHandler {
    private SuzukiSimple mutex;
    private SimpleSuzukiReplyMessage clientMessage;

    public SuzukiSimpleReplyHandler(Message clientMessage, DistributedMutex mutex){
        this.mutex = (SuzukiSimple) mutex;
        this.clientMessage = (SimpleSuzukiReplyMessage) clientMessage;
    }
    @Override
    public void run() {
        AppConfig.myServentInfo.setMutexToken(clientMessage.getMutexToken());
        AppConfig.timestampedStandardPrint("Got mutex - " + AppConfig.myServentInfo.getMutexToken());
    }
}
