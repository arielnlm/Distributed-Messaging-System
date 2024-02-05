package cli.command.file;

import app.AppConfig;
import cli.command.CLICommand;
import mutex.DistributedMutex;
import networking.file.FileLibrary;
import networking.Network;

import java.io.File;

public class AddFileCommand implements CLICommand {

    private DistributedMutex mutex;
    public AddFileCommand(DistributedMutex mutex){
        this.mutex = mutex;
    }
    @Override
    public String commandName() {
        return "add";
    }

    @Override
    public void execute(String args) {
        args = AppConfig.WORKING_ROOT + "/" + args;
        AppConfig.timestampedStandardPrint("Adding file " + args);
        File file = new File(args);
        addFilesRecursively(file);
    }

    private void addFilesRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    addFilesRecursively(child);
                }
            }
        } else {
            if(FileLibrary.getInstance().addFile(file)){
                Network.getInstance().sendUpdateStateMessageLock();
            }
        }
    }
}
