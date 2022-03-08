package com.example.virma;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

public class LocationReadingService {

    private LocationManager mLocationManager;

    TextView textLocation;

    /*public final void onCreate(){
        textLocation = (TextView) findViewById(R.id.textLocation);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }*/
}
