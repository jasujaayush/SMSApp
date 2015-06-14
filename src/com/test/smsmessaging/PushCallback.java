package com.test.smsmessaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;


public class PushCallback implements MqttCallback {

    private ContextWrapper context;

    public PushCallback(ContextWrapper context) {

        this.context = context;
    }

    @Override
    public void connectionLost(Throwable cause) {
    	System.out.println("PushCallback ---- Connection lost");
        //We should reconnect here
    }

//    public void messageArrived(MqttTopic topic, MqttMessage message) throws Exception {
//    	System.out.println("PuchCallback -----"+topic.getName() + " : " + message.toString());
//    }
//
//    public void deliveryComplete(MqttDeliveryToken token) {
//        //We do not need this because we do not publish
//    }

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		System.out.println("PushCallback -----"+topic + " : " + message.toString());
		
	}
}
