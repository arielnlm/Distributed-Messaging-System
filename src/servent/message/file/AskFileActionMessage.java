package servent.message.file;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class AskFileActionMessage extends BasicMessage {

    private String fileName;
    public AskFileActionMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, String fileName) {
        super(type, originalSenderInfo, receiverInfo);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
