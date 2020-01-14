package com.example.mbeledos_bengkel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static PubNub pubNub;

    public static String phoneNumber = "083834121715";

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialization pubnub
        initPubnub();

        getInstanceIDFirebase();
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

}
