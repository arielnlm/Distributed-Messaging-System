package servent.handler.organization;

import app.AppConfig;
import app.ServentInfo;
import networking.Network;
import networking.SystemState;
import networking.architecture.Organizer;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.organization.AddMeMessage;
import servent.message.organization.UpdateStateMessage;
import servent.message.organization.WelcomeMessage;
import servent.message.util.MessageUtil;

public class AddServentHandler implements MessageHandler {
    private AddMeMessage clientMessage;

    public AddServentHandler(Message clientMessage){
        this.clientMessage = (AddMeMessage)clientMessage;
    }
    @Override
    public void run() {
        Organizer.getInstance().addServentToSystem(clientMessage.getToAddServent());
        WelcomeMessage welcomeMessage = new WelcomeMessage(
                MessageType.WELCOME,
                AppConfig.myServentInfo,
                clientMessage.getOriginalSenderInfo(),
                true
        );
        MessageUtil.sendMessage(welcomeMessage);

    }
}
