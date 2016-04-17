package edu.umd.cschroe2.event_garden;

import java.util.ArrayList;

/**
 * Created by Christine Schroeder on 4/16/2016.
 */
public class Filter {

    ArrayList<String> filt_categories;
    String radius;
    String date;
    String time;


    public Filter(){
        filt_categories=new ArrayList<String>();
    }

    public void setRadius(String rad){
        radius=rad;
    }

    public void setDate(String d){
        date=d;
    }

    public void addFilter(String t){
        filt_categories.add(t);
    }
}
