package loginregistration;


import android.support.v4.app.Fragment;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.classroom.applicationactivity.R;

public class LoginFragment extends Fragment {

    OnLoginRegisterInterface mCallback;

    public static final LoginFragment newInstance() {
        LoginFragment lf = new LoginFragment();
        Bundle args = new Bundle();
        lf.setArguments(args);
        return lf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Importing all assets like buttons, text fields
        final EditText inputUsername = (EditText) view.findViewById(R.id.loginUsername);
        final EditText inputPassword = (EditText) view.findViewById(R.id.loginPassword);
        Button btnLogin = (Button) view.findViewById(R.id.btnLogin);
        Button btnLinkToRegister = (Button) view.findViewById(R.id.btnLinkToRegisterScreen);
        TextView loginErrorMsg = (TextView) view.findViewById(R.id.login_error);
        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LoginTask loginTask = new LoginTask(getActivity());
                loginTask.execute(inputUsername.getText().toString(),inputPassword.getText().toString() );
            }
        });
        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.setRegisterFragment();
            }
        });
        return view;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (OnLoginRegisterInterface) activity;
    }


}
