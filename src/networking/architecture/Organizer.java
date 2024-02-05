package networking.architecture;

import app.AppConfig;
import app.ServentInfo;
import mutex.DistributedMutex;
import mutex.TokenMutex;
import networking.Address;
import networking.Network;
import networking.ServentNeighbours;
import networking.SystemState;
import java.util.ArrayList;
import java.util.Map;

public class Organizer {
    private static Organizer instance;

    private DistributedMutex mutex;

    public static Organizer getInstance() {
        if(instance == null){
            instance = new Organizer();
        }
        return instance;
    }

    private Organizer(){}

    public void addServentToSystem(ServentInfo newServent) {
        synchronized (TokenMutex.class) {
            SystemState systemState = SystemState.getInstance();
            AppConfig.timestampedStandardPrint("Need mutex [add] " + mutex);
            mutex.lock();
            AppConfig.timestampedStandardPrint("In mutex [add]");
            Address newAddress = newServent.getAddress();
            int size = systemState.getActiveNodes().size();
            systemState.getActiveNodes().add(newServent);
            ServentNeighbours newNodeNeighbours = new ServentNeighbours();
            switch (size) {
                case 0:
                    // First node, all neighbours point to itself
                    newNodeNeighbours.setAll(newServent);
                    systemState.getNeighbours().put(newServent, newNodeNeighbours);
                    break;
                case 1:
                    // Second node, all neighbours point to each other
                    ServentNeighbours myNeighbours = systemState.getNeighbours().get(AppConfig.myServentInfo);
                    myNeighbours.setAll(newServent);
                    newNodeNeighbours.setAll(AppConfig.myServentInfo);
                    systemState.getNeighbours().put(AppConfig.myServentInfo, myNeighbours);
                    systemState.getNeighbours().put(newServent, newNodeNeighbours);
                    break;
                case 2:
                    // Third node, first and second neighbours are the same
                    ArrayList<Map.Entry<ServentInfo, ServentNeighbours>> entryList = new ArrayList<>(systemState.getNeighbours().entrySet());

                    Map.Entry<ServentInfo, ServentNeighbours> firstEntry = entryList.get(0);
                    Map.Entry<ServentInfo, ServentNeighbours> secondEntry = entryList.get(1);

                    newNodeNeighbours.setFirstLeft(firstEntry.getKey());
                    newNodeNeighbours.setSecondLeft(secondEntry.getKey());
                    newNodeNeighbours.setFirstRight(secondEntry.getKey());
                    newNodeNeighbours.setSecondRight(firstEntry.getKey());

                    firstEntry.getValue().setFirstRight(newServent);
                    firstEntry.getValue().setSecondRight(secondEntry.getKey());
                    firstEntry.getValue().setFirstLeft(secondEntry.getKey());
                    firstEntry.getValue().setSecondLeft(newServent);

                    secondEntry.getValue().setFirstRight(firstEntry.getKey());
                    secondEntry.getValue().setSecondRight(newServent);
                    secondEntry.getValue().setFirstLeft(newServent);
                    secondEntry.getValue().setSecondLeft(firstEntry.getKey());

                    systemState.getNeighbours().put(newServent, newNodeNeighbours);
                    systemState.getNeighbours().put(firstEntry.getKey(), firstEntry.getValue());
                    systemState.getNeighbours().put(secondEntry.getKey(), secondEntry.getValue());
                    break;
                default:
                    // Regular case with more than 2 nodes
                    ServentNeighbours orgNeighbours = systemState.getNeighbours().get(AppConfig.myServentInfo);
                    ServentInfo firstLeft = orgNeighbours.getFirstLeft();
                    ServentInfo secondLeft = orgNeighbours.getSecondLeft();
                    ServentInfo firstRight = orgNeighbours.getFirstRight();;
                    ServentInfo secondRight = orgNeighbours.getSecondRight();

                    newNodeNeighbours.setFirstLeft(firstLeft);
                    newNodeNeighbours.setSecondLeft(secondLeft);
                    newNodeNeighbours.setFirstRight(AppConfig.myServentInfo);
                    newNodeNeighbours.setSecondRight(firstRight);

                    // Second left connections
                    ServentNeighbours slNeighbours = systemState.getNeighbours().get(secondLeft);
                    slNeighbours.setSecondRight(newServent);

                    // First left connections
                    ServentNeighbours flNeighbours = systemState.getNeighbours().get(firstLeft);
                    flNeighbours.setFirstRight(newServent);
                    flNeighbours.setSecondRight(AppConfig.myServentInfo);

                    // First right connections
                    ServentNeighbours frNeighbour = systemState.getNeighbours().get(firstRight);
                    frNeighbour.setSecondLeft(newServent);

                    // My connections
                    orgNeighbours.setFirstLeft(newServent);
                    orgNeighbours.setSecondLeft(firstLeft);

                    // Update values
                    systemState.getNeighbours().put(AppConfig.myServentInfo, orgNeighbours);
                    systemState.getNeighbours().put(firstLeft, flNeighbours);
                    systemState.getNeighbours().put(secondLeft, slNeighbours);
                    systemState.getNeighbours().put(firstRight, frNeighbour);
                    systemState.getNeighbours().put(newServent, newNodeNeighbours);
            }
            Network.getInstance().sendUpdateStateMessage();
            mutex.unlock();
            AppConfig.timestampedStandardPrint("Unlock [add]");
            AppConfig.timestampedStandardPrint(newServent + ", New neighbours " + systemState.getNeighbours());
        }
    }

    public void removeServentFromSystem(ServentInfo removeServent){
        synchronized (TokenMutex.class){
            SystemState systemState = SystemState.getInstance();
            AppConfig.timestampedStandardPrint("Need mutex [remove]");
            // LOCK
            mutex.lock();
            // LOCK
            AppConfig.timestampedStandardPrint("In mutex [remove]");

            Address removeNode = removeServent.getAddress();
            int size = systemState.getActiveNodes().size();
            ArrayList<Map.Entry<ServentInfo, ServentNeighbours>> listN = new ArrayList<>(systemState.getNeighbours().entrySet());
            if (size <= 3) {
                listN.removeIf(entry -> entry.getKey().equals(removeServent));
                if(listN.size() == 2){
                    Map.Entry<ServentInfo, ServentNeighbours> first = listN.get(0);
                    Map.Entry<ServentInfo, ServentNeighbours> second = listN.get(1);
                    first.getValue().setAll(second.getKey());
                    second.getValue().setAll(first.getKey());
                    systemState.getNeighbours().put(first.getKey(), first.getValue());
                    systemState.getNeighbours().put(second.getKey(), second.getValue());
                }
                else if(listN.size() == 1){
                    Map.Entry<ServentInfo, ServentNeighbours> first = listN.get(0);
                    first.getValue().setAll(first.getKey());
                }
                else{
                    AppConfig.timestampedStandardPrint("Trying to remove node when there is no node left :(");
                }

            } else {
                ServentNeighbours toRemoveNeighbours = systemState.getNeighbours().get(removeServent);
                AppConfig.timestampedErrorPrint("To remove n " + toRemoveNeighbours + " _ " + removeServent);
                ServentInfo fl = toRemoveNeighbours.getFirstLeft();
                ServentInfo sl = toRemoveNeighbours.getSecondLeft();
                ServentInfo fr = toRemoveNeighbours.getFirstRight();
                ServentInfo sr = toRemoveNeighbours.getSecondRight();

                // First left connections
                ServentNeighbours flN = systemState.getNeighbours().get(fl);
                flN.setFirstRight(fr);
                flN.setSecondRight(sr);

                // Second left connections
                ServentNeighbours slN = systemState.getNeighbours().get(sl);
                slN.setSecondRight(fr);

                // First right connections
                ServentNeighbours frN = systemState.getNeighbours().get(fr);
                frN.setFirstLeft(fl);
                frN.setSecondLeft(sl);

                // Second right connections
                ServentNeighbours srN = systemState.getNeighbours().get(sr);
                srN.setSecondLeft(fl);

                systemState.getNeighbours().put(fl, flN);
                systemState.getNeighbours().put(sl, slN);
                systemState.getNeighbours().put(fr, frN);
                systemState.getNeighbours().put(sr, srN);
            }

            systemState.getNeighbours().remove(removeServent);
            systemState.getActiveNodes().remove(removeServent);
            Network.getInstance().sendUpdateStateMessage();
            // UNLOCK
            mutex.unlock();
            // UNLOCK
            AppConfig.timestampedStandardPrint("Unlock [remove]");
            AppConfig.timestampedStandardPrint("Remove neighbours " + systemState.getNeighbours());
        }
    }
    public void setMutex(DistributedMutex mutex){
        this.mutex = mutex;
    }
}
