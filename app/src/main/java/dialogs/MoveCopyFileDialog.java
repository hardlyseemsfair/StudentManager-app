package dialogs;

//import com.classroom.studentmanager.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.classroom.applicationactivity.R;

import util.ToastMessages;

/**
 * Dialog to handle file move / copy processes
 * TriggeredBy: FileSelectedDialog
 * @author JACK
 *
 */
public class MoveCopyFileDialog extends DialogFragment {

    private DialogTaskListener mCallback;

    //Instance Variables
    private String filename;
    private String username;
    private String workingDIR;

    //Class tags
    private final String MOVE_FILE = "move file";
    private final String COPY_FILE = "copy file";

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
        builder.setTitle(R.string.copy_move_file_select_options_title)
                .setItems(R.array.file__move_copy_dialog_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onSelect(which);
                    }
                });
        return builder.create();
    }


    /**
     * Selector for action
     * @param pos index of selected item
     */
    private void onSelect(int pos){
        String[] options = getActivity().getResources().getStringArray(R.array.file__move_copy_dialog_options);
        Bundle bundle = new Bundle();
        if(options[pos].equalsIgnoreCase(MOVE_FILE)){
            if(validMove()){
                bundle.putString("flag", MOVE_FILE);
                bundle.putString("filename", filename);
                bundle.putString("username", username);
                bundle.putString("workingDIR", workingDIR);
                MoveCopyDestinationDialog mcd = new MoveCopyDestinationDialog();
                mcd.setArguments(bundle);
                mcd.show(getFragmentManager(), "destinationDialog");
            } else {
                ToastMessages.shortToast("You can not move files from shared spaces", 16, getActivity());
                this.dismiss();
            }
        } else if (options[pos].equalsIgnoreCase(COPY_FILE)){
            //set flag to copy
            bundle.putString("flag", COPY_FILE);
            bundle.putString("filename", filename);
            bundle.putString("username", username);
            bundle.putString("workingDIR", workingDIR);
            MoveCopyDestinationDialog mcd = new MoveCopyDestinationDialog();
            mcd.setArguments(bundle);
            mcd.show(getFragmentManager(), "destinationDialog");
        }
    }

    private boolean validMove(){
        return(workingDIR.equals(mCallback.getUsername()) || workingDIR.equalsIgnoreCase("camera"));
    }

    public void onAttach(Activity activity){
        super.onAttach(activity);
        mCallback = (DialogTaskListener) activity;
    }

}
