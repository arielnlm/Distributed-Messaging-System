package networking;

import app.AppConfig;
import app.ServentInfo;
import mutex.DistributedMutex;
import mutex.suzuki.SuzukiMutex;
import mutex.suzuki.SuzukiToken;
import networking.failure.Backuper;
import networking.failure.Buddy;
import servent.message.MessageType;
import servent.message.organization.UpdateStateMessage;
import servent.message.util.MessageUtil;

public class Network {
    private DistributedMutex mutex;
    private static Network instance;

    public static Network getInstance() {
        if (instance == null)
            instance = new Network();
        return instance;
    }

    private Network(){}

    // Backup mechanism
    public void backup(){
       Backuper.getInstance().backup();
    }

    // PING & PONG mechanism
    public void ping(){
       Buddy.getInstance().ping();
    }
    public void pong(ServentInfo ponged){
        Buddy.getInstance().pong(ponged);
    }

    public void sendUpdateStateMessageLock(){
        synchronized (SuzukiToken.class){
            AppConfig.timestampedStandardPrint("Need mutex [update]");
            mutex.lock();
            AppConfig.timestampedStandardPrint("In mutex [update]");
            sendUpdateStateMessage();
            mutex.unlock();
            AppConfig.timestampedStandardPrint("Unlock [update]");
        }
    }
    public void sendUpdateStateMessage(){
        UpdateStateMessage message = new UpdateStateMessage(
                MessageType.UPDATE_STATE,
                AppConfig.myServentInfo,
                null,
                SystemState.getInstance().copy()
        );
        MessageUtil.sendMessage(message);
        for(ServentInfo servent : SystemState.getInstance().getActiveNodes()){
            if(servent.getAddress() == AppConfig.myServentInfo.getAddress())
                continue;
            UpdateStateMessage mssg = new UpdateStateMessage(
                    MessageType.UPDATE_STATE,
                    AppConfig.myServentInfo,
                    servent,
                    SystemState.getInstance().copy()
            );
            MessageUtil.sendMessage(mssg);
        }
    }

    public void setMutex(DistributedMutex mutex){
        this.mutex = mutex;
    }

}
