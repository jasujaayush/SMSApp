package com.test.smsmessaging;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
 
@SuppressLint("NewApi")
public class SmsReceiver extends BroadcastReceiver
{
    @TargetApi(Build.VERSION_CODES.DONUT)
	@SuppressLint("NewApi")
	@Override
    public void onReceive(Context context, Intent intent) 
    {
    	try 
    	{
			FileOutputStream fout = context.openFileOutput("messages.txt",1);
			OutputStreamWriter osw = new OutputStreamWriter(fout);
	    	
	        //---get the SMS message passed in---
	        Bundle bundle = intent.getExtras();        
	        SmsMessage[] msgs = null;
	        String str = "";            
	        if (bundle != null)
	        {
	            //---retrieve the SMS message received---
	            Object[] pdus = (Object[]) bundle.get("pdus");
	            msgs = new SmsMessage[pdus.length];            
	            for (int i=0; i<msgs.length; i++){
	                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
	                str += "SMS from " + msgs[i].getOriginatingAddress();                     
	                str += " :";
	                str += msgs[i].getMessageBody().toString();
	                str += "\n";  
	                osw.write(str);
	            }
	            //---display the new SMS message---
	            Toast.makeText(context, str, Toast.LENGTH_LONG).show();
	        }
	        osw.close();
		} 
    	catch (FileNotFoundException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
     }
}
