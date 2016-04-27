package edu.umd.cschroe2.event_garden;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Christine Schroeder on 4/9/2016.
 */
public class Profile extends AppCompatActivity {
    Button seeEvents;
    TextView desc;
    TextView eventsHosted;
    TextView name;

    String description;
    int reputation;
    int numEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        eventsHosted = (TextView) findViewById(R.id.eventsHosted);
        desc = (TextView) findViewById(R.id.desc);
        seeEvents = (Button) findViewById(R.id.seeEvents);
        name = (TextView) findViewById(R.id.profileName);

        seeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile.this, PastEvents.class);
                startActivity(i);
            }
        });
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
            case R.id.add:

                return true;
            case R.id.profile:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
