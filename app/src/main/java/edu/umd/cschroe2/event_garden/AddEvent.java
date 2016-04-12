package edu.umd.cschroe2.event_garden;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Christine Schroeder on 4/9/2016.
 */
public class AddEvent extends AppCompatActivity {

    private Button date;
    private Button time;
    private String date_string;
    private String time_string;

    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);

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



        Button create = (Button) findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText title = (EditText) findViewById(R.id.name);
                String name = title.getText().toString();
                //http://stackoverflow.com/questions/6304463/how-to-create-edittext-more-than-one-line-in-android
                EditText descript = (EditText) findViewById(R.id.description);
                String description = descript.getText().toString();
                EditText address= (EditText) findViewById(R.id.address);
                EditText city= (EditText) findViewById(R.id.city);
                EditText country= (EditText) findViewById(R.id.country);
                String location = address.getText().toString()+" "+city.getText().toString()+" "+country.getText().toString();
                HashMap<String,Integer> temp = new HashMap<String, Integer>();
                temp.put("trash bags",4);
                ArrayList<String> str = new ArrayList<String>();
                str.add("recreation");
                Event event = new Event(name,date_string,time_string,description,location,temp,str);
                Intent i = new Intent();
                i.putExtra("event", event);
                setResult(RESULT_OK, i);
                finish();
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


}
