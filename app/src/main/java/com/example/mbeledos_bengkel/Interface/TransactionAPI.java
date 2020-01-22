package com.example.mbeledos_bengkel.Interface;

import com.example.mbeledos_bengkel.Model.TransaksiModel;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TransactionAPI {

    @POST("insert")
    Call<JsonObject> InsertData(@Body TransaksiModel model);

}
