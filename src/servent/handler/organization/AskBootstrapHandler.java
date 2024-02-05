package servent.handler.organization;

import app.AppConfig;
import app.ServentInfo;
import networking.Address;
import networking.SystemState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.organization.BootstrapResponseMessage;
import servent.message.util.MessageUtil;
import java.util.Random;

public class AskBootstrapHandler implements MessageHandler {

    private Message clientMessage;
    public AskBootstrapHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }
    @Override
    public void run() {
        AppConfig.timestampedStandardPrint("Bootstrap received message " + clientMessage.getMessageType());
        ServentInfo sender = clientMessage.getOriginalSenderInfo();
        BootstrapResponseMessage response;
        if(SystemState.getInstance().getActiveNodes().contains(sender)){
            response = new BootstrapResponseMessage(MessageType.BOOTSTRAP_RESPONSE,
                    AppConfig.myServentInfo,
                    clientMessage.getOriginalSenderInfo(),
                    null);
            AppConfig.timestampedStandardPrint("Can't add new node, port already exists..." + sender.getAddress());
        }
        else{
            // Make better decision making
            if(SystemState.getInstance().getActiveNodes().size() == 0){ // node that ask would be first node
                response = new BootstrapResponseMessage(MessageType.BOOTSTRAP_RESPONSE,
                        AppConfig.myServentInfo,
                        clientMessage.getOriginalSenderInfo(),
                        AppConfig.myServentInfo, true);
                AppConfig.timestampedStandardPrint("First node is coming ");
            }
            else{
                int random = new Random().nextInt(SystemState.getInstance().getActiveNodes().size());
                ServentInfo randomServent = SystemState.getInstance().getActiveNodes().get(random);
                response = new BootstrapResponseMessage(MessageType.BOOTSTRAP_RESPONSE,
                        AppConfig.myServentInfo,
                        clientMessage.getOriginalSenderInfo(),
                        randomServent
                        );
                AppConfig.timestampedStandardPrint("Ask " + randomServent.getAddress() + " to add " + sender.getAddress());
            }
        }
        // direct communication between every node & bootstrap
        MessageUtil.sendMessage(response);
    }
}
