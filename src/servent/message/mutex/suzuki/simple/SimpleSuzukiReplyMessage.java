package servent.message.mutex.suzuki.simple;

import app.ServentInfo;
import mutex.suzuki.SuzukiToken;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class SimpleSuzukiReplyMessage extends BasicMessage {
    private SuzukiToken mutexToken;
    public SimpleSuzukiReplyMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, SuzukiToken mutexToken) {
        super(type, originalSenderInfo, receiverInfo);
        this.mutexToken = mutexToken;
    }

    public SuzukiToken getMutexToken() {
        return mutexToken;
    }
}
