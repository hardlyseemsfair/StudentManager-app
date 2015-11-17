package com.classroom.applicationactivity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import contentfragments.ApplicationFragmentListener;
import handlers.UserDBHandler;
import util.Config;

import java.util.ArrayList;

/**
 * Creates application top bar containing folder selection spinner and logout button
 * @author JACK
 *
 */
public class TitleBarFragment extends Fragment implements OnItemSelectedListener {

    //Class variables
    ApplicationFragmentListener mCallback;
    private Spinner spinner;
    TextView folder;
    TextView user;
    private Button logoutButton, menuButton;
    private UserDBHandler userDB;
    ArrayList<String> directories;
    ArrayAdapter<String> adapter;
    View view;


    /**
     * onCreate
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_titlebar, container, false);
        setDefaultDirectories();
        menuButton = (Button) view.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.hide_keyboard((Activity) mCallback);
                mCallback.onMenuClick();
            }
        });
        //folder = (TextView) view.findViewById(R.id.directory);
        user = (TextView) view.findViewById(R.id.userText);
        //setSpinnerBlock(view);
        setLogoutButtonBlock(view);
        return view;
    }

    /**
     * Sets the entries to be used with the spinner
     */
    private void setDefaultDirectories(){
        directories = new ArrayList<String>();
        directories.add(userDB.getUserName());
        directories.add("Camera");
    }

    public void setDirectoryText(String t){
        //folder.setText(Config.formatGroupNameDisplay(t));
    }

    public void setUserText(String t){
        user.setText("Hi " + t);
    }

    public void setUserText(){

    }


    /**
     * Creates the spinner contents
     * @param view associated view
     */
    private void setSpinnerBlock(View view){
        //TextView txtUser = (TextView) view.findViewById(R.id.directoryText);
        //spinner = (Spinner) view.findViewById(R.id.directorySpinner);
       // setSpinnerContents(view);
    }

    /**
     * Sets spinner contents, adapter and listener. Called at initial creation and when spinner contents requires update by updateAdapter
     * @param view  associated view
     */
    private void setSpinnerContents(View view){
        adapter = getSpinnerAdapter(directories);
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        spinner.setOnItemSelectedListener(this);


    }

    /**
     * Creates relevant adapter for spinner
     * @param directories list of directories spinner should allow viewing of
     * @return adapter for use with spinner
     */
    private ArrayAdapter<String> getSpinnerAdapter(ArrayList<String> directories){
        String[] adapterArray = directories.toArray(new String[directories.size()]);
        ArrayAdapter<String> spinnerList = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, adapterArray);
        spinnerList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return spinnerList;
    }


    /**
     * Builds the logout button block
     * @param view associated view
     */
    private void setLogoutButtonBlock(View view){
        TextView txtUser = (TextView) view.findViewById(R.id.userText);
        txtUser.setText("User: " +  userDB.getUserName());
        logoutButton = (Button) view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Execute logout in activity
                mCallback.onLogout();
            }
        });
    }


    /**
     * Updates the spinenr adapter with new information. Triggers when usergroup directories are modified elsewhere
     * @param dirs the new directories to allow viewing of
     */
    public void updateAdapter(ArrayList<String> dirs){
/*
        if(dirs.size() != spinner.getAdapter().getCount()){
            directories = dirs;
            Log.v("TITLE BAR FRAGMENT","UPDATING SPINNER" + dirs.toString());
            setSpinnerContents(view);
        }*/
    }

    /**
     * select listener for spinner. Fires updates to the folder view fragment
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        mCallback.setContentFragment(parent.getItemAtPosition(pos).toString());
    }


    /**
     * Sets the adapter entry to the relevant foilder being viewed
     * @param dir the directory the set the spinner to
     */
    public void setSpinnerEntry(String dir){
        spinner.setSelection(adapter.getPosition(dir));
    }

    /**
     * Unused
     */
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    /**
     * Attach contruct
     */
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        userDB = new UserDBHandler(activity);
        try{
            mCallback = (ApplicationFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement OnItemClickListener");
        }
    }



}
