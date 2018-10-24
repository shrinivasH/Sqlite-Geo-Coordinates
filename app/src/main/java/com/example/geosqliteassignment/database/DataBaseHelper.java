package com.example.geosqliteassignment.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.geosqliteassignment.model.LatLong;

public class DataBaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "notes_db";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(LatLong.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insertLatLongInDb(LatLong latLng) {
        SQLiteDatabase dataBaseHelper = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(LatLong.COLUMN_LAT, latLng.getLatitude());
        values.put(LatLong.COLUMN_LONG, latLng.getLongitude());

        // insert row
        long id = dataBaseHelper.insert(LatLong.TABLE_NAME, null, values);

        // close db connection
        dataBaseHelper.close();

        // return newly inserted row id
        return id;
    }

    public LatLong getLastInsertedLatLong() {
        LatLong mLatLong;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + LatLong.TABLE_NAME + " ORDER BY " +
                LatLong.COLUMN_ID + " DESC" + " LIMIT" + " 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                mLatLong = new LatLong();
                mLatLong.setId(cursor.getInt(cursor.getColumnIndex(LatLong.COLUMN_ID)));
                mLatLong.setLatitude(cursor.getString(cursor.getColumnIndex(LatLong.COLUMN_LAT)));
                mLatLong.setLongitude(cursor.getString(cursor.getColumnIndex(LatLong.COLUMN_LONG)));

            } while (cursor.moveToNext());
        } else {
            mLatLong = new LatLong();
        }

        // close db connection
        db.close();

        // return notes list
        return mLatLong;
    }
}