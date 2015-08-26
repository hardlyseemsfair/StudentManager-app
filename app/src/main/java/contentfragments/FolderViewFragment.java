package contentfragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;




import handlers.UserDBHandler;
import util.Config;
import util.LogFlag;
import util.SessionLog;

import android.app.Activity;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.classroom.applicationactivity.R;

/**
 * Fragment for handling folder view, contents and listener
 * @author JACK
 *
 */

public class FolderViewFragment extends Fragment {

    //Log
    private SessionLog sessionLog;

    //Instance variables
    private GridView gridView;
    private Activity activity;
    private String username;
    private String dirPath;
    private String workingDir;
    public ApplicationFragmentListener mCallback;
    private ImageAdapter ia;

    /**
     * Instance constructor
     * @return self object
     */
    public static final FolderViewFragment newInstance(String folder) {
        FolderViewFragment fvf = new FolderViewFragment();
        Bundle args = new Bundle();
        args.putString("folder", folder);
        fvf.setArguments(args);
        return fvf;
    }

    /**
     * onCreate
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * onCreateView
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        workingDir = getArguments().getString("folder");
        View view = inflater.inflate(R.layout.fragment_folderview, container, false);
        dirPath = setDirPath(workingDir);
        String[] files = getFileNames();
        TextView tv = (TextView) view.findViewById(R.id.foldertext);
        tv.setText(workingDir);
        setGridView(view, files);
        return view;
    }

    /**
     * Builds the grid view, attaches an image adapter and sets the listener
     * @param view associated view
     * @param fileNames file names array to be used
     */
    private void setGridView(View view, String[] fileNames) {
        gridView = (GridView) view.findViewById(R.id.grid_view);
        if(fileNames != null) {
            Log.i("FOLDER VIEW FILES", "Folder view files to load: " + Arrays.toString(fileNames));
            ia = new ImageAdapter(activity, fileNames);
            gridView.setAdapter(ia);
            final String[] fnames = fileNames;
            gridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    //TODO code
                    Log.d("FOLDER VIEW FRAGMENT", "File clicked: " + fnames[position]);
                    sessionLog.writeLog(LogFlag.USER_ACTION, "File clicked " + fnames[position]);
                    mCallback.onFileSelected(workingDir, fnames[position]);
                }

            });
        }
    }

    /**
     * Sets the dir path to use
     * @param dir directory name to use
     * @return the absolute dir path
     */
    private String setDirPath(String dir) {
        String path;
        if (dir.equalsIgnoreCase("camera")) {
            path = Config.getCameraDirectoryRoot(username);
        } else {
            path = Config.getWorkingDirectory(dir, getActivity());
        }
        return path;

    }

    /**
     * GGets file names of the fragments working directory
     * @return string array of file names
     */
    private String[] getFileNames() {
        String[] files;
        File file = new File(dirPath);
        Log.v("FILE VIEW FRAGMENT", "Directory path: " + dirPath);
        files = file.list();
        Log.e("FILE LIST","FIle list for dir: " + dirPath + "\n" + Arrays.toString(files));
        if(files != null) {
            files = formatFolderContents(files);
        }
        return files;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    /**
     * Formats contents to strip any directories / non files present in the fetch
     * @param filenames file names to modify
     * @return string array of file names without extensions
     */
    private String[] formatFolderContents(String[] filenames){
        ArrayList<String> names = new ArrayList<>(Arrays.asList(filenames));
        Iterator<String> iter = names.iterator();
        while(iter.hasNext()){
            String f = iter.next();
            File file = new File(dirPath, f);
            if(!file.isFile()){
                iter.remove();
            } else if (file.getName().substring(file.getName().length()-1).equalsIgnoreCase("~")){
                iter.remove();
            }
        }
        String[] fnames = new String[names.size()];
        fnames = names.toArray(fnames);
        return fnames;
    }

    /**
     * Updates the view adapter. Triggered when new files are added to a dir currently being viewed
     */
    public void updateAdapter(){
        Log.i("FOLDER FRAGMENT", "Updating image adapter");
        String[] files = getFileNames();
        ia = new ImageAdapter(activity, files);
        gridView.setAdapter(ia);
        final String[] fnames = files;
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id ){
                //TODO code
                Log.d("FOLDER VIEW FRAGMENT", "File clicked: " + fnames[position]);
                mCallback.onFileSelected(workingDir, fnames[position]);
            }

        });
    }



    /**
     * on attach constructor
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (ApplicationFragmentListener) activity;
        sessionLog = mCallback.getSessionLog();
        this.activity = activity;
        UserDBHandler userDB = new UserDBHandler(activity);
        username = userDB.getUserName();
    }


    /**
     * OnResume to ensure spinner information is displayed corectly when arriving at this fragment from the backstack
     */
    @Override
    public void onResume(){
        super.onResume();
        mCallback.setSpinnerEntry(workingDir);
    }



}
