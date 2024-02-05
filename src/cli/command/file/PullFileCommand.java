package cli.command.file;

import app.AppConfig;
import app.ServentInfo;
import cli.command.CLICommand;
import networking.file.FileLibrary;
import networking.SystemState;
import servent.message.MessageType;
import servent.message.file.AskFileActionMessage;
import servent.message.util.MessageUtil;

import java.io.*;

public class PullFileCommand implements CLICommand {
    @Override
    public String commandName() {
        return "pull";
    }

    @Override
    public void execute(String args) {
        args = AppConfig.WORKING_ROOT + "/" + args;
        AppConfig.timestampedStandardPrint("Getting file " + args);
        // Do I have that file?
        File file = FileLibrary.getInstance().findFile(args);
        if(file != null){
           FileLibrary.getInstance().printFile(file);
        }
        else{
            AppConfig.timestampedStandardPrint("Library " + SystemState.getInstance().getFiles());
            ServentInfo fileOwner = SystemState.getInstance().findFileByName(args);
            if(fileOwner == null){
                AppConfig.timestampedStandardPrint("Can't find file with name: " + args);
                return;
            }
            AppConfig.timestampedStandardPrint("Ask " + fileOwner + " for file " + args);
            AskFileActionMessage askMessage = new AskFileActionMessage(
                    MessageType.FILE_ASK,
                    AppConfig.myServentInfo,
                    fileOwner,
                    args
            );
            MessageUtil.sendMessage(askMessage);
        }
    }
}
