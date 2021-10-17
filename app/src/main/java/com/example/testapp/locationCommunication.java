package com.example.testapp;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class locationCommunication implements Runnable {
    String ip;
    Integer port;

    // Current LatLng pos
    LatLng cLoc;
    // the polyline for the active route
    String output;
    Boolean actionNeeded = false;

    // timeout after 1 minute
    Integer messageSendTime = 1;
    long timeLastMsgSend;

    boolean hadError = false;
    Integer errorCode = -1;
    String errorMsg = "";

    commuication comm;
    locationCommunication(String ip, Integer port, LatLng cLoc) {
        this.ip = ip;
        this.port = port;
        this.comm = new commuication();
        this.cLoc = cLoc;
        comm.connect(ip,port);
    }

    public void run() {
        Boolean connected = true;
        long positionUpdateTimerStart = 0;
        while (connected) {
            if ((System.nanoTime() - positionUpdateTimerStart)/1000000000 > messageSendTime*60) {
                // do the calling stuff
                JSONObject json = new JSONObject();
                try {
                    json.put("type", 0);
                    json.put("lat", this.cLoc.latitude);
                    json.put("long", this.cLoc.longitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                comm.sendMessage(json.toString());
                String response = comm.getMessage();
                response = response.substring(response.indexOf('{'));
                response = response.replace("\\\"","\"");
                response = convertStandardJSONString(response);
                Integer responseCode = -1;
                try {
                    JSONObject r = new JSONObject(response);
                    responseCode = (Integer) r.get("action");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (responseCode) {
                    case -1:
                        // issue with response, continue on clientside
                        this.errorCode = -1;
                        this.actionNeeded = false;
                    case 0:
                        // on route
                        this.errorCode = -1;
                        this.actionNeeded = false;
                    case 1:
                        this.actionNeeded = true;
                        response = comm.getMessage();
                        response = response.substring(response.indexOf('{'));
                        response = response.replace("\\\"","\"");
                        response = convertStandardJSONString(response);
                        try {
                            JSONObject r = new JSONObject(response);
                            // doenst return polyline returns Int array cause python doesnt like escape characters
                            String[] raw = ((String) r.get("polyline")).split("-");
                            String poly = "";
                            for (Integer i = 0; i < raw.length; i++) {
                                int v = (Integer.valueOf(raw[i]));
                                char w = (char) v;
                                poly = poly + w;
                            }
                            this.output = poly;
                            this.actionNeeded = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    case 2:
                        // reached destination
                        this.output = "";
                        this.actionNeeded = true;

                }
            }
        }
    }

    public boolean update() {
        return this.actionNeeded;
    }

    public String getNewPoly() {
        this.actionNeeded = false;
        return this.output;
    }

    public static String convertStandardJSONString(String data_json) {
        data_json = data_json.replaceAll("\\\\r\\\\n", "");
        data_json = data_json.replace("\"{", "{");
        data_json = data_json.replace("}\",", "},");
        data_json = data_json.replace("}\"", "}");
        return data_json;
    }

    public void setLocation(LatLng newLT) {
        this.cLoc = newLT;
    }
}
