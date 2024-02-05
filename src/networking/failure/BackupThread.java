package networking.failure;

import app.Cancellable;
import networking.Network;

public class BackupThread implements Runnable, Cancellable {
    private volatile boolean working;
    public BackupThread(){
        working = true;
    }
    @Override
    public void stop() {
        working = false;
    }

    @Override
    public void run() {
        while(working){
            Network.getInstance().backup();
        }
    }
}
