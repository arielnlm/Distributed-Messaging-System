package servent.message.organization;

import app.ServentInfo;
import networking.Address;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class AddMeMessage extends BasicMessage {

    private ServentInfo toAddServent;

    public AddMeMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, ServentInfo toAddAddress) {
        super(type, originalSenderInfo, receiverInfo);
        this.toAddServent = originalSenderInfo;
    }

    public ServentInfo getToAddServent() {
        return toAddServent;
    }
}
