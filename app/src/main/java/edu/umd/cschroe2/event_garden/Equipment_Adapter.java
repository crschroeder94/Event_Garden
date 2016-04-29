package edu.umd.cschroe2.event_garden;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;

/**
 * Created by Christine Schroeder on 4/27/2016.
 */
public class Equipment_Adapter extends ArrayAdapter<String> {

        public Equipment_Adapter(Context context, ArrayList<String> equipment) {
            super(context, 0, equipment);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            String equip = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.equipment_checkbox, parent, false);
            }

            //CheckBox equip_check = (CheckBox) convertView.findViewById(R.id.equip_check);
            //equip_check.setText(equip);

            return convertView;
        }
}

