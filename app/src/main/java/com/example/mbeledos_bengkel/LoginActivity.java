package com.example.mbeledos_bengkel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mbeledos_bengkel.Interface.BengkelAPI;
import com.example.mbeledos_bengkel.Model.SigninBengkelModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText nohp;

    private Button btnlogin;

    private BengkelAPI bengkelAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initChannel();

        nohp = findViewById(R.id.nohp);

        btnlogin = findViewById(R.id.btnlogin);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.106:8080/bengkel/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        bengkelAPI = retrofit.create(BengkelAPI.class);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SigninBengkel();
            }
        });
    }

    public void initChannel() {
        Log.d(TAG, "initChannel: Inizalitation Channel");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "NotificationOrder",
                    "Notification Order Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notification For Order Barbershop");
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void SigninBengkel() {
        String phonenumber = nohp.getText().toString();

        SigninBengkelModel model = new SigninBengkelModel(phonenumber);

        Call<SigninBengkelModel> call = bengkelAPI.LoginBengkel(model);

        call.enqueue(new Callback<SigninBengkelModel>() {
            @Override
            public void onResponse(Call<SigninBengkelModel> call, Response<SigninBengkelModel> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse Failed : " + response.code());
                    return;
                }

                SigninBengkelModel res = response.body();
                MainActivity.phoneNumber = res.getPhonenumber();
                MainActivity.namabengkel = res.getNama_bengkel();

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(Call<SigninBengkelModel> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage() );
            }
        });
    }
}
