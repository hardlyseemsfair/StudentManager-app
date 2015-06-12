package services;

import java.util.ArrayList;

import util.Config;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class FileMonitorService extends Service {

    private ArrayList<String> directories;
    private ArrayList<DirectoryObserver> directoryObservers;

    public FileMonitorService() {}

    @Override
    public int onStartCommand(Intent workIntent, int flags, int startID) {
        if( workIntent != null) {
            directories = workIntent.getStringArrayListExtra("directories");
            Log.v("SERVICE STARTED", "DATA: " + directories.toString());
            buildObservers();
        } else {
            Log.v("MONITOR SERVICE", "null data to observe");
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    private void buildObservers() {
        directoryObservers = new ArrayList<>();
        for (String dir : directories) {
            if (!dir.equalsIgnoreCase("camera")) {
                DirectoryObserver dirob = new DirectoryObserver(this, Config.getWorkingDirectory(dir, this), dir);
                directoryObservers.add(dirob);
            } else {
                DirectoryObserver dirob = new DirectoryObserver(this, Config.getCameraDirectoryRoot(Config.getCurrentUsername(getApplication())), dir);
                directoryObservers.add(dirob);
                directoryObservers.add(dirob);
            }
        }
        for(DirectoryObserver obs : directoryObservers){
            obs.startWatching();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.v("FILE MONITOR SERVICE", "Destroying self...");
        for(DirectoryObserver obs : directoryObservers){
            obs.stopWatching();
        }
    }


}
