package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import helper.FileData;
import handlers.ServerRequestHandler;

import android.content.Context;
import android.util.Log;

public class FileCollection implements Iterable<FileData>{

    private Context context;
    private String workingDIR;
    private ArrayList<FileData> files;


    public FileCollection(Context context, String workingDIR){
        this.workingDIR = workingDIR;
        files = new ArrayList<>();
        this.context = context;
    }


    public  void buildLocalCollection(){
        String path = Config.getWorkingDirectory(workingDIR, context);
        File dirpath = new File(path);
        String[] filenames = dirpath.list();
        if(filenames != null) {
            for (int i = 0; i < filenames.length; i++) {
                File f = new File(dirpath, filenames[i]);
                if (!f.isDirectory())
                    files.add(new FileData(f.getName(), f.length(), f.lastModified()));
            }
        }
    }

    public void buildRemoteCollection(String username){
        JSONObject json = ServerRequestHandler.getDirFileList(username, workingDIR);
        int responseCode = 0;
        // check for response
        try {
            if (json.getString(Config.KEY_SUCCESS) != null) {
                Log.i("PROCESS", "Processing JSON string");
                String res = json.getString(Config.KEY_SUCCESS);
                if (Integer.parseInt(res) == 1) {
                    // Everything good, build the message list
                    // for each group create an array
                    try {
                        JSONArray ja = json.getJSONArray(workingDIR);
                        if (ja != null) {
                            // if that array is not null
                            int len = ja.length();
                            for (int i = 0; i < len; i++) {
                                FileData fd = getFileDataFromString(ja.getString(i));
                                //fd is null if string is ., .. or camera
                                if(fd != null) add(fd);
                            }
                        }
                    } catch (JSONException e) {
                        Log.v("FILE SYNC ERROR", "Error in result " );
                        e.printStackTrace();
                    }
                } else {
                    // Error in process
                    Log.i("FILE SYNC ERROR", "Error in server filelist");
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("INTENT SERVICE", "Remote files built");
    }


    private FileData getFileDataFromString(String s){
        String[] parts = s.split("::");
        if(parts[0].equalsIgnoreCase(".") || parts[0].equalsIgnoreCase("..") || parts[0].equalsIgnoreCase("camera") ) return null;
        String name = parts[0];
        long length = Long.valueOf(parts[1]);
        long lastModified = Long.valueOf(parts[2]) * 1000;
        return new FileData(name, length, lastModified);
    }

    public boolean fileExistsInCollection(FileData f){
        for(FileData file : files){
            if(f.getName().equalsIgnoreCase(file.getName())){
                return true;
            }
        }
        return false;
    }


    public void add(FileData f){
        files.add(f);
    }

    public FileData get(int i){
        return files.get(i);
    }

    public Iterator<FileData> iterator(){
        return files.iterator();
    }

    public int size(){
        return files.size();
    }

    public boolean isEmpty(){
        if(files.isEmpty() || files == null){
            return true;
        } else {
            return false;
        }
    }


    public String getWorkingDIR() {
        return workingDIR;
    }


    public void setWorkingDIR(String workingDIR) {
        this.workingDIR = workingDIR;
    }


    public ArrayList<FileData> getFiles() {
        return files;
    }


    public void setFiles(ArrayList<FileData> files) {
        this.files = files;
    }

    @Override
    public String toString(){
        return files.toString();
    }



}
