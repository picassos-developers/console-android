package com.picassos.mint.console.android;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class App extends Application {

    @SuppressLint("StaticFieldLeak")
    private static App instance;

    public App() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Fresco.initialize(this);

        // Init firebase.
        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("Firebase", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    Log.d("Firebase", task.getResult());
                });

        subscribeTopicNotification();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channel_id = "channel";
            String channel_name = "Channel Android";
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channel_id, channel_name, NotificationManager.IMPORTANCE_LOW));
        }
    }

    private void subscribeTopicNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic("ALL-DEVICE");
    }

    public static synchronized App getInstance() {
        return instance;
    }
}