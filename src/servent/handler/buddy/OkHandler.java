package servent.handler.buddy;

import app.ServentInfo;
import networking.failure.Buddy;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.buddy.OkMessage;

public class OkHandler implements MessageHandler {

    private OkMessage clientMessage;
    public OkHandler(Message clientMessage){
        this.clientMessage = (OkMessage) clientMessage;
    }
    @Override
    public void run() {
        ServentInfo okServnt = clientMessage.getOkServent();
        Buddy.getInstance().removeFromCheckFailure(okServnt);
    }
}
