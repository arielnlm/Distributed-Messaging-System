package servent.message.buddy;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GiveBackupMessage extends BasicMessage {
    private List<File> backupFiles;
    public GiveBackupMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, List<File> backupFiles) {
        super(type, originalSenderInfo, receiverInfo);
        this.backupFiles = new ArrayList<>(backupFiles);
    }

    public List<File> getBackupFiles() {
        return backupFiles;
    }
}
