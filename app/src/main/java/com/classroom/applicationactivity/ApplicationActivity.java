package com.classroom.applicationactivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.DownloadManager;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import contentfragments.ApplicationFragmentListener;
import contentfragments.ChatViewFragment;
import contentfragments.FolderViewFragment;
import dialogs.DialogTaskListener;
import dialogs.FileSelectedDialog;
import dialogs.SelectChatGroupDialog;
import dialogs.SelectNewFileDir;
import handlers.ApplicationReceiver;
import handlers.ApplicationReceiverInterface;
import helper.FileData;
import helper.FileMoveCopyResponse;
import helper.FileUploadResponse;
import helper.Message;
import helper.ServerDeleteResponse;
import handlers.FileHandler;
import handlers.ServerRequestHandler;
import handlers.UserDBHandler;
import loginregistration.DashboardActivity;
import services.FileMonitorService;
import services.FullFileSyncService;
import tasks.CommitChatMessageTask;
import tasks.DeleteFromServerTask;
import tasks.FileMoveCopyTask;
import tasks.GetUserChatMessagesTask;
import tasks.OnTaskCompleteListener;
import tasks.RegisterDeviceTask;
import tasks.RenameServerFileTask;
import tasks.RequestHelpTask;
import tasks.UploadToServerTask;
import util.Config;
import util.LogFlag;
import util.MessageBank;
import util.SessionLog;
import util.SnapShotManager;
import util.ToastMessages;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Primary application Activity. Handles all initial setup, service actions and
 * task / service / dialog callbacks
 *
 * @author JACK
 */

public class ApplicationActivity extends FragmentActivity implements ApplicationFragmentListener, DialogTaskListener, OnTaskCompleteListener, ApplicationReceiverInterface {

    // Activity preferences and logging
    private SessionLog sessionLog;

    // Device identifiers
    private String deviceID;
    private String username;

    // Service Intents and Recievers
    private ApplicationReceiver fileObserver;
    private Intent fileMonitorIntent;
    private Intent fileSyncIntent;

    // Stored folderview fragments - consider redesign for singleton access if
    // time allows. This is a nasty nasty way of being able to check if fragments are visible
    // Bad design they should be instantiated as needed not kept around.
    private FolderViewFragment fvf;
    private ChatViewFragment cvf;

    // Masks for camera activities
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private static final int CAMERA_SHOT = 1;
    private static final int VIDEO_SHOT = 2;

    // User directories
    private ArrayList<String> directories = new ArrayList<>(); // A list of all
    // directories
    // the user has
    // access to
    // including
    // groups,
    // username and
    // camera
    private ArrayList<String> usergroups = new ArrayList<String>(); // A list of all groups the user
    // belongs too, excludes username
    // and camera

    // User Chat messages
    private ArrayList<MessageBank> messageBanks = new ArrayList<>();

    //Google Play services data
    // Resolutiuon value
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //Flags
    public static final String PROPERTY_REG_ID = "registration_id";
    //Sender ID
    String SENDER_ID = "1077430934909";
    //Objects
    GoogleCloudMessaging gcm;
    String regID;
    FileData openedFile = null;

    //Drawer Layout
    private DrawerLayout menuSelect;
    private ListView menuItemsList;
    private List<NavDrawerObject> menuItems;

    private NavDrawerAdapter drawerAdapter;

    // Other settings
    private ProgressBar loadSpinner;


    //TODO not be hell budget and fix this nasty ass implement
    public Uri camerafile = null;
    public void setCameraFile(Uri uri){
        camerafile = uri;
    }


    /**
     * Activity on create. Sets device id, inflates views, starts user log and
     * then registers the device. Followup actions and final setup are performed
     * from onRegisterDeviceComplete.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        setContentView(R.layout.activity_application);
        //Build default menuSelect
        menuSelect = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuItemsList = (ListView) findViewById(R.id.drawer);
        menuItems = new ArrayList<>();
        menuItems.add(new NavDrawerHeader("Folders"));
        drawerAdapter = new NavDrawerAdapter(this, R.layout.drawer_list_item, menuItems);
        if(menuItemsList != null) {
            menuItemsList.setAdapter(drawerAdapter);
        }


        loadSpinner = (ProgressBar) findViewById(R.id.loadSpinner);
        loadSpinner.setVisibility(View.VISIBLE);
        setUser();
        startLogSession();
        setUserDirectories();


        registerDevice();
        registerApplicationReceiver();
        sessionLog.writeLog(LogFlag.NOTIFICATION, "Starting default fragment...");
        setDefaultFragment(username);

    }

    /**
     * Setup related methods
     */

    /**
     * Queries the local database for the current username and assigns it.
     */
    private void setUser() {
        UserDBHandler userDB = new UserDBHandler(this);
        username = userDB.getUserName();
    }

    /**
     * Creates the custom log object for activity logging
     */
    private void startLogSession() {
        Log.e("SESSION LOG", "Creating new session log...");
        sessionLog = new SessionLog(username);
        sessionLog.startLog();
    }

    /**
     * Sets directories and user groups arrays.
     */
    private void setUserDirectories() {
        String path = Config.getUserDirectoryRoot(username);
        File file = new File(path);
        if (!file.isDirectory()) {
            Log.v("APPLICATION", "Creating user directories");
            file.mkdirs();
            new File(path + File.separator + "Camera").mkdir();
        }
        directories.add(username);
        directories.add("camera");
    }

    /**
     * Register device to user with username, id and any GCM code through
     * ASyncTask
     */
    private void registerDevice() {
        //check and register for Google Play service
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regID = getRegistrationID(this);
            if (regID.isEmpty()) {
                getGooglePlayRegID();
            } else {
                RegisterDeviceTask rdt = new RegisterDeviceTask(this);
                rdt.execute(username, deviceID, regID);
            }
        } else {
            Log.i("APPLICATION ACTIVITY", " GCM ERROR: No valid google play apk found");
        }

    }

    /**
     * On completion of device registration. Most operational code here. Ensures
     * server data has been recieved and application values set before setup
     *
     * @param result returned by RegisterDeviTask.onProcessComplete(). List
     *               contains groups the user belongs to
     */
    @Override
    public void onRegisterDeviceComplete(ArrayList<String> result) {
        Log.v("APPLICATION ACTIVITY", "Device registration complete...");
        Log.v("GITHUB TEST", "hndfhnedfjkhsajkhbsed");
        if (result != null) {
            usergroups = result;
            for (String dir : result) {
                setGroupDirectory(dir);
            }
            directories.addAll(result);
        }
        // Once all directories / groups known, execute dir observers service
        startFileSyncService();
        startFileMonitorService();
        //Update Drawer Layout menu
        buildNavMenu();


        // Create fragments
        FragmentManager fm = getFragmentManager();
        TitleBarFragment tbf = (TitleBarFragment) fm.findFragmentById(R.id.titlebar_fragment);
        tbf.updateAdapter(getDirectories());
        // Once groups are set, execute chat messages task
        GetUserChatMessagesTask task = new GetUserChatMessagesTask(this);
        task.execute(username);
    }

    private boolean hasUserGroups(){
        if(usergroups == null || usergroups.isEmpty()) return false;
        return true;
    }

    private void buildNavMenu(){
        menuItems = new ArrayList<>();
        menuItems.add(new NavDrawerHeader("My Folders"));
        menuItems.add(new NavDrawerItem(username));
        menuItems.add(new NavDrawerItem("Camera"));

        if(hasUserGroups()) {
            menuItems.add(new NavDrawerHeader("Group Folders"));
            for (String s : usergroups) {
                menuItems.add(new NavDrawerItem(Config.formatGroupNameDisplay(s)));
            }
        }
        menuItems.add(new NavDrawerHeader("Tools"));
        menuItems.add(new NavDrawerItem("Group Chat"));
        menuItems.add(new NavDrawerItem("Camera Shot"));
        menuItems.add(new NavDrawerItem("New File..."));
        drawerAdapter = new NavDrawerAdapter(this, R.layout.drawer_list_item, menuItems);
        if(menuItemsList != null) {
            menuItemsList.setAdapter(drawerAdapter);
        }
        menuItemsList.setOnItemClickListener(new MenuItemSelectListener());
    }

    /**
     * Checks if the directories for a given groupname exist and creates them if
     * not
     *
     * @param groupname the groupname to check
     */
    private void setGroupDirectory(String groupname) {
        String path = Environment.getExternalStorageDirectory().toString() + File.separator + groupname;
        File file = new File(path);
        if (!file.isDirectory()) {
            Log.v("APPLICATION ACTIVITY", "Creating user directories");
            file.mkdirs();
        }
    }

    /**
     * Check device to ensure google play services is active, display a dialog if service not available
     * To fix ensure google play store apk available on device
     *
     * @return user access to goggle play apk
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("GOOGLE PLAY CONNECTION", "Service not supported on device...");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Set device registration ID for use with Google play
     *
     * @param context Application context
     * @return regID for Google Play services
     */
    private String getRegistrationID(Context context) {
        final SharedPreferences prefs = getSharedPreferences(ApplicationActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        String registrationID = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationID.isEmpty()) {
            Log.i("GOOGLE PLAY REG ID", "Registration not found...");
            return "";
        }
        return registrationID;
    }

    private void getGooglePlayRegID() {
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regID = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regID;
                    // Store the registration ID - no need to register again.
                    storeRegistrationId(regID);
                } catch (IOException ex) {
                    //TODO handle error from no response
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //Execute device registration
                RegisterDeviceTask rdt = new RegisterDeviceTask(ApplicationActivity.this);
                rdt.execute(username, deviceID, regID);
            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     *
     * @param regId registration ID
     */
    private void storeRegistrationId(String regId) {
        final SharedPreferences prefs = getSharedPreferences(ApplicationActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        Log.i("APPLICATION ACTIVITY", ": GCM REG: Saving regId on app ");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.commit();
    }


    /**
     * Set the initial file view fragment to be used at login
     *
     * @param path folder to view
     */
    private void setDefaultFragment(String path) {
        fvf = FolderViewFragment.newInstance(path);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_content, fvf).commit();
    }

    /**
     * Service and Reciever Registration
     */

    /**
     * Starts ServiceIntent to sync files on login. Should not be called
     * repeatedly due to high overhead. During operation rely instead on change
     * / push notifications
     */
    private void startFileSyncService() {
        fileSyncIntent = new Intent(this, FullFileSyncService.class);
        fileSyncIntent.putStringArrayListExtra("dirs", directories);
        startService(fileSyncIntent);
        sessionLog.writeLog(LogFlag.SYSTEM_ACTION, "File sync service started...");
    }

    /**
     * Starts the service that monitors file directories. Actionable from
     * ObserverReciever
     */
    private void startFileMonitorService() {
        fileMonitorIntent = new Intent(this, FileMonitorService.class);
        fileMonitorIntent.putStringArrayListExtra("directories", directories);
        Log.v("SERVICE ACTION", "Starting service...");
        startService(fileMonitorIntent);
        sessionLog.writeLog(LogFlag.SYSTEM_ACTION, "Directory service started...");
    }

    /**
     * Registers the activity's ObserverReciever where callbacks from
     * application services are performed
     */
    private void registerApplicationReceiver() {
        IntentFilter intentfilter = new IntentFilter("com.classroom.applicationactivity.USER_ACTION");
        intentfilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        fileObserver = new ApplicationReceiver(this);
        registerReceiver(fileObserver, intentfilter);
    }



    /**
     * Creat a new file on instruction from NewFileDIalog
     * @param filename the filename of the new file
     * @param workingDIR the working directory to creat the file in
     */
    @Override
    public void createNewFile(String filename, String workingDIR){
        FileHandler.createFile(filename, workingDIR, this);
        updateServerFile(filename, workingDIR);
    }

    /**
     * Component callback methods
     */

    /**
     * Source: TitleBarFragment Logs the user out of the application and shuts
     * down running services and broadcast recievers
     */
    @Override
    public void onLogout() {
        ServerRequestHandler.logoutUser(getApplicationContext());
        Intent login = new Intent(getApplicationContext(), DashboardActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(login);
        // Closing ApplicationActivity screen
        unregisterReceiver(fileObserver);
        stopService(fileMonitorIntent);
        stopService(fileSyncIntent);
        sessionLog.writeLog(LogFlag.NOTIFICATION, "Logging out use " + username + "...");
        finish();
    }

    /**
     * Open navigation drawer
     */
    @Override
    public void onMenuClick(){
        menuSelect.openDrawer(Gravity.LEFT);
    }

    /**
     * Source: BottomBarFragment Executes an instance of SnapShotManager in
     * order to access the device camera and take picutres / video
     */
    public void onCameraClick() {

        Log.v("CAMERA", "CameraClick actioned");
        SnapShotManager snapShot = new SnapShotManager(this, CAMERA_SHOT, username);
        snapShot.executeCameraShot();
        sessionLog.writeLog(LogFlag.USER_ACTION, "Camera shot used...");
    }


    /**
     * Source: BottomBarFragment, TitleBarFragment Sets the current content pane
     * fragment
     */
    @Override
    public void setContentFragment(String path) {
        if (path.equals("CHAT_FRAGMENT")) {
            // load chat frament if results only contains 1 entry (there is only
            // 1 possible group)
            if (usergroups.isEmpty() || usergroups == null) {
                ToastMessages.longToast("You have not been assigned to any group", 24, this);
                fvf = FolderViewFragment.newInstance(path);
                buildFolderViewFragment();
            } else if (usergroups.size() == 1) {
                sessionLog.writeLog(LogFlag.USER_ACTION, "Fragment set to chat for group " );
                buildChatViewFragment(usergroups.get(0));
                sessionLog.writeLog(LogFlag.USER_ACTION, "Fragment set to chat for group " + cvf.getChatgroup());

                // else execute dialog of results
            } else {
                String[] ug = usergroups.toArray(new String[usergroups.size()]);
                Bundle bundle = new Bundle();
                Log.v("APPLICATION ACTIVITY", "CHAT GROUP ARRAY: " + Arrays.toString(ug));
                bundle.putStringArray("usergroups", ug);

                SelectChatGroupDialog dialog = new SelectChatGroupDialog();
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "fileSelectDialog");

            }
        } else {
            fvf = FolderViewFragment.newInstance(path);
            sessionLog.writeLog(LogFlag.USER_ACTION, "Fragment set to folder view on folder " + fvf.getWorkingDir());
            buildFolderViewFragment();
        }
    }

    /**
     * Build the and commit a instance of ChatViewFragment
     */
    private void buildChatViewFragment(String chatGroup) {
        cvf = ChatViewFragment.newInstance(username, chatGroup);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, cvf);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Commit the current version of FolderViewFragment
     */
    private void buildFolderViewFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, fvf);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Beggins delete process, executeing server task
     */
    @Override
    public void deleteFileTask(String filename, String workingDIR) {
        DeleteFromServerTask dst = new DeleteFromServerTask(this);
        dst.execute(filename, workingDIR, username);
    }

    @Override
    public void serverFileDeleted(ServerDeleteResponse sdr) {
        if (sdr.getResponse() == 1) {
            FileHandler.deleteFile(sdr.getFilename(), Config.getWorkingDirectory(sdr.getWorkingDIR(), this));
            updateFolderViewContents();
            sessionLog.writeLog(LogFlag.USER_ACTION, "File " + sdr.getFilename() + " deleted from " + Config.getWorkingDirectory(sdr.getWorkingDIR(), this) );
        } else {
            ToastMessages.shortToast("Error deleting", 20, this);
        }
    }

    /**
     * Dialog call to upload when a file is copied or moved
     */
    @Override
    public void fileMoveCopy(String filename, String sourceDIR, String destDIR, String mask) {
        String deviceSourcePath = Config.getWorkingDirectory(sourceDIR, this);
        String deviceDestPath = Config.getWorkingDirectory(destDIR, this);
        if(FileHandler.fileExistsInDirectory(deviceDestPath, filename)) {
            ToastMessages.shortToast("File exists in directory, cannot copy.", 16, this);
        } else {
            if(mask.equalsIgnoreCase(Config.FILE_MOVE)) {
                FileHandler.moveFile(filename, deviceSourcePath, deviceDestPath);
            } else if (mask.equalsIgnoreCase(Config.FILE_COPY)){
                FileHandler.copyFile(filename, deviceSourcePath, deviceDestPath);
            }
            FileMoveCopyTask fmt = new FileMoveCopyTask(this);
            fmt.execute(filename, sourceDIR, destDIR, mask);
        }
    }

    public void onFileMoveCopyResponse(FileMoveCopyResponse response){
        //todo handle response
    }


    /**
     * Renames a file on the server
     * @param newFilename the new file name
     * @param workingDIR the files directory
     * @param oldFilename the old file name
     */
    @Override
    public void renameServerFile(String newFilename, String workingDIR, String oldFilename){
        RenameServerFileTask rst = new RenameServerFileTask(this);
        rst.execute(newFilename,workingDIR, oldFilename, username);
        updateFolderViewContents();
    }

    /**
     * Called by application reciever on a user being added to a new group. Creates necessary directories,
     * adds the folder name to directories array and rebuilds the nav menu to include the new dir.
     * @param newGroupname the name of the group / directory
     */
    @Override
    public void updateGroupDirectory(String newGroupname){
        //Add new group to dir structure
        usergroups.add(newGroupname);
        directories.add(newGroupname);
        setGroupDirectory(newGroupname);
        //Add to spinner and nav menu
        FragmentManager fm = getFragmentManager();
        TitleBarFragment tbf = (TitleBarFragment) fm.findFragmentById(R.id.titlebar_fragment);
        tbf.updateAdapter(directories);
        buildNavMenu();

        //update chat message data
        GetUserChatMessagesTask gcm = new GetUserChatMessagesTask(this);
        gcm.execute(username);
    }

    /**
     * Updates a server file based on its name and directory
     * @param filename the file name on the server
     * @param destination the directory on the server
     */
    public void updateServerFile(String filename, String destination) {
        UploadToServerTask ust = new UploadToServerTask(this);
        ust.execute(filename, destination);
    }
//
//    /**
//     * Uploads a file to the server on a move copy
//     * Messy implementation should be rebuilt
//     * @param mask the mask for the operation, MOVE_FILE, COPY_FILE or FILE_UPLOAD
//     * @param filename the file name
//     * @param sourceDIR the source file dir
//     * @param destDIR the setination dir
//     */
//    private void uploadFileToServerTask(String mask, String filename, String sourceDIR, String destDIR) {
//        UploadToServerTask ust = new UploadToServerTask(this);
//        ust.execute(mask, filename, destDIR, sourceDIR);
//    }

    /**
     * Upload a file to server with a given directory
     */
    private void uploadFileToServerTask(String mask, String filename, String destDIR) {
        UploadToServerTask ust = new UploadToServerTask(this);
        ust.execute(mask, filename, destDIR);
    }

    /**
     * Server response to file upload
     * TO FINISH HANDLE RESPONSE
     */
    public void uploadFileToServerComplete(FileUploadResponse fur) {
        updateFolderViewContents();
    }

    /**
     * Load the relevant chat fragment
     */
    @Override
    public void executeChatFragment(String name) {
        buildChatViewFragment(name);
    }

    /**
     * Folder view listener
     */
    @Override
    public void onFileSelected(String workingDIR, String filename) {
        Bundle bundle = new Bundle();
        bundle.putString("filename", filename);
        bundle.putString("username", username);
        bundle.putString("workingDIR", workingDIR);
        FileSelectedDialog dialog = new FileSelectedDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "fileSelectDialog");
    }

    /**
     * Reset spinner value when fragment resumed
     */
    @Override
    public void setSpinnerEntry(String name) {
        FragmentManager fm = getFragmentManager();
        TitleBarFragment tbf = (TitleBarFragment) fm.findFragmentById(R.id.titlebar_fragment);
        tbf.setSpinnerEntry(name);
    }



    /**
     * Compeletion for snapshot
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                // Toast.makeText(this, "Image saved to:\n" + data.getData(),
                // Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
                Log.v("CAMERASHOT", "Image capture failed");
            }
        } else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }
        if(camerafile != null){
            Log.i("CAMERA SHIT", camerafile.getPath());
            File f = new File(camerafile.getPath());
            updateServerFile(f.getName(),"camera");
            camerafile = null;
        }
        updateFolderViewContents();

    }

    /**
     * Builds chat contents for all user groups on creation
     */
    @Override
    public void setAllChatGroupMessages(ArrayList<MessageBank> ml) {
        if (ml == null)
            return;
        messageBanks = ml;
        Log.v("APPLICATION ACTIVITY", "GROUP CHAT MESSAGES" + messageBanks.toString());
        loadSpinner.setVisibility(View.GONE);

    }

    /**
     * Commits a chat message to relevant usergroup
     */
    @Override
    public void commitChatMessage(String message, String chatGroup) {
        CommitChatMessageTask task = new CommitChatMessageTask(this);
        task.execute(username, chatGroup, message);
    }

    /**
     * Dialog construction
     */

    /**
     * OnHelpRequest listener
     */
    @Override
    public void onHelpRequested(String filename, String value, String message) {
        RequestHelpTask rht = new RequestHelpTask(this);
        rht.execute(username, filename, value, message);
        sessionLog.writeLog(LogFlag.USER_ACTION, "User requesting help for " + filename );

    }

    /**
     * Activity assit methods
     */

    /**
     * Updates the adapter of the current FolderViewFragment
     */
    public void updateFolderViewContents() {
        sessionLog.writeLog(LogFlag.SYSTEM_ACTION, "Folder view updated...");
        fvf.updateAdapter();
    }

    /**
     *
     * @param m The message to add to the bank
     */

    /**
     * Adds a given Message object to chatgroup in messageBanks with same name as chatGroup
     *
     * @param chatGroup ChatGroup to add message to
     * @param m         Message to add
     */
    public void updateMessageBanks(String chatGroup, Message m) {
        Log.i("APPLICATION ACTIVITY", "Adding message: " + m.toString());
        for (MessageBank mb : messageBanks) {
            if(mb.getChatgroup().equalsIgnoreCase(chatGroup)){
                Log.i("APPLICATION ACTIVITY", "Message: " + m.toString() + " | added to messageBank " + mb.getChatgroup());
                mb.addMessage(m);
            }
        }
        updateChatView(chatGroup);
    }

    private void updateChatView(String chatGroup){
        Log.i("APPLICATION ACTIVITY", "Updating chat view...");
        sessionLog.writeLog(LogFlag.USER_ACTION, "Chat view updated...");
        if(cvf != null && cvf.isVisible()){
            if(cvf.getChatgroup().equalsIgnoreCase(chatGroup)){
                cvf.updateAdapter();
            }
        }
    }

    /**
     *
     */
    public String getUsername() {
        return Config.getCurrentUsername(this);
    }


    /**
     * @return directories array list
     */
    @Override
    public ArrayList<String> getDirectories() {
        return directories;
    }

    /**
     * @return usergroups array list
     */
    @Override
    public ArrayList<String> getUserGroups() {
        return usergroups;
    }



    public MessageBank getMessages(String groupname) {
        Log.v("APPLICATION ACTIVITY", "BUILDING MESSAGES" + " Group name search: " + groupname);

        for (MessageBank ml : messageBanks) {
            if (groupname.equalsIgnoreCase(ml.getChatgroup())) {
                return ml;
            }
        }
        return null;
    }

    @Override
    public void storeFileInfo(FileData fd){
        openedFile = fd;
    }

    @Override
    public SessionLog getSessionLog(){
        return sessionLog;
    }


    /**
     * Checks if a service is running
     *
     * @param serviceClass
     * @return true if service is active otherwise false
     */

    @Override
    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * onResume for app, ensure google play services reactivates
     */
    @Override
    public void onResume() {
        super.onResume();
        sessionLog.writeLog(LogFlag.NOTIFICATION, "Returning from onResume" );
        Log.e("ON RESUME", "HITTING ON RESUME ");
        checkPlayServices();
        if(!(openedFile == null)){
            Log.i("ON RESUME", "openedFile not null, file details: " + openedFile.getName() + " | " + openedFile.getSize() + " | " + openedFile.getDir());
            //if opened file is not null, we have a file stored call, check to see if it has changed
            //compared to local version modified time
            //Get local file of same name / location
            File localFile = new File(Config.getWorkingDirectory(openedFile.getDir(), this), openedFile.getName());
            //Check local file exists
            if(localFile != null){
                Log.i("ON RESUME","Local and stored file details exist");
                        //if the localFile is newer and not the same size it has been updated
                if(localFile.lastModified() > openedFile.lastModified() && localFile.length() != openedFile.getSize()){
                    //upload the new file to the server
                    Log.i("ON RESUME","Files differ, updating server");
                            updateServerFile(localFile.getName(), openedFile.getDir());
                } else {
                    Log.i("ON RESUME","File not changed details the same, do nothing");
                }
            }
            //finish call and set to null
            openedFile = null;
        } else {
            Log.i("ON RESUME", "No file changed");
            if (!isMyServiceRunning(FullFileSyncService.class)) {
                startFileSyncService();
                Log.e("ON RESUME", "Starting file monitor sync");
            }

        }

    }

    /**
     * Reciever class for the activity. Used by services for callback
     *
     */

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("APPLICATION ACTIVITY", "DESTROYING!!");
        sessionLog.writeLog(LogFlag.SYSTEM_ACTION, "Application destroying..." );
        //delete temp files
        try {
            onLogout();
        } catch (Exception e) {
            Log.w("APPLICATION ACTIVITY", "Logout failed. May have already happened");
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        sessionLog.writeLog(LogFlag.SYSTEM_ACTION, "Application paused..." );
        Log.e("ON PAUSE", "HITTING ON PAUSE ");
    }

    private class MenuItemSelectListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id){
            setAction(position);
        }

        private void setAction(int position){
            String sel = menuItems.get(position).getName();
            if(sel.equalsIgnoreCase("Group Chat")){
                setContentFragment("CHAT_FRAGMENT");
            } else if (sel.equalsIgnoreCase("Camera Shot")) {
                onCameraClick();
            } else if (sel.equalsIgnoreCase("New File...")){
                SelectNewFileDir dialog = new SelectNewFileDir();
                dialog.show(getSupportFragmentManager(), "selectFileDirDialog");
            } else {
                for(String s : directories){
                    if(s.equalsIgnoreCase(sel)){
                        setContentFragment(sel);
                        break;
                    }
                }
            }
            menuSelect.closeDrawer(menuItemsList);
        }
    }

}
