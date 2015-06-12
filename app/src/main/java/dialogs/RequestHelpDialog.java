package dialogs;

import com.classroom.applicationactivity.R;

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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


/**
 * Dialog to handle help request processes
 * TriggeredBy: FileSelectedDialog
 * @author JACK
 *
 */
public class RequestHelpDialog extends DialogFragment {

    //Instance Variables
    private String filename;
    private DialogTaskListener mCallback;
    private SeekBar seekBar;
    private EditText helpMessage;
    private int value = 0;


    /**
     * onCreateDialog
     * expects arguments for tags filename(filename help requested for)
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        filename = getArguments().getString("filename");
        builder.setTitle(R.string.file_select_options_title);
        View v = getCustomView();
        seekBar = (SeekBar) v.findViewById(R.id.help_bar);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
            }
        });
        helpMessage = (EditText) v.findViewById(R.id.help_message);
        builder.setView(v);
        //Set confirmation button
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do something with value!
                if (filename == null) {
                    Log.v("HELP REQUEST", "FILENAME NULL");
                } else if (Integer.toString(value) == null) {
                    Log.v("HELP REQUEST", "SEEKBAR NULL");
                } else if (helpMessage.getText().toString() == null) {
                    Log.v("HELP REQUEST", "EDIT TEXT NULL");
                } else {
                    mCallback.onHelpRequested(filename, Integer.toString(value), helpMessage.getText().toString());
                }
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

    /**
     * Returns custom dialog view
     * @return custom view
     */

    private View getCustomView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.dialog_help_content, null);
        return v;
    }

    /**
     * onAttach
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (DialogTaskListener) activity;
    }

}
