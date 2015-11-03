package com.darpa.comms;


import java.util.ArrayList;
import java.util.List;

import org.ros.android.jaguar.RaspberryServiceManager;
import org.ros.android.jaguar.RaspberryServiceReporter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class CommsService extends Service{

	//This app acts to encrypt inMessages and outMessages and send them
	//to Raspberry Pi for transmission
	
	byte[] messageReceivedBytes;
	private RaspberryServiceManager raspberryManager;
	String tag = "RaspberryService";
		

		private class ServiceThread extends Thread {
			@Override public void run() {
					while(!isInterrupted()) {
						while(!isInterrupted()){
							List<CommServiceReporter> targets;
							synchronized (reporters) {
								targets = new ArrayList<CommServiceReporter>(reporters);
							}
							for(CommServiceReporter commServiceReporter : targets) {
								try {
									if(messageReceivedBytes!=null){
										commServiceReporter.reportReceivedMessage(messageReceivedBytes);
										messageReceivedBytes = null;
									}
								} catch (RemoteException e) {
									e.printStackTrace();
								} 
							}
						}
						
				}
				Log.d(tag,"CommService interrupted");
			}
		}
		
		private ServiceThread serviceThread;
		
		@Override
		public void onCreate() {
			super.onCreate();
			Log.d(tag,"CommService onCreate");
			
			
			raspberryManager = new RaspberryServiceManager(this, new RaspberryServiceManager.OnConnectedListener() {
	    		@Override public void onConnected() {
	    			Log.d(tag, "raspberryService connected - adding reporter");
	    			raspberryManager.add(raspberryServiceReporter);
	    			
	    		}
	    		@Override public void onDisconnected() {
	    			Log.d(tag, "raspberryService disconnected - removing reporter");
	    			raspberryManager.remove(raspberryServiceReporter);
	    		}
	    	});
			
		}
		
		private RaspberryServiceReporter raspberryServiceReporter = new RaspberryServiceReporter.Stub() {

			@Override
			public void receivedMessageFromSocket(byte[] inMessageBytesFromSocket) throws RemoteException {
				//Decrypt message, convert to byte array for transmission to UVADE
				String encryptedMessage = new String(inMessageBytesFromSocket);
				try {
					String decryptedMessageBytes = AES256.decrypt(encryptedMessage.toCharArray());
					messageReceivedBytes = decryptedMessageBytes.getBytes();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		

		@Override
		public void onDestroy() {
			if (serviceThread != null) {
				serviceThread.interrupt();
				serviceThread = null;
			}
			Log.d(tag,"CommService onDestroy");
			super.onDestroy();
		}
		
		@Override
		public IBinder onBind(Intent intent) {
			Log.d(tag,"CommService onBind");
			serviceThread = new ServiceThread();
			serviceThread.start();
			return commsBinder;
		}

		
		private List<CommServiceReporter> reporters = new ArrayList<CommServiceReporter>();

		private CommService.Stub commsBinder = new CommService.Stub() {

			@Override
			public void add(CommServiceReporter reporter)throws RemoteException {
				synchronized (reporters) {
					reporters.add(reporter);
				}
			}

			@Override
			public void remove(CommServiceReporter reporter) throws RemoteException {
				synchronized (reporters) {
					reporters.remove(reporter);
				}
			}

			@Override
			public void sendMessage(byte[] outMessageBytes) throws RemoteException {
				//Encrypt message and send
				String rawMessage = new String(outMessageBytes);
				try {
					String encryptedMessage = AES256.encrypt(rawMessage.toCharArray());
					byte[] outMessageToSocket = encryptedMessage.getBytes();
					raspberryManager.sendMessageToSocket(outMessageToSocket);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				
			}
	
		};

	}

