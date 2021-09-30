package com.example.testapp;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.net.Socket;
import java.io.*;

import static android.content.ContentValues.TAG;

public class commuication {



        private Socket s;
        private DataOutputStream dOut;
        private DataInputStream  dIn;

        public boolean connect(String ip, Integer port){
            try {

                s = new Socket(ip,port);
                dOut = new DataOutputStream(s.getOutputStream());
                dIn = new DataInputStream(s.getInputStream());
                return true;
            }
            catch(Exception  e) {
                Log.d(TAG, e.toString());
                return false;
            }
        }
        public Boolean disconnect() {
            try{
                dOut.close();
                dIn.close();
                s.close();
                return true;
            }
            catch(Exception  e) {
                return false;
            }
        }
        public Boolean sendMessage(String message) {
            try {

//        	PrintWriter writer = new PrintWriter(dOut, true);
//        	writer.println(message);
//          dOut.flush();

                dOut.writeUTF(message);

                return true;
            }
            catch (Exception e) {
                return false;
            }
        }
        public String getMessage() {
            byte[] b = new byte[0];
            try {
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                //out.println(json);
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                while (!in.ready()) {
                    continue;
                }
//                String response = in.readLine();
//                return response;
                
                dIn.read(b);
                return (String) b.toString();
            } catch (Exception e) {
                return "Error";
            }
        }
}
