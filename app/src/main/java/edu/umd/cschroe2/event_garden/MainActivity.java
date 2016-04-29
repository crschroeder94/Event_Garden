package edu.umd.cschroe2.event_garden;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ListActivity;
import android.app.ListFragment;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import snow.skittles.BaseSkittle;
import snow.skittles.SkittleBuilder;
import snow.skittles.SkittleLayout;
import snow.skittles.TextSkittle;

public class MainActivity extends AppCompatActivity {

    int ADD_EVENT_REQUEST=1;
    FragmentTabHost tabHost;
    public ArrayList<String> attending;
    Filter filter;

    DatabaseHelper eventGardenDatabase;
//https://github.com/mikepenz/Android-Iconics
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //http://stackoverflow.com/questions/12246388/remove-shadow-below-actionbar
        getSupportActionBar().setElevation(0);
        //getSupportActionBar().setIcon(R.drawable.plant_icon);
        // Setup database
        eventGardenDatabase = DatabaseHelper.getInstance(this);
        eventGardenDatabase.onCreate(eventGardenDatabase.getWritableDatabase());

        attending = new ArrayList<String>();
        filter=new Filter();

        //https://www.youtube.com/watch?v=QutovPrajXs
        tabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        //tabHost.setup();
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        tabHost.addTab(
                tabHost.newTabSpec("tab1").setIndicator("List", null),
                List_Fragment.class, null);

        tabHost.addTab(
                tabHost.newTabSpec("tab2").setIndicator("Map", null),
                Map_Fragment.class, null);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                setTabColor();
            }
        }); //try setting all the points up here

        //initial tab color set
        setTabColor();


//http://stackoverflow.com/questions/7815689/how-do-you-obtain-a-drawable-object-from-a-resource-id-in-android-package
//https://android-arsenal.com/details/1/2076
        final SkittleLayout skittleLayout = (SkittleLayout)findViewById(R.id.skittleLayout);
        final SkittleBuilder skittleBuilder = SkittleBuilder.newInstance((SkittleLayout) findViewById(R.id.skittleLayout), false);


        skittleBuilder.addSkittle(new TextSkittle.Builder("Add Event", ContextCompat.getColor(this, R.color.deep_blue),
                ContextCompat.getDrawable(this, android.R.drawable.ic_menu_add)).setTextBackground(
                ContextCompat.getColor(this, android.R.color.background_light)).build());

        skittleBuilder.addSkittle(new TextSkittle.Builder("Filter Events", ContextCompat.getColor(this, R.color.deep_blue),
                ContextCompat.getDrawable(this, android.R.drawable.ic_menu_sort_by_size)).setTextBackground(
                ContextCompat.getColor(this, android.R.color.background_light)).build());

        skittleBuilder.addSkittle(new TextSkittle.Builder("View Profile", ContextCompat.getColor(this, R.color.deep_blue),
                ContextCompat.getDrawable(this, android.R.drawable.ic_menu_myplaces)).setTextBackground(
                ContextCompat.getColor(this, android.R.color.background_light)).build());

        skittleBuilder.setSkittleClickListener(new SkittleBuilder.OnSkittleClickListener() {
            int up_down = 0;

            @Override
            public void onSkittleClick(BaseSkittle skittle, int position) {
                switch (position) {
                    case 0: //Add Event
                        Intent intent = new Intent(MainActivity.this, AddEvent.class);
                        startActivityForResult(intent, ADD_EVENT_REQUEST);
                        break;
                    case 1: //Filters
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.filter_dialog);
                        dialog.setTitle("Set Filters");
                        dialog.setCancelable(true);
                        Calendar calendar = Calendar.getInstance();
                        int d = calendar.get(Calendar.DAY_OF_MONTH);
                        int m = calendar.get(Calendar.MONTH);
                        String mon = "" + m;
                        String da = "" + d;
                        final EditText month = (EditText) dialog.findViewById(R.id.month);
                        month.setText(mon);
                        final EditText day = (EditText) dialog.findViewById(R.id.day);
                        day.setText(da);
                        final EditText radius = (EditText) dialog.findViewById(R.id.rad);

                        CheckBox enviro = (CheckBox) dialog.findViewById(R.id.checkbox_enviro);
                        CheckBox rec = (CheckBox) dialog.findViewById(R.id.checkbox_rec);
                        CheckBox arts = (CheckBox) dialog.findViewById(R.id.checkbox_arts);
                        CheckBox animals = (CheckBox) dialog.findViewById(R.id.checkbox_animals);
                        CheckBox social = (CheckBox) dialog.findViewById(R.id.checkbox_social);
                        enviro.setChecked(filter.filter_categories.get("Environmental"));
                        rec.setChecked(filter.filter_categories.get("Recreation"));
                        arts.setChecked(filter.filter_categories.get("Arts"));
                        animals.setChecked(filter.filter_categories.get("Environmental"));
                        social.setChecked(filter.filter_categories.get("Environmental"));

                        dialog.show();
                        Button saveButton = (Button) dialog.findViewById(R.id.save);
                        saveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                filter.setRadius(radius.getText().toString());
                                filter.setDate(month.getText().toString() + "-" + day.getText().toString() + "-2016");
                                filter_checkboxes(dialog);

                                List_Fragment list = (List_Fragment) getSupportFragmentManager().findFragmentByTag("tab1");
                                list.applyFilters(filter);
                                dialog.dismiss();
                            }
                        });
                        Button cancel = (Button) dialog.findViewById(R.id.cancel);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        break;
                    case 2: //Profile
                        Intent i = new Intent(MainActivity.this, Profile.class);
                        startActivity(i);
                        break;
                }
            }

            @Override
            public void onMainSkittleClick() {
                if (up_down == 0) {
                    up_down = 1;
                    Bitmap bmpOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.up);
                    Bitmap bmResult = Bitmap.createBitmap(bmpOriginal.getWidth(), bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas tempCanvas = new Canvas(bmResult);
                    tempCanvas.rotate(180, bmpOriginal.getWidth() / 2, bmpOriginal.getHeight() / 2);
                    tempCanvas.drawBitmap(bmpOriginal, 0, 0, null);
                    Drawable drawable = new BitmapDrawable(getResources(), bmResult);
                    skittleBuilder.changeMainSkittleIcon(drawable);
                } else {
                    skittleBuilder.changeMainSkittleIcon(getResources().getDrawable(R.drawable.up));
                    up_down = 0;
                }


            }
        });


    }


    public void filter_checkboxes( Dialog dialog){
        CheckBox enviro = (CheckBox) dialog.findViewById(R.id.checkbox_enviro);
        CheckBox rec = (CheckBox) dialog.findViewById(R.id.checkbox_rec);
        CheckBox arts = (CheckBox) dialog.findViewById(R.id.checkbox_arts);
        CheckBox animals = (CheckBox) dialog.findViewById(R.id.checkbox_animals);
        CheckBox social = (CheckBox) dialog.findViewById(R.id.checkbox_social);
        if(enviro.isChecked()){
            filter.filter_categories.put("Environmental",true);
        }else{
            filter.filter_categories.put("Environmental",false);
        }

        if(rec.isChecked()){
            filter.filter_categories.put("Recreation",true);
        }else{
            filter.filter_categories.put("Recreation",false);
        }

        if(arts.isChecked()){
            filter.filter_categories.put("Arts", true);
        }else{
            filter.filter_categories.put("Arts", false);
        }

        if(animals.isChecked()){
            filter.filter_categories.put("Animals",true);
        }else{
            filter.filter_categories.put("Animals",false);
        }

        if(social.isChecked()){
            filter.filter_categories.put("Social",true);
        }else{
            filter.filter_categories.put("Social",false);
        }
    }


    //Change The Backgournd Color of Tabs
    //http://stackoverflow.com/questions/9356546/is-it-possible-to-change-the-color-of-selected-tab-in-android
    public void setTabColor() {

        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.white)); //unselected

        if(tabHost.getCurrentTab()==0)
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(getResources().getColor(R.color.colorAccent)); //1st tab selected
        else
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(getResources().getColor(R.color.colorAccent)); //2nd tab selected
    }

    //http://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
    //http://stackoverflow.com/questions/23467612/communication-between-fragments-of-fragment-tab-host
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i){
        if(requestCode == ADD_EVENT_REQUEST && resultCode == RESULT_OK){

            //add event object to SQL database
            Event event = (Event) i.getSerializableExtra("event");
            long eventID = eventGardenDatabase.insertEvent(event);
            if (eventID == -1){
                Toast.makeText(MainActivity.this, "Unable to access Sqlite database.", Toast.LENGTH_SHORT).show();
            }

            // Todo This is a debug statement for the sql database.
            Log.d("Events found", ""+eventGardenDatabase.getAllEvents().size());
            for (Event e : eventGardenDatabase.getAllEvents()){
                Log.d("EVENTNAME", e.event_name);
                Log.d("event date", e.date);
                Log.d("event time", e.time);
                Log.d("event location", e.location);
                Log.d("event description", e.description);
                for (String s : e.filters){
                    Log.d("event category", s);
                }
                Log.d("Event","Equipment");
                java.util.Iterator iter = e.equipment.entrySet().iterator();
                while (iter.hasNext()){
                    Map.Entry pair = (Map.Entry) iter.next();
                    Log.d("Equipment Name",(String)pair.getKey());
                    Log.d("Equipment quantity",pair.getValue().toString());
                }
            }
            // Todo End debug for sqldatabase.

            // Add event to adapter.
            String curr_tab_tag = tabHost.getCurrentTabTag();
            //Log.i("current tab tag",curr_tab_tag);
            //List_Fragment f = (List_Fragment) getSupportFragmentManager().findFragmentByTag(curr_tab_tag);
            //f.addtoAdapt(event);
            List_Fragment list =(List_Fragment)getSupportFragmentManager().findFragmentByTag("tab1");
            Map_Fragment map =(Map_Fragment)getSupportFragmentManager().findFragmentByTag("tab2");
            if(map != null){ //add markers to array?
                map.addMarker(event);
            }
            list.addtoAdapt(event);


        }
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
