package edu.umd.cschroe2.event_garden;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Christine Schroeder on 4/11/2016.
 */
public class Event implements Serializable {

    String event_name;
    String date; //date in format xx-xx-xxxx
    String time; //time in military time xx:xx
    String description;
    String location;
    HashMap<String, Integer> equipment;
    ArrayList<String> filters;
    boolean attending;
    int id;

    public Event(String event_name_in, String date_in, String time_in, String descrip_in, String location_in,
                 HashMap<String,Integer> equip_in, ArrayList<String> cat_in){
        event_name = event_name_in;
        date = date_in;
        time = time_in;
        description = descrip_in;
        location = location_in;
        equipment = equip_in;
        filters = cat_in;
        attending = false;
    }

    public boolean hasFilter(String filter){
        for(String i : filters){
            if(i.equals(filter)){
                return true;
            }
        }
        return false;
    }

    public void setId(int id_in){
        id = id_in;
        Log.i("Setting id",""+id);
    }
}
