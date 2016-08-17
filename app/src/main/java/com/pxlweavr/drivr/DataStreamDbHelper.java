package com.pxlweavr.drivr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by IanDMiller on 8/16/16.
 */
public class DataStreamDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DataStreamDb.db";
    public static final String TABLE_NAME = "data_stream";

    public DataStreamDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +
                    "(id INTEGER PRIMARY KEY, name TEXT, abbrev TEXT, format INTEGER, channel INTEGER)");
    }

    /**
     * Insert a DataStream into the DB.  Update the DB if the stream already is there
     * @param stream The stream to insert into the DB
     */
    public void insertDataStream(DataStream stream) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("id", stream.getId());
        contentValues.put("name", stream.getName());
        contentValues.put("abbrev", stream.getAbbrev());
        contentValues.put("format", stream.getFormat());
        contentValues.put("channel", stream.getChannel());
        db.replace(TABLE_NAME, null, contentValues);
    }

    /**
     * Get a cursor to all the entries in the database
     * @return A cursor to all the entries in the database
     */
    public Cursor getDataStreams() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return c;
    }

    /**
     * Construct a datastream from the data pointed to by the cursor
     * @param c The Cursor pointing to the DataStream entry to reconstruct
     * @return A DataStream with the configuration of the entry pointed to by the Cursor
     */
    public DataStream getDataStreamAtCursor(Cursor c) {
        Integer id = c.getInt(c.getColumnIndex("id"));
        String name = c.getString(c.getColumnIndex("name"));
        String abbrev = c.getString(c.getColumnIndex("abbrev"));
        Integer format = c.getInt(c.getColumnIndex("format"));
        Integer channel = c.getInt(c.getColumnIndex("channel"));

        DataStream stream = new DataStream(name, abbrev, channel, format, 1000, id);
        return stream;
    }

    /**
     * Delete the row equivalent to the given datastream
     * @param ds The DataStream whose data we want to delete
     */
    public void deleteDataStream(DataStream ds) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = " + ds.getId(), null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //We'll be lazy here for the time being.  Just destroy old data
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
