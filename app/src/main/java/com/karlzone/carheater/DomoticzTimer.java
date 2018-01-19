package com.karlzone.carheater;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;



public class DomoticzTimer {


    private String isActive;
    private int cmd;
    private int days;
    private String date;
    private String time;
    private int Mday;
    private int type;
    private String idx;
    private int occurence;
    private int month;
    private String randomness;



    private String typeString;
    private String daysString;
    private String dateString;
    private String activeToggle_url;



    public DomoticzTimer(JSONObject jsonObject) {


        try {

            this.isActive = jsonObject.getString("Active");
            this.cmd = jsonObject.getInt("Cmd");
            this.date = jsonObject.getString("Date");
            this.days = jsonObject.getInt("Days");
            this.Mday = jsonObject.getInt("MDay");
            this.month = jsonObject.getInt("Month");
            this.time = jsonObject.getString("Time");
            this.type = jsonObject.getInt("Type");
            this.idx = jsonObject.getString("idx");
            this.occurence = jsonObject.getInt("Occurence");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTimerSetDays() {

        switch (this.days) {

                case 0:
                    this.daysString = "";
                    break;
                case 1:
                    this.daysString = "Mån";
                    break;
                case 2:
                    this.daysString = "Tis";
                    break;
                case 4:
                    this.daysString = "Ons";
                    break;
                case 8:
                    this.daysString = "Tors";
                    break;
                case 16:
                    this.daysString = "Fre";
                    break;
                case 32:
                     this.daysString = "Lör";
                     break;
                case 64:
                    this.daysString = "Sön";
                    break;
                case 128:
                    this.daysString = "Alla dagar";
                    break;


             }
        return this.daysString;
    }


    public String getTypeString(){

        switch (this.type){

            case 0: this.typeString = "Före soluppgång";
                break;

            case 1: this.typeString = "Efter soluppgång";
                break;

            case 2: this.typeString = "I Tid";
                break;

            case 3: this.typeString = "Före solnedgång";
                break;

            case 4: this.typeString = "Efter solnedgång";
                break;

            case 5: this.typeString = "Fast Datum/Tid";
                break;

            case 6: this.typeString = "Udda Dagar";
                break;

            case 7: this.typeString = "Jämna Dagar";
                break;

            case 8: this.typeString = "Udda veckonummer";
                break;

            case 9: this.typeString = "Jämna veckonummer";
                break;

            case 10: this.typeString = "Månadsvis";
                break;

            case 11: this.typeString = "Månadsvis (veckodag)";
                break;

            case 12: this.typeString = "Årlig";
                break;

            case 13: this.typeString = "Årlig (veckodag)";
                break;

        }

        return typeString + " " +this.daysString;

    }

    public String getDateString(){

        if (this.date.equals("")){

            this.dateString = ("Tid: "+this.time);

        }else{

            this.dateString = ("Datum: "+this.date+"  Tid: "+this.time);

        }

        return dateString;
    }

    public String getActiveToggleUrl() {

        String hour,min;

        String[] hour_min = this.time.split(":");

        hour = hour_min[0];
        min = hour_min[1];

        if (this.isActive.equals("true")){

            activeToggle_url = "/json.htm?type=command&param=updatetimer&idx=" + this.idx + "&active=false" + "&timertype=" + this.type +
                                "&date=" + this.date + "&hour=" + hour + "&min=" + min + "&randomness=" + this.randomness + "&command=" +this.cmd +
                                "&level=100&hue=0" + "&days=" + this.days + "&mday=" + this.Mday + "&month=" + this.month + "&occurence=" + this.occurence;

            //http://192.168.10.11:8080/json.htm?type=command&param=updatetimer&idx=5&active=false&timertype=0&date=&hour=0&min=0&randomness=false&command=0&level=100&hue=0&days=1&mday=1&month=1&occurence=1
        } else {

            activeToggle_url = "/json.htm?type=command&param=updatetimer&idx=" + this.idx + "&active=true" + "&timertype=" + this.type +
                    "&date=" + this.date + "&hour=" + hour + "&min=" + min + "&randomness=" + this.randomness + "&command=" +this.cmd +
                    "&level=100&hue=0" + "&days=" + this.days + "&mday=" + this.Mday + "&month=" + this.month + "&occurence=" + this.occurence;

        }

        Log.d("TOGGLE_URL", activeToggle_url);

        return activeToggle_url;
    }

    //Returns true/false if timer is active
    public String isTimerActive() {

        return isActive;
    }

    //Returns the idx number
    public String getIDX() {

        return this.idx;
    }
}