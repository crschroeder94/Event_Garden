package edu.umd.cschroe2.event_garden;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Christine Schroeder on 4/9/2016.
 */
public class Profile extends AppCompatActivity {
    mehdi.sakout.fancybuttons.FancyButton seeEvents;
    TextView desc;
    TextView eventsHosted;
    TextView name;

    String description;
    int reputation;
    int numEvents;
    DatabaseHelper eventGardenDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        eventGardenDatabase = DatabaseHelper.getInstance(this);
        //String a = getIntent().getStringExtra("name");


        eventsHosted = (TextView) findViewById(R.id.eventsHosted);
        desc = (TextView) findViewById(R.id.desc);
        seeEvents = (mehdi.sakout.fancybuttons.FancyButton) findViewById(R.id.seeEvents);
        name = (TextView) findViewById(R.id.profileName);

        seeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile.this, PastEvents.class);
                startActivity(i);
            }
        });

        /*if(a.equals("homeless shelter a")){
            name.setText(a);
            desc.setText()
        }*/

        // TODO DEBUG FOR GET EVENTS FROM PROFILE
        ArrayList<Event> eventsArrayList = eventGardenDatabase.getAllEventsForProfile(1);
        for (Event e : eventsArrayList){
            Log.d("EventsForProfile Output", e.event_name);
        }
        // TODO END DEBUG
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.search:

                return true;
            case R.id.profile:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
