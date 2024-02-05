package servent.handler.organization;

import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.organization.WelcomeMessage;

public class WelcomeHandler implements MessageHandler {

    private WelcomeMessage clientMessage;

    public WelcomeHandler(Message clientMessage){
        this.clientMessage = (WelcomeMessage) clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage.isCanJoin()){
            // Nice
        }
        else{
            // Not nice exit
        }

    }
}
