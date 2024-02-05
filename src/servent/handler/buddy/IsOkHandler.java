package servent.handler.buddy;

import app.AppConfig;
import app.ServentInfo;
import networking.failure.Buddy;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.buddy.IsOkMessage;
import servent.message.buddy.NotOkMessage;
import servent.message.buddy.OkMessage;
import servent.message.buddy.PingMessage;
import servent.message.util.MessageUtil;

public class IsOkHandler implements MessageHandler {

    private IsOkMessage clientMessage;

    public IsOkHandler(Message clientMessage){
        this.clientMessage = (IsOkMessage) clientMessage;
    }
    @Override
    public void run() {
        ServentInfo toCheck = clientMessage.getIsOkServent();
        Buddy.getInstance().addToCheckFailure(toCheck);
        PingMessage pingMessage = new PingMessage(MessageType.PING, AppConfig.myServentInfo, toCheck, "PING_CHECK");
        MessageUtil.sendMessage(pingMessage);

        try {
            Thread.sleep(AppConfig.SOFT_FAILURE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Message message;
        if(!Buddy.getInstance().getCheckForFailure().contains(toCheck)){
            // All good, servent is alive
            message = new OkMessage(MessageType.OK, AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo(), toCheck);
        }
        else{
            message =  new NotOkMessage(MessageType.NOT_OK, AppConfig.myServentInfo, clientMessage.getOriginalSenderInfo(), toCheck);
        }

        MessageUtil.sendMessage(message);
    }
}
