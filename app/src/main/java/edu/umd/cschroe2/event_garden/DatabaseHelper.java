package edu.umd.cschroe2.event_garden;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * TODO Get equipment remaining working properly.
 *
 * Created by Thomas McHale on 4/18/2016.
 *
 * This class is a singleton.
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
 *      in line with each other with respect to data management.
 * http://developer.android.com/guide/topics/data/data-storage.html#db
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper database;
    private SQLiteDatabase sqLiteDatabase;

    // TODO add all the colum names up here in static form for consistency's sake.

    /**
     * Returns the singleton of the DatabaseHelper Class.
     *
     * @return the singleton instance of the database helper.
     */
    public static DatabaseHelper getInstance(Context context){
        if (database == null){
            database = new DatabaseHelper(context.getApplicationContext()); // ApplicationContext because we don't know what activitity this is initially being called from.
        }
        return database;
    }

    private DatabaseHelper(Context context) {
        super(context, "Event_Garden.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * Sqlite SQL statement differences:
         * SQLite does not like fields on integers, and cannot do autoincrement on unsigned integers.
         */
        // Schema help: http://stackoverflow.com/questions/17371639/how-to-store-arrays-in-mysql
        // Create Event Table


        db.execSQL("CREATE TABLE IF NOT EXISTS Events ("
                + "id INTEGER PRIMARY KEY,"
                + "event_name VARCHAR(30) NOT NULL,"
                + "event_date DATE,"
                + "event_time VARCHAR(20),"
                + "description VARCHAR(50),"
                + "location VARCHAR(50),"
                + "attending BOOLEAN DEFAULT false,"
                + "Environmental BOOLEAN DEFAULT false,"
                + "Recreation BOOLEAN DEFAULT false,"
                + "Arts BOOLEAN DEFAULT false,"
                + "Animals BOOLEAN DEFAULT false,"
                + "Social BOOLEAN DEFAULT false"
                + ")");

        // Create Table of all equipment logs.
        db.execSQL("CREATE TABLE IF NOT EXISTS Equipment ("
                + "id INTEGER PRIMARY KEY,"
                + "equipment_name VARCHAR(30) NOT NULL,"
                + "equipment_quantity INTEGER,"
                + "equipment_remaining INTEGER DEFAULT 0"
                + ")");

        // Create table to associate events and equipment entries
        db.execSQL("CREATE TABLE IF NOT EXISTS Event_Equipment ("
                + "event_id INTEGER NOT NULL,"
                + "equipment_id INTEGER NOT NULL,"
                + "PRIMARY KEY ('event_id', 'equipment_id')"
                + ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS Profiles ("
                +"id INTEGER PRIMARY KEY,"
                +"name VARCHAR(20),"
                + "description VARCHAR(50),"
                +"reputation INTEGER DEFAULT 0"
                + ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS Profile_Event ("   // this is the table of whos hosting
                + "profile_id INTEGER NOT NULL,"
                + "event_id INTEGER NOT NULL,"
                + "PRIMARY KEY ('event_id', 'profile_id')"
                + ")");

        sqLiteDatabase = db;


        //ADD DUMMY DATA
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Remove old table and create the new one.
        db.execSQL("DROP TABLE IF EXISTS Events");
        db.execSQL("DROP TABLE IF EXISTS Equipment");
        db.execSQL("DROP TABLE IF EXISTS Event_Equipment");
        onCreate(db);
    }

    /**
     * Inserts the given event into the sqldatabase in the "Events" table.
     *
     * @param event The event being added to the SQL table.
     * @param eventOwnerID profile ID of the event Owner.
     * @return the ID of the event in the SQL table.
     */
    public long insertEvent(Event event, int eventOwnerID){

        long eventID;

        // Open a transaction with the database so that we can ensure atomic operation
        sqLiteDatabase.beginTransaction();

        // Insert into event table.
        ContentValues eventContentValues = new ContentValues();
        eventContentValues.put("event_name", event.event_name);
        eventContentValues.put("event_date", event.date); // TODO Check the database to see if translates correctly.
        eventContentValues.put("event_time", event.time);
        eventContentValues.put("description", event.description);
        eventContentValues.put("location", event.location);
        eventContentValues.put("attending", event.attending);

        //Todo Make a better way to programatically add filters to the table.
        eventContentValues.put("Environmental", event.hasFilter("Environmental"));
        eventContentValues.put("Recreation", event.hasFilter("Recreation"));
        eventContentValues.put("Arts", event.hasFilter("Arts"));
        eventContentValues.put("Animals", event.hasFilter("Animals"));
        eventContentValues.put("Social", event.hasFilter("Social"));
        eventID = sqLiteDatabase.insert("Events", null, eventContentValues);

        Log.d("New Event ID",""+eventID);

        boolean insertSuccess = (eventID != -1);
        if (eventID == -1){
            sqLiteDatabase.endTransaction();
        }


        // Insert into Equipment table
        Iterator iter = event.equipment.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry) iter.next();
            ContentValues equipmentContentValues = new ContentValues();
            equipmentContentValues.put("equipment_name", (String) pair.getKey());
            equipmentContentValues.put("equipment_quantity", (Integer) pair.getValue());

            // Update our rolling success value to keep track of all success of transaction.
            long equipmentID = sqLiteDatabase.insert("Equipment", null, equipmentContentValues);
            insertSuccess = insertSuccess & (equipmentID != -1);
            if (equipmentID == -1){
                sqLiteDatabase.endTransaction();
            }

            // Now we need to add an entry into the Event_Equipment table to link the new Equipment row to the Event.
            ContentValues event_EquipmentContentValues = new ContentValues();
            event_EquipmentContentValues.put("event_id", eventID);
            event_EquipmentContentValues.put("equipment_id", equipmentID);
            insertSuccess = insertSuccess & (sqLiteDatabase.insert("Equipment", null, equipmentContentValues) != -1);
        }

        // Add link to Profile_Event Table
        ContentValues PELinkContentValues = new ContentValues();
        PELinkContentValues.put("profile_id", eventOwnerID);
        PELinkContentValues.put("event_id", eventID);

        insertSuccess = insertSuccess & (sqLiteDatabase.insert("Profile_Event", null, PELinkContentValues) !=-1);


        // Finish transaction and close the database. http://stackoverflow.com/questions/6951506/database-lock-issue-in-htc-desire/6955195#6955195
        if (insertSuccess) {
            // No insertion failed, so the whole transaction can be cleared for finalization.
            sqLiteDatabase.setTransactionSuccessful();
        }
        sqLiteDatabase.endTransaction();

        return eventID;
    }

    /**
     * This is done through the use of a cursor. (Basically an iterator for the rows in the database) over the database.
     * The events are not ordered by any particular column.
     *
     * @return ArrayList of all events in the Events table.
     */
    public ArrayList<Event> getAllEvents(){
        // http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html
        ArrayList<Event> retArray = new ArrayList<Event>();

        Cursor eventCursor = sqLiteDatabase.rawQuery("SELECT * FROM Events;", null);

        if (eventCursor.getCount() == 0){
            Log.d("DatabaseHelper","No events found.");
        }

        // iterate through the events returned.
        while (eventCursor.moveToNext()){

            int eventID = eventCursor.getInt(eventCursor.getColumnIndex("id"));

            // Todo Figure out why using the relational table was erroring. (Is it being created correctly?)
            // Populate Equipment HashMap
            HashMap<String, Integer> equipmentMap = new HashMap<String, Integer>();
            Cursor equipCursor = sqLiteDatabase.rawQuery("SELECT * FROM Equipment WHERE id="+eventID+";", null);
            while (equipCursor.moveToNext()) {
                equipmentMap.put(equipCursor.getString(equipCursor.getColumnIndex("equipment_name")),
                        equipCursor.getInt(equipCursor.getColumnIndex("equipment_quantity")));
            }
            equipCursor.close();

            

            // Populate categories array.
            ArrayList<String> categories = new ArrayList<String>();

            String[] categoryTypes = {"Environmental", "Recreation", "Arts","Animals","Social"};
            for (int i=0; i < categoryTypes.length; i++) {
                if (eventCursor.getInt(eventCursor.getColumnIndex(categoryTypes[i])) > 0) {
                    categories.add(categoryTypes[i]);
                }
            }

            // Create Event.
            Event e = new Event( eventCursor.getString(eventCursor.getColumnIndex("event_name")),
                                 eventCursor.getString(eventCursor.getColumnIndex("event_date")),
                                 eventCursor.getString(eventCursor.getColumnIndex("event_time")),
                                 eventCursor.getString(eventCursor.getColumnIndex("description")),
                                 eventCursor.getString(eventCursor.getColumnIndex("location")),
                                 equipmentMap,
                                 categories);
            e.attending = eventCursor.getInt(eventCursor.getColumnIndex("attending")) > 0;
            e.id = eventCursor.getInt(eventCursor.getColumnIndex("id"));

            retArray.add(e);
        }
        eventCursor.close();
        return retArray;
    }

    /**
     * Changes the value of attendance for the event with id <code>eventID</code> to <code> attend </code>
     * @param eventId - ID of event to update
     * @param attend - New attendance value
     */
    public void changeAttendance(int eventId, boolean attend){
        sqLiteDatabase.beginTransaction();

        ContentValues eventContentValues = new ContentValues();
        eventContentValues.put("attending", (attend ? 1 : 0));
        long rowsUpdated = sqLiteDatabase.update("Events", eventContentValues, "id="+eventId,null);

        if (rowsUpdated == 0){
            sqLiteDatabase.endTransaction();
        }else{
            sqLiteDatabase.setTransactionSuccessful();
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * Checks the value of Attending in the SQLiteDatabase for the event given by id.
     * @param eventID
     * @return true if event is attended, false if not.
     */
    public boolean checkIfAttending(int eventID){
        Cursor equipCursor = sqLiteDatabase.rawQuery("SELECT * FROM Events WHERE id="+eventID+";", null);

        while (equipCursor.moveToNext()) {
            return equipCursor.getInt(equipCursor.getColumnIndex("attending")) > 0;
        }
        equipCursor.close();
        return false;
    }

    /**
     * Gathers all the events associated with the given profile
     * @param profileID
     * @return ArrayList of Events that are linked to <code>profileID</code>
     */
    public ArrayList<Event> getAllEventsForProfile(int profileID){
        // http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html
        ArrayList<Event> retArray = new ArrayList<Event>();

        Cursor eventCursor = sqLiteDatabase.rawQuery("SELECT * FROM Events e_table WHERE e_table.id IN (SELECT link.event_id FROM Profile_Event link WHERE link.profile_id="+profileID+")", null);

        if (eventCursor.getCount() == 0){
            Log.d("DatabaseHelper","No events found.");
        }

            // iterate through the events returned.
            while (eventCursor.moveToNext()){

            int eventID = eventCursor.getInt(eventCursor.getColumnIndex("id"));

            // Todo Figure out why using the relational table was erroring. (Is it being created correctly?)
            // Populate Equipment HashMap
            HashMap<String, Integer> equipmentMap = new HashMap<String, Integer>();
            Cursor equipCursor = sqLiteDatabase.rawQuery("SELECT * FROM Equipment WHERE id="+eventID+";", null);
            while (equipCursor.moveToNext()) {
                equipmentMap.put(equipCursor.getString(equipCursor.getColumnIndex("equipment_name")),
                        equipCursor.getInt(equipCursor.getColumnIndex("equipment_quantity")));
            }
            equipCursor.close();



            // Populate categories array.
            ArrayList<String> categories = new ArrayList<String>();

            String[] categoryTypes = {"Environmental", "Recreation", "Arts","Animals","Social"};
            for (int i=0; i < categoryTypes.length; i++) {
                if (eventCursor.getInt(eventCursor.getColumnIndex(categoryTypes[i])) > 0) {
                    categories.add(categoryTypes[i]);
                }
            }

            // Create Event.
            Event e = new Event( eventCursor.getString(eventCursor.getColumnIndex("event_name")),
                    eventCursor.getString(eventCursor.getColumnIndex("event_date")),
                    eventCursor.getString(eventCursor.getColumnIndex("event_time")),
                    eventCursor.getString(eventCursor.getColumnIndex("description")),
                    eventCursor.getString(eventCursor.getColumnIndex("location")),
                    equipmentMap,
                    categories);
            e.attending = eventCursor.getInt(eventCursor.getColumnIndex("attending")) > 0;
            e.id = eventCursor.getInt(eventCursor.getColumnIndex("id"));

            retArray.add(e);
        }
        eventCursor.close();
        return retArray;
    }

    public ArrayList<String> getAllEquip (int id){
        ArrayList<String> equip = new ArrayList<String>();
        Cursor equipCursor = sqLiteDatabase.rawQuery("SELECT equipment_id FROM Event_Equipment WHERE event_id=" + id + ";", null);
        while (equipCursor.moveToNext()) {
            int equip_id = equipCursor.getInt(equipCursor.getColumnIndex("equipment_id"));
            Cursor equipCursor1 = sqLiteDatabase.rawQuery("SELECT equipment_name FROM Equipment WHERE id=" + equip_id + ";", null);
            while (equipCursor.moveToNext()) {
                String name = equipCursor1.getString(equipCursor.getColumnIndex("equipment_name"));
                equip.add(name);
            }
            equipCursor1.close();
        }
        equipCursor.close();

        return equip;
    }

    /**
     * Returns the SQLiteDatabase's id for the event provided. Note that this is based on an equality check for multiple fields of the object, not a direct comparison.
     * Check is made based on name, time, date, and location.
     * @param event - Event object.
     * @return - ID of first event found that matches <code>event</code> in SQLite Database.
     */
    public int getId(Event event){
        Cursor equipCursor = sqLiteDatabase.rawQuery("SELECT id FROM Events WHERE event_name=\"" + event.event_name + "\" and " +
                "event_date="+event.date+" and event_time=\"" +event.time+"\" and location=\""+event.location+"\";", null);
        while (equipCursor.moveToNext()) {
            Log.i("find id", equipCursor.getInt(equipCursor.getColumnIndex("id"))+"");
            return equipCursor.getInt(equipCursor.getColumnIndex("id"));
        }
        equipCursor.close();
        return -1;
    }

}
