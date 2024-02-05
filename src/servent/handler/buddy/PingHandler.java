package servent.handler.buddy;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.buddy.PongMessage;
import servent.message.util.MessageUtil;

public class PingHandler implements MessageHandler {
    private Message clientMessage;
    public PingHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        // Assuming ping will be possible with direct neighbours only
        Message pongMessage;
        if(clientMessage.getMessageText().equalsIgnoreCase("PING_CHECK")){ // Special case for failure checking
            pongMessage = new PongMessage(MessageType.PONG, AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo(), "PONG_CHECK");;
        }
        else{
            pongMessage = new PongMessage(MessageType.PONG, AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo(), "PONG");
        }
        MessageUtil.sendMessage(pongMessage);
    }
}
