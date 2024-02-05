package cli.command.organization;

import app.AppConfig;
import cli.command.CLICommand;
import networking.Address;
import servent.message.MessageType;
import servent.message.organization.AskBootstrapMessage;
import servent.message.util.MessageUtil;

public class AddServentCommand implements CLICommand {
    @Override
    public String commandName() {
        return "add_me";
    }

    @Override
    public void execute(String args) {
        // Send message to bootstrap to tell me who to ask to add me to network
        AskBootstrapMessage askBootstrapMessage = new AskBootstrapMessage(MessageType.BOOTSTRAP_ASK, AppConfig.myServentInfo, null);
        MessageUtil.sendMessage(askBootstrapMessage);
    }
}
