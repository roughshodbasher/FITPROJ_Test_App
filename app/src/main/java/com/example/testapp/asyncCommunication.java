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
    Integer type;

    Boolean gotMessage = false;
    JSONObject message;
    asyncCommunication(String ip, Integer port, JSONObject message, Integer type) {
        this.ip = ip;
        this.port = port;
        this.message = message;
        this.type = type;
    }

    @Override
    public void run() {
        Gson gson = new Gson();
        commuication s = new commuication();
        s.connect(this.ip,this.port);
        // Send recv
        if (this.type == 0) {
            s.sendMessage(this.message.toString());

            String t = s.getMessage();
            output = gson.toJson(t);
            Log.d(TAG,output);
            output = output.substring(output.indexOf('{'),output.lastIndexOf('}')+1);
            output = output.replace("\\","");
            this.output = convertStandardJSONString(output);
            this.gotMessage = true;
        }
        // Send
        else if (this.type == 1) {
            s.sendMessage(this.message.toString());

        }
        // recv
        else if (this.type == 2) {
            String t = s.getMessage();
            output = gson.toJson(t);
            output = output.substring(output.indexOf('{'),output.lastIndexOf('}')+1);
            output = output.replace("\\","");
            output = convertStandardJSONString(output);
            this.gotMessage = true;
        }
        s.disconnect();
    }

    public String getServerResponse() {
        if (this.gotMessage) {
            return this.output;
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
