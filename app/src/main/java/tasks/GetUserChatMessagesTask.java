package tasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import handlers.ServerRequestHandler;
import helper.Message;
import util.Config;
import util.MessageBank;

public class GetUserChatMessagesTask extends AsyncTask<String, ArrayList<MessageBank>, ArrayList<MessageBank>> {

    Activity activity;
    OnTaskCompleteListener mCallback;

    public GetUserChatMessagesTask(Activity activity) {
        mCallback = (OnTaskCompleteListener) activity;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(ArrayList<MessageBank> result) {
        mCallback.setAllChatGroupMessages(result);
    }

    @Override
    protected ArrayList<MessageBank> doInBackground(String... values) {
        String username = values[0];
        ArrayList<MessageBank> messagelist = new ArrayList<>();
        Log.v("SET CHAT MESSAGE", username);
        JSONObject json = ServerRequestHandler.getUserMessages(username);
        int responseCode = 0;
        // check for response
        try {
            if (json.getString(Config.KEY_SUCCESS) != null) {
                Log.i("PROCESS", "Processing JSON string");
                String res = json.getString(Config.KEY_SUCCESS);
                if (Integer.parseInt(res) == 1) {
                    // Everything good, build the message list
                    messagelist = buildList(json);
                } else {
                    // Error in process
                    Log.i("PROCESS", "HELP REQUEST FAILURE string");
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("PROCESS", "Returning from getting chat messages");
        return messagelist;
    }

    private ArrayList<MessageBank> buildList(JSONObject json) {
        ArrayList<MessageBank> messagelist = new ArrayList<>();
        ArrayList<String> groups = mCallback.getUserGroups();
        if (!groups.isEmpty() || groups == null) {
            for (String group : groups) {
                // for each group create an array
                try {
                    JSONArray ja = json.getJSONArray(group);
                    if (ja != null) {
                        // if that array is not null
                        int len = ja.length();
                        MessageBank ml = new MessageBank(group);
                        // loop through and create messages from each line, and
                        // enter messages into a message list
                        for (int i = 0; i < len; i++) {
                            ml.addMessage(Message.formatNewMessage(ja.getString(i)));
                        }
                        messagelist.add(ml);
                    }
                } catch (JSONException e) {
                    Log.v("GET CHAT TASK ERROR", "Error in messagelist build ");
                    e.printStackTrace();
                }
            }
        }
        return messagelist;
    }

}
