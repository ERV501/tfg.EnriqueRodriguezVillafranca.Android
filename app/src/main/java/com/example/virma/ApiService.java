package com.example.virma;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

interface ApiService {
    //Mensaje POST de tipo multipart para poder mandar la imagen
    @Multipart
    @POST("/")
    Call<ResponseBody> postImage(@Part MultipartBody.Part imageFile,
                                 @Part MultipartBody.Part azimuth,
                                 @Part MultipartBody.Part latitude,
                                 @Part MultipartBody.Part longitude);
}
