package com.example.testapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;
import com.example.testapp.commuication;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import static android.content.ContentValues.TAG;

//private static class

public class asyncCommunication implements Runnable {
    String ip;
    Integer port;
    String startLocation;
    List<String> destinations;
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
}
