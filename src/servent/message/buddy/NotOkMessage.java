package servent.message.buddy;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class NotOkMessage extends BasicMessage {
    private ServentInfo notOkServent;
    public NotOkMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, ServentInfo notOkServent) {
        super(type, originalSenderInfo, receiverInfo);
        this.notOkServent = notOkServent;
    }

    public ServentInfo getNotOkServent() {
        return notOkServent;
    }
}
