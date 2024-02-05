package networking;

import app.AppConfig;
import app.ServentInfo;
import mutex.DistributedMutex;
import networking.file.FileLibrary;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SystemState implements Serializable {
    private static SystemState instance;
    private int id;
    private ServentInfo bootstrap;
    private Map<ServentInfo, ServentNeighbours> neighbours = new ConcurrentHashMap<>();
    private Map<ServentInfo, List<String>> files = new ConcurrentHashMap<>();
    private List<ServentInfo> activeNodes = new ArrayList<>();
    private DistributedMutex mutex;

    public void setMutex(DistributedMutex mutex) {
        this.mutex = mutex;
    }

    private SystemState(){}

    public static void setInstance(SystemState state){
        instance = state;
    }

    public static SystemState getInstance(){
        if(instance == null)
            instance = new SystemState();
        return instance;
    }
    public SystemState copy() {
        SystemState newState = new SystemState();

        // Copy primitive fields
        newState.id = this.id;
        newState.bootstrap = this.bootstrap;

        // Deep copy ConcurrentHashMap
        newState.neighbours = new ConcurrentHashMap<>();
        for (Map.Entry<ServentInfo, ServentNeighbours> entry : this.neighbours.entrySet()) {
            newState.neighbours.put(entry.getKey(), entry.getValue());
        }

        // Deep copy FileLibrary map
        newState.files = new ConcurrentHashMap<>();
        for (Map.Entry<ServentInfo, List<String>> entry : this.files.entrySet()) {
            newState.files.put(entry.getKey(), entry.getValue());
        }

        // Deep copy List of active nodes
        newState.activeNodes = new ArrayList<>();
        for (ServentInfo serventInfo : this.activeNodes) {
            newState.activeNodes.add(serventInfo);
        }

        return newState;
    }

    public ServentInfo findFileByName(String name){
        for(Map.Entry<ServentInfo, List<String>> entry : files.entrySet()){
            if(entry.getValue().contains(name)){
                return entry.getKey();
            }
        }
        return null;
    }

    // After some time send backup to neighbours
    public boolean removeFileByName(String name){
        for(Map.Entry<ServentInfo, List<String>> entry : files.entrySet()){
            if(entry.getValue().contains(name)){
                List<String> copy = new ArrayList<>(entry.getValue());
                copy.remove(name);
                files.put(entry.getKey(), copy);
                return true;
            }
        }
        return false;
    }

    public boolean addFileByName(String name) {
        for(Map.Entry<ServentInfo, List<String>> entry : files.entrySet()){
            if(entry.getValue().contains(name)){
                return false;
            }
        }
        // If files.get() is null then make an empty list
        List<String> copy = files.get(AppConfig.myServentInfo);
        if(copy == null) {
            copy = new ArrayList<>();
        } else {
            copy = new ArrayList<>(copy); // create a copy of existing list
        }

        copy.add(name);
        files.put(AppConfig.myServentInfo, copy);
        return true;
    }

    public List<ServentInfo> getActiveNodes() {
        return activeNodes;
    }

    public Map<ServentInfo, List<String>> getFiles() {
        return files;
    }

    public Map<ServentInfo, ServentNeighbours> getNeighbours() {
        return neighbours;
    }

    public ServentInfo getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(ServentInfo bootstrap) {
        this.bootstrap = bootstrap;
    }

    public List<ServentInfo> getNeighbours(ServentInfo forServent){
        ServentNeighbours sn = neighbours.get(forServent);

        if (sn == null) {
            return Collections.emptyList();
        }

        return Stream.of(sn.getFirstLeft(), sn.getSecondRight(), sn.getSecondLeft(), sn.getFirstRight())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    public void transferFiles(ServentInfo from){
        AppConfig.timestampedStandardPrint("Backing up files for " + from.getAddress());
        // Update physical files location
        FileLibrary.getInstance().transferFiles(from);
        // Update information about location
        files.remove(from);
        files.put(AppConfig.myServentInfo, FileLibrary.getInstance().getFileNames());
    }
    public ServentInfo myServentToPing(){
        ServentNeighbours sn = neighbours.get(AppConfig.myServentInfo);
        if(sn == null)
            return null;
        ServentInfo left = sn.getFirstRight();

        return left;
    }
}
