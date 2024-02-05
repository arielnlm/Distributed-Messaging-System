package servent.message.organization;

import app.ServentInfo;
import networking.Address;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class AskBootstrapMessage extends BasicMessage {

    private ServentInfo toAddServent;
    public AskBootstrapMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
        super(type, originalSenderInfo, receiverInfo);
        this.toAddServent = originalSenderInfo;
    }
}
