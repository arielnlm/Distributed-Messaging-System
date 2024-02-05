package servent.handler.file;

import app.AppConfig;
import networking.file.FileLibrary;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.file.TellFileActionMessage;

import java.io.File;

public class TellFileActionHandler implements MessageHandler {

    private TellFileActionMessage clientMessage;
    public TellFileActionHandler(Message clientMessage){
        this.clientMessage = (TellFileActionMessage) clientMessage;
    }

    @Override
    public void run() {
        File file = clientMessage.getFile();
        if(file == null){
            AppConfig.timestampedStandardPrint("File " + file.getName() + " not found");
        }
        else{
            FileLibrary.getInstance().printFile(file);
        }
    }
}
