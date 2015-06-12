package tasks;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import helper.FileUploadResponse;
import handlers.ServerRequestHandler;
import util.Config;

public class UploadToServerTask extends AsyncTask<String, Void, FileUploadResponse> {


    OnTaskCompleteListener mCallback;
    Activity activity;

    public UploadToServerTask(Activity activity) {
        this.activity = activity;
        mCallback = (OnTaskCompleteListener) activity;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(FileUploadResponse fur) {
        mCallback.uploadFileToServerComplete(fur);
    }

    @Override
    protected FileUploadResponse doInBackground(String... values) {
        String filename = values[0];
        String destDIR = values[1];

        Log.v("UPLOAD TO SERVER TASK", "Uploading to server");
        FileUploadResponse fur = new FileUploadResponse(0, filename, destDIR);
        JSONObject json = ServerRequestHandler.uploadFileToServer(filename, destDIR, activity);
        // check for response
        try {
            if (json.getString(Config.KEY_SUCCESS) != null) {
                Log.i("UPLOAD TO SERVER TASK", "Processing JSON string");
                String res = json.getString(Config.KEY_SUCCESS);
                if (Integer.parseInt(res) == 1) {
                    // Everything good, build the message list
                    fur.setResponse(1);
                } else {
                    // Error in process
                    fur.setResponse(0);
                    Log.i("UPLOAD TO SERVER TASK", "HELP REQUEST FAILURE string");
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("UPLOAD TO SERVER TASK", "Returning from getting chat messages");
        return fur;
    }






}
