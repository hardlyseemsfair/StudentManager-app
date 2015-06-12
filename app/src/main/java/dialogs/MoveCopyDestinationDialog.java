package dialogs;

import handlers.FileHandler;
import util.Config;
import util.ToastMessages;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.classroom.applicationactivity.R;

/**
 * Dialog to handle move and copy destination selections
 * TriggeredBy: MoveCopyFileDialog
 * @author JACK
 *
 */

public class MoveCopyDestinationDialog extends DialogFragment {

    //Instance Variables
    private DialogTaskListener mCallback;
    private String[] dirs;
    private String flag;
    private String filename;
    private String username;
    private String workingDIR;
    private final String MOVE_FILE = "move file";
    private final String COPY_FILE = "copy file";

    /**
     * onCreateDialog
     * expects arguments for tags flag(the required operation MOVE_FILE or COPY_FILE) filename(filename to delete),
     * username (current user) and dirPath(directory of file to be deleted)
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        flag = getArguments().getString("flag");
        filename = getArguments().getString("filename");
        username = getArguments().getString("username");
        workingDIR = getArguments().getString("workingDIR");
        builder.setTitle(R.string.copy_move_file_destination_options_title).setItems(dirs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onSelect(which);
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
     * Selector for action
     * @param pos index of selected item
     */
    private void onSelect(int pos) {
        String destination_directory = "";
        if (dirs[pos].equalsIgnoreCase("Camera")) {
            destination_directory = Config.getCameraDirectoryRoot(username);
        } else {
            destination_directory = Config.getWorkingDirectory(dirs[pos], getActivity());
        }
        boolean fileExists = FileHandler.fileExistsInDirectory(destination_directory, filename);
        if (!fileExists) {
            if (flag.equalsIgnoreCase(COPY_FILE)) {
                mCallback.fileMoveCopy(filename, workingDIR, dirs[pos], Config.FILE_COPY);
            } else if (flag.equalsIgnoreCase(MOVE_FILE)) {
                mCallback.fileMoveCopy(filename, workingDIR, dirs[pos], Config.FILE_MOVE);
            }
        } else {
            ToastMessages.longToast("File with that name already exists in destination", 30, getActivity());
        }
    }

    /**
     * onAttach
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (DialogTaskListener) activity;
        dirs = mCallback.getDirectories().toArray(new String[mCallback.getDirectories().size()]);
    }
}
