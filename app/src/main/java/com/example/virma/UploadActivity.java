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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        textOrientation = (TextView) findViewById(R.id.textOrientation);

        LocalBroadcastManager.getInstance(this).registerReceiver(
            new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    double azimuth = intent.getDoubleExtra(OrientationReadingService.EXTRA_AZIMUTH, 0);
                    textOrientation.setText(String.format("%.2f", azimuth));
                }
            }, new IntentFilter(OrientationReadingService.ACTION_ORIENTATION_BROADCAST)
        );
    }

    protected void onResume() {
        super.onResume();
        startService(new Intent(this, OrientationReadingService.class));
    }

    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, OrientationReadingService.class));
    }
}
