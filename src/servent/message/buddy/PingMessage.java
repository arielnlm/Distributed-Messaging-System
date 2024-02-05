package servent.message.buddy;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class PingMessage extends BasicMessage {
    public PingMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, String messageText) {
        super(type, originalSenderInfo, receiverInfo, messageText);
    }
}
