package tasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import handlers.ServerRequestHandler;
import util.Config;
import util.ToastMessages;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class RegisterDeviceTask extends AsyncTask<String, Integer, ArrayList<String>>{

    private OnTaskCompleteListener mCallback;
    private ArrayList<String> groups = new ArrayList<>();

    public RegisterDeviceTask(Activity activity){
        mCallback = (OnTaskCompleteListener) activity;
    }


    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        if(!result.isEmpty()){
            //we have groups, deal
            mCallback.onRegisterDeviceComplete(result);
        } else {
            //no groups, dont do anything
            mCallback.onRegisterDeviceComplete(null);
        }
    }

    @Override
    protected ArrayList<String> doInBackground(String... values) {
        String username = values[0];
        String deviceID = values[1];
        String gcmID = values[2];
        Log.i("REGISTERING DEVICE", username +  " : " + gcmID);
        JSONObject json = ServerRequestHandler.registerDeviceAndGetGroups(username, deviceID, gcmID);
        int responseCode = 0;
        // check for login response
        try {
            if (json.getString(Config.KEY_SUCCESS) != null) {
                Log.i("PROCESS", "Processing JSON string: " + json.toString());
                String res = json.getString(Config.KEY_SUCCESS);
                if (Integer.parseInt(res) == 1) {
                    //Everything good, grab any user directories
                    JSONArray ja = json.getJSONArray("groups");
                    if(ja != null){
                        int len = ja.length();
                        for(int i = 0; i < len; i++){
                            groups.add(ja.getString(i));
                        }
                    }
                    responseCode = 1;

                } else {
                    // Error in process
                    Log.i("PROCESS", "HELP REQUEST FAILURE string");
                    responseCode = 0;
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("PROCESS", "Returning from register device task rsponse : " + responseCode );
        return groups;
    }


}
