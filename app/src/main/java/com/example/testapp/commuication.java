package com.example.testapp;
import static android.content.ContentValues.TAG;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class commuication {



        private Socket s;
        private DataOutputStream dOut;
        private DataInputStream  dIn;

        public boolean connect(String ip, Integer port){
            try {

                s = new Socket(ip,port);
                // timesout after a minute

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

                InputStream in = null;
                in = s.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String request;
                request = br.readLine();
                int x = 0;
                return request;
            } catch (Exception e) {
                return "Error";
            }
        }
}
