package servent.handler.file;

import app.AppConfig;
import networking.file.FileLibrary;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.file.AskFileActionMessage;
import servent.message.file.TellFileActionMessage;
import servent.message.util.MessageUtil;

import java.io.File;

public class AskFileActionHandler implements MessageHandler {

    private AskFileActionMessage clientMessage;

    public AskFileActionHandler(Message clientMessage){
        this.clientMessage = (AskFileActionMessage) clientMessage;
    }

    @Override
    public void run() {
        File file = FileLibrary.getInstance().findFile(clientMessage.getFileName());
        TellFileActionMessage message = new TellFileActionMessage(
                MessageType.FILE_TELL,
                AppConfig.myServentInfo,
                clientMessage.getOriginalSenderInfo(),
                file
        );
        MessageUtil.sendMessage(message);
    }
}
