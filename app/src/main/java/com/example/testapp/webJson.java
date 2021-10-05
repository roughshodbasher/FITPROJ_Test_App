package com.example.testapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class webJson implements Runnable {
    JSONObject output;
    String strOutput;
    String input;
    Boolean complete;
    webJson(String input) {
        this.input = input;
        this.complete = false;

    }
    @Override
    public void run() {
        HttpURLConnection connection;
        BufferedReader reader;
        try {
            URL url = new URL(this.input);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
                Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

            }

             strOutput = buffer.toString();
             try {
                 this.output = new JSONObject(strOutput);
                 this.complete = true;
             } catch (JSONException e) {
                 e.printStackTrace();
             }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean isComplete() { return this.complete; }

    public JSONObject getOutput() {
        if (this.complete) {
            return this.output;
        }
        else {
            return null;
        }
    }

//    public String getPoly() {
//
//    }
}
