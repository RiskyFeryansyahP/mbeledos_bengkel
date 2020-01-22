package com.example.mbeledos_bengkel.Firebase;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mbeledos_bengkel.MainActivity;
import com.example.mbeledos_bengkel.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pubnub.api.enums.PNPushType;

public class MyFirebaseMessageService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessage";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "onNewToken: " + s);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived From : " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            if (isForeground()) {
                Log.d(TAG, "Foreground Data : " + remoteMessage.getData());
                broadcastToMain(remoteMessage);
            } else {
                Log.d(TAG, "Background Data : " + remoteMessage.getData());
                SendNotificationWithAndroidOreoAndAfter(remoteMessage);
            }
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "onMessageReceived Notification : " + remoteMessage.getNotification().getBody());
        }
    }

    public boolean isForeground() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE);
    }

    private void SendNotificationWithAndroidOreoAndAfter(@NonNull RemoteMessage remoteMessage)
    {
        Log.d(TAG, "SendNotificationWithAndroidOreoAndAfter: Called");
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.putExtra("action", "order");
//        intent.putExtra("orderPhone", remoteMessage.getData().get("orderPhone"));
//        intent.putExtra("barbershop", remoteMessage.getData().get("barbershop"));
//        intent.putExtra("harga", Integer.parseInt(remoteMessage.getData().get("harga")));
//        intent.putExtra("locationFrom", remoteMessage.getData().get("locationFrom"));
//        intent.putExtra("locationDestination", remoteMessage.getData().get("locationDestination"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "NotificationOrder")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(defaultSoundUri)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        notificationManagerCompat.notify(0, builder.build());
    }

    private void broadcastToMain(RemoteMessage remoteMessage) {
        Intent intent = new Intent("having-order");

        intent.putExtra("phonenumber", remoteMessage.getData().get("phone"));
        intent.putExtra("latitude", Double.parseDouble(remoteMessage.getData().get("latitude")));
        intent.putExtra("longitude", Double.parseDouble(remoteMessage.getData().get("longitude")));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
