package networking.failure;

import app.AppConfig;
import app.ServentInfo;
import networking.ServentNeighbours;
import networking.SystemState;
import networking.file.FileLibrary;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.buddy.GiveBackupMessage;
import servent.message.util.MessageUtil;

public class Backuper {
    private static int BACKUP_TIME = 5000;
    private static Backuper instance;

    public static Backuper getInstance() {
        if(instance == null)
            instance = new Backuper();
        return instance;
    }

    private Backuper(){}

    public void backup(){
        try {
            Thread.sleep(BACKUP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ServentNeighbours myNeighbours = SystemState.getInstance().getNeighbours().get(AppConfig.myServentInfo);
        if(myNeighbours == null)
            return;
        ServentInfo left = myNeighbours.getFirstLeft();
        ServentInfo right = myNeighbours.getFirstRight();

        if(left != null){
            Message message = new GiveBackupMessage(MessageType.GIVE_BACKUP, AppConfig.myServentInfo, left, FileLibrary.getInstance().getMyFiles());
            MessageUtil.sendMessage(message);
        }

        if(right != null){
            Message message = new GiveBackupMessage(MessageType.GIVE_BACKUP, AppConfig.myServentInfo, right, FileLibrary.getInstance().getMyFiles());
            MessageUtil.sendMessage(message);
        }
    }
}
