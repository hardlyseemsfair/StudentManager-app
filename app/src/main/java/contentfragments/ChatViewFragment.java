package contentfragments;

//import com.classroom.studentmanager.R;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.classroom.applicationactivity.R;

import java.util.Collections;

import util.LogFlag;
import util.MessageBank;
import util.SessionLog;


/**
 * Fragment used to display chat messages. Layout requires revision
 *
 * @author JACK
 */

public class ChatViewFragment extends Fragment {

    //Log
    SessionLog sessionLog;

    //Instance variables
    private Activity activity;
    private ApplicationFragmentListener mCallback;
    private String username;
    private String chatGroup;
    private TextView textView;
    private ListView listView;
    private EditText sendMessage;
    private Button sendButton;
    private MessageBank messages;
    private ChatAdapter ca;

    /**
     * Instance constructor
     *
     * @param username  current username
     * @param chatGroup chat group to display messages for
     * @return self object
     */
    public static final ChatViewFragment newInstance(String username, String chatGroup) {
        ChatViewFragment cvf = new ChatViewFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("chatgroup", chatGroup);
        cvf.setArguments(args);
        return cvf;
    }

    /**
     * onCreate
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    /**
     * onCreateView
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        username = getArguments().getString("username");
        chatGroup = getArguments().getString("chatgroup");
        messages = mCallback.getMessages(chatGroup);
        messages.sort();
        Log.v("MESSAGES", messages.toString());
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        //dirPath = setDirPath(directory);
        //String[] files = getFileNames();
        setTitle(view, "Chat Group: " + chatGroup);
        setListView(view);
        setSendBar(view);
        return view;
    }


    /**
     * Set title of chat
     *
     * @param view  associated view
     * @param title chat title (usually group name)
     */
    private void setTitle(View view, String title) {
        textView = (TextView) view.findViewById(R.id.chat_title);
        textView.setText(title);
    }

    /**
     * Create list view to be used for chat to display messages
     *
     * @param view associated view
     */
    private void setListView(View view) {
        listView = (ListView) view.findViewById(R.id.chat_message_list);
        ca = new ChatAdapter(activity, messages);
        listView.setAdapter(ca);
    }

    /**
     * Set send bar functionality for messages
     *
     * @param view associated view
     */
    private void setSendBar(View view) {
        sendMessage = (EditText) view.findViewById(R.id.chat_message_text);
        sendButton = (Button) view.findViewById(R.id.chat_message_send);
        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String message = sendMessage.getText().toString();
                mCallback.commitChatMessage(message, chatGroup);
                sessionLog.writeLog(LogFlag.NOTIFICATION, "Sending chat message...");
                sendMessage.setText("");
            }
        });
    }

    public String getChatgroup(){
        return chatGroup;
    }

    public void updateAdapter(){
        Log.i("CHAT ADAPTER", "Updating adapter data...");
        messages = mCallback.getMessages(chatGroup);
        Log.i("CHAT ADAPTER", "Message data...: " + messages.toString());
        messages.sort();
        ca = new ChatAdapter(activity, messages);

        listView.setAdapter(ca);
    }


    /**
     * Attach contructor
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        mCallback = (ApplicationFragmentListener) activity;
        sessionLog = mCallback.getSessionLog();

    }

    /**
     * OnResume to ensure that when returning from the back stack the groupchat button is set correctly
     */
    @Override
    public void onResume() {
        super.onResume();
    }

}
