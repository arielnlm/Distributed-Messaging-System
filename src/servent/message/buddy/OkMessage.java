package servent.message.buddy;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class OkMessage extends BasicMessage {
    private ServentInfo okServent;
    public OkMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, ServentInfo okServent) {
        super(type, originalSenderInfo, receiverInfo);
        this.okServent = okServent;
    }

    public ServentInfo getOkServent() {
        return okServent;
    }
}
