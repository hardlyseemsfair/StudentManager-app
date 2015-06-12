package handlers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import services.GcmIntentService;

/**
 * Created by JACK on 3/29/2015.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent){
        //Force the intent to GcmIntentService
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
        //Start the wakeful service, ensure no lock out while recieveing
        startWakefulService(context, (intent.setComponent(comp)));
    }

}
