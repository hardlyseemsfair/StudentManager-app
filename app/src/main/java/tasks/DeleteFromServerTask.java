package tasks;


import org.json.JSONException;
import org.json.JSONObject;

import helper.ServerDeleteResponse;
import handlers.ServerRequestHandler;
import util.Config;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class DeleteFromServerTask extends AsyncTask<String, Void, ServerDeleteResponse> {


    OnTaskCompleteListener mCallback;

    public DeleteFromServerTask(Activity activity) {
        mCallback = (OnTaskCompleteListener) activity;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(ServerDeleteResponse result) {
        mCallback.serverFileDeleted(result);
    }

    @Override
    protected ServerDeleteResponse doInBackground(String... values) {
        String filename = values[0];
        String workingDIR = values[1];
        String username = values[2];

        ServerDeleteResponse sdr = new ServerDeleteResponse(0, workingDIR, filename);
        Log.v("DELETE FROM SERVER TASK", "Deleteing from server");
        JSONObject json = ServerRequestHandler.deleteFileFromServer(workingDIR, filename, username);
        // check for response
        try {
            if (json.getString(Config.KEY_SUCCESS) != null) {
                Log.i("DELETE FROM SERVER TASK", "Processing JSON string");
                String res = json.getString(Config.KEY_SUCCESS);
                if (Integer.parseInt(res) == 1) {
                    // Everything good, build the message list
                    sdr.setResponse(1);
                } else {
                    // Error in process
                    sdr.setResponse(0);
                    Log.i("DELETE FROM SERVER TASK", "HELP REQUEST FAILURE string");
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("DELETE FROM SERVER TASK", "Returning from getting chat messages");
        return sdr;
    }




}
