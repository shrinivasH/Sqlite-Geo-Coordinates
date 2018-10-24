package com.example.geosqliteassignment.activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.Snackbar;

import com.example.geosqliteassignment.R;
import com.example.geosqliteassignment.database.DataBaseHelper;
import com.example.geosqliteassignment.model.LatLong;
import com.example.geosqliteassignment.utiles.GpsUtils;
import com.example.geosqliteassignment.utiles.MyStartServiceReceiver;
import com.example.geosqliteassignment.utiles.NetworkSchedule;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.masterLayout)
    LinearLayout linearLayout;
    @BindView(R.id.display)
    Button display;
    @BindView(R.id.display_lat)
    TextView display_lat;
    @BindView(R.id.display_long)
    TextView display_long;


    private LocationRequest mLocationRequest;
    private DataBaseHelper dataBaseHelper;
    private long UPDATE_INTERVAL = 90 * 1000;  /* 90 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private static final int PERMISSION_REQUEST_LAT_LONG = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        dataBaseHelper = new DataBaseHelper(this);


        if (!GpsUtils.checkAndGetLocation(MainActivity.this)) {
            GpsUtils.displayLocationSettingsRequest(MainActivity.this);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("get_Data_from_db"));

        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(new MyStartServiceReceiver(), intentFilter);
    }

    @OnClick({R.id.display, R.id.display_lat, R.id.display_long})
    public void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.display:
                /*On click event navigate to the map*/
                /*check run time permission here first*/
                if (!GpsUtils.checkAndGetLocation(MainActivity.this)) {
                    GpsUtils.displayLocationSettingsRequest(MainActivity.this);
                }
                getLatLongPermission();
                break;
            case R.id.display_lat:
                /*Open map here*/
                callToOpenMapFunction(display_lat.getText().toString().trim(), display_long.getText().toString().trim());
                break;
            case R.id.display_long:
                /*Open map here*/
                callToOpenMapFunction(display_lat.getText().toString().trim(), display_long.getText().toString().trim());
                break;
        }


    }

    private void callToOpenMapFunction(String latitiude, String longitude) {
        String geoUri = "http://maps.google.com/maps?q=loc:" + latitiude + "," + longitude + " (" + "TestLocation" + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        startActivity(intent);
    }

    private void getLatLongPermission() {
        /*check lat long permission is available*/
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)) {
            startLocationUpdates();
        } else {
            requestPermission();
        }
    }

    public void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LAT_LONG);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LAT_LONG);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_LAT_LONG) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(linearLayout, R.string.permission_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
                startLocationUpdates();
            } else {
                Snackbar.make(linearLayout, R.string.permission_denied,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        }
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();


        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        //   Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLong latLng = new LatLong(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        dataBaseHelper.insertLatLongInDb(latLng);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();
            asyncTaskRunner.execute();
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private class AsyncTaskRunner extends AsyncTask<Void, Void, LatLong> {


        @Override
        protected LatLong doInBackground(Void... voids) {
            LatLong latLong = dataBaseHelper.getLastInsertedLatLong();
            return latLong;
        }

        @Override
        protected void onPostExecute(LatLong latLong) {

            display_lat.setText(latLong.getLatitude());
            display_long.setText(latLong.getLongitude());
        }
    }
}
