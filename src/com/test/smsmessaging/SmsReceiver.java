package com.test.smsmessaging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
 
@SuppressLint("NewApi")
public class SmsReceiver extends BroadcastReceiver
{
	public static final String URL = "http://jasuja.pagekite.me/MyFirstServlet/HelloServlet?param1=";
	private static final String TAG = "SMSReceiver";
	String tempURL = "";
	Context appContext ;
	
    @TargetApi(Build.VERSION_CODES.DONUT)
	@SuppressLint("NewApi")
	@Override
    public void onReceive(Context context, Intent intent) 
    {	
    	Toast.makeText(context, "Message Received", Toast.LENGTH_SHORT).show();
	        //---get the SMS message passed in---
	        Bundle bundle = intent.getExtras();        
	        SmsMessage[] msgs = null;       
	        String jsonstr = "";
	        String number = "";
	        String lastTen = "";
	        String message = "";
	        
	        if (bundle != null)
	        {
	            //---retrieve the SMS message received---
	        	Date startOnReceive = new Date();
	            Object[] pdus = (Object[]) bundle.get("pdus");
	            msgs = new SmsMessage[pdus.length]; 
	            Date endOnReceive = new Date();
	            Log.d(TAG, "SMSRead time:("+endOnReceive.getTime()+"-"+ startOnReceive.getTime()+")");
	            for (int i=0; i<msgs.length; i++){
	                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]); 
	                number = msgs[i].getOriginatingAddress();
	                //lastTen = number.length() > 9 ? number.substring(number.length() - 10) : number;
	                message = msgs[i].getMessageBody().toString();
	                jsonstr = "{" + number + ":" + message + "}\n";
	            	              
	                try
	                {
	                	//tempURL = URL+URLEncoder.encode(jsonstr, "UTF-8");
	                	//new GetXMLTask().execute(new String[] { tempURL });
	                	
	                	if(message.length() > 0 && number.matches("^(?:0091|\\+91|0)?[7-9][0-9]{9}$"))
	                	{
		                	System.out.println("Before calling Push.getInstance()");	                	
		                	Push push = Push.getInstance();
		                	push.sendmessage(context,jsonstr);
	                	}
	                	else
	                	{
	                		System.out.println("Number was improper");	
	                	}
	                }
	                catch (Exception e) 
	                {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	            }
	            
	            //---display the new SMS message---
	            Log.d(TAG,"Inside OnReceive");
	            Toast.makeText(context, jsonstr, Toast.LENGTH_SHORT).show();
	        }
     }
    
    @SuppressLint("NewApi")
	private class GetXMLTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String output = null;
            for (String url : urls) {
                output = getOutputFromUrl(url);
            }
            return output;
        }
         
        private String getOutputFromUrl(String url) {
            String output = null;
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
 
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                output = EntityUtils.toString(httpEntity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return output;
        }
        
        @Override
        protected void onPostExecute(String output) {
            //outputText.setText(output);
        	Toast.makeText(appContext, "From Servlet: " +output, 
                    Toast.LENGTH_LONG).show();
        }
    }
}
