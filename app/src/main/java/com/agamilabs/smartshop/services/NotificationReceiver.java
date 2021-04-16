package com.agamilabs.smartshop.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.agamilabs.smartshop.R;
import com.agamilabs.smartshop.database.DatabaseHandler;
import com.agamilabs.smartshop.model.NotifyModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationReceiver extends FirebaseMessagingService {
    String TAG = "TAG";
    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            Map<String, String> map = remoteMessage.getData();

            //data save in db
            NotifyModel notifyModel = new NotifyModel(
                    map.containsKey("id")?map.get("id"):"",
                    map.containsKey("title")?map.get("title"):"",
                    map.containsKey("message")?map.get("message"):"",
                    remoteMessage.getTo()
            );
            db.insertUserInfo(notifyModel.getTitle(), notifyModel.getBody_text(), notifyModel.getTopic());

            createNotification(notifyModel);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            //data save in db
            NotifyModel notifyModel = new NotifyModel(
                    "",
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(),
                    remoteMessage.getTo()
            );
            db.insertUserInfo(notifyModel.getTitle(), notifyModel.getBody_text(), notifyModel.getTopic());

            createNotification(notifyModel);

            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }



    public void createNotification(NotifyModel notifyModel) {

        String NOTIFICATION_CHANNEL_ID = "AGAMi_Smart_Shop";

        long pattern[] = {0, 1000, 500, 1000};

        NotificationManager mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Your Notifications",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(pattern);
            notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        // to diaplay notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
            channel.canBypassDnd();
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
//                .setContentTitle(getString(R.string.app_name))
                .setContentTitle(notifyModel.getTitle() + " - " + getString(R.string.app_name))
//                .setContentText(remoteMessage.getNotification().getBody())
                .setContentText(notifyModel.getBody_text())
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true);

        mNotificationManager.notify(1000, notificationBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}
