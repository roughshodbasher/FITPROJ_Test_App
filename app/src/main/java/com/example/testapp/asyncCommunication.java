package com.example.testapp;

import android.util.Log;

import org.json.JSONObject;

import com.google.gson.Gson;

import java.util.List;

import static android.content.ContentValues.TAG;

//private static class

public class asyncCommunication implements Runnable {
    String ip;
    Integer port;
    String output;

    Boolean gotMessage = false;
    JSONObject message;
    asyncCommunication(String ip, Integer port, JSONObject message) {
        this.ip = ip;
        this.port = port;
        this.message = message;
    }

    @Override
    public void run() {
        Gson gson = new Gson();
        commuication s = new commuication();
        s.connect(this.ip,this.port);
        s.sendMessage(this.message.toString());

        String t = s.getMessage();
        //s.disconnect();
        Log.d(TAG, t);
        Log.d(TAG,"HERE");

        output = gson.toJson(t);
        output = output.substring(output.indexOf('{'),output.lastIndexOf('}')+1);
        output = output.replace("\\","");
        output = convertStandardJSONString(output);
        gotMessage = true;

    }

    public String getServerResponse() {
        if (gotMessage) {
            return output;
        }
        else {
            return "";
        }
    }

    public Boolean finished() {
        return gotMessage;
    }

    public static String convertStandardJSONString(String data_json) {
        data_json = data_json.replaceAll("\\\\r\\\\n", "");
        data_json = data_json.replace("\"{", "{");
        data_json = data_json.replace("}\",", "},");
        data_json = data_json.replace("}\"", "}");
        return data_json;
    }
}
