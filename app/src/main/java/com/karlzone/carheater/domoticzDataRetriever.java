package com.karlzone.carheater;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;



public class domoticzDataRetriever extends Application{

    String ServerAdress;
    String ServerPort;
    static String BASE_URL;
    static String status_url;
    static String toggleswitch_url;
    static String temp_url;
    static String timerlist_url;
    static String deletetimer_url;

    String deviceIDX;
    String tempIDX;


    Context mCtx;

    SharedPreferences sharedprefs;


public domoticzDataRetriever (Context ctx) {

        mCtx = ctx;

        sharedprefs = PreferenceManager.getDefaultSharedPreferences(mCtx);

        ServerAdress = sharedprefs.getString("domoticz_server", null);
        ServerPort = sharedprefs.getString("domoticz_server_port", null);    //8080;
        deviceIDX = sharedprefs.getString("heater_idx", null);
        tempIDX = sharedprefs.getString("temperature_idx", null);

        BASE_URL = "Http://"+ServerAdress+":"+ServerPort;
        status_url = BASE_URL+"/json.htm?type=devices&rid="+deviceIDX;
        toggleswitch_url = BASE_URL+"/json.htm?type=command&param=switchlight&idx="+deviceIDX+"&switchcmd=Toggle";
        temp_url = BASE_URL+"/json.htm?type=devices&rid="+tempIDX;
        timerlist_url = BASE_URL+"/json.htm?type=timers&idx="+deviceIDX;
        deletetimer_url = BASE_URL+"/json.htm?type=command&param=deletetimer&idx=";

}


 public void getIDXStatus(VolleyCallbackInterface volleyCallbackInterface) {


    makeVolleyRequest(status_url, volleyCallbackInterface );

 }


 public void toggleSwitch(VolleyCallbackInterface volleyCallbackInterface) {

     makeVolleyRequest(toggleswitch_url, volleyCallbackInterface);

 }


 public void getTemperature(VolleyCallbackInterface volleyCallbackInterface){

        makeVolleyRequest(temp_url, volleyCallbackInterface);

 }

    //Hämta timerlista
 public void getTimers(VolleyCallbackInterface volleyCallbackInterface){

    makeVolleyRequest(timerlist_url, volleyCallbackInterface);

 }

 public void deleteTimer(VolleyCallbackInterface volleyCallbackInterface, String idx){

     makeVolleyRequest(deletetimer_url+idx, volleyCallbackInterface);

 }

 public void toggleActiveState(VolleyCallbackInterface volleyCallbackInterface, String toggle_url){

     makeVolleyRequest(BASE_URL+toggle_url, volleyCallbackInterface);

 }




private void makeVolleyRequest(String url ,final VolleyCallbackInterface volleyCallbackInterface) {

    JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Method.GET, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {


                        volleyCallbackInterface.onJSONSuccess(response);

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub

                }

            });



        MySingleton.getInstance(mCtx).addToRequestQueue(jsObjRequest);


    }
}


