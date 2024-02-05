package servent.handler.organization;

import app.AppConfig;
import networking.SystemState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.organization.UpdateStateMessage;

public class UpdateStateHandler implements MessageHandler {

    private UpdateStateMessage clientMessage;

    public UpdateStateHandler(Message clientMessage){
        this.clientMessage = (UpdateStateMessage) clientMessage;
    }
    @Override
    public void run() {
        SystemState.setInstance(clientMessage.getNewSystemState());
    }
}
