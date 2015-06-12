package services;

import java.util.ArrayList;

import handlers.FileHandler;
import handlers.ServerRequestHandler;
import util.Config;
import util.FileCollection;
import util.FileSyncCollection;
import util.FileSyncCollection.SyncObject;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class FullFileSyncService extends IntentService{


    public FullFileSyncService() {
        super("FullFileSyncService");
    }


    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        Log.v("SYNC INTENT SERVICE", "Starting File Sync service...");
        ArrayList<String> directories = workIntent.getStringArrayListExtra("dirs");
        Log.i("FILE SYNC SERVICE", "Looking at directories: " + directories);
        for(String dir : directories){
            Log.i("FILE SYNC SERVICE", "Looking at dir: " + dir);
            syncFolder(dir);
        }
    }

    private void syncFolder(String dir){
        FileCollection localFiles = new FileCollection(this, dir);
        localFiles.buildLocalCollection();
        FileCollection serverFiles = new FileCollection(this, dir);
        serverFiles.buildRemoteCollection(Config.getCurrentUsername(this));
        FileSyncCollection fsc = new FileSyncCollection(dir, serverFiles, localFiles);
        fsc.buildSyncCollection();
        Log.v("SYNC SERVICE RESULT", "DIR: " + fsc.getWorkingDir() + ", Sync Files: " + fsc.toString());
        ArrayList<SyncObject> soa = fsc.getSyncObjectsArray();
        for(SyncObject so : soa){
            if(so.getFlag().equalsIgnoreCase(Config.SEND_TO_DEVICE)){
                FileHandler.downloadFile(so.getName(), so.getSourceDIR(), this);
            } else if (so.getFlag().equalsIgnoreCase(Config.SEND_TO_SERVER)){
                Log.v("SYNC SERVICE","Upload FIle: " + so.getName());
                ServerRequestHandler.uploadFileToServer(so.getName(), so.getSourceDIR(), this);

            }
        }
    }


}
