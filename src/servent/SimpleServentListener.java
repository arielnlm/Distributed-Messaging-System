package servent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.AppConfig;
import app.Cancellable;
import app.ServentInfo;
import mutex.DistributedMutex;
import servent.handler.MessageHandler;
import servent.handler.NullHandler;
import servent.handler.buddy.*;
import servent.handler.file.AskFileActionHandler;
import servent.handler.file.TellFileActionHandler;
import servent.handler.mutex.LamportReleaseHandler;
import servent.handler.mutex.LamportReplyHandler;
import servent.handler.mutex.LamportRequestHandler;
import servent.handler.mutex.TokenHandler;
import servent.handler.mutex.suzuki.SuzukiReplyHandler;
import servent.handler.mutex.suzuki.SuzukiRequestHandler;
import servent.handler.mutex.suzuki.simple.SuzukiSimpleReplyHandler;
import servent.handler.mutex.suzuki.simple.SuzukiSimpleRequestHandler;
import servent.handler.organization.*;
import servent.message.BasicMessage;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class SimpleServentListener implements Runnable, Cancellable {

	private volatile boolean working = true;
	
	private DistributedMutex mutex;
	
	public SimpleServentListener(DistributedMutex mutex) {
		this.mutex = mutex;
	}
	
	/*
	 * Thread pool for executing the handlers. Each client will get it's own handler thread.
	 */
	private final ExecutorService threadPool = Executors.newWorkStealingPool();
	
	@Override
	public void run() {
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(AppConfig.myServentInfo.getListenerPort(), 100);
			/*
			 * If there is no connection after 1s, wake up and see if we should terminate.
			 */
			listenerSocket.setSoTimeout(1000);
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint("Couldn't open listener socket on: " + AppConfig.myServentInfo.getListenerPort());
			System.exit(0);
		}
		
		while (working) {
			try {
				/*
				 * This blocks for up to 1s, after which SocketTimeoutException is thrown.
				 */
				Socket clientSocket = listenerSocket.accept();
				
				//GOT A MESSAGE! <3
				Message clientMessage = MessageUtil.readMessage(clientSocket);
				// Should rebroadcast?
				BasicMessage basicMessage = (BasicMessage) clientMessage;
				AppConfig.timestampedErrorPrint("Info: " + basicMessage + " - " + basicMessage.getTravellingRoute());
				ServentInfo nextReceiver = basicMessage.getTravellingRoute().peek();
				if(nextReceiver != null && !nextReceiver.getAddress().equals(AppConfig.myServentInfo.getAddress())){
					// Rebroadcast
					AppConfig.timestampedStandardPrint("From " + clientMessage.getOriginalSenderInfo() + " to " + clientMessage.getReceiverInfo() + " current " + nextReceiver);
					MessageUtil.sendMessage(clientMessage);
					continue;

				}
				AppConfig.timestampedStandardPrint("Parse message " + clientMessage);
				MessageHandler messageHandler = new NullHandler(clientMessage);
				
				/*
				 * Each message type has it's own handler.
				 * If we can get away with stateless handlers, we will,
				 * because that way is much simpler and less error prone.
				 */
				switch (clientMessage.getMessageType()) {
				case TOKEN:
					messageHandler = new TokenHandler(clientMessage, mutex);
					break;
					case LAMPORT_REQUEST:
						messageHandler = new LamportRequestHandler(clientMessage,mutex);
						break;
					case LAMPORT_RELEASE:
						messageHandler = new LamportReleaseHandler(clientMessage, mutex);
						break;
					case LAMPORT_REPLY:
						messageHandler = new LamportReplyHandler(clientMessage, mutex);
						break;
					case BOOTSTRAP_ASK:
						messageHandler = new AskBootstrapHandler(clientMessage);
						break;
					case BOOTSTRAP_RESPONSE:
						messageHandler = new BootstrapResponseHandler(clientMessage);
						break;
					case ADD_ME:
						messageHandler = new AddServentHandler(clientMessage);
						break;
					case UPDATE_STATE:
						messageHandler = new UpdateStateHandler(clientMessage);
						break;
					case WELCOME:
						messageHandler = new WelcomeHandler(clientMessage);
						break;
					case FILE_ASK:
						messageHandler = new AskFileActionHandler(clientMessage);
						break;
					case FILE_TELL:
						messageHandler = new TellFileActionHandler(clientMessage);
						break;
					case IS_OK:
						messageHandler = new IsOkHandler(clientMessage);
						break;
					case NOT_OK:
						messageHandler = new NotOkHandler(clientMessage);
						break;
					case OK:
						messageHandler = new OkHandler(clientMessage);
						break;
					case PING:
						messageHandler = new PingHandler(clientMessage);
						break;
					case PONG:
						messageHandler = new PongHandler(clientMessage);
						break;
					case SUZUKI_REQUEST:
						messageHandler = new SuzukiSimpleRequestHandler(clientMessage, mutex);
						break;
					case SUZUKI_REPLY:
						messageHandler = new SuzukiSimpleReplyHandler(clientMessage, mutex);
						break;
					case GIVE_BACKUP:
						messageHandler = new GiveBackupHandler(clientMessage);
						break;
				case POISON:
					break;
				}
				
				threadPool.submit(messageHandler);
			} catch (SocketTimeoutException timeoutEx) {
				//Uncomment the next line to see that we are waking up every second.
//				AppConfig.timedStandardPrint("Waiting...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop() {
		this.working = false;
	}

}
