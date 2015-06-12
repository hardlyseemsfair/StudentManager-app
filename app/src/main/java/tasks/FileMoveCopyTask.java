package tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import handlers.ServerRequestHandler;
import helper.FileMoveCopyResponse;
import util.Config;

/**
 * Created by NAPOLEON on 5/17/2015.
 */
public class FileMoveCopyTask extends AsyncTask <String, FileMoveCopyResponse, FileMoveCopyResponse> {

    private OnTaskCompleteListener mCallback;

    public FileMoveCopyTask(Activity activity){
        mCallback = (OnTaskCompleteListener)activity;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(FileMoveCopyResponse response) {
        mCallback.onFileMoveCopyResponse(response);
    }


    @Override
    protected FileMoveCopyResponse doInBackground(String... val){
        String filename = val[0];
        String sourceDir = val[1];
        String destDir = val[2];
        String mask = val[3];
        FileMoveCopyResponse response = new FileMoveCopyResponse(mask, filename, sourceDir, destDir);
        JSONObject json = ServerRequestHandler.serverFileMoveCopy(filename,sourceDir,destDir,mask);
        try {
            if (json.getString(Config.KEY_SUCCESS) != null) {
                Log.i("UPLOAD TO SERVER TASK", "Processing JSON string");
                String res = json.getString(Config.KEY_SUCCESS);
                if (Integer.parseInt(res) == 1) {
                    // Everything good, build the message list
                } else {
                    // Error in process
                    response.setError(Integer.parseInt(json.getString("error")));
                    response.setErrorMessage(json.getString("error_msg"));
                    Log.i("UPLOAD TO SERVER TASK", "HELP REQUEST FAILURE string");
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("UPLOAD TO SERVER TASK", "Returning from server upload");
        return response;
    }


}