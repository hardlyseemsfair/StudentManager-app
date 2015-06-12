package tasks;

import org.json.JSONException;
import org.json.JSONObject;

import handlers.ServerRequestHandler;
import util.Config;
import util.ToastMessages;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class CommitChatMessageTask extends AsyncTask<String, Integer, Integer>{




    Activity activity;
    OnTaskCompleteListener mCallback;

    public CommitChatMessageTask(Activity activity){
        mCallback = (OnTaskCompleteListener) activity;
        this.activity = activity;
    }


    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Integer result) {
        if(result == 0){
            //failure toast
            ToastMessages.longToast("Error in message", 20, activity);
        }

    }

    @Override
    protected Integer doInBackground(String... values) {
        String username = values[0];
        String chatgroup = values[1];
        String message = values[2];
        Log.v("SET CHAT MESSAGE", username);
        JSONObject json = ServerRequestHandler.commitChatMessage(username, chatgroup, message);
        int responseCode = 0;
        // check for response
        try {
            if (json.getString(Config.KEY_SUCCESS) != null) {
                Log.i("PROCESS", "Processing JSON string");
                String res = json.getString(Config.KEY_SUCCESS);
                if (Integer.parseInt(res) == 1) {
                    //Everything good
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
        Log.i("PROCESS", "Returning from chat task response : " + responseCode);
        return responseCode;
    }






}
