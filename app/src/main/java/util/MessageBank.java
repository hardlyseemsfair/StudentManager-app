package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import helper.Message;

public class MessageBank {


    ArrayList<Message> messages = new ArrayList<Message>();
    String chatgroup;

    public MessageBank(String chatgroup){
        this.chatgroup = chatgroup;
    }


    public void addMessage(Message m){
        messages.add(m);
    }

    public Iterator<Message> getIterator(){
        return messages.iterator();
    }

    public Message getMessage(int index){
        return messages.get(index);
    }

    public MessageBank getMessageList(){
        return this;
    }

    public String getChatgroup(){
        return chatgroup;
    }

    public void sort(){
        Collections.sort(messages);
    }

    public int size(){
        return messages.size();
    }

    @Override
    public String toString(){
        String s = "Group Name: " + this.chatgroup;
        for(Message m : this.messages){
            s += m.toString() + ", ";
        }
        return s;
    }

}
