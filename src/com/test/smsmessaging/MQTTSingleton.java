package com.test.smsmessaging;

import org.fusesource.mqtt.client.MQTT;

public class MQTTSingleton {
	private static MQTT mqtt = null;
	
	public synchronized static MQTT getInstance(){
		if(mqtt==null){
			mqtt = new MQTT();
			return mqtt;
		}
		else return mqtt;
	}
	
}
