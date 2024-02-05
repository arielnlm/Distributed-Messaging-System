package networking.failure;

import app.AppConfig;
import app.Cancellable;
import app.ServentInfo;
import networking.Network;
import networking.SystemState;
import servent.message.MessageType;
import servent.message.buddy.PingMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PingThread implements Runnable, Cancellable {

    private volatile boolean working;

    public PingThread(){
        this.working = true;
    }

    @Override
    public void run() {
        while(working) {
                Network.getInstance().ping();
        }
    }

    @Override
    public void stop() {
        AppConfig.timestampedStandardPrint("Not working anymore");
        this.working = false;
    }

}
