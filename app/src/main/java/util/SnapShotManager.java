package util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.classroom.applicationactivity.ApplicationActivity;


/**
 * Handles camera operations for snap shots
 * @author JACK
 *
 */
public class SnapShotManager {


    //Instance variables
    private String username;
    private Activity activity;
    private ApplicationActivity aa;


    //Instance Tags
    private static final int CAMERA_SHOT = 1;
    private static final int VIDEO_SHOT = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private static final String IMAGE = ".jpg";
    private static final String VIDEO = ".mp4";


    /**
     * Constructor
     * @param activity application activity
     * @param sentinel flag for camera or video action
     * @param username current username
     */
    public SnapShotManager(Activity activity, int sentinel, String username) {
        this.activity = activity;
        aa = (ApplicationActivity) activity;
        this.username = username;
        switch (sentinel) {
            case CAMERA_SHOT:
                executeCameraShot();
                break;
            case VIDEO_SHOT:
                executeVideoShot();
                break;
        }
    }


    /**
     * Fire intent for camera use and store the
     */
    public void executeCameraShot() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(activity.getPackageManager()) != null) {
            File image = null;
            Uri fileUri = null;
            try{
                fileUri = Uri.fromFile(createStoreFile(IMAGE));
            } catch (IOException e){
                //Failed to create storage file
                Log.v("CAMERASHOT","Failed to create storage file");
            }
            if(fileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                aa.setCameraFile(fileUri);
                activity.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }

    }

    /**
     * Call for video and store
     */
    public void executeVideoShot() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(intent.resolveActivity(activity.getPackageManager()) != null) {
            File video = null;
            try{
                video = createStoreFile(IMAGE);
            } catch (IOException e){
                //Failed to create storage file
            }
            if(video != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(video));
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                activity.startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    /**
     * Create image / video file
     * @param prefix
     * @return file
     * @throws IOException
     */
    private File createStoreFile(String prefix) throws IOException{
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = username + "_" + timestamp + ".jpg";
        File mediaFile;
        mediaFile = new File(Config.getUserDirectoryRoot(username) + File.separator + "Camera", filename);
        return mediaFile;
    }


}
