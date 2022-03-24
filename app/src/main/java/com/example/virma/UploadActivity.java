package com.example.virma;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class UploadActivity extends AppCompatActivity {

    Button btnAccept; //Select image button
    Button btnCancel; //Upload image button

    ImageView IVPreviewImageUpload; //In order to preview selected image

    TextView textOrientationUpload; //Device orientation readings towards magnetic north
    TextView textLocationUpload; //Device location readings

    //Image info storage variables
    Bitmap bmImage;
    String b64Image;
    double azimuth;
    double latitude;
    double longitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // register the UI widgets with their appropriate IDs
        btnAccept = findViewById(R.id.btnAccept);
        btnCancel = findViewById(R.id.btnCancel);

        IVPreviewImageUpload = findViewById(R.id.IVPreviewImageUpload);

        textOrientationUpload = findViewById(R.id.textOrientationUpload);
        textLocationUpload = findViewById(R.id.textLocationUpload);

        //Get data to upload
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            bmImage = (Bitmap) extras.get("imageBitmap");
            b64Image = extras.getString("image64");
            azimuth = extras.getDouble("azimuth");
            latitude = extras.getDouble("latitude");
            longitude = extras.getDouble("longitude");
        }

        IVPreviewImageUpload.setImageBitmap(bmImage); //Update preview
        textOrientationUpload.setText(String.format("%.2f", azimuth)); //Set final value
        textLocationUpload.setText(String.format("Latitude: %.2f \n Longitude: %.2f ", latitude, longitude)); //Set final value

        btnAccept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createJSON();
            }
        });
    }

    public void createJSON(){
        JSONObject jsonData = new JSONObject();
        String jsonDataString = null;

        try{
            jsonData.put("image64",b64Image);
            jsonData.put("azimuth",Double.toString(azimuth));
            jsonData.put("latitude",Double.toString(latitude));
            jsonData.put("longitude",Double.toString(longitude));

            jsonDataString = jsonData.toString(); //JSON data to upload

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Debug JSON data
        int maxLogSize = 1000;
        for(int i = 0; i <= jsonDataString.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > jsonDataString.length() ? jsonDataString.length() : end;
            Log.v("JSON", jsonDataString.substring(start, end));
        }
    }
}
