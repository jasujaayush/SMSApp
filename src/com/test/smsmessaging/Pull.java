/*package com.test.smsmessaging;

import java.net.URISyntaxException;
import java.util.Date;

import com.test.smsmessaging.MQTTImmutable;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class Pull {
	
	private final String TAG = "MQTTClient";
	String sAddress = "tcp://54.149.71.100:1883";
	String sUserName = "system";
	String sPassword = "manager";
	String sDestination = "jugaado";
	String sOutgoing = "outgoing_jugaado";
	String sMessage = "test";
	static Date date = null;
	
	private static Pull instance; 
	
	MQTT mqtt = null;
	//FutureConnection connection = null;
	BlockingConnection connection = null;
	public Pull()
	{
		System.out.println("Initializing Pull()");
		connect();
	}
	
	public static Pull getInstance(){
		if(instance==null)
			{
				instance = new Pull();
				return instance;
			}
		else{
			return instance;
		}
	}
    
    private void connect()
	{
		//mqtt = new MQTT();
		mqtt = MQTTImmutable.getInstance();
		mqtt.setClientId("android-mqtt-example");

		try
		{
			mqtt.setHost(sAddress);
			Log.d(TAG, "Address set: " + sAddress);
		}
		catch(URISyntaxException urise)
		{
			Log.e(TAG, "URISyntaxException connecting to " + sAddress + " - " + urise);
		}
		
		if(sUserName != null && !sUserName.equals(""))
		{
			mqtt.setUserName(sUserName);
			Log.d(TAG, "UserName set: [" + sUserName + "]");
		}
		
		if(sPassword != null && !sPassword.equals(""))
		{
			mqtt.setPassword(sPassword);
			Log.d(TAG, "Password set: [" + sPassword + "]");
		}
		
		//connection = mqtt.futureConnection();
		connection = mqtt.blockingConnection();
		try {
			connection.connect();
			System.out.println("Connected in pull");
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
//		connection.connect().then(new Callback<Void>(){
//			public void onSuccess(Void value) {
//				
//				System.out.println("Boss Connected");
//			}
//			public void onFailure(Throwable e) {
//				
//				Log.e(TAG, "Exception connecting to " + sAddress + " - " + e);
//			}
//		});

	}
    
    private void disconnect()
	{
		try
		{
			if(connection != null && connection.isConnected())
			{
				//connection.kill();
				connection.kill();
				/*connection.kill().then(new Callback<Void>(){
					public void onSuccess(Void value) {
						System.out.println("Disconnected");
					}
					public void onFailure(Throwable e) {
						System.out.println("Problem disconnecting");
						Log.e(TAG, "Exception disconnecting from " + sAddress + " - " + e);
					}
				});*//*
			}
			else
			{
				System.out.println("Not Connected");
			}
		}
		catch(Exception e)
		{
			Log.e(TAG, "Exception " + e);
		}
	}
    
  

    public void listenoutgoing()
    {
    	Message msg = null;
    	if(connection != null)
		{
			// automatically connect if no longer connected
			if(!connection.isConnected() )
			{
				System.out.println("connection.isConnected="+connection.isConnected());
				System.out.println("Calling connect() from listenoutgoing function");
				System.out.println(connection.toString());
				connect();
			}
			
			Topic[] topics = {new Topic(sOutgoing, QoS.AT_LEAST_ONCE)};
			try {
				connection.subscribe(topics);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while(true)
			{
				try {
					msg = connection.receive();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Outgoing Jugaado msg :"+msg.toString());
			}
		}
    }
}*/
