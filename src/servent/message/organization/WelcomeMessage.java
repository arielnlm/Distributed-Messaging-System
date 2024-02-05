package servent.message.organization;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class WelcomeMessage extends BasicMessage {
    private boolean canJoin;
    public WelcomeMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, boolean canJoin) {
        super(type, originalSenderInfo, receiverInfo);
        this.canJoin = canJoin;
    }

    public boolean isCanJoin() {
        return canJoin;
    }
}
