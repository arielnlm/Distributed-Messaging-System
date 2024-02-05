package mutex.suzuki;

import app.ServentInfo;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SuzukiToken implements Serializable {
    private Queue<ServentInfo> queue;

    public SuzukiToken(){
        queue = new ConcurrentLinkedQueue<>();
    }

    public SuzukiToken(Queue<ServentInfo> queue){
        this.queue = new ConcurrentLinkedQueue<>(queue);
    }
    public void addToQueue(ServentInfo servent){
        this.queue.add(servent);
    }
    public Queue<ServentInfo> getQueue() {
        return queue;
    }
}
