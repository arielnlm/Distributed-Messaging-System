package servent.handler.buddy;

import app.AppConfig;
import app.ServentInfo;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.buddy.NotOkMessage;
import servent.message.buddy.OkMessage;

public class NotOkHandler implements MessageHandler {
    private NotOkMessage clientMessage;
    public NotOkHandler(Message clientMessage){
        this.clientMessage = (NotOkMessage) clientMessage;
    }
    @Override
    public void run() {
        ServentInfo notOkServent = clientMessage.getNotOkServent();
        AppConfig.timestampedStandardPrint("Servent " + notOkServent.getAddress() + " hard failed!");
    }
}
