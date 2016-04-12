package edu.umd.cschroe2.event_garden;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    int ADD_EVENT_REQUEST=1;
    FragmentTabHost tabHost;
    ArrayList<String> attending;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        attending = new ArrayList<String>();

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
        });
        setTabColor();

        //list_adapt = new List_Adapt(getApplicationContext());

        android.support.design.widget.FloatingActionButton add_event =
                (android.support.design.widget.FloatingActionButton)findViewById(R.id.addevent);
        add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddEvent.class);
                startActivityForResult(intent, ADD_EVENT_REQUEST);
            }
        });

        android.support.design.widget.FloatingActionButton filters =
                (android.support.design.widget.FloatingActionButton)findViewById(R.id.filters);
        filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            Log.i("current tab tag",curr_tab_tag);
            //List_Fragment f = (List_Fragment) getSupportFragmentManager().findFragmentByTag(curr_tab_tag);
            //f.addtoAdapt(event);
            List_Fragment list =(List_Fragment)getSupportFragmentManager().findFragmentByTag("tab1");
            Map_Fragment map =(Map_Fragment)getSupportFragmentManager().findFragmentByTag("tab2");
            map.addMarker(event);
            list.addtoAdapt(event);
//TO DO these both do not save

        }
    }




}
