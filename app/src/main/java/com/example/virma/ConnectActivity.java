package com.example.virma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ConnectActivity extends AppCompatActivity {

    int TAKE_PHOTO_CODE = 1001; //In order to check if the code is correct
    int UPLOAD_PHOTO_CODE = 1002; //In order to check if the code is correct

    Button btnTakePhoto; //Select image button
    Button btnUploadPhoto; //Upload image button

    ImageView IVPreviewImage; //In order to preview selected image

    TextView textOrientation; //Device orientation readings towards magnetic north
    TextView textLocation; //Device location readings

    //Image info storage variables
    String b64Image;
    double azimuth;
    double latitude;
    double longitude;

    private BroadcastReceiver orientationReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            azimuth = intent.getDoubleExtra(OrientationReadingService.EXTRA_AZIMUTH, 0);
            textOrientation.setText(String.format("%.2f", azimuth)); //Set final value
        }
    };

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            latitude = intent.getDoubleExtra(LocationReadingService.EXTRA_LAT, 0);
            longitude = intent.getDoubleExtra(LocationReadingService.EXTRA_LON, 0);
            textLocation.setText(String.format("Latitude: %.2f \n Longitude: %.2f ", latitude, longitude)); //Set final value
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        // register the UI widgets with their appropriate IDs
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);

        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);

        textOrientation = findViewById(R.id.textOrientation);
        textLocation = findViewById(R.id.textLocation);

        // handle the Choose Image button to trigger
        // the image chooser function
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imageChooser();
            }
        });

        // handle the Upload Image button to trigger
        // the image uploader function
        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imageUploader();
            }
        });
    }

    public void imageChooser() {

        if (ContextCompat.checkSelfPermission(ConnectActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(ConnectActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKE_PHOTO_CODE);

        Intent intent = new Intent((MediaStore.ACTION_IMAGE_CAPTURE)); //Create new instance
        //intent.setType("image/*"); //Any image type
        //intent.setAction(Intent.ACTION_GET_CONTENT); //Action to be performed

        //startActivityForResult(Intent.createChooser(intent, "Take Photo"), TAKE_PHOTO_CODE); //Start activity and wait for it to complete
        startActivityForResult(intent, TAKE_PHOTO_CODE); //Start camera and wait for it to complete

        startReadingServices(); //Start orientation and location services
        getReadings(); //Start receiving orientation and location
    }

    public void getReadings(){
        //Receive orientation readings
        LocalBroadcastManager.getInstance(this).registerReceiver(orientationReceiver
                , new IntentFilter(OrientationReadingService.ACTION_ORIENTATION_BROADCAST) //Set final value
        );

        //Receive location readings
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver
                , new IntentFilter(LocationReadingService.ACTION_LOCATION_BROADCAST)
        );

    }

    // Called when a launched activity exits,
    // giving out the requestCode it was started with,
    // the resultCode it returned,
    // and any additional data from it
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) { //Check the activity has started correctly
            if (requestCode == TAKE_PHOTO_CODE) { //Check the code is valid

                //Get the photo data
                Bundle extras = data.getExtras();
                Bitmap bmImage = (Bitmap) extras.get("data");
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                //Compress to JPEG format, at 100% quality and store in outputStream
                bmImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // bm is the bitmap object
                byte[] b = outputStream.toByteArray();

                b64Image = Base64.encodeToString(b,Base64.DEFAULT); //Base64 image to write in JSON

                if (null != bmImage) {
                    IVPreviewImage.setImageBitmap(bmImage); //Update preview
                    stopReadingServices(); //Stop orientation and location services once the photo has been taken
                }

            }
        }
    }

    protected void startReadingServices() {
        startService(new Intent(this, OrientationReadingService.class));
        startService(new Intent(this, LocationReadingService.class));
    }

    protected void stopReadingServices() {
        stopService(new Intent(this, OrientationReadingService.class));
        LocalBroadcastManager.getInstance(this).unregisterReceiver(orientationReceiver);

        stopService(new Intent(this, LocationReadingService.class));
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);
    }

    // En caso de querer seleccionar desde la galeria (necesario?)
    // Ejemplo de https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media#custom-gallery-selector
    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // On newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // On older versions of Android by use the old getBitmap
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void imageUploader() {

        if (ContextCompat.checkSelfPermission(ConnectActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(ConnectActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, UPLOAD_PHOTO_CODE);

        startActivity(new Intent(this, UploadActivity.class));
    }
}