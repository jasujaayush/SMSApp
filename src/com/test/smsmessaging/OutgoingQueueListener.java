package com.test.smsmessaging;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class OutgoingQueueListener extends Service {

	static Push pull = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate(){
		Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
		System.out.println("OutgoingQueueListener");
		pull = Push.getInstance();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
		System.out.println("Service Started");
		new Thread(new Runnable(){
		    public void run() {
		    	pull.listenOutgoing(getBaseContext());
				                    }
		}).start();
		return START_STICKY;
	}
}
