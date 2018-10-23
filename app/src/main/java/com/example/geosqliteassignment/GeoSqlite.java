package com.example.geosqliteassignment;

import android.app.Application;
import android.content.Context;

import com.example.geosqliteassignment.database.DataBaseHelper;

public class GeoSqlite extends Application {
    private Context mContext;
    private DataBaseHelper mDataBaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=getApplicationContext();
        mDataBaseHelper=new DataBaseHelper(mContext);
    }
}