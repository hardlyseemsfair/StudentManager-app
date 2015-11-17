package dialogs;

import java.io.File;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.classroom.applicationactivity.R;

import helper.FileData;
import util.Config;
import util.LogFlag;
import util.SessionLog;


/**
 * Dialog to handle file action processes
 * TriggeredBy: FolderViewFragment listview listener
 * @author JACK
 *
 */
public class FileSelectedDialog extends DialogFragment {

    //Class tags
    private static final String OPEN_FILE = "open file";
    private static final String GET_HELP = "get help";
    private static final String MOVE_FILE = "send file...";
    private static final String RENAME_FILE = "rename file";
    private static final String DELETE_FILE = "delete";

    //Instance Variables
    private String filename;
    private String username;
    private String workingDIR;
    private DialogTaskListener mCallback;
    private SessionLog sessionLog;

    /**
     * onCreateDialog
     * expects arguments for tags filename(filename to delete), username (current user) and dirPath(directory of file to be deleted)
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        filename = getArguments().getString("filename");
        username = getArguments().getString("username");
        workingDIR = getArguments().getString("workingDIR");
        builder.setTitle(R.string.file_select_options_title)
                .setItems(R.array.file_select_dialog_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onSelect(which);
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled, no action required
            }
        });
        return builder.create();
    }

    /**
     * Selector for action
     * @param pos index of selected item
     */
    private void onSelect(int pos){
        String[] options = getActivity().getResources().getStringArray(R.array.file_select_dialog_options);
        if(options[pos].equalsIgnoreCase(OPEN_FILE)){
            //open file
            openFile();
        } else if (options[pos].equalsIgnoreCase(GET_HELP)){
            //open help dialog
            executeHelpDialog();
        } else if (options[pos].equalsIgnoreCase(MOVE_FILE)){
            //open move file dialog
            executeMoveCopyFileDialog();
        } else if (options[pos].equalsIgnoreCase(RENAME_FILE)){
            //open rename file dialog
            executeRenameFileDialog();
        } else if (options[pos].equalsIgnoreCase(DELETE_FILE)){
            //open confirmation dialog
            executeDeleteDialog();
        }
    }

/**
 * 											Dialog actions
 */

    /**
     * create and execute an instance of RequestHelpDialog
     */
    private void executeHelpDialog(){
        Bundle bundle = new Bundle();
        bundle.putString("filename", filename);
        bundle.putString("username", username);
        RequestHelpDialog rhd = new RequestHelpDialog();
        rhd.setArguments(bundle);
        rhd.show(getFragmentManager(), "helpDialog");
    }

    /**
     * create and execute an instance of MoveCopyFileDialog
     */
    private void executeMoveCopyFileDialog(){
        Bundle bundle = new Bundle();
        bundle.putString("filename", filename);
        bundle.putString("username", username);
        bundle.putString("workingDIR", workingDIR);
        MoveCopyFileDialog rhd = new MoveCopyFileDialog();
        rhd.setArguments(bundle);
        rhd.show(getFragmentManager(), "helpDialog");
    }

    /**
     * create and execute an instance of RenameFileDialog
     */
    private void executeRenameFileDialog(){
        Bundle bundle = new Bundle();
        bundle.putString("filename", filename);
        bundle.putString("workingDIR", workingDIR);
        RenameFileDialog rfd = new RenameFileDialog();
        rfd.setArguments(bundle);
        rfd.show(getFragmentManager(), "renameFileDialog");
    }

    /**
     * create and execute an instance of DeleteFileDialog
     */
    private void executeDeleteDialog(){
        Bundle bundle = new Bundle();
        bundle.putString("filename", filename);
        bundle.putString("workingDIR", workingDIR);
        DeleteFileDialog dfd = new DeleteFileDialog();
        dfd.setArguments(bundle);
        dfd.show(getFragmentManager(), "deleteDialog");
    }

    /**
     * Trigger openFile dialog
     */
    private void openFile(){
        File file = new File(Config.getWorkingDirectory(workingDIR, getActivity()), filename);
        Log.i("FILE DIALOG", "Attempting to open=: " + Config.getWorkingDirectory(workingDIR, getActivity()) + File.separator + filename);
        sessionLog.writeLog(LogFlag.USER_ACTION, "Opening file " + filename );
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(android.content.Intent.ACTION_VIEW);
        //Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(fileExt(file.toString()).substring(1));
        newIntent.setDataAndType(Uri.fromFile(file),mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            mCallback.storeFileInfo(new FileData(file.getName(), file.length(), file.lastModified(), workingDIR));
            getActivity().startActivity(newIntent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No handler for this type of file.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get file extensions from filename
     * @param fname filename to use
     * @return filename extension
     */
    private String fileExt(String fname) {
        if (fname.indexOf("?")>-1) {
            fname = fname.substring(0,fname.indexOf("?"));
        }
        if (fname.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = fname.substring(fname.lastIndexOf(".") );
            if (ext.indexOf("%")>-1) {
                ext = ext.substring(0,ext.indexOf("%"));
            }
            if (ext.indexOf("/")>-1) {
                ext = ext.substring(0,ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }

    public void onAttach(Activity activity){
        super.onAttach(activity);
        mCallback = (DialogTaskListener) activity;
        sessionLog = mCallback.getSessionLog();
    }

}
