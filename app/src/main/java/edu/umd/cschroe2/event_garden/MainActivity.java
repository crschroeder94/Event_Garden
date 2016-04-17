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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TabHost;
import android.widget.TabWidget;
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

public class MainActivity extends AppCompatActivity {

    int ADD_EVENT_REQUEST=1;
    FragmentTabHost tabHost;
    public ArrayList<String> attending;
    Filter filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        setTabColor();

        //list_adapt = new List_Adapt(getApplicationContext());

        android.support.design.widget.FloatingActionButton add_event =
                (android.support.design.widget.FloatingActionButton)findViewById(R.id.addevent);
        add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEvent.class);
                startActivityForResult(intent, ADD_EVENT_REQUEST);
            }
        });

        android.support.design.widget.FloatingActionButton filters =
                (android.support.design.widget.FloatingActionButton)findViewById(R.id.filters);
        filters.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
                enviro.setChecked(filter.filter_categories.get("Environmental"));
                rec.setChecked(filter.filter_categories.get("Recreation"));
                arts.setChecked(filter.filter_categories.get("Arts"));

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
            }
        });


        android.support.design.widget.FloatingActionButton profile =
                (android.support.design.widget.FloatingActionButton)findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Profile.class);
                startActivity(intent);
            }
        });

        android.support.design.widget.FloatingActionButton search =
                (android.support.design.widget.FloatingActionButton)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void filter_checkboxes( Dialog dialog){
        CheckBox enviro = (CheckBox) dialog.findViewById(R.id.checkbox_enviro);
        CheckBox rec = (CheckBox) dialog.findViewById(R.id.checkbox_rec);
        CheckBox arts = (CheckBox) dialog.findViewById(R.id.checkbox_arts);
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
    }

    //INSIDE OF FILTERS
    /*public void onFilterCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()) {
            case R.id.checkbox_rec:
                if (checked) {
                    filter.addFilter("Recreation");
                }
                break;
            case R.id.checkbox_arts:
                if (checked) {
                    filter.addFilter("Arts");
                }

                    break;
            case R.id.checkbox_enviro:
                if (checked) {
                    filter.addFilter("Environmental");
                }
                    break;

        }
    }*/

    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }

    //Change The Backgournd Color of Tabs
    //http://stackoverflow.com/questions/9356546/is-it-possible-to-change-the-color-of-selected-tab-in-android
    public void setTabColor() {

        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.white)); //unselected

        if(tabHost.getCurrentTab()==0)
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(getResources().getColor(R.color.green)); //1st tab selected
        else
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(getResources().getColor(R.color.green)); //2nd tab selected
    }

    //http://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
    //http://stackoverflow.com/questions/23467612/communication-between-fragments-of-fragment-tab-host
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i){
        if(requestCode == ADD_EVENT_REQUEST && resultCode == RESULT_OK){
            //add event object to adapter
            Event event = (Event) i.getSerializableExtra("event");
            String curr_tab_tag = tabHost.getCurrentTabTag();
            //Log.i("current tab tag",curr_tab_tag);
            //List_Fragment f = (List_Fragment) getSupportFragmentManager().findFragmentByTag(curr_tab_tag);
            //f.addtoAdapt(event);
            List_Fragment list =(List_Fragment)getSupportFragmentManager().findFragmentByTag("tab1");
            Map_Fragment map =(Map_Fragment)getSupportFragmentManager().findFragmentByTag("tab2");
            if(map != null){
                map.addMarker(event);
            }else{

            }
            list.addtoAdapt(event);


        }
    }




}
