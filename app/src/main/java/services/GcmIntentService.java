package services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import handlers.GcmBroadcastReceiver;

/**
 * Created by JACK on 3/29/2015.
 */
public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;


    public GcmIntentService(){
        super("GcmIntentService");
    }

    @Override
    public void onHandleIntent(Intent intent){
        //Object setup\
        Log.i("GCM INTENT", "Intent triggered...");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        //Get message type from intent
        String messageType = gcm.getMessageType(intent);

        if(!extras.isEmpty()){  //Ensure bundle can be unpacked
            //Check for error of deleted tags
            if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)){
                Log.i("GCM INTENT", "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)){
                Log.i("GCM INTENT", "Deleted tag");
            //Otherwise process message to receiver
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                broadcastIntent(extras);
            }
            GcmBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    /**
     * Broadcast intent to main activity receiver to action
     */
    private void broadcastIntent(Bundle extras){
        Intent i = new Intent("com.classroom.applicationactivity.USER_ACTION");
        i.putExtra("result", "gcm_message");
        i.putExtra("message", extras.getString("message"));
        sendBroadcast(i);
    }
}
