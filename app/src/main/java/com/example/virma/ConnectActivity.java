package com.example.virma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ConnectActivity extends AppCompatActivity {

    Button btnSelectPhoto; //Select image button
    Button btnUploadPhoto; //Upload image button

    ImageView IVPreviewImage; //In order to preview selected image

    int SELECT_IMAGE_CODE = 1001; //In order to check if the code is correct
    int UPLOAD_IMAGE_CODE = 1002; //In order to check if the code is correct

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        // register the UI widgets with their appropriate IDs
        btnSelectPhoto = (Button) findViewById(R.id.btnSelectPhoto);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);

        btnUploadPhoto = (Button) findViewById(R.id.btnUploadPhoto);

        // handle the Choose Image button to trigger
        // the image chooser function
        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        // handle the Upload Image button to trigger
        // the image uploader function
        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUploader();
            }
        });
    }

    public void imageChooser() {

        if (ContextCompat.checkSelfPermission(ConnectActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(ConnectActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECT_IMAGE_CODE);

        Intent intent = new Intent(); //Create new instance
        intent.setType("image/*"); //Any image type
        intent.setAction(Intent.ACTION_GET_CONTENT); //Action to be performed

        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE_CODE); //Start activity and wait for it to complete
    }

    public void imageUploader() {

        if (ContextCompat.checkSelfPermission(ConnectActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(ConnectActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, UPLOAD_IMAGE_CODE);

        startActivity(new Intent(this, SensorActivity.class));
    }

    // Called when a launched activity exits,
    // giving out the requestCode it was started with,
    // the resultCode it returned,
    // and any additional data from it
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) { //Check the activity has started correctly
            if (requestCode == SELECT_IMAGE_CODE) { //Check the code is valid

                Uri selectedImageUri = data.getData(); // Get the url of the image from data

                Bitmap bmImage = loadFromUri(selectedImageUri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                //Compress to JPEG format, at 100% quality and store in outputStream
                bmImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // bm is the bitmap object
                byte[] b = outputStream.toByteArray();

                String b64Image = Base64.encodeToString(b,Base64.DEFAULT); //Base64 image to write in JSON

                if (null != selectedImageUri) {
                    IVPreviewImage.setImageURI(selectedImageUri); //Update preview
                }

            }
        }
    }

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
}