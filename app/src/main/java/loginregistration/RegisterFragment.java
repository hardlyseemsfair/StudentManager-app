package loginregistration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.classroom.applicationactivity.R;

public class RegisterFragment extends Fragment {

    private OnLoginRegisterInterface mCallback;



    public static final RegisterFragment newInstance() {
        RegisterFragment rf = new RegisterFragment();
        Bundle args = new Bundle();
        rf.setArguments(args);
        return rf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Importing all assets like buttons, text fields
        final EditText firstname = (EditText) view.findViewById(R.id.registerFirstName);
        final EditText lastname = (EditText) view.findViewById(R.id.registerLastName);
        final EditText username = (EditText) view.findViewById(R.id.registerUsername);
        final EditText password = (EditText) view.findViewById(R.id.registerPassword);
        Button btnRegister = (Button) view.findViewById(R.id.btnRegister);
        Button btnLinkToLogin = (Button) view.findViewById(R.id.btnLinkToLoginScreen);
        TextView registerErrorMsg = (TextView) view.findViewById(R.id.register_error);

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Registering...");
                RegisterTask registerTask = new RegisterTask(getActivity(), progressDialog);
                registerTask.execute(firstname.getText().toString(), lastname.getText().toString()
                        , username.getText().toString(), password.getText().toString());
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mCallback.setLoginFragment();
            }
        });
        return view;
    }


    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mCallback = (OnLoginRegisterInterface) activity;
    }

}