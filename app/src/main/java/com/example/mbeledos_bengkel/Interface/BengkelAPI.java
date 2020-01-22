package com.example.mbeledos_bengkel.Interface;

import com.example.mbeledos_bengkel.Model.SigninBengkelModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BengkelAPI {

    @POST("login")
    Call<SigninBengkelModel> LoginBengkel(@Body SigninBengkelModel model);

}
