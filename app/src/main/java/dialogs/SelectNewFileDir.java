package dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

import com.classroom.applicationactivity.R;

import java.util.List;

/**
 * Created by NAPOLEON on 4/30/2015.
 */
public class SelectNewFileDir extends DialogFragment {

    private DialogTaskListener mCallback;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Folder for new file...");
        List<String> dirs = mCallback.getDirectories();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, dirs);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onSelect(adapter, which);
            }
        });
        return builder.create();
    }

    private void onSelect(ArrayAdapter<String> adapter, int position){
        String dir = adapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putString("workingDIR", dir);
        NewFileDialog nfd = new NewFileDialog();
        nfd.setArguments(bundle);
        nfd.show(getFragmentManager(), "createNewFileDialog");
    }


    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mCallback = (DialogTaskListener) activity;
    }



}
