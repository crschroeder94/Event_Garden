package edu.umd.cschroe2.event_garden;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.support.v4.app.Fragment;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Christine Schroeder on 4/8/2016.
 */
public class List_Fragment extends ListFragment {

    List_Adapt list_adapt;
    LinearLayout layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list, container, false);

        return view;
    }

    //http://stackoverflow.com/questions/3465841/how-to-change-visibility-of-layout-programaticly
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list_adapt = new List_Adapt(getActivity().getApplicationContext());
        getListView().setAdapter(list_adapt);
        getListView().setClickable(true);
        list_adapt.add(new Event("Event 1", "date", "time", "description", "location", new HashMap<String, Integer>(),new ArrayList<String>()));
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                layout = (LinearLayout) view.findViewById(R.id.hidden_layout);
                if(layout.getVisibility()==View.GONE) {
                    layout.setVisibility(View.VISIBLE);
                }else if(layout.getVisibility() == View.VISIBLE){
                    layout.setVisibility(View.GONE);
                }

            }
        });
    }

    public void addtoAdapt(Event a){
        list_adapt.add(a);
    }




}
