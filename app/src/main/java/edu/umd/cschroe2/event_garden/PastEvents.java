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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
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

public class PastEvents extends AppCompatActivity {
    ListView lv;
    List_Adapt la;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.past_events);
        la = new List_Adapt(getApplicationContext(), PastEvents.this);

        lv = (ListView) findViewById(R.id.my_list);
        lv.setAdapter(la);

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        ArrayList<Event> events = databaseHelper.getDummyEvents();
        for (Event event : events) {
            la.add(event);
        }


    }

}