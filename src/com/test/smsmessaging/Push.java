package com.test.smsmessaging;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.stomp.StompConnection;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Push {
	
	String sAddress = "tcp://192.168.1.35:1883";
	String sUserName = "system";
	String sPassword = "manager";
	String sDestination = "jugaado";
	String sMessage = "test";
	
	MQTT mqtt = null;
	FutureConnection connection = null;
	
    public void sendmessage(Context context, String msg)
    {
    	System.out.println("Inside sendmessage");
    	mqtt = new MQTT();
		mqtt.setClientId("jugaado-android-mqtt");
		
		try
		{
			mqtt.setHost(sAddress);
		}
		catch(URISyntaxException urise)
		{
			System.out.println("Error in SetHost");
		}
		
		mqtt.setUserName(sUserName);
		mqtt.setPassword(sPassword);
		
		connection = mqtt.futureConnection();
		connection.connect();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (connection.isConnected())
		{
			System.out.println("Connection is Established");
			Topic[] topics = {new Topic(sDestination, QoS.AT_LEAST_ONCE)};
			connection.subscribe(topics);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			connection.publish(sDestination, msg.getBytes(), QoS.AT_MOST_ONCE, false);
			System.out.println("Check Topic in ActiveMQ");
			
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
			connection.unsubscribe(new String[]{sDestination});
			connection.disconnect().then(new Callback<Void>(){
				public void onSuccess(Void value) {
					System.out.println("Disconnected");
				}
				public void onFailure(Throwable e) {
					System.out.println("Problem disconnecting");
				}
			});
		}
		else
		{
			System.out.println("No Connection is Established");
		}
    }
    
}
