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
import br.com.mauker.materialsearchview.MaterialSearchView;
import br.com.mauker.materialsearchview.MaterialSearchView.OnQueryTextListener;


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
    MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        setSupportActionBar(myToolbar);
        eventGardenDatabase = DatabaseHelper.getInstance(this);
        String org_name = getIntent().getStringExtra("org_name");
        eventsHosted = (TextView) findViewById(R.id.eventsHosted);
        desc = (TextView) findViewById(R.id.desc);
        seeEvents = (mehdi.sakout.fancybuttons.FancyButton) findViewById(R.id.seeEvents);
        name = (TextView) findViewById(R.id.profileName);
        if(org_name != null){
            if(org_name.equals("DC Homeless Shelter")){
                name.setText("DC Homeless Shelter");
                eventsHosted.setText("12");
                desc.setText("We're a non profit organization based on helping the homeless. We're based in Arlington VA.");
            }else if(org_name.equals("Habitat for Humanity")){
                name.setText("Habitat for Humanity");
                desc.setText("We're a non profit organization to fight for loss of habitat. We're based in Rockville MD.");
                eventsHosted.setText("14");
            }else if(org_name.equals("Anacostia Watershed Society")){
                name.setText("Anacostia Watershed Society");
                eventsHosted.setText("4");
                desc.setText("We're a non profit organization to clean up the Anacostia. We're based in College Park MD.");
            }
        }



        seeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile.this, PastEvents.class);
                i.putExtra("Events", DatabaseHelper.getInstance(getApplicationContext()).getAllEvents());
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

                searchView.openSearch();
                ArrayList<String> profileNames = new ArrayList<String>();
                /*ArrayList<Event> allEvents = eventGardenDatabase.getAllEvents();
                for (Event e : allEvents) {
                    eventNames.add(e.event_name);
                }
                searchView.addSuggestions(eventNames);
*/
                profileNames.add("DC Homeless Shelter");
                profileNames.add("Habitat for Humanity");
                profileNames.add("Anacostia Watershed Society");
                searchView.addSuggestions(profileNames);
                searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        Log.i("Query is ", query);
                        Intent intent = new Intent(Profile.this, Profile.class);
                        intent.putExtra("org_name", query);

                        startActivity(intent);

                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        Log.i("newText is ", newText);
                        return true;
                    }
                });


                return true;
            case R.id.profile:
                Intent i = new Intent(Profile.this ,Profile.class );
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
