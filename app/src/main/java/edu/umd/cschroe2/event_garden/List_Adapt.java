package edu.umd.cschroe2.event_garden;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Christine Schroeder on 4/8/2016.
 */
public class List_Adapt extends BaseAdapter {

    private final Context mContext;
    private final List<Event> temp = new ArrayList<Event>();
    //private final List<Event> filtered = new ArrayList<Event>();



    public List_Adapt(Context context) {

        mContext = context;
    }

    public void sorted_add(Event f) {
        boolean added = false;
        for(int i = 0; i < temp.size(); i++){
            Event event = temp.get(i);
            String[] old_date = event.date.split("-");
            String[] new_date = f.date.split("-");
            if(Integer.parseInt(new_date[0]) < Integer.parseInt(old_date[0])){ //check month
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
        date.setText(event.date);
        TextView time = (TextView) itemLayout.findViewById(R.id.time);
        time.setText(event.time);
        TextView address = (TextView) itemLayout.findViewById(R.id.address);
        address.setText(event.location);

        Button attend = (Button) itemLayout.findViewById(R.id.attend);
        attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "You are attending "+event.event_name+"!", Toast.LENGTH_SHORT).show();
                event.attending=true;
                TextView attending_text = (TextView) itemLayout.findViewById(R.id.attending_text);
                attending_text.setVisibility(View.VISIBLE);
            }
        });

        Button collapse = (Button) itemLayout.findViewById(R.id.collapse);
        collapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandable_layout.setVisibility(View.GONE);
            }
        });
        return itemLayout;

    }

    @Override
    public void notifyDataSetChanged(){

        super.notifyDataSetChanged();
    }


}
