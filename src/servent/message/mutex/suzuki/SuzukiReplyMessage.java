package servent.message.mutex.suzuki;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SuzukiReplyMessage extends BasicMessage {
    private Map<ServentInfo, Integer> requestNumbers;
    private Queue<ServentInfo> requestQueue;
    public SuzukiReplyMessage(MessageType type, ServentInfo originalSenderInfo, ServentInfo receiverInfo, Queue<ServentInfo> requestQueue, Map<ServentInfo, Integer> requestNumbers) {
        super(type, originalSenderInfo, receiverInfo);
        this.requestQueue = new ConcurrentLinkedQueue<>(requestQueue);
        this.requestNumbers = new ConcurrentHashMap<>(requestNumbers);
    }

    public Queue<ServentInfo> getRequestQueue() {
        return requestQueue;
    }

    public Map<ServentInfo, Integer> getRequestNumbers() {
        return requestNumbers;
    }
}
