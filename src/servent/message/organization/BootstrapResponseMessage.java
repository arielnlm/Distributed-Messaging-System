package servent.message.organization;

import app.AppConfig;
import app.ServentInfo;
import networking.Address;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class BootstrapResponseMessage extends BasicMessage {

    private ServentInfo serventToAsk;
    private boolean firstNode;
    public BootstrapResponseMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, ServentInfo serventToAsk) {
        super(type, originalSenderInfo, receiverInfo);
        this.serventToAsk = serventToAsk;
        this.firstNode = false;
    }

    public BootstrapResponseMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, ServentInfo serventToAsk, boolean firstNode) {
        this(type, originalSenderInfo, receiverInfo, serventToAsk);
        this.firstNode = firstNode;
    }

    public boolean isFirstNode() {
        return firstNode;
    }

    public ServentInfo getServentToAsk() {
        return serventToAsk;
    }
}
