package com.example.virma;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

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
            SERVER_URL = "http://192.168.1.135:3000/";

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
            //byte[] bytes = extras.getByteArray("imageBitmap"); //Decode bytes to use as Bitmap

            bmImage = Global.imageBitmap;
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
                PostJSON();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void PostJSON() {
        OkHttpClient client = new OkHttpClient();
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .client(client);

        Retrofit retrofit = retrofitBuilder.build();
        ApiService apiService = retrofit.create(ApiService.class);

        File file = new File(imageFile);
        Log.d("FILE",imageFile);

        MultipartBody.Part rq_imageFile = MultipartBody.Part.createFormData("imageFile", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));

        MultipartBody.Part rq_azimuth =
                        MultipartBody.Part.createFormData("azimuth", String.valueOf(azimuth));

        MultipartBody.Part rq_latitude =
                        MultipartBody.Part.createFormData("latitude", String.valueOf(latitude));

        MultipartBody.Part rq_longitude =
                        MultipartBody.Part.createFormData("longitude", String.valueOf(longitude));

        Call<ResponseBody> call = apiService.postImage(rq_imageFile, rq_azimuth, rq_latitude, rq_longitude);

        call.enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("POST", "Uploaded Succeeded!");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("POST", "Uploaded Succeeded!");
                Log.d("POST", t.toString());
            }
        });
    }
}
