package com.test.smsmessaging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
	String tempURL = "";
	Context appContext ;
	
    @TargetApi(Build.VERSION_CODES.DONUT)
	@SuppressLint("NewApi")
	@Override
    public void onReceive(Context context, Intent intent) 
    {	
    		appContext = context;
	        //---get the SMS message passed in---
	        Bundle bundle = intent.getExtras();        
	        SmsMessage[] msgs = null;
	        String str = "";       
	        String jsonstr = "";
	        if (bundle != null)
	        {
	            //---retrieve the SMS message received---
	            Object[] pdus = (Object[]) bundle.get("pdus");
	            msgs = new SmsMessage[pdus.length];            
	            for (int i=0; i<msgs.length; i++){
	                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]); 
	                jsonstr += "{";
	                //str += "SMS from " + msgs[i].getOriginatingAddress(); 
	                jsonstr += msgs[i].getOriginatingAddress();
	                //str += " :";
	                jsonstr += ":";
	                //str += msgs[i].getMessageBody().toString();
	                jsonstr += msgs[i].getMessageBody().toString();
	                jsonstr += "}\n";
	                //str += "\n"; 
	                
	              
	                try
	                {
	                	tempURL = URL+URLEncoder.encode(jsonstr, "UTF-8");
	                	//new GetXMLTask().execute(new String[] { tempURL });
	                	
	                	Push push = Push.getInstance();
	                	push.sendmessage(context,jsonstr);
	                	//Toast.makeText(context, tempURL, Toast.LENGTH_LONG).show();
	                }
	                catch (UnsupportedEncodingException e) 
	                {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	              
	            }
	            //---display the new SMS message---
	            System.out.println("Inside OnReceive");
	            Log.d("jugaado","Inside OnReceive");
	            Toast.makeText(context, jsonstr, Toast.LENGTH_LONG).show();
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
