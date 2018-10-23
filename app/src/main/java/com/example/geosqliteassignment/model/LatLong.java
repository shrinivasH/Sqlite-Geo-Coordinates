package com.example.geosqliteassignment.model;

public class LatLong {
    public static final String TABLE_NAME = "geoDetails";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LAT = "latitude";
    public static final String COLUMN_LONG = "longitude";

    private int id;
    private String mLatitude;
    private String mLongitude;


    public LatLong(String mLatitude, String mLongitude) {
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String mLatitude) {
        this.mLatitude = mLatitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String mLongitude) {
        this.mLongitude = mLongitude;
    }

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_LAT + " TEXT,"
                    + COLUMN_LONG + " TEXT"
                    + ")";
}