package handlers;

import java.io.File;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import util.Config;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

/**
 * Static class that handles file actions for the user (copy, move, delete, exists, renamne, upload, download) 
 * and relevant assit methods
 * @author JACK
 *
 */



public class FileHandler {

    protected FileHandler() { }
	
/*
 * 													Callable / Actionable static methods
 */


    public static final String FILE_UPLOAD_TAG = "file_upload";
    public static final String FILE_CHECK_TAG = "filecheck";
    /**
     * Copies a file from one location to another
     * @param filename the file name
     * @param fromPath path the file is to be copied from
     * @param toPath path the file is to be copied to
     */
    public static void copyFile(String filename, String fromPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            //create output directory if it doesn't exist
            File dir = new File (toPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
            in = new FileInputStream(fromPath + File.separator + filename);
            out = new FileOutputStream(toPath + File.separator + filename);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            // write the output file
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("FILEHANDLER", "copyFile - Error in copy: " + e.getMessage());
        }

    }

    public static void createFile(String filename, String workingDIR, Context context){
        String path = Config.getWorkingDirectory(workingDIR, context);
        File f = new File(path, filename);
        try {
            f.createNewFile();
        } catch (IOException e){
            Log.e("FILE HANDLER", "Error creating new file");
        }
    }

    /**
     * Creates a version of the file in the same directory with _BK appended to show a backup file
     * @param filename The name of the file to copy
     * @param workingDIR the directory the file exist in
     * @param context the application context
     */
    public static void createTempFile(String filename, String workingDIR, Context context){
        String absPath = Config.getWorkingDirectory(workingDIR, context);
        String tmpFilename = filename + "_BK";
        FileHandler.renameFile(filename, absPath, tmpFilename);
    }


    /**
     * Deletes a file from the provided path
     * @param filename the name of file to be deleted
     * @param filePath the path of the file to be deleted
     */
    public static void deleteFile(String filename, String filePath) {
        try {
            // delete the original file
            new File(filePath + File.separator + filename).delete();
        } catch (Exception e) {
            Log.e("FILEHANDLER", "deleteFile - Error in delete: " + e.getMessage());
        }
    }

    /**
     * Delete temporary files. Currently removes with ~ as the last character. Modify to address other
     * types
     * @param workingDIR the directory to address
     * @param context application context
     */
    public static void deleteTempFiles(String workingDIR, Context context){
        Log.e("FILE HANDLER", "DELETING TEMP FILES...");
        String[] files;
        File dir = new File(Config.getWorkingDirectory(workingDIR, context));
        files = dir.list();
        for(int i = 0; i < files.length; i++){
            Log.e("FILE HANDLER", "DELETING TEMP FILES, IN LOOP...Filename: " + files[i]);
            if(files[i].substring(files[i].length()-1).equalsIgnoreCase("~")){
                Log.e("FILE HANDLER", "IF STATEMENT. Checking " + files[i]);
                Log.e("FILE HANDLER", "Compare: " + files[i].substring(files[i].length()-1) + " to ~");
                deleteFile(files[i], Config.getWorkingDirectory(workingDIR, context));
                i--;
            }
        }
    }

    /**
     * Checks if file exists within the provided directory
     * @param path the directory path of the file to be checked
     * @param filename the name of the file to be checked
     * @return
     */
    public static boolean fileExistsInDirectory(String path, String filename){
        File f = new File(path,filename);
        return f.isFile();

    }

    /**
     * Moves a file from the provided fromPath to the toPath
     * @param filename the name of the file to be moved
     * @param fromPath the path the file is moved from
     * @param toPath the path the file is moved to
     */
    public static void moveFile(String filename, String fromPath, String toPath){
        File from = new File(fromPath, filename);
        File to = new File(toPath, filename);
        from.renameTo(to);
    }

    /**
     * Renames a file
     * @param oldFilename the file to be renamed
     * @param filePath the path of the file to be renamed
     * @param newFilename the new name for the file
     */
    public static void renameFile(String newFilename, String filePath, String oldFilename){
        File currentFile = new File(filePath,oldFilename);
        File newFile = new File(filePath,newFilename);
        Log.e("FILE RENAME", "Renaming " + oldFilename + " to " + newFilename);
        currentFile.renameTo(newFile);
    }

    /**
     * Uploads a provided file and path to remote server
     * @param filename the name of the file to be uploaded
     * @param workingDIR the directory the file belongs to (working DIR's are shared across server and local device)
     * @param context application context
     * @return JSON object of the result of the server file upload operation
     */
    public static MultipartEntityBuilder bundleFileUpload(String filename, String workingDIR, Context context){
        String filepath = Config.getWorkingDirectory(workingDIR, context);
        Log.i("FILEHANDLER", "uploadFile - Uploading File: " + filename);
        //Build post content. Use multipartentitybuilder from Apache as post needs file and post data
        File file = new File(filepath, filename);
        FileBody fb = new FileBody(file);
        StringBody tag = new StringBody(FILE_UPLOAD_TAG, ContentType.MULTIPART_FORM_DATA);
        StringBody username = new StringBody(Config.getCurrentUsername(context), ContentType.MULTIPART_FORM_DATA);
        StringBody dir = new StringBody(workingDIR, ContentType.MULTIPART_FORM_DATA);
        Log.i("FILEHANDLER", "uploadFile verify: file from " + file.getAbsolutePath() + ", size: " + file.length());
        if (!file.exists()) {
            Log.i("FILEHANDLER", "uploadFile: Error file not exists");
        }
        //Create connection and mpe
        MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
        mpEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mpEntity.addPart("file", fb);
        mpEntity.addPart("tag", tag);
        mpEntity.addPart("username", username);
        mpEntity.addPart("dir", dir);
        return mpEntity;
    }

    /**
     * Downloads a file from server. If the file already exists will attempt to delete, however if this fails
     * file will download with an alternate name and may trigger a new round of download / upload actions
     * @param filename filename to be downloaded
     * @param workingDIR the working directory (a group name, the username or a camera directory)
     * @param context application context
     */
    public static void downloadFile(String filename, String workingDIR, Context context){
        Log.i("FILEHANDLER", "Commiting download..");
        String destinationpath = getDestinationPath(context, workingDIR);
        String url = getDownloadURL(context, workingDIR, filename);
        Log.i("FILEHANDLER", "Attempt to access " + url);
        DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        // Log.i("FILE TRANSFER TASK", "Commiting destination " + filename +
        // " to local storage " + dest);
        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(File.separator + destinationpath, filename);
        request.allowScanningByMediaScanner();
        //Need to delete the current file if it already exists - if it open it will download as a rename file and bounce itself back to the ser
        // this may or may not need to be addressed
        checkFile(context, workingDIR, filename);
        long downloadID = downloadmanager.enqueue(request);
    }
	
	
	
	
	
	/*
	 *                                     Private assister methods for FileHandling
	 */


    /**
     * Check if file exists prior to download and deletes it if it is. Does nto check for error handling if file is open, and if it is
     * delete will not occur
     * @param context Application context
     * @param workingDIR Current working directory
     * @param filename filename to be checked
     */
    private static void checkFile(Context context, String workingDIR, String filename){
        String dir;
        if(workingDIR.equalsIgnoreCase("camera")){
            dir = Config.getCameraDirectoryRoot(Config.getCurrentUsername(context));
        } else {
            dir = Config.getWorkingDirectory(workingDIR, context);
        }
        deleteFile(filename, dir);
    }

    private static String getDestinationPath(Context context, String workingDIR){
        if (workingDIR.equalsIgnoreCase("camera")) {
            return Config.getCurrentUsername(context) + File.separator + "Camera";
        } else {
            return workingDIR;
        }
    }

    private static String getDownloadURL(Context context, String workingDIR, String filename){
        if (workingDIR.equalsIgnoreCase("camera")) {
            return  Config.SERVER_USER_ROOT_DIR + Config.getCurrentUsername(context) + File.separator + "Camera" + File.separator + filename;
        } else {
            return  Config.SERVER_USER_ROOT_DIR + workingDIR + File.separator + filename;
        }
    }

}
