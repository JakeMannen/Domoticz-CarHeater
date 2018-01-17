package com.karlzone.carheater;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jocke on 2017-03-14.
 */

public class InputMatcher {

    String timeregex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
    String dateregex = "((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])";
    Pattern pattern;
    Matcher matcher;
    Boolean timeOkSet;
    Boolean dateOkSet;

    public InputMatcher() {


    }

    Boolean validateTime(String time_in){

        pattern.compile(timeregex);

        timeOkSet = pattern.matches(timeregex, time_in);

        Log.d("TIME SET OK",timeOkSet.toString());

        return timeOkSet;
    }

    Boolean validateDate(String date_in){

        pattern.compile(dateregex);

        dateOkSet = pattern.matches(dateregex, date_in);


        return dateOkSet;
    }

}
