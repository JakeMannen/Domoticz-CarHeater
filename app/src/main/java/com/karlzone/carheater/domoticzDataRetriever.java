package com.karlzone.carheater;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


public class domoticzDataRetriever extends Application{

    String ServerAdress;
    String ServerPort;
    static String BASE_URL;
    static String status_url;
    static String toggleswitch_url;
    static String temp_url;
    static String timerlist_url;
    static String deletetimer_url;
    static String setState_url;

    String deviceIDX;
    String tempIDX;
    String heat_prestart_time;

    Context mCtx;

    SharedPreferences sharedprefs;


public domoticzDataRetriever (Context ctx) {

        mCtx = ctx;

        sharedprefs = PreferenceManager.getDefaultSharedPreferences(mCtx);

        ServerAdress = sharedprefs.getString("domoticz_server", null);
        ServerPort = sharedprefs.getString("domoticz_server_port", null);    //8080;
        deviceIDX = sharedprefs.getString("heater_idx", null);
        tempIDX = sharedprefs.getString("temperature_idx", null);
        heat_prestart_time = sharedprefs.getString("hour_set_list","");

        BASE_URL = "Http://"+ServerAdress+":"+ServerPort;
        status_url = BASE_URL+"/json.htm?type=devices&rid="+deviceIDX;
        toggleswitch_url = BASE_URL+"/json.htm?type=command&param=switchlight&idx="+deviceIDX+"&switchcmd=Toggle";
        temp_url = BASE_URL+"/json.htm?type=devices&rid="+tempIDX;
        timerlist_url = BASE_URL+"/json.htm?type=timers&idx="+deviceIDX;
        deletetimer_url = BASE_URL+"/json.htm?type=command&param=deletetimer&idx=";
        setState_url = BASE_URL+"/json.htm?type=command&param=switchlight&idx="+deviceIDX+"&switchcmd=";


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

 /*Set a quick timer*/
 public void setQuickTimer(VolleyCallbackInterface volleyCallbackInterface, String time, String date){

    String[] splitTime = time.split(":");
    String[] splitDate = date.split("-");

    String hour = splitTime[0];
    String min = splitTime[1];

    String day = splitDate[2];
    String month = splitDate[1];
    String year = splitDate[0];

    /*Remove leading zero & rebuild date to match call url */
    String newhour = hour.replaceAll("^0","");
    String newmin = min.replaceAll("^0","");
    String newDateString = day+"/"+month+"/"+year;

    InputMatcher datecheck = new InputMatcher();

    Calendar timercalendar = Calendar.getInstance();
    timercalendar.set(Calendar.YEAR,Integer.parseInt(year));
    timercalendar.set(Calendar.MONTH,Integer.parseInt(month)-1);
    timercalendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(day));
    timercalendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hour));
    timercalendar.set(Calendar.MINUTE,Integer.parseInt(min));
    timercalendar.add(Calendar.MINUTE, -(Integer.parseInt(heat_prestart_time)));


    Calendar calendarNow = Calendar.getInstance();

    /*Get the time difference between calendars in minutes*/
    long timediff = (timercalendar.getTimeInMillis() - calendarNow.getTimeInMillis()) / 60 / 1000;

    /*If set time plus preheat time is later than now, we request to set a timer*/
    if(timediff > 0) {

        // /json.htm?type=command&param=addtimer&idx=79&active=true&timertype=5&date=01/26/2018&hour=12&min=10&randomness=false&command=0&level=100&hue=0&days=128&mday=1&month=1&occurence=1

        makeVolleyRequest(BASE_URL+"/json.htm?type=command&param=addtimer&idx="+deviceIDX+"&active=true&timertype=5&date="+newDateString+"&hour="+newhour+"&min="+newmin+"&randomness=false&command=0&level=100&hue=0&days=128&mday=1&month=1&occurence=1", volleyCallbackInterface);

    }
    /*If set time has passed we request to start the heating immediately*/
    else{

        makeVolleyRequest(setState_url+"On", volleyCallbackInterface);
    }
 }

/*Make the request call to Domoticz*/
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

                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(mCtx, "Network Timeout!", Toast.LENGTH_LONG).show();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(mCtx, "Authentification Error!", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(mCtx, "Server Error!", Toast.LENGTH_LONG).show();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(mCtx, "Network Error!", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(mCtx, "Parsing Error!", Toast.LENGTH_LONG).show();
                    }

                    error.printStackTrace();

                }

            });

        MySingleton.getInstance(mCtx).addToRequestQueue(jsObjRequest);


    }
}


