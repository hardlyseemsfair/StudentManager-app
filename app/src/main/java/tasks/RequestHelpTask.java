package tasks;

import org.json.JSONException;
import org.json.JSONObject;

import handlers.UserDBHandler;
import handlers.ServerRequestHandler;
//import com.classroom.studentmanager.R;
import util.Config;
import util.ToastMessages;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

public class RequestHelpTask extends AsyncTask<String, Integer, Integer> {

    Activity activity;
    OnTaskCompleteListener mCallback;

    public RequestHelpTask(Activity activity){
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
            ToastMessages.longToast("Help request failed. Please try again", 20, activity);
        } else {
            //success toast
            ToastMessages.longToast("Help request entered successfully", 20, activity);
        }
    }

    @Override
    protected Integer doInBackground(String... values) {
        String username = values[0];
        String filename = values[1];
        String rating = values[2];
        String message = values[3];
        JSONObject json = ServerRequestHandler.commitUserHelpRequest(username, filename, rating, message);
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
                    Log.i("PROCESS", "HELP REQUEST FAILURE string");
                    responseCode = 0;
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("PROCESS", "Returning from login task rsponse : " + responseCode);
        return responseCode;
    }


}
