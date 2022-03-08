package com.example.virma;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LocationReadingService extends Service implements LocationListener {

    public static final String
            ACTION_LOCATION_BROADCAST = LocationReadingService.class.getName() + "LocationBroadcast",
            EXTRA_LAT = "extra_lat",
            EXTRA_LON = "extra_lon";

    public static final int
            MIN_TIME_MS = 1000, //5s
            MIN_DISTANCE_M = 0; //1m

    public LocationManager mLocationManager;
    public Criteria criteria;

    public double
            latitude,
            longitude;

    @SuppressLint("MissingPermission")
    public void onCreate() {
        super.onCreate();

        mLocationManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        String bestProvider = String.valueOf(mLocationManager.getBestProvider(criteria, true)).toString();

        Location location = mLocationManager.getLastKnownLocation(bestProvider);
        if(location != null){
            //Send broadcast
            broadcastLocation(location);
        }else{
            mLocationManager.requestLocationUpdates(bestProvider, MIN_TIME_MS, MIN_DISTANCE_M,this);
            //Send broadcast
            broadcastLocation(location);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onLocationChanged(Location location) {
        //Send broadcast
        broadcastLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {   }

    @Override
    public void onProviderEnabled(String provider) {    }

    @Override
    public void onProviderDisabled(String provider) {   }

    private void broadcastLocation(Location location){
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LAT, latitude);
        intent.putExtra(EXTRA_LON, longitude);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
