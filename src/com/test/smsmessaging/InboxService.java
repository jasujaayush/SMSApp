package com.test.smsmessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.Loader.OnLoadCompleteListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class InboxService extends Service implements OnLoadCompleteListener<Cursor>, MqttCallback {

	private static final int LOADER_ID_NETWORK = 0;
	private final String SMS_ALL = "content://sms/inbox";
	private static int count=0;
	private static List<SMSData> smsList = new ArrayList<SMSData>();
	private static SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
	private static Date lastReadDate = new Date();
	private static String msg,body,date,address;
	private static long msdate;
	private static Date datefromsms,maxDate = new Date();
    private static CursorLoader cursorLoader;
    
    //MqttClient Variables
    public static final String BROKER_URL = "tcp://52.26.15.67:1883";
    public static final String TOPIC = "outgoing_jugaado";
    private char[] password = "manager".toCharArray();
    private String username = "system";
    private MqttClient mqttClient;
    private MqttConnectOptions mqttConnectionOptions = new MqttConnectOptions();
    private MemoryPersistence persistence = new MemoryPersistence();
    String clientId = "Jugaado-pahomqtt-client-" + UUID.randomUUID().toString();
    

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate() {
		Toast.makeText(getBaseContext(), "Inbox Reading Service Started", Toast.LENGTH_SHORT).show();
		Uri uri = Uri.parse(SMS_ALL);
		cursorLoader = new CursorLoader(getBaseContext(), uri, null, null, null, "date desc");
		cursorLoader.registerListener(LOADER_ID_NETWORK, this);
		cursorLoader.startLoading();
	}

	// Setting up MQTTClient connection in this method
	@Override
    public void onStart(Intent intent, int startId) {

		try {
			mqttClient = new MqttClient(BROKER_URL, clientId, persistence);
		} catch (MqttException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        mqttClient.setCallback(this);
        mqttConnectionOptions.setUserName(username);
        mqttConnectionOptions.setPassword(password);
        mqttConnectionOptions.setCleanSession(false);
        connect();

        super.onStart(intent, startId);
    }	
	
	@Override
	public void onLoadComplete(Loader<Cursor> arg0, Cursor cursorLoader) {
		count = 0;
	       
	      // Read the sms data and store it in the list
	      if(cursorLoader.moveToFirst()) {
	          for(int i=0; i < cursorLoader.getCount(); i++) 
	          {
	              SMSData sms = new SMSData();
	              
	              body = cursorLoader.getString(cursorLoader.getColumnIndexOrThrow("body")).toString();
	              sms.setBody(body);
	              address = cursorLoader.getString(cursorLoader.getColumnIndexOrThrow("address")).toString();
	              sms.setNumber(address);
	              date = cursorLoader.getString(cursorLoader.getColumnIndexOrThrow("date")).toString();
	              msdate = Long.valueOf(date).longValue();
	              datefromsms = new Date(msdate);
	              
	              if(datefromsms.after(lastReadDate))
	              {
	            	System.out.println("Msg Date : " + datefromsms.toString());  
	           	   	msg = "{" + address + ":" + body + "}";
	           	   	Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
	           	   	if(maxDate.before(datefromsms))
	           	   	{
	           	   		maxDate = datefromsms;
	           	   	}
	           	   	count++;
	              	System.out.println(msg);
	              }            
	              else
	              {
	           	   System.out.println("No new messages.Hence,breaking the loop");
	           	   break;
	              }
	              
	              try
	                {      	
	                	if(body.length() > 0 && address.matches("^(?:0091|\\+91|0)?[7-9][0-9]{9}$"))
	                	{
	                		//smsList.add(sms);
//		                	System.out.println("Before calling Push.getInstance() in Inboxservice");	                	
//		                	Push push = Push.getInstance();
//		                	push.sendmessage(getBaseContext(),msg);
	                		if(!mqttClient.isConnected())
	                		{
	                			connect();
	                		}
	                		mqttClient.publish("jugaado", msg.getBytes(), 1, true);
	                		
	                	}
	                	else
	                	{
	                		Toast.makeText(getBaseContext(), "Number was improper", Toast.LENGTH_SHORT).show();
	                		System.out.println("Number was improper");
	                	}
	                }
	                catch (Exception e) 
	                {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	           
	              cursorLoader.moveToNext();
	          }
	      }
	      
	      if(!lastReadDate.equals(maxDate))
	      {
		       lastReadDate = maxDate;
		       System.out.println("lastReadDate : "+lastReadDate);
		       System.out.println("Count : "+count);
	      }
	      //c.close();
	      return;
	      // Set smsList in the ListAdapter
	      //setListAdapter(new ListAdapter(this, smsList));
		
	}
	
	@Override
	public void onDestroy() {

	    // Stop the cursor loader
	    if (cursorLoader != null) {
	    	cursorLoader.unregisterListener(this);
	    	cursorLoader.cancelLoad();
	    	cursorLoader.stopLoading();
	    }
	    
	    try {
	    	mqttClient.unsubscribe(TOPIC);
            mqttClient.disconnect(0);
        } catch (MqttException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
	}
	
	private class SMSData {
		 
		   // Number from witch the sms was send
		   private String number;
		   // SMS text body
		   private String body;
		    
		   public String getNumber() {
		       return number;
		   }
		    
		   public void setNumber(String number) {
		       this.number = number;
		   }
		    
		   public String getBody() {
		       return body;
		   }
		    
		   public void setBody(String body) {
		       this.body = body;
		   }
		    
		}

	
	//MqttCalback functions
	
	@Override
	public void connectionLost(Throwable arg0) {
		System.out.println("PushCallback ---- Connection lost");
		connect();	
	}

	
	@Override
	
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception {
		System.out.println("PushCallback -----"+topic + " : " + msg.toString());
		String message = msg.toString();
		String number = message.substring(message.indexOf("{") + 1, message.indexOf(":"));
		String reply = message.substring(message.indexOf(":") + 1, message.indexOf("}"));
		System.out.println("Inside thread, number = "+number);
		Intent i = new Intent("SEND MESSAGE");
		i.putExtra("number",number);
		i.putExtra("reply",reply);
		//Toast.makeText(context, "Sending Message", Toast.LENGTH_LONG).show();
		getBaseContext().sendBroadcast(i);
	
	}

	private void connect()
	{
		if(mqttClient.isConnected())
			return;
		
		boolean isConnected = false;
		
		while(!isConnected)
		{
		
			try 
			{
	            mqttClient.connect(mqttConnectionOptions);
	            mqttClient.subscribe(TOPIC, 1);
	//            MqttMessage message = new MqttMessage();
	//	        message.setPayload("A single message from my computer APCA"
	//	                .getBytes());
	//            mqttClient.publish("APCA", message);
	        } catch (Exception e) 
	        {
	        	System.out.println("Something went wrong, when connecting!" + e.getMessage());
	            //Toast.makeText(getApplicationContext(), "Something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
	            //e.printStackTrace();
	        }
			
			if(mqttClient.isConnected())
			{
				isConnected = true;
				System.out.println("Connection Established");
			}
			else
			{
				System.out.println("No Connection Established, retrying....");
				try
				{
					Thread.sleep(2000);
				}
				catch(Exception e)
				{
					System.out.println("Something went wrong, when calling sleep!" + e.getMessage());
				}
			}
			
		
		}
	}
	
}
