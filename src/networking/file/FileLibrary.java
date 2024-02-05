package networking.file;

import app.AppConfig;
import app.ServentInfo;
import networking.SystemState;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class FileLibrary implements Serializable {
    private List<File> myFiles;
    private Map<ServentInfo, List<File>> backup;
    private static FileLibrary instance;

    public static FileLibrary getInstance() {
        if(instance == null)
            instance = new FileLibrary();
        return instance;
    }

    private FileLibrary(){
        this.myFiles = new CopyOnWriteArrayList<>();
        this.backup = new ConcurrentHashMap<>();
    }

    public void transferFiles(ServentInfo from){
        Iterator<Map.Entry<ServentInfo, List<File>>> iterator = backup.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<ServentInfo, List<File>> next = iterator.next();
            if(next.getKey().equals(from)){
                // Add to my files
                myFiles.addAll(next.getValue());
                // Remove from backup part
                iterator.remove();
                return;
            }
        }
    }
    public boolean addFile(File file){
        boolean success = this.myFiles.add(file); // Updates local file library
        if(success)
            SystemState.getInstance().addFileByName(file.getPath()); // Updates global file info
        return success;
    }
    public boolean removeFile(File file){
        boolean success = this.myFiles.remove(file);
        if(success){
            SystemState.getInstance().removeFileByName(file.getName());
        }
        return success;
    }

    public File findFile(String name){
        AppConfig.timestampedStandardPrint("Library " + myFiles + " finding " + name);
        for(File file : myFiles){
            if(file.getPath().equals(name))
                return file;
        }
        return null;
    }

    public void printFile(File file){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                sb.append(line).append("\n");
            }
            AppConfig.timestampedStandardPrint(file.getName() + ":\n" + sb.toString());
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addToBackup(ServentInfo serventInfo, List<File> backupFiles){
        this.backup.put(serventInfo, backupFiles);
    }
    public List<File> getMyFiles() {
        return myFiles;
    }

    public List<String> getFileNames(){
        List<String> allFiles = new ArrayList<>();
        for(File f : myFiles){
            allFiles.add(f.getPath());
        }
        return  allFiles;
    }

    public Map<ServentInfo, List<File>> getBackup() {
        return backup;
    }
    public List<File> getBackup(ServentInfo servent) {
        return backup.get(servent);
    }

}
