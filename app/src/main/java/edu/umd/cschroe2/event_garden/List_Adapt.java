package edu.umd.cschroe2.event_garden;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christine Schroeder on 4/8/2016.
 */
public class List_Adapt extends BaseAdapter {

    private final Context mContext;
    private final List<Event> temp = new ArrayList<Event>();
    Activity activity;
    int VIEW_EVENT_REQUEST = 1;
    DatabaseHelper db;
    //private final List<Event> filtered = new ArrayList<Event>();



    public List_Adapt(Context context, Activity a) {
        activity = a;
        mContext = context;
        db = DatabaseHelper.getInstance(context);
    }

    public void sorted_add(Event f) {
        boolean added = false;
        for(int i = 0; i < temp.size(); i++){
            Event event = temp.get(i);
            String[] old_date = event.date.split("-");
            String[] new_date = f.date.split("-");
            if(Integer.parseInt(new_date[0]) < Integer.parseInt(old_date[0])){ //check month
                //check distance
                    temp.add(i,f);
                    added = true;
                    break;
            }else if(Integer.parseInt(new_date[0]) == Integer.parseInt(old_date[0])){ //same month
                if(Integer.parseInt(old_date[1]) > Integer.parseInt(new_date[1])) { //check day
                    temp.add(i,f);
                    added = true;
                    break;
                }
            }
        }
        if(!added) {
            temp.add(f);
        }
        notifyDataSetChanged();
    }

    public void add(Event f){ //for adding dummy events quickly
        temp.add(f);
        notifyDataSetChanged();
    }

    public void clear() {

        temp.clear();
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {

        return temp.size();

    }
    @Override
    public Event getItem(int pos) {

        return temp.get(pos);

    }

    @Override
    public long getItemId(int pos) {

        return pos;

    }

    public void filterEvents(Filter f, List_Adapt unfiltered){
            for(String category : f.filter_categories.keySet()){
                if(f.filter_categories.get(category) == true) { //check if category is checked
                    for (Object a : unfiltered.getArr()) {
                        Event curr = (Event) a;
                        if (curr.filters.contains(category)) { //check if filter in list of filters
                            if (!temp.contains(curr)) {
                                sorted_add(curr);
                            }
                        }
                    }
                }
            }
    }

    public List getArr(){
        return temp;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Event event = getItem(position);
        final RelativeLayout itemLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.event_item, parent, false);
        final LinearLayout expandable_layout = (LinearLayout) itemLayout.findViewById(R.id.hidden_layout);
        TextView event_name = (TextView)itemLayout.findViewById(R.id.text);
        event_name.setText(event.event_name);
        TextView info = (TextView) itemLayout.findViewById(R.id.event_info);
        info.setText(event.description);
        TextView date = (TextView) itemLayout.findViewById(R.id.date);
        String pretty_date = prettifyDate(event.date);
        date.setText(pretty_date);
        TextView time = (TextView) itemLayout.findViewById(R.id.time);
        time.setText(event.time);
        TextView address = (TextView) itemLayout.findViewById(R.id.address);
        address.setText(event.location);

        String cat = "";
        com.mikepenz.iconics.view.IconicsTextView categories = (com.mikepenz.iconics.view.IconicsTextView) itemLayout.findViewById(R.id.categories);
        for(String i : event.filters){
            if(i.equals("Environmental")){
                cat += "{faw_recycle} ";
            }
            if(i.equals("Recreation")){
                cat += "{faw-futbol-o}";
            }
            if(i.equals("Arts")){
                cat += "{faw-paint_brush}";
            }
            if(i.equals("Animals")){
                cat += "{faw-paw}";
            }
            if(i.equals("Social")){
                cat += "{faw-glass}";
            }
        }
        categories.setText(cat);

        TextView attending_text = (TextView) itemLayout.findViewById(R.id.attending_text);
        //https://github.com/medyo/Fancybuttons
        final mehdi.sakout.fancybuttons.FancyButton attend = (mehdi.sakout.fancybuttons.FancyButton) itemLayout.findViewById(R.id.attend);

        Log.i("check if attending", "id: " + event.id + ": " + db.checkIfAttending(event.id));
        changeAttendinglayout(attending_text, attend, event);
        attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView attending_text = (TextView) itemLayout.findViewById(R.id.attending_text);
                if (db.checkIfAttending(event.id)) {
                    db.changeAttendance(event.id, false);
                } else {
                    db.changeAttendance(event.id, true);
                }
                changeAttendinglayout(attending_text, attend, event);
            }
        });

        final mehdi.sakout.fancybuttons.FancyButton event_page = (mehdi.sakout.fancybuttons.FancyButton) itemLayout.findViewById(R.id.gotoevent);
        event_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, EventPage.class);
                i.putExtra("event", event);
                activity.startActivity(i);
            }

        });

        final TextView seeOnMap = (TextView) itemLayout.findViewById(R.id.see_on_map);
        seeOnMap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainActivity.getTabHost().setCurrentTabByTag("tab2");
            }
        });
        return itemLayout;

    }

    public boolean changeAttendinglayout(TextView attending_text,mehdi.sakout.fancybuttons.FancyButton attend, Event event){

        if(db.checkIfAttending(event.id)){
            event.attending = true;
            attending_text.setVisibility(View.VISIBLE);
            attend.setText("UNATTEND");
            return true;
        }else{
            event.attending = false;
            attending_text.setVisibility(View.GONE);
            attend.setText("ATTEND");
            return false;
        }
    }


    public String prettifyDate(String old){ //old date in format xx-xx-xxxx
        String pretty = "";
        String[] months = mContext.getResources().getStringArray(R.array.months);
        String[] arr = old.split("-");
        pretty+=months[Integer.parseInt(arr[0]) - 1];
        pretty+=" " + Integer.parseInt(arr[1]);
        return pretty;
    }

    @Override
    public void notifyDataSetChanged(){

        super.notifyDataSetChanged();
    }


}
