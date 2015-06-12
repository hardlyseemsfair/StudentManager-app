package tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import handlers.ServerRequestHandler;
import util.Config;

/**
 * Created by NAPOLEON on 4/20/2015.
 */
public class RenameServerFileTask extends AsyncTask <String, Void, Void> {





    Activity activity;
    OnTaskCompleteListener mCallback;

    public RenameServerFileTask(Activity activity){
        mCallback = (OnTaskCompleteListener) activity;
        this.activity = activity;
    }


    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Void v) {

    }

    @Override
    protected Void doInBackground(String... values) {
        String filename = values[0];
        String workingDIR = values[1];
        String oldFilename = values[2];
        String username = values[3];
        JSONObject json = ServerRequestHandler.renameServerFile(filename, workingDIR, oldFilename, username);
        int responseCode = 0;
        // check for login response
        try {
            if (json.getString(Config.KEY_SUCCESS) != null) {
                Log.i("PROCESS", "Processing JSON string");
                String res = json.getString(Config.KEY_SUCCESS);
                if (Integer.parseInt(res) == 1) {
                    //Everything good
                    responseCode = 1;

                } else {
                    // Error in process
                    Log.i("PROCESS", "FILE RENAME FAILURE string");
                    responseCode = 0;
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("PROCESS", "Returning from file rename task response : " + responseCode);
        return null;
    }




}
