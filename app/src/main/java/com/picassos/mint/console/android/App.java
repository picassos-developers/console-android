package com.picassos.mint.console.android;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.Constants;
import com.google.firebase.messaging.FirebaseMessaging;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;

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

        // init fresco
        Fresco.initialize(this);

        // init paypal
        CheckoutConfig config = new CheckoutConfig(
                this,
                Config.PAYPAL_CLIENT_ID,
                Environment.SANDBOX,
                String.format("%s://paypalpay", BuildConfig.APPLICATION_ID),
                CurrencyCode.USD,
                UserAction.PAY_NOW,
                new SettingsConfig(
                        true,
                        false
                )
        );
        PayPalCheckout.setConfig(config);

        // init firebase.
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