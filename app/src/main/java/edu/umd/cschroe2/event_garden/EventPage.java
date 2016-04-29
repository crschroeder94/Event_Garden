package edu.umd.cschroe2.event_garden;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Christine Schroeder on 4/27/2016.
 */
public class EventPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_page);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //http://stackoverflow.com/questions/12246388/remove-shadow-below-actionbar
        getSupportActionBar().setElevation(0);
        final DatabaseHelper db = DatabaseHelper.getInstance(this);

        final Event event = (Event) getIntent().getSerializableExtra("event");

        TextView name = (TextView) findViewById(R.id.event_name);
        name.setText(event.event_name);
        TextView date = (TextView) findViewById(R.id.event_date);
        date.setText(event.date);
        TextView time = (TextView) findViewById(R.id.event_time);
        time.setText(event.time);
        TextView address = (TextView) findViewById(R.id.event_address);
        address.setText(event.location);
        TextView descrip = (TextView) findViewById(R.id.description);
        descrip.setText(event.description);

        com.mikepenz.iconics.view.IconicsTextView categories = (com.mikepenz.iconics.view.IconicsTextView) findViewById(R.id.categories);
        //String curr_text = categories.getText().toString();
        String temp = "";
        if(event.hasFilter("Environmental")) {
            temp +="Environmental {faw_recycle}\n";
        }
        if(event.hasFilter("Recreation")) {
            temp +="Recreation {faw-futbol-o}\n";
        }
        if(event.hasFilter("Arts")) {
            temp +="Arts {faw-paint_brush}\n";
        }
        if(event.hasFilter("Animals")) {
            temp +="Animals {faw-paw}\n";
        }
        if(event.hasFilter("Social")) {
            temp +="Social {faw-glass}\n";
        }
        categories.setText(temp);

        ListView equipment_list = (ListView) findViewById(R.id.equip);
        ArrayList<String> equip = db.getAllEquip(event.id);
        Log.i("equip for id", event.id+"");
        Log.i("equip_size", equip.size()+"");
        Equipment_Adapter equipAdapter =
                new Equipment_Adapter(this, equip);
        equipment_list.setAdapter(equipAdapter);


        final mehdi.sakout.fancybuttons.FancyButton attend = (mehdi.sakout.fancybuttons.FancyButton) findViewById(R.id.attend);

        if(db.checkifAttending(event.id)) {
            attend.setText("UNATTEND");
        }else{
            attend.setText("ATTEND");
        }

        attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(attend.getText().equals("ATTEND")){
                    attend.setText("UNATTEND");
                    db.attendunattendEvent(event.id, true);
                }else{
                    attend.setText("ATTEND");
                    db.attendunattendEvent(event.id, false);
                }
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
            case R.id.profile:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
