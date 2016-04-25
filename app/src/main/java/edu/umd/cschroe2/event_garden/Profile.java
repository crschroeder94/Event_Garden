package edu.umd.cschroe2.event_garden;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
}
