package edu.umd.cschroe2.event_garden;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Thomas McHale on 4/18/2016.
 *
 * Only these methods should be used to interact with the database:
 * - boolean insertEvent(Event e)
 * - ArrayList<Event> getAllEvents();
 *
 * If you want other things from the database, write public methods for accessing them, or ask me to write them.
 * I'm trying to keep all the SQL contained to this class.
 *
 *
 * Class for creating an Sqlite database to store events for the application.
 * This class basically serves to fake connection to a web server with access to all these events.
 * It also serves to help keep the design of the web and mobile applications
 *  in line with each other with respect to data management.
 * http://developer.android.com/guide/topics/data/data-storage.html#db
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context) {
        super(context, "Event_Garden.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * Sqlite SQL statement differences:
         * SQLite does not like fields on integers, and cannot do autoincrement on unsigned integers.
         */
        db.execSQL("CREATE TABLE Events ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "event_name VARCHAR(30) NOT NULL,"
                + "event_date DATE,"
                + "event_time VARCHAR(20),"
                + "description VARCHAR(50),"
                + "location VARCHAR(50),"
                + "attending BOOLEAN DEFAULT false,"
                + "equipment VARCHAR(250) DEFAULT '-/-/-',"
                + "Environmental BOOLEAN DEFAULT false,"
                + "Recreation BOOLEAN DEFAULT false,"
                + "Arts BOOLEAN DEFAULT false"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Remove old table and create the new one.
        db.execSQL("DROP TABLE IF EXISTS Events");
        onCreate(db);
    }

    /**
     * Inserts the given event into the sqldatabase in the "Events" table.
     * @param event
     * @return
     */
    public boolean insertEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("event_name",event.event_name);
        contentValues.put("event_date",event.date); // TODO Check the database to see if translates correctly.
        contentValues.put("event_time",event.time);
        contentValues.put("description",event.description);
        contentValues.put("location",event.location);
        contentValues.put("attending",event.attending);
        contentValues.put("equipment","");

        //Todo Make a better way to programatically add filters to the table.
        contentValues.put("Environmental",event.hasFilter("Environmental"));
        contentValues.put("Recreation",event.hasFilter("Recreation"));
        contentValues.put("Arts",event.hasFilter("Arts"));
        return (db.insert("Events", null, contentValues) != -1);
    }

    /**
     * Returns an ArrayList of all events in the Events table.
     * This is done through the use of a cursor. (Basically an iterator for the rows in the database) over the database.
     * @return
     */
    public ArrayList<Event> getAllEvents(){
        // http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html
        ArrayList<Event> retArray = new ArrayList<Event>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Events", null);

        if (cursor.getCount() == 0){
            // TODO Show error if table is empty? necessary?
        }

        while (cursor.moveToNext()){

            ArrayList<String> categories = new ArrayList<String>();
            //Todo find a better way of filling in categories.
            if (cursor.getInt(8) > 0){
                categories.add("Environmental");
            }
            if (cursor.getInt(9) > 0){
                categories.add("Recreation");
            }
            if (cursor.getInt(10) > 0){
                categories.add("Arts");
            }
            Event e = new Event( cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), null,categories);
            e.attending = cursor.getInt(6) > 0;

            retArray.add(e);

        }
        return retArray;
    }
}
