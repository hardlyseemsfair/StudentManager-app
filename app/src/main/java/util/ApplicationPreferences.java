package util;

import android.content.Context;
import android.content.SharedPreferences;

public class ApplicationPreferences {

    //App values
    private final String appprefs = "application preferences";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;


    //Key Values
    private String USERNAME = "USERNAME";
    private String REGISTER_DEVICE = "REGISTER_DEVICE";
    private String SYNC_REMOTE_FILES = "SYNC_REMOTE_FILES";


    public ApplicationPreferences(Context context){
        prefs = context.getSharedPreferences(appprefs, 0);
        editor = prefs.edit();
    }

    public void setUsername(String username){
        editor.putString(USERNAME, username);
    }

    public String getUsername(){
        return prefs.getString(USERNAME, null);
    }

}
