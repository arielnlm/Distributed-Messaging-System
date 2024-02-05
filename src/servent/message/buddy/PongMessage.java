package servent.message.buddy;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class PongMessage extends BasicMessage {
    public PongMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, String messageText) {
        super(type, originalSenderInfo, receiverInfo, messageText);
    }
}
