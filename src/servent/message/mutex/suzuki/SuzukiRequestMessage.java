package servent.message.mutex.suzuki;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class SuzukiRequestMessage extends BasicMessage {

    private int seqNum;
    public SuzukiRequestMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, int seqNum) {
        super(type, originalSenderInfo, receiverInfo);
        this.seqNum = seqNum;
    }

    public int getSeqNum() {
        return seqNum;
    }
}
