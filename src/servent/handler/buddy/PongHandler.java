package servent.handler.buddy;

import networking.Network;
import networking.failure.Buddy;
import servent.handler.MessageHandler;
import servent.message.Message;

public class PongHandler implements MessageHandler{
    private Message clientMessage;
    public PongHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        // Assuming ping will be possible with direct neighbours only
        String text = clientMessage.getMessageText();
        if(text.equalsIgnoreCase("PONG_CHECK")){
            Buddy.getInstance().removeFromCheckFailure(clientMessage.getOriginalSenderInfo());
        }
        else{
            Network.getInstance().pong(clientMessage.getOriginalSenderInfo());
        }
    }
}
