package dialogs;

import handlers.FileHandler;

import util.Config;

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

import com.classroom.applicationactivity.R;


/**
 * Dialog to handle file rename processes
 * TriggeredBy: FileSelectedDialog
 * @author JACK
 *
 */
public class RenameFileDialog extends DialogFragment {

    //Instance Variables
    private String filename;
    private String workingDIR;
    private DialogTaskListener mCallback;
    View v;
    EditText newFileName;

    /**
     * onCreateDialog
     * expects arguments for tags filename(filename to delete) and dirPath(directory of file to be deleted)
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        filename = getArguments().getString("filename");
        workingDIR= getArguments().getString("workingDIR");
        v = getCustomView();
        newFileName = (EditText) v.findViewById(R.id.rename_new_name);
        builder.setView(v);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // Rename file
                renameFile(newFileName.getText().toString());
            }

        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        return builder.create();
    }

    /**
     * Returns the inflater view
     * @return inflated view
     */
    private View getCustomView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.dialog_rename_file, null);
        return v;
    }

    /**
     * Rename the class filename
     * @param newname the new name to use
     */
    private void renameFile(String newname){
        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i);
        }
        String newFilename = newname + extension;
        FileHandler.renameFile(newFilename, Config.getWorkingDirectory(workingDIR, getActivity()), filename);
        Log.i("FILE RENAME", "Local file renamed sending to server");
        mCallback.renameServerFile(newFilename, workingDIR, filename);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mCallback = (DialogTaskListener) activity;
    }
}
