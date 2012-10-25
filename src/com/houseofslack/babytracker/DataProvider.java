/*
Copyright 2011 Joshua Buergel

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.houseofslack.babytracker;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

public class DataProvider extends ContentProvider {

    private static final Uri CONTENT_URI = Uri.parse("content://"
                + DataProvider.AUTHORITY + "/events");
    
    public static final class Events implements BaseColumns
    {
        private Events() 
        {
        }

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.houseofslack.babyprovider.events";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.houseofslack.babyprovider.events";
    
        public static final String EVENT_ID = "_id";
    
        public static final String TYPE = "type";
        
        public static final String TIME = "time";
        
        public static final String BABY = "baby";
        
        public static final String QUANTITY = "quantity";
    }
    
    public enum EventType
    {
        FEEDING,
        WET_DIAPER,
        BM_DIAPER,
        SLEEP,
        CRYING,
        CUSTOM,
        CUSTOM_TIME,
        INVALID
    }
    
    public static EventType valueOf(int convert)
    {
        for (EventType value : EventType.values())
        {
            if (value.ordinal() == convert)
            {
                return value;
            }
        }
        return EventType.INVALID;
    }
    
    public static class Event
    {
        private String dateString;
        private String babyName;
        private EventType type;
        private long dateMillis;
        private int babyIndex;
        private float quantity;
        
        public Event(Context context, int babyIndex, long date, EventType type, float quantity)
        {
            String timeFormat = "%R";
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.twenty_four_hour_key), true)) {
                timeFormat = "%I:%M %p";
            }
            this.dateMillis = date;
            this.babyIndex = babyIndex;
            Time time = new Time();
            time.set(date);
            dateString = time.format(timeFormat + " on %D");
            if (0 == babyIndex)
            {
                babyName = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.baby_name_key_1), "Baby");
            }
            else
            {
                babyName = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.baby_name_key_2), "Baby");
            }
            this.type = type;
            this.quantity = quantity;
        }
        
        public int getBabyIndex()
        {
            return babyIndex;
        }
        
        public long getDateMillis()
        {
            return dateMillis;
        }
        
        public String getDateString()
        {
            return dateString;
        }
        
        public String getBabyName()
        {
            return babyName;
        }
        
        public EventType getType()
        {
            return type;
        }
        
        public float getQuantity()
        {
        	return quantity;
        }
    }
    
    private static final String[] standardProjection = new String[] {Events._ID, Events.TYPE, Events.TIME, Events.BABY, Events.QUANTITY};
    
    private static final String TAG = "DataProvider";

    private static final String DATABASE_NAME = "events.db";

    private static final int DATABASE_VERSION = 2;

    private static final String EVENTS_TABLE_NAME = "events";

    public static final String AUTHORITY = "com.houseofslack.babytracker.DataProvider";

    private static final UriMatcher sUriMatcher;

    private static final int EVENTS = 1;
    private static final int EVENT_ID = 2;

    private static HashMap<String, String> eventsProjectionMap;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + EVENTS_TABLE_NAME + " (" + Events.EVENT_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," 
                    + Events.TYPE + " INTEGER,"
                    + Events.TIME + " INTEGER,"
                    + Events.BABY + " INTEGER,"
                    + Events.QUANTITY + " REAL"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	// bring a version 1 database up to snuff by adding the quantity column
        	if (1 == oldVersion) {
                Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                        + " by adding column QUANTITY");
        		db.execSQL("ALTER TABLE " + EVENTS_TABLE_NAME + " ADD COLUMN " + Events.QUANTITY + " REAL DEFAULT 0.0;");
        	} else {
                Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                        + ", which will destroy all old data");
                db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE_NAME);
                onCreate(db);
        	}
        }
    }

    private DatabaseHelper dbHelper;

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case EVENTS:
                count = db.delete(EVENTS_TABLE_NAME, where, whereArgs);
                break;

            case EVENT_ID:
                String eventId = uri.getPathSegments().get(1);
                count = db.delete(EVENTS_TABLE_NAME, Events._ID + "=" + eventId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case EVENTS:
                return Events.CONTENT_TYPE;

            case EVENT_ID:
                return Events.CONTENT_ITEM_TYPE;
                
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != EVENTS) { throw new IllegalArgumentException("Unknown URI " + uri); }

        ContentValues values;
        if (initialValues != null) 
        {
            values = new ContentValues(initialValues);
        } 
        else 
        {
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(EVENTS_TABLE_NAME, Events.TIME, values);
        if (rowId > 0) {
            Uri eventUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(eventUri, null);
            return eventUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case EVENTS:
                qb.setTables(EVENTS_TABLE_NAME);
                qb.setProjectionMap(eventsProjectionMap);
                break;

            case EVENT_ID:
                qb.setTables(EVENTS_TABLE_NAME);
                qb.setProjectionMap(eventsProjectionMap);
                qb.appendWhere(Events._ID + "=" + uri.getPathSegments().get(1));
                break;
                
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case EVENTS:
                count = db.update(EVENTS_TABLE_NAME, values, where, whereArgs);
                break;

            case EVENT_ID:
                String eventId = uri.getPathSegments().get(1);
                count = db.update(EVENTS_TABLE_NAME, values, Events._ID + "=" + eventId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, EVENTS_TABLE_NAME, EVENTS);
        sUriMatcher.addURI(AUTHORITY, EVENTS_TABLE_NAME + "/#", EVENT_ID);

        eventsProjectionMap = new HashMap<String, String>();
        eventsProjectionMap.put(Events.EVENT_ID, Events.EVENT_ID);
        eventsProjectionMap.put(Events.TYPE, Events.TYPE);
        eventsProjectionMap.put(Events.TIME, Events.TIME);
        eventsProjectionMap.put(Events.BABY, Events.BABY);
        eventsProjectionMap.put(Events.QUANTITY, Events.QUANTITY);
    }
    
    public static void putEvent(ContentResolver cr, EventType type, long time, int baby, float quantity)
    {
        ContentValues values = new ContentValues();
        values.put(Events.TYPE, type.ordinal());
        values.put(Events.TIME, time);
        values.put(Events.BABY, baby);
        values.put(Events.QUANTITY, quantity);
        cr.insert(CONTENT_URI, values);
    }
    
    public static Event getEvent(Context context, Cursor cursor)
    {
        long time = cursor.getLong(cursor.getColumnIndex(Events.TIME));
        int baby = cursor.getInt(cursor.getColumnIndex(Events.BABY));
        EventType type = valueOf(cursor.getInt(cursor.getColumnIndex(Events.TYPE)));
        float quantity = 0.0f;
        try 
        {
	        quantity = cursor.getFloat(cursor.getColumnIndex(Events.QUANTITY));
        } 
        catch (Exception ex) 
        {
        	// nothing to do, just leave it as a 0
        }
        return new Event(context, baby, time, type, quantity);
    }
    
    public static void deleteEvent(ContentResolver cr, EventType type, long time, int baby)
    {
        cr.delete(CONTENT_URI, "(" + Events.TYPE + "=" + type.ordinal() + ") AND (" + Events.TIME + "=" + time + ") AND (" + Events.BABY + "=" + baby + ")", null);
    }
    
    public static Cursor queryTypeCursor(ContentResolver cr, EventType type)
    {
        if (null == type) 
        {
            return cr.query(CONTENT_URI, standardProjection, null, null, Events.TIME + " ASC");
        } 
        else 
        {
            return cr.query(CONTENT_URI, standardProjection, "(" + Events.TYPE + "=" + type.ordinal() + ")", null, Events.TIME + " ASC");
        }
    }
    
    public static void deleteAll(ContentResolver cr, EventType type)
    {
        cr.delete(CONTENT_URI, "(" + Events.TYPE + "=" + type.ordinal() + ")", null);
    }
    
    public static void deleteAll(ContentResolver cr)
    {
        cr.delete(CONTENT_URI, "", null);
    }
    
    public static int countEvents(ContentResolver cr, EventType type, Integer baby)
    {
        Cursor cursor = null;
        int count = 0;
        
        try
        {
            if (null == baby)
            {
                cursor = cr.query(CONTENT_URI, standardProjection, "(" + Events.TYPE + "=" + type.ordinal() + ")", null, Events.TIME + " DESC");
            }
            else
            {
                cursor = cr.query(CONTENT_URI, standardProjection, "(" + Events.TYPE + "=" + type.ordinal() + " AND " + Events.BABY + "=" + baby.toString() + ")", null, Events.TIME + " DESC");
            }
            if (null != cursor)
            {
                count = cursor.getCount();
            }
        }
        finally
        {
            if (null != cursor)
            {
                cursor.close();
            }
        }
        return count;
    }
    
    public static Event getLastEvent(Context context, ContentResolver cr, EventType type, Integer baby)
    {
        Cursor cursor = null;
        Event event = null;
        
        try
        {
            if (null == baby)
            {
                cursor = cr.query(CONTENT_URI, standardProjection, "(" + Events.TYPE + "=" + type.ordinal() + ")", null, Events.TIME + " DESC");
            }
            else
            {
                cursor = cr.query(CONTENT_URI, standardProjection, "(" + Events.TYPE + "=" + type.ordinal() + " AND " + Events.BABY + "=" + baby.toString() + ")", null, Events.TIME + " DESC");
            }
            if ((null != cursor) && cursor.moveToFirst())
            {
                event = getEvent(context, cursor);
            }
        }
        finally
        {
            if (null != cursor)
            {
                cursor.close();
            }
        }
        return event;
    }
}
