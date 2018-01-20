package com.karlzone.carheater;

import android.app.AlertDialog;
import android.app.DatePickerDialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.Calendar;



public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        final Button cartimer = (Button) findViewById(R.id.btnTimer);

        final ImageButton heaterToggle = (ImageButton) findViewById(R.id.heat_toggle);

        final domoticzDataRetriever domo = new domoticzDataRetriever(this);



        SharedPreferences SharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);


        //Check if a server is set
        if (SharedPrefs.getString("domoticz_server",null).equals("") || SharedPrefs.getString("domoticz_server", null) == null) {
            Toast.makeText(this,"Ställ in server adress", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder startDialog = new AlertDialog.Builder(this);

            startDialog.setTitle("Server inställning");
            startDialog.setMessage("Du har inte angivit någon server att ansluta till. Vill du göra det nu?");

            startDialog.setNegativeButton("Nej", null);

            startDialog.setPositiveButton("Ja", new AlertDialog.OnClickListener(){

                public void onClick(DialogInterface dialog, int wich ){

                    Intent optionsOpen = new Intent(MainActivity.this, OptionsActivity.class);

                    startActivity(optionsOpen);
                }

            });

            startDialog.show();
        }
        else {
            Toast.makeText(this, "Server: "+SharedPrefs.getString("domoticz_server", null), Toast.LENGTH_SHORT).show();
        }

        updateStatus();

        fetchTemperature();

        //Clicking Heater Toggle Button
        heaterToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

               //Toggle heater on click
                domo.toggleSwitch(new VolleyCallbackInterface() {

                    //Successful click
                    @Override
                    public void onJSONSuccess(JSONObject dataresponse) {

                        //Update the status of Heater button
                        updateStatus();

                    }
                });

            }

        });

        /*Clicking the "When are you leaving?" button*/
        cartimer.setOnClickListener(new View.OnClickListener() {


            String quicktimer_timeset;
            String quicktimer_dateset;

            @Override
            public void onClick(View v) {

                AlertDialog.Builder quicktimerDialog = new AlertDialog.Builder(MainActivity.this);

                View aView = getLayoutInflater().inflate(R.layout.quick_timer_dialog, null);

                final TextView nTime = (TextView) aView.findViewById(R.id.quick_timer_timetxt);
                final TextView nDate = (TextView) aView.findViewById(R.id.quick_timer_datetxt);

                final Calendar calendar = Calendar.getInstance();

                /*Start the time picker*/
                nTime.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v){

                        int hournow = calendar.get(Calendar.HOUR_OF_DAY);
                        int minutesnow = calendar.get(Calendar.MINUTE);

                        TimePickerDialog timepick = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {

                                      @Override
                                      public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                            quicktimer_timeset = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);

                                          nTime.setText(quicktimer_timeset);

                            }
                        }, hournow, minutesnow, true);

                                      timepick.show();
                                        nTime.setError(null);
                    }

                });

                /*Start the date picker*/
                nDate.setOnClickListener(new View.OnClickListener() {

                    int yearnow = calendar.get(Calendar.YEAR);
                    int monthnow = calendar.get(Calendar.MONTH);
                    int dayofmonthnow = calendar.get(Calendar.DAY_OF_MONTH);

                    @Override
                    public void onClick(View v) {

                        DatePickerDialog datepick = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                                                     @Override
                                                     public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {quicktimer_dateset = year + "-" + String.format("%02d", month+1) + "-" + String.format("%02d", dayOfMonth);

                                                         nDate.setText(quicktimer_dateset);

                                                     }
                                                 }, yearnow, monthnow, dayofmonthnow);

                        datepick.show();
                        nDate.setError(null);
                    }
                });

                quicktimerDialog.setView(aView);

                quicktimerDialog.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                quicktimerDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


                final AlertDialog dialog = quicktimerDialog.create();

                dialog.setCanceledOnTouchOutside(false);


                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){

                    @Override

                    public void onClick(View v){


                        InputMatcher validate = new InputMatcher();


                        /*If date is not valid input*/
                        if(!validate.validateDate(nDate.getText().toString())){

                            nDate.setError("Datum ej korrekt!");

                        /*If date is today & time is not valid input*/
                        }else if (validate.isDateToday(nDate.getText().toString()) && !validate.validateTime(nTime.getText().toString())){

                            nTime.setError("Tid ej korrekt!");

                        }

                        /*On successful time date validation*/
                        else {

                            /*Try to set timer*/
                            domo.setQuickTimer(new VolleyCallbackInterface() {

                                /*Timer successfully set*/
                                @Override
                                public void onJSONSuccess(JSONObject dataresponse) {

                                    try{

                                        /*If the heater was started at once*/
                                        if(dataresponse.getString("title").equals("SwitchLight")){

                                            Toast.makeText(MainActivity.this, "Tiden för timer har passerat, startar värmning nu.", Toast.LENGTH_LONG).show();
                                            updateStatus();
                                        }
                                        /*If a timer is set*/
                                        else if(dataresponse.getString("title").equals("AddTimer")){

                                            Toast.makeText(MainActivity.this, "Timer inställd: \n" + nDate.getText() + " " + nTime.getText(), Toast.LENGTH_LONG).show();

                                        }
                                        else {

                                            Toast.makeText(MainActivity.this, "Något gick fel!", Toast.LENGTH_LONG).show();

                                        }


                                    }
                                    catch (JSONException e){

                                    }

                                }
                            }, quicktimer_timeset, quicktimer_dateset);

                            /*Close dialog*/
                            dialog.dismiss();

                        }


                    }
                });
            }

        });


    }

    @Override
    protected void onResume(){
        super.onResume();

        updateStatus();
        fetchTemperature();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(MainActivity.this, OptionsActivity.class);
            startActivity(intent);

            return true;
            }

        if (id == R.id.action_timer){

            Intent intent = new Intent(MainActivity.this, TimerActivity.class);

            startActivity(intent);

            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /*  Update status  */

      void updateStatus() {


        final domoticzDataRetriever domo = new domoticzDataRetriever(this);



        domo.getIDXStatus(new VolleyCallbackInterface() {

            @Override

            public void onJSONSuccess(JSONObject dataresponse) {


                TextView heaterStatusText = (TextView) findViewById(R.id.heaterStatusText);
                ImageButton heaterToggle = (ImageButton) findViewById(R.id.heat_toggle);

                try {


                    JSONArray jsonArr = dataresponse.getJSONArray("result");

                    /*If JSON status is now ON*/
                    if (jsonArr.getJSONObject(0).getString("Status").equals("On")) {

                        heaterToggle.setColorFilter(Color.RED);
                        heaterStatusText.setText("Just nu värms bilen!");

                    /*If JSON status is now OFF*/
                    } else {

                        heaterToggle.setColorFilter(Color.BLACK);
                        heaterStatusText.setText("Bilen värms inte");

                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

        });
      }

    /* Get Temperature */

    void fetchTemperature() {

        SharedPreferences SharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final TextView mTextView = (TextView) findViewById(R.id.tmpTxt);

        //If temperature is not set preferences then exit
        if (!SharedPrefs.getBoolean("temperature_setting_switch", false)) {

            mTextView.setVisibility(View.INVISIBLE);
            return;
        }

        /*If no temperature device IDX is set*/
        else if (SharedPrefs.getString("temperature_idx", null) == null) {

            mTextView.setVisibility(View.INVISIBLE);

            Toast.makeText(this, "No Temperature IDX set", Toast.LENGTH_SHORT).show();
            return;

        }
        final domoticzDataRetriever domoTemperature = new domoticzDataRetriever(this);

        domoTemperature.getTemperature(new VolleyCallbackInterface() {

            @Override

            public void onJSONSuccess(JSONObject dataresponse) {


                try {


                    JSONArray jsonArr = dataresponse.getJSONArray("result");

                    mTextView.setText(jsonArr.getJSONObject(0).getString("Temp") + "\u2103");


                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

        });
    }




}