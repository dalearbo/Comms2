package com.swarmcop.raspberry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class Raspberry extends Service{
	
	private Socket socket;
	private static final int SERVERPORTRECEIVE = 5005;			//Use a separate port for receiving message
	private static final int SERVERPORTSEND = 5004;			//Port for sending message
	private static final String SERVER_IP = "192.168.0.143";
	private DataOutputStream outChannel;
	private String tag = "RaspSocket";
	private Thread clientThread0;
	private Thread clientThread1;
    List<RaspberryServiceReporter> targets;
	
	
	public Raspberry(){
		Log.d(tag, "Ros/Socket Initialize");
		clientThread0 = new ClientThread(SERVERPORTSEND);
		clientThread1 = new ClientThread(SERVERPORTRECEIVE);
		clientThread0.start();
		clientThread1.start();
	}
	
	
	private List<RaspberryServiceReporter> reporters = new ArrayList<RaspberryServiceReporter>();
	
	
	public void sendMessageToSocket(byte[] outMessageToSocket) throws RemoteException {
		try{
			if (socket!=null && !socket.isClosed()){
				outChannel.write(outMessageToSocket);
				outChannel.flush();
			} else{
				Log.d(tag,"Socket is closed");
			}
		}catch(UnknownHostException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}	
	}
	
	
	 public RaspberryService.Stub socketBinder = new RaspberryService.Stub() {
	        @Override public void add(RaspberryServiceReporter reporter) throws RemoteException {
	            synchronized (reporters) {
	                reporters.add(reporter);
	            }
	        }

	        @Override public void remove(RaspberryServiceReporter reporter) throws RemoteException {
	            synchronized (reporters) {
	                reporters.remove(reporter);
	            }
	        }

			@Override
			public void sendMessageToSocket(byte[] outMessageToSocket) throws RemoteException {
				try{
					if (socket!=null && !socket.isClosed()){
	    				//TODO May need to find another way to designate EOL marker
						//commandString.concat("\0");	//needed as an end of line marker
	    				outChannel.write(outMessageToSocket);
	    				outChannel.flush();
	    			} else{
	    				Log.d(tag,"Socket is closed");
	    			}
				}catch(UnknownHostException e){
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		} catch (Exception e){
	    			e.printStackTrace();
	    		}	
			}
	    };
	 
	
	class ClientThread extends Thread{

		private final int SERVERPORT;
		private MessageReceiveThread receiveThread;
		
		public ClientThread(int socketPort){
			this.SERVERPORT = socketPort;
		}
		
		@Override
		public void run() {
			while(!isInterrupted()){
				try{
					if(socket == null || socket.isClosed()){
						//Log.d(tag,"Trying to open Socket");
						InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
						socket = new Socket(serverAddr, SERVERPORT);
						while(socket==null){
							socket = new Socket(serverAddr, SERVERPORT);
						}
						
						if(SERVERPORT==SERVERPORTSEND){							//outChannel is set up on the send port only
							outChannel = new DataOutputStream(socket.getOutputStream());
						} else {	//SERVERPORT==SERVERPORTRECEIVE
							if(receiveThread==null){
								receiveThread = new MessageReceiveThread(socket);	//only need comm port for receive sockets
								new Thread(receiveThread).start();
							}
							
						} 
							
						Log.d(tag,"Socket "+SERVERPORT+" Opened");
					}
				} catch(UnknownHostException e1){
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} 
			}
		}
	}
	
	class MessageReceiveThread implements Runnable {

		private Socket clientSocket;
		private DataInputStream input;
		
		public MessageReceiveThread(Socket clientSocket){
			this.clientSocket = clientSocket;
			try{
				this.input = new DataInputStream(clientSocket.getInputStream());
				
				} catch(IOException e){
					e.printStackTrace();
				}
		}
		
		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()){
				try{
					byte[] inMessageBytes = new byte[10000];
					if(!clientSocket.isClosed()){
		        		 input.read(inMessageBytes);
		        		 synchronized (reporters) {
		     					targets = new ArrayList<RaspberryServiceReporter>(reporters);
		     				}
		     				for(RaspberryServiceReporter raspberryReporter : targets) {
		     					try {
		     						raspberryReporter.receivedMessageFromSocket(inMessageBytes);
		     					} catch (RemoteException e) {
		     						Log.e(tag,"report",e);
		     					}
		        		 }
		        	 }
				
				} catch(IOException e){
					e.printStackTrace();
				}
				catch(NumberFormatException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	
	 @Override
	    public void onDestroy() {
	        if(clientThread0 != null){
	            clientThread0.interrupt();
	            clientThread0 = null;
	        }
	        if(clientThread1 != null){
	            clientThread1.interrupt();
	            clientThread1 = null;
	        }
	        Log.d(tag,"Socket RaspberryPi onDestroy");
	        super.onDestroy();
	    }

	    @Override
	    public void onCreate() {
	        super.onCreate();
	        Log.d(tag, "Socket RaspberryPi onCreate");

	    }

		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}
		
		

}
