package loginregistration;

import org.json.JSONException;
import org.json.JSONObject;

import handlers.UserDBHandler;
import handlers.ServerRequestHandler;
//import com.classroom.studentmanager.R;
import util.Config;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

/**
 * Basic and old, while unchanged needs ot have a lot of strip out
 * @author JACK
 *
 */

public class RegisterTask extends AsyncTask<String, Void, Integer> {

    private ProgressDialog progressDialog;
    private int responseCode = 0;
    private OnLoginRegisterInterface mCallback;

    /**
     *
     * @param activity Activity reference to listener
     * @param progressDialog dialog for register
     */
    public RegisterTask(Activity activity, ProgressDialog progressDialog) {
        mCallback = (OnLoginRegisterInterface) activity;
        this.progressDialog = progressDialog;
    }

    /**
     * onPreExecute
     */
    @Override
    protected void onPreExecute() {
        progressDialog.show();
    }

    /**
     * doInBackground expects 4 arguments
     */
    @Override
    protected Integer doInBackground(String... arg) {
        String firstName = arg[0];
        String lastName = arg[1];
        String username = arg[2];
        String password = arg[3];
        JSONObject json = ServerRequestHandler.registerUser(firstName, lastName, username, password);
        try {
            if (json.getString(Config.KEY_SUCCESS) != null) {
                // registerErrorMsg.setText("");
                String res = json.getString(Config.KEY_SUCCESS);
                if (Integer.parseInt(res) == 1) {
                    // user successfully registred
                    // successful registration, kick back to login (could be changed here to auto login, or give back the relevant value to a pref to store login
                    responseCode = 1;
                } else {
                    // Error in registration
                    responseCode = 0;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("REGISTER TASK", "Returning response code: " + responseCode);
        return responseCode;
    }

    /**
     * onPOstExecute returns response code 1 for success 0 for failure
     */
    @Override
    protected void onPostExecute(Integer responseCode) {
        progressDialog.dismiss();
        mCallback.onRegisterComplete(responseCode);
    }



}