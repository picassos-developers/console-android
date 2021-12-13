package com.picassos.mint.console.android.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.NotificationPreviewActivity;
import com.picassos.mint.console.android.entities.NotificationEntity;
import com.picassos.mint.console.android.room.APP_DATABASE;
import com.picassos.mint.console.android.room.DAO;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private DAO dao;

    @Override
    public void onNewToken(@NotNull String token) {
        Log.d("Firebase", "Refreshed token: " + token);
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        dao = APP_DATABASE.requestDatabase(this).requestDAO();

        Log.d("GOOGLE_FIREBASE", "myFirebaseMessagingService - onMessageReceived - Message " + remoteMessage);

        try {
            NotificationEntity ne = null;
            if (remoteMessage.getData().size() > 0) {
                Object obj = remoteMessage.getData();
                ne = new Gson().fromJson(new Gson().toJson(obj), NotificationEntity.class);
            }
            if (remoteMessage.getNotification() != null) {
                RemoteMessage.Notification rn = remoteMessage.getNotification();
                if (ne == null) ne = new NotificationEntity();
                ne.title = rn.getTitle();
                ne.content = rn.getBody();
            }

            if (ne == null) return;
            ne.id = System.currentTimeMillis();
            ne.created_at = System.currentTimeMillis();
            ne.read = false;

            // request show notification
            showNotification(ne, null);

            // save notification to room db
            saveNotification(ne);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNotification(NotificationEntity notification, Bitmap bitmap) {
        Intent intent = NotificationPreviewActivity.navigateBase(this, notification, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        String channelId = getString(R.string.notification_channel_server);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setContentTitle(notification.title);
        builder.setContentText(notification.content);
        builder.setSmallIcon(R.drawable.splash_logo);
        builder.setDefaults(android.app.Notification.DEFAULT_LIGHTS);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        builder.setPriority(android.app.Notification.PRIORITY_HIGH);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        builder.setLargeIcon(largeIcon);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(notification.content));
        if (bitmap != null) {
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setSummaryText(notification.content));
        }

        // display push notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_LOW);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
        int unique_id = (int) System.currentTimeMillis();
        Objects.requireNonNull(notificationManager).notify(unique_id, builder.build());
    }

    private void saveNotification(NotificationEntity notification) {
        dao.requestInsertNotification(notification);
    }

}
