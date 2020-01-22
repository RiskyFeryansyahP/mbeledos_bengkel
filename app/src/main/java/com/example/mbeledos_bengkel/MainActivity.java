package com.example.mbeledos_bengkel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.example.mbeledos_bengkel.Interface.TransactionAPI;
import com.example.mbeledos_bengkel.Model.TransaksiModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static PubNub pubNub;

    public static String phoneNumber = "083834121715";
    public static String namabengkel = "";

    private String token;

    TransactionAPI transactionAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialization pubnub
        initPubnub();

        getInstanceIDFirebase();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.106:8080/transaction/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        transactionAPI = retrofit.create(TransactionAPI.class);
    }

    private void initPubnub() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setPublishKey(getResources().getString(R.string.PUBLISH_KEY_PUBNUB));
        pnConfiguration.setSubscribeKey(getResources().getString(R.string.SUBSCRIBE_KEY_PUBNUB));
        pnConfiguration.setSecure(true);
        pubNub = new PubNub(pnConfiguration);
    }

    private void getInstanceIDFirebase() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "Get Instance ID Failed " + task.getException() );
                            return;
                        }

                        token = task.getResult().getToken();
                        SendRegistrationTokenToPubnub(token);
                    }
                });
    }

    private void SendRegistrationTokenToPubnub(String token) {
        pubNub.addPushNotificationsOnChannels()
            .pushType(PNPushType.GCM)
            .channels(Arrays.asList(phoneNumber))
            .deviceId(token)
            .async(new PNCallback<PNPushAddChannelResult>() {
                @Override
                public void onResponse(PNPushAddChannelResult result, PNStatus status) {
                    Log.d("PUBNUB", "-->PNStatus.getStatusCode = " + status.getStatusCode());
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("having-order"));
    }

    // handler for received Intents for the event
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String phonenumber = intent.getStringExtra("phonenumber");
            Double latitude = intent.getDoubleExtra("latitude", 0);
            Double longitude = intent.getDoubleExtra("longitude", 0);

            Log.d(TAG, "Phone Number : " + phonenumber);
            Log.d(TAG, "Latitude : " + latitude);
            Log.d(TAG, "Longitude : " + longitude);

            showDoalogOrder(phonenumber);
        }
    };

    private void showDoalogOrder(String phone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Orderan");
        builder.setMessage("Anda Mempunyai Orderan");
        builder.setPositiveButton("TERIMA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                terimaOrder(phone);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    private void terimaOrder(String phone) {
        Log.d(TAG, "terimaOrder: Diterima");
        TransaksiModel model = new TransaksiModel(phone, namabengkel);

        Call<JsonObject> call = transactionAPI.InsertData(model
        );
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Failed : " + response.code() );
                    return;
                }

                Log.d(TAG, "onResponse: " + response.body());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage() );
            }
        });
    }
}
