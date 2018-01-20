package com.karlzone.carheater;

import android.util.Log;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InputMatcher {

    String timeregex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
    String dateregex = "((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])";
    Pattern pattern;
    Matcher matcher;
    Boolean timeOkSet;
    Boolean dateOkSet;


    final Calendar calendar;

    public InputMatcher() {


        calendar = Calendar.getInstance();
    }

    /*Validates time according to regex*/
    Boolean validateTime(String time_in){

        pattern.compile(timeregex);

        timeOkSet = (pattern.matches(timeregex, time_in) && isTimeFuture(time_in));


        return timeOkSet;
    }

    /*Validates date according to regex*/
    Boolean validateDate(String date_in){

        pattern.compile(dateregex);

        dateOkSet = (pattern.matches(dateregex, date_in) && isDateFuture(date_in));


        return dateOkSet;
    }

    /*Returns true if set date is today*/
    public Boolean isDateToday(String dateToSet) {

        String[] date_split = dateToSet.split("-");

        int dayToSet = Integer.parseInt(date_split[2]);
        int monthToSet = Integer.parseInt(date_split[1]);
        int yearToSet = Integer.parseInt(date_split[0]);

        int dayNow = calendar.get(Calendar.DAY_OF_MONTH);
        int monthNow = calendar.get(Calendar.MONTH)+1;
        int yearNow = calendar.get(Calendar.YEAR);

        //Check if date IS today
        if(yearToSet == yearNow){

            if(monthToSet == monthNow){

                if(dayToSet == dayNow){

                    return true;

                }
            }
        }
        return false;
    }

    /*Returns true if date set is today or forward*/
    private Boolean isDateFuture(String dateToSet){

        String[] date_split = dateToSet.split("-");

        int dayToSet = Integer.parseInt(date_split[2]);
        int monthToSet = Integer.parseInt(date_split[1]);
        int yearToSet = Integer.parseInt(date_split[0]);

        int dayNow = calendar.get(Calendar.DAY_OF_MONTH);
        int monthNow = calendar.get(Calendar.MONTH)+1;
        int yearNow = calendar.get(Calendar.YEAR);


        //Check if date is today or later
        if(yearToSet >= yearNow){

            if(monthToSet >= monthNow){

                if(monthToSet > monthNow){

                    return true;
                }

                else if(dayToSet >= dayNow){

                    return true;

                }
            }
        }

        //Date is past

        return false;

    }

    /*Returns true if time set is now or later, only useful if date is today*/
    private Boolean isTimeFuture(String timeToSet){

        String[] time_split = timeToSet.split(":");

        int minToSet = Integer.parseInt(time_split[1]);
        int hourToSet = Integer.parseInt(time_split[0]);

        int hourNow = calendar.get(Calendar.HOUR_OF_DAY);
        int minNow = calendar.get(Calendar.MINUTE);


        //Check if time is now or later

        if (hourToSet > hourNow) return true;

        if(hourToSet >= hourNow){

            if(minToSet >= minNow){

                return true;
            }
        }

        //Time is past

        return false;

    }
}