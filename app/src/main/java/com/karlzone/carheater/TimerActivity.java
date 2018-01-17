package com.karlzone.carheater;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);



        fetchTimerList();

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

            Intent intent = new Intent(TimerActivity.this, OptionsActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.action_timer){

            Intent intent = new Intent(TimerActivity.this, TimerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchTimerList() {

        domoticzDataRetriever domotimer = new domoticzDataRetriever(this);

        domotimer.getTimers(new VolleyCallbackInterface() {
            @Override
            public void onJSONSuccess(JSONObject dataresponse) {



                try {

                    JSONArray TimersArray = dataresponse.getJSONArray("result");

                    ListView Timer_lv = (ListView) findViewById(R.id.ListViewTimer);
                    ArrayList<DomoticzTimer> TimerLst = new ArrayList<DomoticzTimer>();

                    for (int i=0; i < TimersArray.length(); i++) {

                        JSONObject JsonTimerItem = TimersArray.getJSONObject(i);

                        //DomoticzTimer domoticzTimer = new DomoticzTimer(JsonTimerItem);

                        TimerLst.add(new DomoticzTimer(JsonTimerItem));


                    }


                    TimerListAdapter adapter = new TimerListAdapter(TimerActivity.this, R.layout.timer_row_layout, TimerLst);
                    Timer_lv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();





                }catch (JSONException e){

                    e.printStackTrace();
                }
            }
        });

    }

}
