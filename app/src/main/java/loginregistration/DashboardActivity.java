package loginregistration;

import android.os.Bundle;

import com.classroom.applicationactivity.ApplicationActivity;
import com.classroom.applicationactivity.R;

import handlers.UserDBHandler;
import handlers.ServerRequestHandler;
import util.ToastMessages;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Button;

public class DashboardActivity extends FragmentActivity implements OnLoginRegisterInterface {
    Button btnLogout;
    UserDBHandler dbHandler;

    // private String user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        if (ServerRequestHandler.isUserLoggedIn(getApplicationContext())) {
            // user already logged in start FileViewActivity
            startApplication();
        } else {
            // user is not logged in show login screen
            setLoginFragment();
        }
    }

    /**
     * Executes main ApplicationActivity
     */
    @Override
    public void startApplication() {
        Intent fileview = new Intent(getApplicationContext(), ApplicationActivity.class);
        fileview.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(fileview);
        finish();
        setContentView(R.layout.dashboard);
    }

    /**
     * Set login fragment
     */
    @Override
    public void setLoginFragment() {
        LoginFragment lf = LoginFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.dashboard_content, lf).commit();
    }

    @Override
    public void setRegisterFragment() {
        RegisterFragment rf = RegisterFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.dashboard_content, rf);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onLoginComplete(int response) {
        Log.i("LOGIN FRAGMENT", "Executing task complete");
        if (response == 0) {
            Log.i("LOGIN FRAGMENT", "Executing failed login");
            ToastMessages.longToast("Error logging in, please try again.", 30, this);
        } else {
            startApplication();
        }
    }

    @Override
    public void onRegisterComplete(int response) {
        if (response == 0) {
            ToastMessages.shortToast("Registration Error", 30, getApplicationContext());
        } else {
            ToastMessages.shortToast("Registration Succesful", 30, getApplicationContext());
            setLoginFragment();
        }
    }

}
