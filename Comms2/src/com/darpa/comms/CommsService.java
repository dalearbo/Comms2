package com.darpa.comms;


import java.util.ArrayList;
import java.util.List;

import com.swarmcop.raspberry.Raspberry;
import com.swarmcop.raspberry.RaspberryServiceReporter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class CommsService extends Service{

	//This app acts to encrypt inMessages and outMessages and send them
	//to Raspberry Pi for transmission
	
	static byte[] messageReceivedBytes;
	private Raspberry raspberry;
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
										//Log.d("comm","Reporting message");
										commServiceReporter.reportReceivedMessage(messageReceivedBytes);
										messageReceivedBytes = null;
									}
									Thread.sleep(50);
								} catch (RemoteException e) {
									e.printStackTrace();
								} catch (InterruptedException e) {
									e.printStackTrace();
								} 
							}
						}
						
				}
				Log.d(tag,"CommService interrupted");
			}
		}
		
		public static void updateMessageVariable(String message){
			messageReceivedBytes = message.getBytes();
		}
		
		
		
		private ServiceThread serviceThread;
		
		@Override
		public void onCreate() {
			super.onCreate();
			Log.d(tag,"CommService onCreate");
			
			raspberry = new Raspberry();
		}
		

		@Override
		public void onDestroy() {
			try {  //Tell server to shut down
				commsBinder.sendMessage(new String("Shut Down Server").getBytes());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
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
				//testMessageContents(outMessageBytes);
				String rawMessage = new String(outMessageBytes);
				try {
					//String encryptedMessage = "0"+AES256.encrypt(rawMessage.toCharArray());		//add 0 to format for socket
					String encryptedMessage = "0"+rawMessage;
					raspberry.sendMessage(encryptedMessage);
					
				} catch (Exception e) {
					e.printStackTrace();
				}	
				
			}
	
		};

	}

