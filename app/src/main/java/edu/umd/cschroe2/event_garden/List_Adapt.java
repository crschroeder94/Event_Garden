package edu.umd.cschroe2.event_garden;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christine Schroeder on 4/8/2016.
 */
public class List_Adapt extends BaseAdapter {

    private final Context mContext;
    private final List<Event> temp = new ArrayList<Event>();



    public List_Adapt(Context context) {

        mContext = context;
    }

    public void add(Event f) {
        temp.add(f);
        notifyDataSetChanged();
    }

    public void clear() {

        temp.clear();
        notifyDataSetChanged();

    }

    // Returns the number of ToDoItems

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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Event event = (Event) getItem(position);
        RelativeLayout itemLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.event_item, parent, false);
        TextView t = (TextView)itemLayout.findViewById(R.id.text);
        t.setText(event.event_name);
        TextView text = (TextView) itemLayout.findViewById(R.id.event_info);
        text.setText(event.description);
        Button button = (Button) itemLayout.findViewById(R.id.attend);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"You are attending!",Toast.LENGTH_SHORT).show();
                //add to attending here
                //somehow recollapse the list
            }
        });
        return itemLayout;
    }

    public void onCheckBoxClicked(View view){
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_recreation:
                if (checked) {

                };
            case R.id.checkbox_environmental:
                if (checked) {

                };
        }
    }


}
