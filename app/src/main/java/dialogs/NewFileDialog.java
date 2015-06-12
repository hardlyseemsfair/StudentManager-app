package dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.classroom.applicationactivity.R;

/**
 * Created by NAPOLEON on 4/30/2015.
 */
public class NewFileDialog extends DialogFragment {

    private DialogTaskListener mCallback;

    String extension = "";

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String workingDIR = getArguments().getString("workingDIR");
        builder.setTitle("Enter file details");
        View v = getCustomView();

        final EditText filename = (EditText) v.findViewById(R.id.filename);
        builder.setView(v);
        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.text_file: extension = ".txt"; break;
                    case R.id.word_document: extension = ".doc"; break;
                }
            }
        });

        //Set confirmation button
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do something with value!
                String fname = filename.getText().toString() + extension;
                Log.i("CREATE NEW FILE", "New file started filename: " + fname + " in directory = " + workingDIR);
                mCallback.createNewFile(fname, workingDIR);
            }
        });
        //Set cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled, no action required
            }
        });
        return builder.create();
    }

    private View getCustomView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.dialog_new_file, null);
        return v;
    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.text_file: extension = ".txt"; break;
            case R.id.word_document: extension = ".doc"; break;
        }
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mCallback = (DialogTaskListener) activity;

    }
}
