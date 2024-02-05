package cli.command;

import java.util.List;

import app.AppConfig;
import networking.failure.BackupThread;
import networking.failure.PingThread;
import cli.CLIParser;
import servent.SimpleServentListener;
import servent.message.util.FifoSendWorker;

public class StopCommand implements CLICommand {

	private CLIParser parser;
	private PingThread pinger;
	private BackupThread backuper;
	private SimpleServentListener listener;
	private List<FifoSendWorker> senderWorkers;
	
	public StopCommand(CLIParser parser, SimpleServentListener listener,
					   List<FifoSendWorker> senderWorkers, PingThread pinger, BackupThread backuper) {
		this.parser = parser;
		this.listener = listener;
		this.senderWorkers = senderWorkers;
		this.pinger = pinger;
		this.backuper = backuper;
	}
	
	@Override
	public String commandName() {
		return "stop";
	}

	@Override
	public void execute(String args) {
		AppConfig.timestampedStandardPrint("Stopping...");
		parser.stop();
		listener.stop();
		for (FifoSendWorker senderWorker : senderWorkers) {
			senderWorker.stop();
		}
		pinger.stop();
		backuper.stop();
	}

}
