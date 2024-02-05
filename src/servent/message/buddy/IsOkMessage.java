package servent.message.buddy;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class IsOkMessage extends BasicMessage {
    private ServentInfo isOkServent;
    public IsOkMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, ServentInfo isOkServent) {
        super(type, originalSenderInfo, receiverInfo);
        this.isOkServent = isOkServent;
    }

    public ServentInfo getIsOkServent() {
        return isOkServent;
    }
}
