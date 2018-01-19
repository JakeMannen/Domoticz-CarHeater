package com.karlzone.carheater;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

import static android.R.id.list;




public class TimerListAdapter extends ArrayAdapter<DomoticzTimer> {

    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResourceId;
    private ArrayList<DomoticzTimer> mDataSource;

    public TimerListAdapter(Context context, int resource, ArrayList<DomoticzTimer> timers) {

        super(context, resource, timers);

        this.mContext = context;
        this.mDataSource = timers;
        this.layoutResourceId = resource;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }




    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final DomoticzTimer timer = mDataSource.get(position);

        if (convertView==null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.timer_row_layout,parent,false);
        }

        final ImageButton activateTimerButton = (ImageButton)convertView.findViewById(R.id.timerActiveButton);
        final ImageButton deleteTimerButton = (ImageButton)convertView.findViewById(R.id.timerDeleteButton);
        TextView timerDateTextView = (TextView)convertView.findViewById(R.id.timerDateTxt);
        TextView timerTypeTextView = (TextView)convertView.findViewById(R.id.timerTypeTxt);
        final domoticzDataRetriever domot = new domoticzDataRetriever(mContext);

        timerDateTextView.setText(timer.getDateString());
        timerTypeTextView.setText(timer.getTypeString());

        deleteTimerButton.setImageResource(R.drawable.ic_delete_black_48dp);

        if (timer.isTimerActive().equals("true")){

            activateTimerButton.setImageResource(R.drawable.ic_timer_black_48dp);
        }else{

            activateTimerButton.setImageResource(R.drawable.ic_timer_off_black_48dp);

        }


        deleteTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder adb=new AlertDialog.Builder(mContext);
                adb.setTitle("Ta bort?");
                adb.setMessage("Vill du verkligen ta bort timer");
                adb.setNegativeButton("Avbryt", null);
                adb.setPositiveButton("Ja", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        domot.deleteTimer(new VolleyCallbackInterface() {
                            @Override
                            public void onJSONSuccess(JSONObject dataresponse) {

                                ((TimerActivity)mContext).fetchTimerList();


                                Toast.makeText(mContext,"Timer borttagen",Toast.LENGTH_SHORT).show();

                            }
                        }, timer.getIDX());


                    }});
                adb.show();

            }
        });



        activateTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                domot.toggleActiveState(new VolleyCallbackInterface() {
                    @Override
                    public void onJSONSuccess(JSONObject dataresponse) {

                        ((TimerActivity)mContext).fetchTimerList();


                        if (timer.isTimerActive().equals("false")){

                            Toast.makeText(mContext,"Timer Aktiverad", Toast.LENGTH_SHORT).show();
                        }else {

                            Toast.makeText(mContext,"Timer Inaktiverad", Toast.LENGTH_SHORT).show();
                        }


                    }
                }, timer.getActiveToggleUrl());

            }
        });


        return convertView;


    }

}
