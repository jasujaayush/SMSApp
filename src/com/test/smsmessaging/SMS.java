package com.test.smsmessaging;
 
import javax.jms.JMSException;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SMS extends Activity implements OnClickListener
{
    Button btnSendSMS;
    EditText txtPhoneNo;
    EditText txtMessage;
    TextView outputText;
    
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(MessageSender, new IntentFilter("SEND MESSAGE"));
        startService();
        findViewsById();
        
        btnSendSMS.setOnClickListener(this); 
    }
	
	public void startService() {
	      startService(new Intent(new Intent(getBaseContext(), OutgoingQueueListener.class)));
	   }
    
	public void onClick(View v) 
    {  
        String phoneNo = txtPhoneNo.getText().toString();
        String message = txtMessage.getText().toString();
       
        if (phoneNo.length()>0 && message.length()>0)                
            sendSMS(phoneNo, message);                
        else
            Toast.makeText(getBaseContext(), 
                "Please enter both phone number and message.", 
                Toast.LENGTH_SHORT).show();
    }

	private void findViewsById() {
		btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        outputText = (TextView) findViewById(R.id.outputTxt);
    }
	
	BroadcastReceiver MessageSender = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	String number = intent.getStringExtra("number");
	    	String reply = intent.getStringExtra("reply");
	    	String remainingReply = reply;
	    	number = number.substring(0,number.indexOf("@"));
	    	System.out.println("Number received : " + number);
	    	System.out.println("Reply received : " + reply);
	    	while(remainingReply.length() > 160)
    		{
    			reply = remainingReply.substring(0,160);
    			remainingReply = remainingReply.substring(160,remainingReply.length());
	    		sendSMS(number,reply);
    		}
	    	if(remainingReply.length() > 0)
	    	{
	    		sendSMS(number,remainingReply);
	    	}
	    }
	};

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    unregisterReceiver(MessageSender);
	}

	
  //---sends an SMS message to another device---
    @TargetApi(Build.VERSION_CODES.DONUT)
	@SuppressLint("NewApi")
	private void sendSMS(String phoneNumber, String message)
    {        
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
 
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);
 
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
            new Intent(DELIVERED), 0);
        
        SentReceiver sentReceiver = new SentReceiver();
 
        //---when the SMS has been sent---
        registerReceiver(sentReceiver, new IntentFilter(SENT));
 
        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        }, new IntentFilter(DELIVERED));        
 
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI); 
        
    }
    
    private class SentReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "SMS sent", 
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(getBaseContext(), "Generic failure", 
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(getBaseContext(), "No service", 
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(getBaseContext(), "Null PDU", 
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(getBaseContext(), "Radio off", 
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    
}





