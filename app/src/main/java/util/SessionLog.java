package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


import handlers.UserDBHandler;

public class SessionLog {




    private File file;




    public SessionLog(String uname) {
        String username = uname;
        String userDIR = buildUserLogDir(username);

        String filename = System.currentTimeMillis() + "_" + username + ".slog";
        file = new File(userDIR, filename);
        Log.v("LOG FILE", "log file: " + filename + " created in " + userDIR);
    }

    public void startLog(){
        writeLog(LogFlag.NOTIFICATION, "Log started");
    }

    public void writeLog(LogFlag marker, String detail) {
        try {
            FileWriter filewriter = new FileWriter(file, true);
            filewriter.write(System.currentTimeMillis() + "::" + marker.name() + "::" + detail);
            filewriter.write(System.getProperty("line.separator"));
            filewriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("LOG FILE", "Error writing file...");
        }
    }


    private  String buildUserLogDir(String username){
        String  path = Environment.getExternalStorageDirectory().toString() + File.separator + "applogs" + File.separator + username;
        Log.e("SESSION LOG", "Building logs dir..");
        File file = new File(path);
        if(!file.isDirectory()){
            Log.e("SESSION LOG", "Building user logs dir..");
            file.mkdirs();
        } else {
            Log.e("SESSION LOG", "Logs dir exists..");
        }
        return path;
    }

    public void clearUserLogs(Context context){
        UserDBHandler userDBhandler = new UserDBHandler(context);
        String username = userDBhandler.getUserName();
        String userDIR = context.getFilesDir() + File.separator + username;
        File dir = new File(userDIR);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }

    public void uploadLog(){

    }


}
