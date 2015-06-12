package handlers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.classroom.applicationactivity.ApplicationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import helper.Message;
import util.Config;
import util.LogFlag;
import util.MessageBank;
import util.SessionLog;
import util.ToastMessages;

/**
 * Created by JACK on 3/30/2015.
 */


public class ApplicationReceiver extends BroadcastReceiver {

    private ApplicationReceiverInterface mCallback;
    private SessionLog sessionLog;

    //Tags
    public static final String FILE_CREATED = "file_created";
    public static final String FILE_MOVED_TO = "file_moved_to";
    public static final String FILE_DELETED = "file_deleted";
    public static final String FILE_UPDATED = "file_updated";


    //Class vars
    private String workingDIR;
    private String result;
    private String filename;
    private Context context;

    public ApplicationReceiver (Activity activity){
        mCallback = (ApplicationReceiverInterface) activity;
        sessionLog = mCallback.getSessionLog();
    }


    /**
     * OnRevieve result is null if call is from any other broadcasts
     * Accessed by synchronize intent and file observers Synchronize service
     * fires a blind broadcast to call an adapter update for when new files
     * have been added
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        result = intent.getExtras().getString("result");

        if (result != null) {
            //FileObserver actions
            if (result.equalsIgnoreCase(FILE_CREATED) || result.equalsIgnoreCase(FILE_UPDATED)
                    ||result.equalsIgnoreCase(FILE_MOVED_TO) || result.equalsIgnoreCase(FILE_DELETED) ) {
                handleFileChange(intent);
            } else if (result.equals("gcm_message")){
                processGcmMessage(intent);
            }
        } else {
            handleBlindFileDownload(intent);
        }
        Log.v("APPLICATION RECEIVER", "Updating folderViewFragment adapter");

    }

    /**
     * Called by file observer service actions
     * @param intent action intent
     */
    private void handleFileChange(Intent intent){
//        if (result.equalsIgnoreCase(FILE_CREATED) ) {
//            Log.v("FILE OBSERVE RECEIVER", "File created");
//        } else if (result.equalsIgnoreCase(FILE_UPDATED)) {
//            // Log.v("FILE OBSERVE RECEIVER", "File updated");
//            //mCallback.updateServerFile(intent.getExtras().getString("filename"), intent.getExtras().getString("dir"));
//        } else if (result.equalsIgnoreCase(FILE_MOVED_TO)) {
//            Log.v("FILE OBSERVE RECEIVER", "File moved");
//        } else if (result.equalsIgnoreCase(FILE_DELETED)) {
//            Log.v("FILE OBSERVE RECEIVER", "File deleted, removing from server");
//            //mCallback.updateFolderViewContents();
//            // deleteLocalAndRemoteFile(filename, workingDIR);
//        }
    }

    /**
     * Process GCM push messages
     * @param intent message intent
     */
    private void processGcmMessage(Intent intent) {
        JSONObject jobj;
        try {
            String message = intent.getExtras().getString("message");
            jobj = new JSONObject(message);
            /*
            Chat message added to member group, update messages
             */
            if (jobj.getString("tag").equals("message_update")) {
                Log.i("APPLICATION RECEIVER: ", "handle message: " + message);
                //TODO handle chat message update call
                sessionLog.writeLog(LogFlag.SYSTEM_ACTION, "Push Notification update chat messages...");
                mCallback.updateMessageBanks(jobj.getString("chatgroup"), Message.formatNewMessage(message));
                ToastMessages.longToast("PUSH NOTIFICATION - Chat Message", 20, context);
                //update mesage view
            /*
            Shared file uploaded to server, update local copy
             */
            } else {
                if (jobj.getString("tag").equals("server_file_changed")) {
                    Log.i("APPLICATION RECEIVER", "Push Notification check file version on " + jobj.getString("filename") + " ...");
                    sessionLog.writeLog(LogFlag.SYSTEM_ACTION, "Push Notification check file version on " + jobj.getString("filename") + " ...");
                    //Get local copy of file
                    File file = new File(Config.getWorkingDirectory(jobj.getString("workingDIR"), context), jobj.getString("filename"));
                /*
                if file is empty, we have no local copy, download it OR if the filesizes do not match (only the origional source will match)
                download it
                 */
                    Log.i("APPLICATION RECEIVER", "File comparison local: " + file.getName() + " | " + file.length() +
                            "\nRemote: " + jobj.getString("filename") + " | " + jobj.getString("filesize"));
                    if (file == null || Long.valueOf(jobj.getString("filesize")) == -1 || file.length() != Long.valueOf(jobj.getString("filesize"))) {
                        Log.i("APPLICATION RECEIVER", "File was null or not match, downloading");
                        ToastMessages.longToast("PUSH NOTIFICATION - File upload called, file " + jobj.getString("filename") + " called for update", 20, context);
                        FileHandler.downloadFile(jobj.getString("filename"), jobj.getString("workingDIR"), context);
                    } else if(file.length() == 0 && Long.valueOf(jobj.getString("filesize")) == 0){
                        try {
                            file.createNewFile();
                        } catch (IOException e){
                            e.printStackTrace();
                            ToastMessages.shortToast("There was a problem with a file copy. Relogging should fix the problem.", 16, context);
                        }
                    } else {
                        ToastMessages.longToast("PUSH NOTIFICATION - File upload called, file " + jobj.getString("filename") + " local version match no download", 20, context);
                    }
                    mCallback.updateFolderViewContents();
            /*
            Server file deleted, delete local version
             */
                } else if (jobj.getString("tag").equals("delete_file")) {
                    sessionLog.writeLog(LogFlag.SYSTEM_ACTION, "Push Notification group file deleted delete local version of " + jobj.getString("filename") + " ...");
                    FileHandler.deleteFile(jobj.getString("filename"), Config.getWorkingDirectory(jobj.getString("workingDIR"), context));
                    ToastMessages.longToast("PUSH NOTIFICATION - delete group file", 20, context);
            /*
            Server file renamed, rename local copy
             */
                } else if (jobj.getString("tag").equals("rename_file")) {
                    sessionLog.writeLog(LogFlag.SYSTEM_ACTION, "Push Notification file renamed in group rename local " + jobj.getString("old_filename") + " to " + jobj.getString("new_filename") + " ...");
                    FileHandler.renameFile(jobj.getString("new_filename"), Config.getWorkingDirectory(jobj.getString("workingDIR"), context), jobj.getString("old_filename"));
                    ToastMessages.longToast("PUSH NOTIFICATION - rename group file", 20, context);
            /*
            User added to a group, update local directories
             */
                } else if (jobj.getString("tag").equalsIgnoreCase("added_to_group")) {
                    mCallback.updateGroupDirectory(jobj.getString("group_name"));
                    ToastMessages.longToast("You have been added to the group " + jobj.getString("group_name"), 20, context);
            /*
            Unknown push message received, take no action
             */
                } else {
                    ToastMessages.longToast("PUSH NOTIFICATION - UNKNOWN MESSAGE", 20, context);

                }
            }
        } catch (JSONException jex) {
            jex.printStackTrace();
        }
        mCallback.updateFolderViewContents();
    }


    /**
     * A file was downloaded blind with no id, update folder contents to reflect
     * @param intent action intent
     */
    private void handleBlindFileDownload(Intent intent){
        Log.v("DOWNLOAD RECEIVER", "File downloaded updating adapter");
        if(workingDIR == null) workingDIR = mCallback.getUsername();
        mCallback.updateFolderViewContents();

    }


}
