package mutex.suzuki;

import app.AppConfig;
import app.ServentInfo;
import mutex.DistributedMutex;
import networking.SystemState;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.mutex.suzuki.SuzukiReplyMessage;
import servent.message.mutex.suzuki.SuzukiRequestMessage;
import servent.message.util.MessageUtil;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
public class SuzukiMutex implements DistributedMutex {


    @Override
    public void lock() {
        if(AppConfig.myServentInfo.isHasToken()){
            AppConfig.myServentInfo.setInCriticalSection(true);
            return;
        }
        incrementMySequenceNumber();

        broadcastRequestMessage();

        while(!AppConfig.myServentInfo.isHasToken()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        AppConfig.myServentInfo.setInCriticalSection(true);
    }


    @Override
    public void unlock() {
        AppConfig.myServentInfo.setInCriticalSection(false);
        // LN[i] = RN[i]
        AppConfig.myServentInfo.getLastNumbers().put(
                AppConfig.myServentInfo,
                AppConfig.myServentInfo.getRequestNumbers().get(AppConfig.myServentInfo)
        );

        for(Map.Entry<ServentInfo, Integer> entry : AppConfig.myServentInfo.getRequestNumbers().entrySet()){
            int reqNum = entry.getValue();
            int lastNum = AppConfig.myServentInfo.getLastNumbers().getOrDefault(entry.getKey(), 0);
            if(reqNum == lastNum + 1){
                if(!AppConfig.myServentInfo.getRequestQueue().contains(entry.getKey())){
                    AppConfig.myServentInfo.getRequestQueue().add(entry.getKey());
                }
            }
        }

        if (!AppConfig.myServentInfo.getRequestQueue().isEmpty()) {
            ServentInfo requested = AppConfig.myServentInfo.getRequestQueue().peek();
            sendTokenTo(requested);
        }
    }
    public void sendTokenTo(ServentInfo serventInfo) {

        // Update token information
        AppConfig.myServentInfo.setHasToken(false);
        AppConfig.myServentInfo.getRequestQueue().remove(serventInfo);
        // Send TOKEN message
        SuzukiReplyMessage message = new SuzukiReplyMessage(
                MessageType.SUZUKI_REPLY,
                AppConfig.myServentInfo, serventInfo,
                new LinkedList<>(AppConfig.myServentInfo.getRequestQueue()),
                new HashMap<>(AppConfig.myServentInfo.getRequestNumbers())
        );
        MessageUtil.sendMessage(message);
    }

    private void incrementMySequenceNumber(){
        AppConfig.myServentInfo.getRequestNumbers().put(AppConfig.myServentInfo,
                AppConfig.myServentInfo.getRequestNumbers().getOrDefault(AppConfig.myServentInfo, 0) + 1);
    }
    private void broadcastRequestMessage(){
        for(ServentInfo servent : SystemState.getInstance().getActiveNodes()){
            if(servent.getAddress().equals(AppConfig.myServentInfo.getAddress()))
                continue; // Skip myself
            Message message = new SuzukiRequestMessage(MessageType.SUZUKI_REQUEST,
                    AppConfig.myServentInfo,
                    servent,
                    AppConfig.myServentInfo.getRequestNumbers().get(AppConfig.myServentInfo));
            MessageUtil.sendMessage(message);
        }
    }
}
