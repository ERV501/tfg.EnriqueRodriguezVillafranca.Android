package com.example.virma;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class UploadActivity extends AppCompatActivity {

    TextView textOrientation;
    TextView textLocation;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //Receive orientation readings
        textOrientation = (TextView) findViewById(R.id.textOrientationUpload);

        LocalBroadcastManager.getInstance(this).registerReceiver(
            new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    double azimuth = intent.getDoubleExtra(OrientationReadingService.EXTRA_AZIMUTH, 0);
                    textOrientation.setText(String.format("%.2f", azimuth));
                }
            }, new IntentFilter(OrientationReadingService.ACTION_ORIENTATION_BROADCAST)
        );

        //Receive location readings
        textLocation = (TextView) findViewById(R.id.textLocationUpload);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        double latitude = intent.getDoubleExtra(LocationReadingService.EXTRA_LAT, 0);
                        double longitude = intent.getDoubleExtra(LocationReadingService.EXTRA_LON, 0);
                        textLocation.setText(String.format("Latitude: %.2f \n Longitude: %.2f ", latitude, longitude));
                    }
                }, new IntentFilter(LocationReadingService.ACTION_LOCATION_BROADCAST)
        );
    }

    protected void onResume() {
        super.onResume();
        startService(new Intent(this, OrientationReadingService.class));
        startService(new Intent(this, LocationReadingService.class));
    }

    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, OrientationReadingService.class));
        stopService(new Intent(this, LocationReadingService.class));
    }
}
