package com.swarmcop.raspberry;
/*

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


public class RaspberryServiceManager {

	private boolean disconnected = false;
    private static RaspberryService raspberryService;
    private static Intent intent = new Intent("com.darpa.ros");
    private OnConnectedListener onConnectedListener;
    private Service service;
    private Activity activity;
    private String tag = "RaspSocket";

    public static interface OnConnectedListener {
    	void onConnected();
    	void onDisconnected();
    }

	public RaspberryServiceManager(Service service, OnConnectedListener onConnectedListener) {
    	this.service = service;
		this.onConnectedListener = onConnectedListener;
		service.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	
	public synchronized void disconnect() {
		disconnected = true;
		if(service!=null)
			service.unbindService(serviceConnection);
		else
			activity.unbindService(serviceConnection);
	}
	public synchronized void add(RaspberryServiceReporter reporter) {
		if (disconnected)
			throw new IllegalStateException("Manager has been explicitly disconnected; you cannot call methods on it");
		try {
			raspberryService.add(reporter);
		} catch (RemoteException e) {
			Log.e(tag, "add reporter", e);
		}
	}
	public synchronized void remove(RaspberryServiceReporter reporter) {
		if (disconnected)
			throw new IllegalStateException("Manager has been explicitly disconnected; you cannot call methods on it");
		try {
			raspberryService.remove(reporter);
		} catch (RemoteException e) {
			Log.e(tag, "remove reporter", e);
		}
	}

    private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override public void onServiceDisconnected(ComponentName name) {
			Log.d(tag, "RaspberryService onServiceDisconnected");
			if (onConnectedListener != null)
				onConnectedListener.onDisconnected();
			raspberryService = null;
		}
		@Override public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(tag,"RosService onServiceConnected");
			raspberryService = RaspberryService.Stub.asInterface(service);
			
			if (onConnectedListener != null)
				onConnectedListener.onConnected();
		}
	};

	public void sendMessageToSocket(byte[] outMessageToSocket) {
		try {
			raspberryService.sendMessageToSocket(outMessageToSocket);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	
}*/
