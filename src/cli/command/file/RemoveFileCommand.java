package cli.command.file;

import app.AppConfig;
import cli.command.CLICommand;
import networking.file.FileLibrary;
import networking.Network;
import networking.SystemState;

import java.io.File;

public class RemoveFileCommand implements CLICommand {
    @Override
    public String commandName() {
        return "remove";
    }

    @Override
    public void execute(String args) {
        args = AppConfig.WORKING_ROOT + "/" + args;
        File file = new File(args);
        AppConfig.timestampedStandardPrint("NEIGHTBOURS " + SystemState.getInstance().getNeighbours());
        if(FileLibrary.getInstance().removeFile(file)){
            Network.getInstance().sendUpdateStateMessageLock();
        }
        else{
            AppConfig.timestampedStandardPrint("Can't remove file with name " + args);
        }
    }
}
