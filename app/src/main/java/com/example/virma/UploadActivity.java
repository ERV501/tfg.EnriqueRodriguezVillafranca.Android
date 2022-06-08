package com.example.virma;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadActivity extends AppCompatActivity {

    public static final String
            SERVER_URL = "http://192.168.1.135:3000/images";

    Button btnAccept; //Select image button
    Button btnCancel; //Upload image button

    ImageView IVPreviewImageUpload; //In order to preview selected image

    TextView textOrientationUpload; //Device orientation readings towards magnetic north
    TextView textLocationUpload; //Device location readings

    //Image info storage variables
    Bitmap bmImage;
    String imageFile;
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
            imageFile = extras.getString("imageFile");
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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void createJSON(){
        JSONObject jsonData = new JSONObject();

        try{
            jsonData.put("imageFile",imageFile);
            jsonData.put("azimuth",Double.toString(azimuth));
            jsonData.put("latitude",Double.toString(latitude));
            jsonData.put("longitude",Double.toString(longitude));

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Send JSON
        PostJSON(jsonData);
    }

    public void PostJSON(JSONObject jsonData){
        Thread threadPost = new Thread(() -> {
            try {

                //Stablish connection
                URL server = new URL(SERVER_URL);
                HttpURLConnection req = (HttpURLConnection) server.openConnection();

                //Configure POST request
                req.setRequestProperty("Content-Type", "application/json; charset=utf-8"); //Tipo de contenido enviado, codificado como UTF-8
                req.setRequestProperty("Accept","application/json");
                req.setDoOutput(true); //Para poder enviar
                req.setRequestMethod("POST"); //POST request

                //Send JSON
                DataOutputStream writer = new DataOutputStream(req.getOutputStream());

                String finalData = jsonData.toString().replace("\\","");
                writer.writeBytes(finalData);

                Log.d("JSON",finalData);

                //Close connection
                writer.flush();
                writer.close();

                req.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        threadPost.start();
    }
}
