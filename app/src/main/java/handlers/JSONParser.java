package handlers;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;


/**
 * Handles JSON construction from either a URL or from an already formed InputStream response
 *
 * @author JACK
 */
public class JSONParser {


    // constructor
    protected JSONParser() {

    }

    /**
     * Return a JSON from the provided URL and pairs parameeters
     *
     * @param url    the server url to query
     * @param params the key value pairs to be commited through POST
     * @return the JSON response from the server
     */
    public static JSONObject getJSONFromUrl(String url, List<NameValuePair> params) {
        InputStream is = null;
        JSONObject jObj = null;
        String json = "";
        // Making HTTP request
        try {
            // defaultHttpClient 
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Bundle response to JSON object
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.i("JSON RAW", json);
        } catch (Exception e) {
            Log.e("JSONParser", "fromURL Buffer Error - Error converting result " + e.toString());
        }
        // try parse the string to a JSON object
        try {
            Log.i("JSONParser url", "PROCESSING JSON: " + json);
            //jObj = new JSONObject(json);
            jObj = new JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1));
        } catch (JSONException e) {
            Log.e("JSONParser url", "Error parsing data " + e.toString());
        } catch (StringIndexOutOfBoundsException e) {
            try {
                jObj = new JSONObject(json);
            } catch (StringIndexOutOfBoundsException ex) {
                Log.e("JSONParser url", json);
                Log.e("JSONParser url", "String index out of bounds, malformed json: " + e.toString());
            }catch (JSONException ex) {
                Log.e("JSONParser url", "Error parsing data " + e.toString());
            }
        }
        // return JSON String
        return jObj;
    }

    /**
     * Return a JSON from the provided URL and pairs parameeters
     *
     * @param is the InputStream to build the JSON from
     * @return the JSON response from the server
     */
    public static JSONObject getJSONFromInputStream(InputStream is) {
        // Bundle response to JSON object
        String json = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.i("JSONParser", "from IS JSON: " + json);
        } catch (Exception e) {
            Log.e("JSONParser", "Buffer Error - Error converting result " + e.toString());
        }
        // try parse the string to a JSON object
        JSONObject jObj = null;
        try {
            Log.i("JSONParser stream", "PROCESSING JSON: " + json);
            jObj = new JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1));
        } catch (JSONException e) {
            Log.e("JSONParser stream", "Error parsing data " + e.toString());
        } catch (StringIndexOutOfBoundsException e) {
            try {
                jObj = new JSONObject(json);
            } catch (StringIndexOutOfBoundsException ex) {
                Log.e("JSONParser stream", json);
                Log.e("JSONParser stream", "String index out of bounds, malformed json: " + e.toString());
            }catch (JSONException ex) {
                Log.e("JSONParser stream", "Error parsing data " + e.toString());
            }
        }
        // return JSON String
        return jObj;
    }
}

