package contentfragments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.classroom.applicationactivity.R;

import helper.Message;
import util.MessageBank;


/**
 * Adapter used by ChatViewFragment
 * @author JACK
 *
 */
public class ChatAdapter extends BaseAdapter{

    //Instance variables
    private Context context;
    private MessageBank messages;


    /**
     * Constructor
     * @param context application context
     * @param messages the message list object to be used in populating
     */
    public ChatAdapter(Context context, MessageBank messages) {
        this.context = context;
        this.messages = messages;
    }


    /**
     * GetView for adapter
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView authorTextView;
        TextView messageTextView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listView;
        if (convertView == null) {
            listView = new View(context);
            // get layout
            listView = inflater.inflate(R.layout.chat_display, null);
            // set value into textview
            authorTextView = (TextView) listView.findViewById(R.id.chat_author);
            Message m = (Message) getItem(position);
            Log.e("CHAT ADAPTER", "Retrieved message at index: " + position + "\nMessage: " + m.getMessage());
            authorTextView.setText(m.getAuthor());
            messageTextView = (TextView) listView.findViewById(R.id.chat_message);
            messageTextView.setText(m.getMessage());
        } else {
            listView = (View) convertView;
        }
        return listView;
    }

    /**
     * Get count
     */
    @Override
    public int getCount() {
        //return fileNames.length;
        return messages.size();
    }


    /**
     * getItem
     */
    @Override
    public Object getItem(int position) {
        return messages.getMessage(position);
    }

    /**
     * getItemID
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

}
