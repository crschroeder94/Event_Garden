package edu.umd.cschroe2.event_garden;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Christine Schroeder on 4/16/2016.
 */
public class Filter {

    //ArrayList<String> filt_categories;
    HashMap<String, Boolean> filter_categories;
    String radius;
    String date;


    public Filter(){

        //filt_categories=new ArrayList<String>();
        filter_categories= new HashMap<String,Boolean>();
        filter_categories.put("Recreation",true);
        filter_categories.put("Environmental",true);
        filter_categories.put("Arts",true);
    }

    public void setRadius(String rad){
        radius=rad;
    }

    public void setDate(String d){
        date=d;
    }

    //public void addFilter(String t){
        //filt_categories.add(t);
    //}
}
