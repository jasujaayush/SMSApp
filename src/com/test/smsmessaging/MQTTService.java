package com.test.smsmessaging;

import java.util.Enumeration;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistable;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.internal.MqttPersistentData;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class MQTTService extends Service{

    public static final String BROKER_URL = "tcp://52.26.15.67:1883";

    /* In a real application, you should get an Unique Client ID of the device and use this, see
    http://android-developers.blogspot.de/2011/03/identifying-app-installations.html */
    public static final String clientId = "Jasuja-android-client";

    public static final String TOPIC = "jugaado";
    private MqttClient mqttClient;


    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {

        try {
        	
        	MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(BROKER_URL, clientId, persistence);

            mqttClient.setCallback(new PushCallback(this));
            MqttConnectOptions mqttConnectionOptions = new MqttConnectOptions();
	        mqttConnectionOptions.setUserName("system");
	        mqttConnectionOptions.setPassword("manager".toCharArray());
            mqttClient.connect(mqttConnectionOptions);
            
            MqttMessage message = new MqttMessage();
	        message.setPayload("A single message from my computer APCA"
	                .getBytes());
            mqttClient.publish("APCA", message);
            
            mqttClient.subscribe(TOPIC);


        } catch (MqttException e) {
        	System.out.println("Something went wrong!" + e.getMessage());
            Toast.makeText(getApplicationContext(), "Something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        try {
            mqttClient.disconnect(0);
        } catch (MqttException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
