package helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Message implements Comparable<Message>{

    private String author;
    private String message;
    private Date created_on;


    public Message(String name, String message, Date created_on) {
        super();
        this.author = name;
        this.message = message;
        this.created_on = created_on;
    }

    public Message(){

    }


    public String getAuthor() {
        return author;
    }


    public void setAuthor(String name) {
        this.author = name;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public Date getCreated_on() {
        return created_on;
    }


    public void setCreated_on(String created_on) {
        //this.created_on = created_on;
    }

    @Override
    public int compareTo(Message m2){
        if(this.created_on.getTime() > m2.created_on.getTime()){
            return 1;
        } else if (this.created_on.getTime() < m2.created_on.getTime()){
            return -1;
        } else {
            return 0;
        }
    }

    public static Message formatNewMessage(String mes) {
        JSONObject jobj;
        try {
            jobj = new JSONObject(mes);
            if (jobj != null) {
                long l = Long.valueOf(jobj.getString("created_on"));
                Date d = new Date(l);
                return new Message(jobj.getString("realname"), jobj.getString("message"), d);
            } else {
                return new Message();
            }
        } catch (JSONException jex) {
            jex.printStackTrace();
            return new Message();
        }
    }


    @Override
    public String toString(){
        return "Name: " + this.author + ", Message: " + this.message;
    }







}
