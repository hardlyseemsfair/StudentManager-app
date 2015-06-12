package services;

import android.content.Context;
import android.content.Intent;
import android.os.FileObserver;


import android.util.Log;

import handlers.ApplicationReceiver;

public class DirectoryObserver extends FileObserver {
    private String absolutePath;
    private String workingDIR;
    private Context context;

    static final int mask = (FileObserver.CREATE |
            FileObserver.DELETE |
            FileObserver.DELETE_SELF |
            FileObserver.MODIFY |
            FileObserver.MOVED_FROM |
            FileObserver.MOVED_TO |
            FileObserver.MOVE_SELF);


    public DirectoryObserver(Context context, String path, String dirName) {
        super(path, FileObserver.ALL_EVENTS);
        absolutePath = path;
        this.workingDIR = dirName;
        this.context = context;
    }

    @Override
    public void onEvent(int event, String filename) {
        //Log.v("FILE OBSERVE", "EVENT TRIGGERED: " + event);
        if (filename == null) { // path is the name of the file...
            return;
        }
        // a new file or subdirectory was created under the monitored directory
        if ((FileObserver.CREATE & event) != 0) {
            // TODO Nothing... yet
            Log.v("DIR OBSERVER", "Observer: " + workingDIR + " CREATED FILE for: " + filename);
            broadcastIntent(ApplicationReceiver.FILE_CREATED, filename);
        }
        // a file or directory was opened
        if ((FileObserver.OPEN & event) != 0) {
            // TODO Nothing... yet
            //Log.v("DIR OBSERVER", "Observer: " + name + " OPEN FILE for: " + path);
        }
        // data was read from a file
        if ((FileObserver.ACCESS & event) != 0) {
            // TODO Nothing... yet
            //Log.v("DIR OBSERVER", "Observer: " + name + " READING FILE for: " + path);
        }
        // data was written to a file
        if ((FileObserver.MODIFY & event) != 0) {
            // TODO Nothing... yet
            Log.v("DIR OBSERVER", "Observer: " + workingDIR + " SAVED NEW DATA for: " + filename);
            broadcastIntent(ApplicationReceiver.FILE_UPDATED, filename);
        }
        // someone has a file or directory open read-only, and closed it
        if ((FileObserver.CLOSE_NOWRITE & event) != 0) {
            // TODO Nothing... yet
            //Log.v("DIR OBSERVER", "Observer: " + name + " SAVED NO WRITE for: " + path);
        }
        // someone has a file or directory open for writing, and closed it
        if ((FileObserver.CLOSE_WRITE & event) != 0) {
            // TODO Nothing... yet
            //broadcastIntent(ApplicationReciever.FILE_UPDATED, filename);
        }
        // [todo: consider combine this one with one below]
        // a file was deleted from the monitored directory
        if ((FileObserver.DELETE & event) != 0) {
            // TODO Remove file from the server
            Log.v("DIR OBSERVER", "Observer: " + workingDIR + " DELETED for: " + filename);
            broadcastIntent(ApplicationReceiver.FILE_DELETED, filename);
        }
        // the monitored file or directory was deleted, monitoring effectively
        // stops
        if ((FileObserver.DELETE_SELF & event) != 0) {
            // TODO Toast an error, recreate the folder, resync and restart
            // monitoring
        }
        // a file or subdirectory was moved from the monitored directory
        if ((FileObserver.MOVED_FROM & event) != 0) {
            // TODO Delete from the server
            //Log.v("DIR OBSERVER", "Observer: " + name + " MOVED FROM FILE for: " + path);
        }
        // a file or subdirectory was moved to the monitored directory
        if ((FileObserver.MOVED_TO & event) != 0) {
            // TODO Nothing... yet
            Log.v("DIR OBSERVER", "Observer: " + workingDIR + " MOVED TO FILE for: " + filename);
            //broadcastIntent(ApplicationReciever.FILE_MOVED_TO, filename);
        }
        // the monitored file or directory was moved; monitoring continues
        if ((FileObserver.MOVE_SELF & event) != 0) {
            // TODO Recreate the folder and show toast
            //Log.v("DIR OBSERVER", "Observer: " + name + " MOVED SELF for: " + path);
        }
        // Metadata (permissions, owner, timestamp) was changed explicitly
        if ((FileObserver.ATTRIB & event) != 0) {
            // TODO Nothing... Yet
        }
    }

    private void broadcastIntent(String flag, String filename){
        Intent i = new Intent("com.classroom.applicationactivity.USER_ACTION");
        i.putExtra("result", flag);
        i.putExtra("filename", filename);
        i.putExtra("dir", workingDIR);
        context.sendBroadcast(i);
    }

    public String getWorkingDIR(){
        return this.workingDIR;
    }

    public String getPath(){
        return this.absolutePath;
    }
}
