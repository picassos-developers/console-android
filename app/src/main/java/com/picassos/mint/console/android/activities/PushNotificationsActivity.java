package com.picassos.mint.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.adapter.NotificationTargetAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.models.NotificationTarget;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.NotificationsPreferencesBottomSheetModal;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PushNotificationsActivity extends AppCompatActivity {

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    // notification details
    private EditText notificationTitle,
            notificationBody,
            notificationImage,
            notificationIcon;
    private TextView notificationTarget;

    // send notification
    private Button sendNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_push_notifications);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // notifications settings
        findViewById(R.id.notifications_settings).setOnClickListener(v -> {
            NotificationsPreferencesBottomSheetModal notificationsPreferencesBottomSheetModal = new NotificationsPreferencesBottomSheetModal();
            notificationsPreferencesBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });

        // notification details
        notificationTitle = findViewById(R.id.notification_title);
        notificationBody = findViewById(R.id.notification_body);
        notificationTarget = findViewById(R.id.notification_target);
        notificationIcon = findViewById(R.id.notification_icon);
        notificationImage = findViewById(R.id.notification_image);

        // open notification target chooser
        findViewById(R.id.notification_target_chooser).setOnClickListener(v -> targetChooserDialog());

        // send notification
        sendNotification = findViewById(R.id.send_notification);
        sendNotification.setOnClickListener(v -> {
            if (TextUtils.isEmpty(notificationTitle.getText().toString())) {
                notificationTitle.setError(getString(R.string.notification_title_required));
                notificationTitle.requestFocus();
                sendNotification.setEnabled(true);
                return;
            }

            if (TextUtils.isEmpty(notificationBody.getText().toString())) {
                notificationBody.setError(getString(R.string.notification_body_required));
                notificationBody.requestFocus();
                sendNotification.setEnabled(true);
                return;
            }

            if (notificationTarget.getText().toString().equals(getString(R.string.notification_target))) {
                notificationTarget.setError(getString(R.string.notification_target_empty));
                notificationTarget.requestFocus();
                sendNotification.setEnabled(true);
                return;
            }

            if (!TextUtils.isEmpty(notificationTitle.getText().toString()) && !TextUtils.isEmpty(notificationBody.getText().toString()) && !notificationTarget.getText().toString().equals(getString(R.string.notification_target))) {
                if (consolePreferences.loadFirebaseAccessToken().equals("exception:error?fak")) {
                    Toasto.show_toast(this, getString(R.string.update_firebase_access_key), 1, 2);
                } else {
                    requestSendNotification();
                }
            }
        });

        // preview notification
        findViewById(R.id.preview_notification).setOnClickListener(v -> {
            if (TextUtils.isEmpty(notificationTitle.getText().toString())) {
                notificationTitle.setError(getString(R.string.notification_title_required));
                notificationTitle.requestFocus();
                sendNotification.setEnabled(true);
                return;
            }

            if (TextUtils.isEmpty(notificationBody.getText().toString())) {
                notificationBody.setError(getString(R.string.notification_body_required));
                notificationBody.requestFocus();
                sendNotification.setEnabled(true);
                return;
            }

            if (notificationTarget.getText().toString().equals(getString(R.string.notification_target))) {
                notificationTarget.setError(getString(R.string.notification_target_empty));
                notificationTarget.requestFocus();
                sendNotification.setEnabled(true);
                return;
            }

            if (!TextUtils.isEmpty(notificationTitle.getText().toString()) && !TextUtils.isEmpty(notificationBody.getText().toString()) && !notificationTarget.getText().toString().equals(getString(R.string.notification_target))) {
                reviewNotification();
            }
        });
    }

    /**
     * review notification
     * before send to devices
     */
    private void reviewNotification() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "notify_001")
                .setSmallIcon(R.drawable.icon_bell_outline)
                .setContentTitle(notificationTitle.getText().toString())
                .setContentText(notificationBody.getText().toString())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelID = "CONSOLE_CHANNEL";
            NotificationChannel channel = new NotificationChannel(
                    channelID,
                    "Mint Console Demo Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channelID);
        }

        notificationManager.notify(0, notificationBuilder.build());

        if (!TextUtils.isEmpty(notificationImage.getText().toString())) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> Picasso.get()
                    .load(notificationImage.getText().toString())
                    .resize(200, 200)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            notificationBuilder.setLargeIcon(bitmap);
                            notificationManager.notify(0, notificationBuilder.build());
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    }));
        }
    }

    /**
     * request send notification
     * to users devices
     */
    private void requestSendNotification() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_SEND_NOTIFICATION,
                    response -> {
                        if (response.contains("Request failed: HTTP status code:")) {
                            Toasto.show_toast(this, getString(R.string.invalid_firebase_access_key), 0, 2);
                        } else if (response.equals("200")) {
                            Toasto.show_toast(this, getString(R.string.notification_send), 0, 0);
                        }
                        notificationTitle.setText("");
                        notificationBody.setText("");
                        notificationTarget.setText("");
                        notificationIcon.setText("");
                        notificationImage.setText("");
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("notification_title", notificationTitle.getText().toString());
                    params.put("notification_body", notificationBody.getText().toString());
                    params.put("notification_icon", notificationIcon.getText().toString());
                    params.put("notification_image", notificationImage.getText().toString());
                    params.put("notification_target", notificationTarget.getText().toString());
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }

    /**
     * open target chooser dialog
     */
    @SuppressLint("NotifyDataSetChanged")
    private void targetChooserDialog() {
        Dialog targetsDialog = new Dialog(this);

        targetsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        targetsDialog.setContentView(R.layout.dialog_notification_target);

        // enable dialog cancel
        targetsDialog.setCancelable(true);
        targetsDialog.setOnCancelListener(dialog -> targetsDialog.dismiss());

        // close dialog
        ImageView dialogClose = targetsDialog.findViewById(R.id.dialog_close);
        dialogClose.setOnClickListener(v -> targetsDialog.dismiss());

        List<NotificationTarget> targetList = new ArrayList<>();
        RecyclerView targetRecyclerview = targetsDialog.findViewById(R.id.recycler_target);

        NotificationTargetAdapter notificationTargetAdapter = new NotificationTargetAdapter(targetList, target -> {
            notificationTarget.setText(target.getTitle());
            targetsDialog.dismiss();
        });

        targetRecyclerview.setAdapter(notificationTargetAdapter);
        targetRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        targetList.add(new NotificationTarget("Target all Topics", "global"));
        targetList.add(new NotificationTarget("Youtube", "youtube"));
        targetList.add(new NotificationTarget("Facebook", "facebook"));
        targetList.add(new NotificationTarget("Vimeo", "vimeo"));
        targetList.add(new NotificationTarget("Pinterest", "pinterest"));
        targetList.add(new NotificationTarget("Imgur", "imgur"));
        targetList.add(new NotificationTarget("RSS", "rss"));
        targetList.add(new NotificationTarget("WordPress", "wordpress"));
        notificationTargetAdapter.notifyDataSetChanged();

        if (targetsDialog.getWindow() != null) {
            targetsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            targetsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        targetsDialog.show();
    }
}