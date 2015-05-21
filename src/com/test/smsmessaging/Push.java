package com.test.smsmessaging;

import java.net.URISyntaxException;
import java.util.Date;

import com.test.smsmessaging.MQTTSingleton;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class Push {
	
	private final String TAG = "MQTTClient";
	String sAddress = "tcp://54.149.71.100:1883";
	String sUserName = "system";
	String sPassword = "manager";
	String sDestination = "jugaado";
	String sOutgoing = "outgoing_jugaado";
	String sMessage = "test";
	static Date date = null;
	
	private static Push instance; 
	
	MQTT mqtt = null;
	//FutureConnection connection = null;
	BlockingConnection connection = null;
	public Push()
	{
		System.out.println("Initializing Push()");
		connect();
	}
	
	public static Push getInstance(){
		if(instance==null)
			{
				instance = new Push();
				return instance;
			}
		else{
			return instance;
		}
	}
	
	
    public void sendmessage(Context context, String msg)
    {
    	System.out.println("Inside sendmessage");
    	if(!connection.isConnected()){
    		System.out.println("Push.sendmessage conneciont is not connected");
    		connect();
    	}
    	System.out.println("Trying to send a message");
    	try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	send(context,msg);
    	
//		while (!connection.isConnected())
//		{
//			connect();
//		}
//			System.out.println("Connection is Established");
//			Topic[] topics = {new Topic(sDestination, QoS.AT_LEAST_ONCE)};
//			connection.subscribe(topics);
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			connection.publish(sDestination, msg.getBytes(), QoS.AT_MOST_ONCE, false);
//			System.out.println("Check Topic in ActiveMQ");
			
//			Topic[] consumerTopics = {new Topic(sOutgoing, QoS.AT_LEAST_ONCE)};
//			connection.subscribe(consumerTopics);
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			Future<Message> message = connection.receive();
//			Message msg = (Message)message;
//			byte[] bytes = new byte[(int) bytesmsg.getBodyLength()];
//        	bytesmsg.readBytes(bytes);
//        	String string = new String(bytes, "UTF-8");
//			System.out.println(string); 
        	
			//connection.publish(sDestination, msg.getBytes(), QoS.AT_MOST_ONCE, false);
		//	System.out.println("Outgoing Message : " + message.toString());
			
			/*
			connection.receive().then(new Callback<Message>() {
				public void onSuccess(Message message) {
					String receivedMesageTopic = message.getTopic();
					byte[] payload = message.getPayload();
					String messagePayLoad = new String(payload);
					message.ack();
					connection.unsubscribe(new String[]{sDestination});
					System.out.println(receivedMesageTopic + ":" + messagePayLoad);
					System.out.println(messagePayLoad);
				}
				public void onFailure(Throwable e) {
					System.out.println("Exception receiving message: " + e);
			
				}
			});
			*/
		//	connection.unsubscribe(new String[]{sDestination});
//			connection.disconnect().then(new Callback<Void>(){
//				public void onSuccess(Void value) {
//					System.out.println("Disconnected");
//				}
//				public void onFailure(Throwable e) {
//					System.out.println("Problem disconnecting");
//				}
//			});
		
//		else
//		{
//			System.out.println("No Connection is Established, trying again");
//			connect();
//		}
    }
    
    private void connect()
	{
		//mqtt = new MQTT();
		mqtt = MQTTSingleton.getInstance();
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
		
/*		connection.connect().then(new Callback<Void>(){
			public void onSuccess(Void value) {
				
				System.out.println("Boss Connected");
			}
			public void onFailure(Throwable e) {
				
				Log.e(TAG, "Exception connecting to " + sAddress + " - " + e);
			}
		});*/

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
				});*/
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
    
    private void send(Context context, final String msg)
	{
    	System.out.println("Inside send function");
		if(connection != null)
		{
			// automatically connect if no longer connected
			if(!connection.isConnected() )
			{
				System.out.println("connection.isConnected="+connection.isConnected());
				System.out.println("Calling connect() from send function");
				System.out.println(connection.toString());
/*				if(connection.isConnected())
				{
					System.out.println("kill : " + connection.toString());
					connection.kill().then(new Callback<Void>(){
						public void onSuccess(Void value) {
							System.out.println("Disconnected");
						}
						public void onFailure(Throwable e) {
							System.out.println("Problem disconnecting");
							Log.e(TAG, "Exception disconnecting from " + sAddress + " - " + e);
						}
					});
				}*/
				
/*				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
				connect();
			}
			
			
			Topic[] topics = {new Topic(sDestination, QoS.AT_LEAST_ONCE)};
			try {
				connection.subscribe(topics);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				connection.publish(sDestination, msg.getBytes(), QoS.AT_MOST_ONCE, false);
				String forNumber =  msg.toString();
				String number = forNumber.substring(forNumber.indexOf("{") + 1, forNumber.indexOf(":"));		
				System.out.println("Published Message : "+msg.toString());
				Intent i = new Intent("SEND MESSAGE");
				i.putExtra("number",number);
				Toast.makeText(context, "Sending Message", Toast.LENGTH_LONG).show();
				context.sendBroadcast(i);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				connection.unsubscribe(new String[]{sDestination});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*connection.subscribe(topics).then(new Callback<byte[]>() {
				public void onSuccess(byte[] subscription) {
					
					Log.d(TAG, "Destination: " + sDestination);
					Log.d(TAG, "Message: " + msg);
					
					// publish message
					connection.publish(sDestination, msg.getBytes(), QoS.AT_MOST_ONCE, false);
					//destinationET.setText("");
					//messageET.setText("");
					System.out.println("Message sent");
					
					 //receive message
					
//					 connection.receive().then(new Callback<Message>() {
//						public void onSuccess(Message message) {
//							String receivedMesageTopic = message.getTopic();
//							byte[] payload = message.getPayload();
//							String messagePayLoad = new String(payload);
//							message.ack();
//							connection.unsubscribe(new String[]{sDestination});
//							//receiveET.setText(receivedMesageTopic + ":" + messagePayLoad);
//							System.out.println(messagePayLoad);
//						}
//						public void onFailure(Throwable e) {
//							Log.e(TAG, "Exception receiving message: " + e);
//						}
//					});
					
				}
				
				public void onFailure(Throwable e) {
					Log.e(TAG, "Exception sending message: " + e);
				}
			});*/
		}
		else
		{
			System.out.println("No connection has been made, please create the connection");
		}
	}  
}
