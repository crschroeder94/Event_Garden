package edu.umd.cschroe2.event_garden;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Christine Schroeder on 4/9/2016.
 */
public class AddEvent extends AppCompatActivity {

    private Button date;
    private Button time;
    private String equipment_list;
    private String quantity_list;
    private int equip_count = 0;
    private String date_string;
    private String time_string;
    private EditText equipment_1;
    private EditText quantity_1;
    ArrayList<String> filters;
    Geocoder coder;

    private Button add_equip;

    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //http://stackoverflow.com/questions/12246388/remove-shadow-below-actionbar
        getSupportActionBar().setElevation(0);

        //set up coder.
        coder = new Geocoder(this.getApplicationContext());

        dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        date = (Button) findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDateField();
            }
        });

        time = (Button) findViewById(R.id.time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimeField();
            }
        });
        filters = new ArrayList<String>();
        equipment_1 = (EditText) findViewById(R.id.equip_1);
        quantity_1 = (EditText) findViewById(R.id.equip_1_quant);
        add_equip = (Button) findViewById(R.id.add_equip);
        add_equip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(equipment_1.getText().toString()) || "".equals(quantity_1.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Please fill in the text before adding the equipment.", Toast.LENGTH_SHORT).show();
                } else {
                    if (equip_count == 0) {
                        equipment_list = equipment_1.getText().toString();
                        quantity_list = quantity_1.getText().toString();
                    } else {
                        equipment_list.concat(", " + equipment_1.getText().toString());
                        quantity_list.concat(", " + quantity_1.getText().toString());
                    }
                }
                equipment_1.setText("");
                quantity_1.setText("");
            }
        });


        Button create = (Button) findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText title = (EditText) findViewById(R.id.name);
                String name = title.getText().toString();
                //http://stackoverflow.com/questions/6304463/how-to-create-edittext-more-than-one-line-in-android
                EditText descript = (EditText) findViewById(R.id.description);
                String description = descript.getText().toString();
                EditText address = (EditText) findViewById(R.id.address);
                TextView errors = (TextView) findViewById(R.id.errors);

                String location = address.getText().toString();
                String temp = validateData(name,date_string,location,filters,time_string);
                if(temp.equals("")) {
                    HashMap<String, Integer> hash = equipmentAdding(v);
                    Event event = new Event(name, date_string, time_string, description, location, hash, filters);
                    Intent i = new Intent();
                    i.putExtra("event", event);
                    setResult(RESULT_OK, i);
                    finish();
                }else{
                    errors.setText(temp);
                    errors.setVisibility(View.VISIBLE);
                }
            }
        });

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(RESULT_CANCELED, i);
                finish();
            }
        });


    }

    public String validateData(String name,String date_string,String location,ArrayList filters,String time){
        String errors = "";
        if(name.equals("")){
            errors += "-No Event Name entered\n";
        }
        if(date_string == null){
            errors += "-Date Not Entered\n";
        }else {
            String[] date_arr = date_string.split("-");
            Calendar c = Calendar.getInstance();
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            Log.i("Month check","date arr month: "+Integer.parseInt(date_arr[0])+"actual month: "+(month+1));
            Log.i("day check","date arr day: "+Integer.parseInt(date_arr[1])+"actual month: "+day);
            if(Integer.parseInt(date_arr[0]) < (month+1)){
                errors += "-Date Invalid\n";
            }else if(Integer.parseInt(date_arr[0]) == (month+1) && Integer.parseInt(date_arr[1]) < day){
                errors += "-Date Invalid\n";
            }
        }
        if(filters.isEmpty()){
            errors += "-No filters entered\n";
        }
        if(time_string==null){
            errors += "-No time entered\n";
        }

        errors+=validateLocation(location);
        return errors;
    }

    public String validateLocation(String location){
        String temp = "";
        try {
            if(!location.equals("")){
                List<Address> possibleAddresses = coder.getFromLocationName(location, 1);

                if (possibleAddresses.isEmpty()) {
                    temp+="-Could not find address\n";
                }
            }else{
                temp+="-No Address Entered\n";
            }

        }catch(IOException e){
            Toast.makeText(getApplicationContext(),"Could not lookup address. Does this app have internet access?",Toast.LENGTH_LONG).show();
        }
        return temp;
    }

    public HashMap<String,Integer> equipmentAdding(View v){
        return new HashMap<>();
    }

    //INSIDE OF FILTERS
    public void onCreateCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()) {
            case R.id.checkbox_rec:
                if (checked) {
                    filters.add("Recreation");
                }
                break;
            case R.id.checkbox_arts:
                if (checked) {
                    filters.add("Arts");
                }

                break;
            case R.id.checkbox_enviro:
                if (checked) {
                    filters.add("Environmental");
                }
                break;
            case R.id.checkbox_animals:
                if (checked) {
                    filters.add("Animals");
                }
                break;
            case R.id.checkbox_social:
                if (checked) {
                    filters.add("Social");
                }
                break;
        }
    }

//http://androidopentutorials.com/android-datepickerdialog-on-edittext-click-event/
    private void setDateField() {

        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog DatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    date.setText(dateFormatter.format(newDate.getTime()));
                    date_string=dateFormatter.format(newDate.getTime());
                }

            },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            DatePickerDialog.show();

    }

    //http://stackoverflow.com/questions/17901946/timepicker-dialog-from-clicking-edittext
    private void setTimeField(){
        Calendar mcurrentTime = Calendar.getInstance();

        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddEvent.this, new TimePickerDialog.OnTimeSetListener() {
            String am_pm = "";
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if(selectedHour > 12){
                    selectedHour -= 12;
                    am_pm="PM";
                }else{
                    am_pm="AM";
                }
                time_string = selectedHour + ":" + selectedMinute +" "+am_pm;
                time.setText(time_string);
            }
        }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.search:

                return true;
            case R.id.profile:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
