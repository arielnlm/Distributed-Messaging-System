package servent.message.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import app.AppConfig;
import app.ServentInfo;
import networking.Address;
import servent.message.BasicMessage;
import servent.message.Message;
import servent.message.MessageType;

/**
 * This worker sends a message asynchronously. Doing this in a separate thread
 * has the added benefit of being able to delay without blocking main or somesuch.
 * 
 * @author bmilojkovic
 *
 */
public class DelayedMessageSender implements Runnable {

	private Message messageToSend;

	public DelayedMessageSender(Message messageToSend) {
		this.messageToSend = messageToSend;
	}

	public void run() {
		/*
		 * A random sleep before sending.
		 * It is important to take regular naps for health reasons.
		 */
		try {
			Thread.sleep((long)(Math.random() * 1000) + 500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		ServentInfo receiverInfo = messageToSend.getReceiverInfo();
		if (MessageUtil.MESSAGE_UTIL_PRINTING) {
			if(messageToSend.getMessageType().equals(MessageType.PING) || messageToSend.getMessageType().equals(MessageType.PONG))
				AppConfig.timestampedErrorPrint("Sending message " + messageToSend);
			else
				AppConfig.timestampedStandardPrint("Sending message " + messageToSend);
		}

		try {
			Socket sendSocket;
			ServentInfo nextReceiver = ((BasicMessage) messageToSend).getTravellingRoute().poll();
			if(receiverInfo == null){ // send to bootstrap
				sendSocket = new Socket(AppConfig.bootstrapAddress.getHost(), AppConfig.bootstrapAddress.getPort());
			}
			else if(nextReceiver != null){
				sendSocket = new Socket(nextReceiver.getAddress().getHost(), nextReceiver.getAddress().getPort());
			}
			else{
				sendSocket = new Socket(receiverInfo.getAddress().getHost(), receiverInfo.getAddress().getPort());
			}
			ObjectOutputStream oos = new ObjectOutputStream(sendSocket.getOutputStream());
			oos.writeObject(messageToSend);
			oos.flush();

			sendSocket.close();

		} catch (IOException e) {
			AppConfig.timestampedErrorPrint("Couldn't send message: " + messageToSend.toString());
		}
	}

}
