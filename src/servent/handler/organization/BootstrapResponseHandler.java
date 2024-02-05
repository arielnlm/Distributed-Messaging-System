package servent.handler.organization;

import app.AppConfig;
import app.ServentInfo;
import mutex.DistributedMutex;
import mutex.suzuki.SuzukiMutex;
import mutex.suzuki.SuzukiToken;
import networking.Address;
import networking.Network;
import networking.SystemState;
import networking.architecture.Organizer;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.organization.AddMeMessage;
import servent.message.organization.BootstrapResponseMessage;
import servent.message.organization.UpdateStateMessage;
import servent.message.util.MessageUtil;

public class BootstrapResponseHandler implements MessageHandler {

    private BootstrapResponseMessage clientMessage;
    public BootstrapResponseHandler(Message clientMessage){
        this.clientMessage = (BootstrapResponseMessage) clientMessage;
    }
    @Override
    public void run() {
        ServentInfo toAsk = clientMessage.getServentToAsk();
        AppConfig.timestampedStandardPrint("Bootstrap response " + toAsk);
        if(toAsk == null){
            // I can't join
            AppConfig.timestampedStandardPrint("I can't join network");
        }
        else if(toAsk.getAddress().equals(AppConfig.bootstrapAddress)){ // I am first to be added
            if(clientMessage.isFirstNode()){
                AppConfig.myServentInfo.setMutexToken(new SuzukiToken());
                AppConfig.myServentInfo.setHasToken(true);
            }
            AppConfig.timestampedStandardPrint("I'm first node in network, adding myself!");
            Organizer.getInstance().addServentToSystem(AppConfig.myServentInfo); // This sends update message aswell
            // Update state
            //Network.getInstance().sendUpdateStateMessage();
        }
        else{
            AppConfig.timestampedStandardPrint("Someone should add me");
            AddMeMessage addMeMessage = new AddMeMessage(
                    MessageType.ADD_ME,
                    AppConfig.myServentInfo,
                    toAsk,
                    AppConfig.myServentInfo
            );
            AppConfig.timestampedStandardPrint(toAsk.getAddress() + " will add me");
            MessageUtil.sendMessage(addMeMessage);
        }
    }
}
