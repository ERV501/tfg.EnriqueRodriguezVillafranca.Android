package com.example.virma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class ConnectActivity extends AppCompatActivity {

    int TAKE_PHOTO_CODE = 1001; //In order to check if the code is correct
    int UPLOAD_PHOTO_CODE = 1002; //In order to check if the code is correct

    Button btnTakePhoto; //Select image button
    Button btnUploadPhoto; //Upload image button

    ImageView IVPreviewImage; //In order to preview selected image

    TextView textOrientation; //Device orientation readings towards magnetic north
    TextView textLocation; //Device location readings

    //Image info storage variables
    File tempFile = null;
    OutputStream outFile = null;
    Uri fileUri = null;
    Bitmap bmImage = null;
    ByteArrayOutputStream outputStream = null;

    //Image info JSON variables
    String imageFile;
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
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);

        IVPreviewImage = findViewById(R.id.IVPreviewImage);

        textOrientation = findViewById(R.id.textOrientation);
        textLocation = findViewById(R.id.textLocation);

        //Upload deactivated until a photo has been taken
        btnUploadPhoto.setEnabled(false);

        // handle the Choose Image button to trigger
        // the image chooser function
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    imageChooser();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    public void imageChooser() throws IOException {

        if (ContextCompat.checkSelfPermission(ConnectActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(ConnectActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKE_PHOTO_CODE);

        try {
            File providerPath = new File(getCacheDir(), "virma_images");
            providerPath.mkdir();

            tempFile = File.createTempFile("virma", ".png", providerPath); //Stored in: /data/user/0/com.example.virma/cache/virma_images/
            imageFile = tempFile.getAbsolutePath();

            Log.d("PATH", String.valueOf(getCacheDir()));
            Log.d("PATH", String.valueOf(imageFile));

            Intent intent = new Intent((MediaStore.ACTION_IMAGE_CAPTURE)); //Create new instance

            fileUri = FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    tempFile
            );

            Log.d("URI", String.valueOf(fileUri));

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            startActivityForResult(intent, TAKE_PHOTO_CODE); //Start camera and wait for it to complete

        }catch (Exception e){
            e.printStackTrace();
        }

        startReadingServices(); //Start orientation and location services
        getReadings(); //Start receiving orientation and location
    }

    // Called when a launched activity exits,
    // giving out the requestCode it was started with,
    // the resultCode it returned,
    // and any additional data from it
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) { //Check the activity has started correctly
            if (requestCode == TAKE_PHOTO_CODE) { //Check the code is valid
                try {
                    //Get the photo data
                    bmImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
                    outputStream = new ByteArrayOutputStream();

                    //Compress to JPEG format, at 100% quality and store in outputStream
                    bmImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // bm is the bitmap object

                    if (null != bmImage) {
                        IVPreviewImage.setImageBitmap(bmImage); //Update preview
                        stopReadingServices(); //Stop orientation and location services once the photo has been taken
                        btnUploadPhoto.setEnabled(true); //Enable upload button
                    }

                    try{
                        outFile = new FileOutputStream(tempFile);
                        bmImage.compress(Bitmap.CompressFormat.JPEG, 100, outFile);
                        outFile.flush();
                        outFile.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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

    public void imageUploader() {

        if (ContextCompat.checkSelfPermission(ConnectActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(ConnectActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, UPLOAD_PHOTO_CODE);

        Intent uploadIntent = new Intent(ConnectActivity.this, UploadActivity.class);

        byte[] b = outputStream.toByteArray();

        uploadIntent.putExtra("imageBitmap", b);
        uploadIntent.putExtra("imageFile", imageFile);
        uploadIntent.putExtra("azimuth", azimuth);
        uploadIntent.putExtra("latitude", latitude);
        uploadIntent.putExtra("longitude", longitude);

        startActivity(uploadIntent);
    }
}