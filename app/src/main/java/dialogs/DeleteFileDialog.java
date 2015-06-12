package dialogs;

import java.io.File;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.classroom.applicationactivity.R;

/**
 * Dialog that handles file deletion
 * Triggered by: FileSelectedDialog
 *
 * @author JACK
 *
 */
public class DeleteFileDialog extends DialogFragment {

    //Instance Variables
    private String filename;
    private String workingDIR;
    private DialogTaskListener mCallback;

    /**
     * onCreateDialog
     * expects arguments for tags filename(filename to delete) and dirPath(directory of file to be deleted)
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        filename = getArguments().getString("filename");
        workingDIR = getArguments().getString("workingDIR");
        Log.i("DELETE DIALOG", "WorkingDIR: " + workingDIR);
        builder.setTitle(R.string.file_delete_title);
        // builder.setMessage("Request help");
        builder.setMessage(R.string.file_delete_message);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // call delete on remote file
                // delete local file
                mCallback.deleteFileTask(filename, workingDIR);
//				try {
//					File file = new File(dirPath, filename);
//					if (file.exists() & file.isFile()) {
//						file.delete();
//					}
//				} catch (Exception e) {
//					Log.e("DELETE FILE DIALOG", "Error deleting file: " + e.getMessage());
//				}
                // restart scheduler after 5 seconds (to allow server to process
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
     * onAttach
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (DialogTaskListener) activity;
    }
}
