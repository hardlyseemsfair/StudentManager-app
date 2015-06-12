package handlers;

import android.content.ContentValues;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import util.Config;
import util.ToastMessages;

/**
 * Created by NAPOLEON on 5/4/2015.
 */
public class ServerConnectHandler {

    public ServerConnectHandler() {

    }


    public static InputStream contactServer(ContentValues cv) {
        InputStream is = null;
        try {
            URL url = new URL(Config.CONNECT_IP);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
        } catch (MalformedURLException murl) {

        } catch (IOException ioe) {

        }


        return is;
    }


}
