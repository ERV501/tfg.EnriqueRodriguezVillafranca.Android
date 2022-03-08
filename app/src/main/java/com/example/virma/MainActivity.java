package com.example.virma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Button btnConnect;

    public static final int
            RQ_PERMISSIONS = 212;   //request permissions code

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnect = (Button) findViewById(R.id.buttonLogin);
        btnConnect.setEnabled(false); //Disable login

        askPermissions();

        btnConnect.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                openConnectActivity();
            }
        });
    }

    public void openConnectActivity(){
        startActivity(new Intent(this, ConnectActivity.class));
    }

    private boolean askPermissions(){
        boolean perm_location = false;

        if(Build.VERSION.SDK_INT >= 23
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            // If no permissions are granted, ask for them
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                            ,RQ_PERMISSIONS);

            perm_location = true;

        }else{
            // Permissions are granted, enable Login
            btnConnect.setEnabled(true);
        }

        return perm_location;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("arr: " + Arrays.toString(grantResults));
        if (requestCode == RQ_PERMISSIONS) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED) {

                // Permissions are granted, enable Login
                btnConnect.setEnabled(true);

            }  else {
                //Ask for permissions again
                askPermissions();
            }
        }
    }

}