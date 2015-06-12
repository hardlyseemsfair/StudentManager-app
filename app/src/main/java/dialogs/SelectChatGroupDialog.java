package dialogs;

import com.classroom.applicationactivity.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;



/**
 * Provides a dialog for chat group selection
 * TriggeredBy: ApplicationActivity
 * @author JACK
 *
 */
public class SelectChatGroupDialog extends DialogFragment {

    //Instance variables
    private String[] usergroups;
    private DialogTaskListener mCallback;


    /**
     * onCreateDialog
     * expects arguments for tags usergroups(usergroups the user belongs to)
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mCallback = (DialogTaskListener) getActivity();

        usergroups = getArguments().getStringArray("usergroups");

        builder.setTitle(R.string.dialog_select_group_title)
                .setItems(usergroups, new DialogInterface.OnClickListener() {
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
        String chatGroup = usergroups[pos];
        mCallback.executeChatFragment(chatGroup);
    }


}
