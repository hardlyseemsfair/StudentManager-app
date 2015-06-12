package handlers;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.content.Context;
import android.util.Log;

import util.Config;



/**
 * Handles server requests from the device, methods of this class should resolve to a usable JSON object for interpretation
 * @author JACK
 *
 */


public class ServerRequestHandler {

    // constructor
    protected ServerRequestHandler(){    }

    /**
     *   					Server requests. Should return a valid JSON response 
     */

    private String CONNECT_IP;

    public static final String REGISTER_USER_TAG = "register";
    public static final String LOGIN_TAG = "login";
    public static final String DELETE_FILE_TAG = "file_delete";
    public static final String FILE_UPLOAD_TAG = "file_upload";
    public static final String FILE_CHECK_TAG = "filecheck";
    public static final String USER_GROUP_CHECK_TAG = "get_user_groups";
    public static final String USER_HELP_REQUEST_TAG = "user_help_request";
    public static final String REGISTER_DEVICE_TAG = "register_device";
    public static final String COMMIT_MESSAGE_TAG = "commit_message";
    public static final String GET_MESSAGE_TAG = "get_group_message";
    public static final String RENAME_FILE_TAG = "rename_file";
    public static final String GET_DIR_FILE_LIST = "get_dir_file_list";
    public static final String MOVE_COPY_SERVER_FILE = "move_copy_server_file";


    /**
     * Commits a new chat message to the server
     * @param username the username committing the change
     * @param chatgroup the chatgroup the message belongs to
     * @param message the message to commit
     * @return response JSON
     */
    public static JSONObject commitChatMessage(String username, String chatgroup, String message){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", COMMIT_MESSAGE_TAG));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("chatgroup", chatgroup));
        params.add(new BasicNameValuePair("chatmessage", message));
        return JSONParser.getJSONFromUrl(Config.CONNECT_IP, params);
    }


    /**
     * Commits a help request to the server and returns a confirmation / error JSON
     * @param username username of the commit
     * @param filename the file the help request references
     * @param rating a rating for the level of help from 1 to 10
     * @param message any associated message provided for the help request
     * @return response JSON
     */
    public static JSONObject commitUserHelpRequest(String username, String filename, String rating, String message){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", USER_HELP_REQUEST_TAG));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("filename", filename));
        params.add(new BasicNameValuePair("rating", rating));
        params.add(new BasicNameValuePair("message", message));
        return JSONParser.getJSONFromUrl(Config.CONNECT_IP, params);
    }

    public static JSONObject deleteFileFromServer(String workingDIR, String filename, String username){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", DELETE_FILE_TAG));
        params.add(new BasicNameValuePair("filename", filename));
        params.add(new BasicNameValuePair("dir", workingDIR));
        params.add(new BasicNameValuePair("username", username));
        return JSONParser.getJSONFromUrl(Config.CONNECT_IP, params);
    }

    /**
     * Gets a json of files in the working directory on the server
     * @param username current user username
     * @param workingDIR the directory to be inspected
     * @return response JSON
     */
    public static JSONObject getDirFileList(String username, String workingDIR){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", GET_DIR_FILE_LIST));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("dir", workingDIR));
        return JSONParser.getJSONFromUrl(Config.CONNECT_IP, params);
    }


    /**
     * Retrieve any groups that the provided username belongs to
     * @param username proovided username
     * @return response JSON
     */
    public static JSONObject getUserGroups(String username){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", USER_GROUP_CHECK_TAG));
        params.add(new BasicNameValuePair("username", username));
        return JSONParser.getJSONFromUrl(Config.CONNECT_IP, params);
    }

    /**
     * Returns a JSON of messages for groups the user is a member of. Should not be used after initial startup due to overhead
     * @param username username to get messages for
     * @return response JSON of messages
     */
    public static JSONObject getUserMessages(String username){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", GET_MESSAGE_TAG));
        params.add(new BasicNameValuePair("username", username));
        return JSONParser.getJSONFromUrl(Config.CONNECT_IP, params);
    }

    /**
     * Login user to server
     * @param username provided username
     * @param password proved password
     * @return response JSON
     */
    public static JSONObject loginUser(String username, String password){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", LOGIN_TAG));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        return JSONParser.getJSONFromUrl(Config.CONNECT_IP, params);
    }


    /**
     * Registers a device to the user using a provided device ID and any current GCM ID to allow server to respond. 
     * Returns a succesful JSON containing of any groups the user belongs to. Should only be called on startup to ensure device
     * registration is current. 
     * @param username username to register
     * @param deviceID the android secure device ID
     * @param gcmID the current GCM ID 
     * @return response JSON for success / failure and any groups the user belongs to
     */
    public static JSONObject registerDeviceAndGetGroups(String username, String deviceID, String gcmID){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", REGISTER_DEVICE_TAG));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("deviceID", deviceID));
        params.add(new BasicNameValuePair("gcmID", gcmID));
        return JSONParser.getJSONFromUrl(Config.CONNECT_IP, params);
    }


    /**
     * Register a new user with the server
     * @param firstname users first name
     * @param lastname users last name
     * @param username users prefered username
     * @param password users prefered password
     * @return response JSON
     */
    public static JSONObject registerUser(String firstname, String lastname, String username, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", REGISTER_USER_TAG));
        params.add(new BasicNameValuePair("firstname", firstname));
        params.add(new BasicNameValuePair("lastname", lastname));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("is_teacher", "0"));
        return JSONParser.getJSONFromUrl(Config.CONNECT_IP, params);
    }

    /**
     * Rename a file on the server to the new filename
     * @param newFilename new filename for server file
     * @param oldFilename old filename for server file
     * @param workingDIR the dir of the server file
     * @return response JSON
     */
    public static JSONObject renameServerFile(String newFilename, String workingDIR, String oldFilename, String username){
        //. Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", RENAME_FILE_TAG));
        params.add(new BasicNameValuePair("new_filename", newFilename));
        params.add(new BasicNameValuePair("workingDIR", workingDIR));
        params.add(new BasicNameValuePair("old_filename", oldFilename));
        params.add(new BasicNameValuePair("username", username));
        Log.i("SERVER FILE RENAME", "Sending rename call to server. " +
                "\nnew_filename: " + newFilename
        + "\nworkingDIR: "+  workingDIR
        + "\nold_filename: " + oldFilename);
        return JSONParser.getJSONFromUrl(Config.CONNECT_IP, params);
    }

    /**
     * Move or Copy a file on the server
     * @param filename the filename to move / copy
     * @param sourceDIR file source dir
     * @param destDIR file destination
     * @param mask mask for move or copy
     * @return server response
     */
    public static JSONObject serverFileMoveCopy(String filename, String sourceDIR, String destDIR, String mask){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", MOVE_COPY_SERVER_FILE));
        params.add(new BasicNameValuePair("filename", filename));
        params.add(new BasicNameValuePair("sourceDIR", sourceDIR));
        params.add(new BasicNameValuePair("destDIR", destDIR));
        params.add(new BasicNameValuePair("mask", mask));
        return JSONParser.getJSONFromUrl(Config.CONNECT_IP, params);
    }


    /**
     * Request to upload a file to the server using FIleHandler
     * @param filename the filename to upload
     * @param workingDIR the directory to upload to
     * @param context application context
     * @return response JSON
     */
    public static JSONObject uploadFileToServer(String filename, String workingDIR, Context context){
        JSONObject json = null;
        MultipartEntityBuilder mpEntity = FileHandler.bundleFileUpload(filename, workingDIR, context);
        HttpClient http = new DefaultHttpClient();
        HttpPost post = new HttpPost(Config.CONNECT_IP + "index.php");
        post.setEntity(mpEntity.build());
        try {
            //Commit post
            HttpResponse response = http.execute(post);
            HttpEntity entity = response.getEntity();
            //Build json from response
            json = JSONParser.getJSONFromInputStream(entity.getContent());
            entity.consumeContent();
            http.getConnectionManager().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("FILEHANDLER", "fileUpload - Return JSON: " + json.toString());
        return json;
    }



    /**
     *  					Database functions used by dashboard to check if a user is logged in and to set them as logged out
     */


    /**
     * Return true false for if a user is logged in, used primarily by initial dashboard
     * @param context application context
     * @return true if a user is currently logged in, otherwise false
     */
    public static boolean isUserLoggedIn(Context context){
        UserDBHandler db = new UserDBHandler(context);
        int count = db.getRowCount();
        if(count > 0){
            // user logged in
            return true;
        }
        return false;
    }

    /**
     * Removes the database tables showing user present
     * @param context application context
     * @return true
     */
    public static boolean logoutUser(Context context){
        UserDBHandler db = new UserDBHandler(context);
        db.resetTables();
        return true;
    }


    /**
     * Get server provided JSON from data
     * @param username current username for server validation
     * @param fileType the 
     * @param tag
     * @return
     */
    // public JSONObject postFileData(String username, String fileType, String tag){
//    	public JSONObject postFileData(String username, String tag){
//    	List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("tag", tag));
//        params.add(new BasicNameValuePair("username", username));
//        //params.add(new BasicNameValuePair("type", fileType));
//        Log.i("SERVER REQUEST HANDLER", "postFileData returning");
//        return JSONParser.getJSONFromUrl(Config.CONNECT_IP, params);
//    }

}
