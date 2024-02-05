package servent.message.file;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.io.File;

public class TellFileActionMessage extends BasicMessage {
    private File file;

    public TellFileActionMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, File file) {
        super(type, originalSenderInfo, receiverInfo);
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
