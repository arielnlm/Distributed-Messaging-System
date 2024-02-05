package servent.message.organization;

import app.ServentInfo;
import networking.SystemState;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class UpdateStateMessage extends BasicMessage {

    private SystemState newSystemState;
    public UpdateStateMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, SystemState newSystemState) {
        super(type, originalSenderInfo, receiverInfo);
        this.newSystemState = newSystemState;
    }

    public SystemState getNewSystemState() {
        return newSystemState;
    }
}
