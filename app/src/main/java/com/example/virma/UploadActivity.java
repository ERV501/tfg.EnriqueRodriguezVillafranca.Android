package com.example.virma;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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

    public void PostJSON(JSONObject jsonData) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        ApiService apiService = new Retrofit.Builder().baseUrl(SERVER_URL).client(client).build().create(ApiService.class);

        RequestBody requestBody =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        imageFile
                );

        MultipartBody.Part rq_imageFile =
                MultipartBody.Part.createFormData("imageFile", imageFile.replace("/", "_"), requestBody);

        RequestBody rq_azimuth =
                RequestBody.create(
                        MediaType.parse("text/plain"), String.valueOf(azimuth));

        RequestBody rq_latitude =
                RequestBody.create(
                        MediaType.parse("text/plain"), String.valueOf(latitude));

        RequestBody rq_longitude =
                RequestBody.create(
                        MediaType.parse("text/plain"), String.valueOf(longitude));

        Call<ResponseBody> req = apiService.postImage(rq_imageFile, rq_azimuth, rq_latitude, rq_longitude);
        req.enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("POST", "Uploaded Succeeded!");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("POST", "Uploaded Failed!");
            }
        });
    }
}
