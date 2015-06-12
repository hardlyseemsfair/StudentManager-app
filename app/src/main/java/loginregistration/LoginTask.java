package loginregistration;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import handlers.UserDBHandler;
import handlers.ServerRequestHandler;
//import com.classroom.studentmanager.R;
import tasks.OnTaskCompleteListener;
import util.Config;


/**
 * Handles login server call and response
 * @author JACK
 *
 */
public class LoginTask extends AsyncTask<String, Void, Integer> {

    //Instance Variables
    private Context context;
    private OnLoginRegisterInterface mCallback;
    private int responseCode = 0;

    /**
     * Constructor
     * @param activity Source fragment
     */
    public LoginTask(Activity activity) {
        mCallback = (OnLoginRegisterInterface) activity;
        context = activity;
    }

    /**
     * onPreExecute
     */
    @Override
    protected void onPreExecute() {	}

    /**
     * onPostExecute callback
     */
    @Override
    protected void onPostExecute(Integer result) {
        mCallback.onLoginComplete(result);
    }

    /**
     * doInBackground
     * returns a response code of 0 for failure, 1 for success
     */
    @Override
    protected Integer doInBackground(String... arg0) {
        String user = arg0[0];
        String password = arg0[1];
        Log.v("LOGIN TASK" , "LOGIN CRED: " + user + " : " + password);
        JSONObject json = ServerRequestHandler.loginUser(user, password);
        // check for login response
        try {
            if (json.getString(Config.KEY_SUCCESS) != null) {
                String res = json.getString(Config.KEY_SUCCESS);
                if (Integer.parseInt(res) == 1) {
                    // user successfully logged in
                    // Store user details in SQLite Database
                    UserDBHandler db = new UserDBHandler(context);
                    JSONObject json_user = json.getJSONObject("user");
                    // Clear all previous data in database in case of artifacts
                    ServerRequestHandler.logoutUser(context);
                    db.addUser( json_user.getString(Config.KEY_USERNAME), json_user.getString(Config.KEY_CREATED_AT));
                    // Login Success
                    responseCode = 1;
                } else {
                    // Error in login
                    Log.i("LOGIN TASK", "LOGING FAILURE string");
                    responseCode = 0;
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("LOGIN TASK", "Returning from login task rsponse : " + responseCode);
        return responseCode;
    }



}
