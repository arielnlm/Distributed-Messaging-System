package networking.failure;

import app.AppConfig;
import app.ServentInfo;
import networking.SystemState;
import networking.architecture.Organizer;
import servent.message.MessageType;
import servent.message.buddy.IsOkMessage;
import servent.message.buddy.PingMessage;
import servent.message.util.MessageUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Buddy {

    private static Buddy instance;
    private List<ServentInfo> checkForFailure;
    private List<ServentInfo> toPing;

    public static Buddy getInstance(){
        if(instance == null)
            instance = new Buddy();
        return  instance;
    }

    private Buddy(){
        this.checkForFailure = new CopyOnWriteArrayList<>();
        this.toPing = new CopyOnWriteArrayList<>();
    }
    public void pong(ServentInfo servent){
        this.toPing.remove(servent);
    }

    public void ping(){
        clearPingBuddy();

        ServentInfo myBuddy = addPingBuddy();

        if(myBuddy == null){
            AppConfig.timestampedStandardPrint("Don't have buddy to ping");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return;
        }

        PingMessage pingMessage = new PingMessage(MessageType.PING, AppConfig.myServentInfo, myBuddy, "PING");
        MessageUtil.sendMessage(pingMessage);

        if(!isSoftFailure())
            return;

        AppConfig.timestampedStandardPrint("Soft fail for " + myBuddy.getAddress());
        checkForFailedNode();

        if(!isHardFailure())
            return;

        AppConfig.timestampedStandardPrint("Hard fail for " + myBuddy.getAddress());
        for(ServentInfo notOk : getCheckForFailure()){
            Organizer.getInstance().removeServentFromSystem(notOk);
            // Get his backup files
            SystemState.getInstance().transferFiles(notOk);
        }
    }
    public ServentInfo addPingBuddy(){
        ServentInfo myBuddy = SystemState.getInstance().myServentToPing();

        if(myBuddy == null)
            return null;

        this.toPing.add(myBuddy);
        return myBuddy;

    }
    public void clearPingBuddy(){
        this.toPing.clear();
    }
    public boolean isSoftFailure(){
        try {
            Thread.sleep(AppConfig.SOFT_FAILURE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if(toPing.isEmpty()){
            // Everyone pinged - all good
            AppConfig.timestampedStandardPrint("Everyone is ok");
            return false;
        }
        return true;
    }
    public boolean isHardFailure(){
        try {
            Thread.sleep(AppConfig.HARD_FAILURE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return !checkForFailure.isEmpty();
    }

    public void checkForFailedNode(){
        checkForFailure.addAll(toPing);
        // Asked failed servent to get checked by his neighbour
        for(ServentInfo failed : toPing){
            List<ServentInfo> failedNei = new CopyOnWriteArrayList<>(SystemState.getInstance().getNeighbours(failed));
            for(ServentInfo failedNeighbour : failedNei){
                if(toPing.contains(failedNeighbour) || failedNeighbour.getAddress().equals(AppConfig.myServentInfo.getAddress()))
                    continue;
                IsOkMessage isOkMessage = new IsOkMessage(MessageType.IS_OK, AppConfig.myServentInfo, failedNeighbour, failed);
                MessageUtil.sendMessage(isOkMessage);
                return;
            }
        }
    }
    public void addToCheckFailure(ServentInfo serventInfo){
        this.checkForFailure.add(serventInfo);
    }

    public void removeFromCheckFailure(ServentInfo serventInfo){
        this.checkForFailure.remove(serventInfo);
    }

    public List<ServentInfo> getCheckForFailure() {
        return checkForFailure;
    }

    public List<ServentInfo> getToPing() {
        return toPing;
    }
}
