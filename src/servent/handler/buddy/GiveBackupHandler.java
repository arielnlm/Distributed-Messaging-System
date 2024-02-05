package servent.handler.buddy;

import networking.file.FileLibrary;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.buddy.GiveBackupMessage;

public class GiveBackupHandler implements MessageHandler {

    private GiveBackupMessage clientMessage;

    public GiveBackupHandler(Message clientMessage){
        this.clientMessage = (GiveBackupMessage) clientMessage;
    }

    @Override
    public void run() {
        FileLibrary.getInstance().addToBackup(clientMessage.getOriginalSenderInfo(), clientMessage.getBackupFiles());
    }

    public GiveBackupMessage getClientMessage() {
        return clientMessage;
    }
}
