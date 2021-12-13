package com.picassos.mint.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.entities.NotificationEntity;

public class NotificationPreviewActivity extends AppCompatActivity {

    private static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";
    private static final String EXTRA_FROM_NOTIF = "key.EXTRA_FROM_NOTIF";

    public static Intent navigateBase(Context context, NotificationEntity obj, Boolean from_notif) {
        Intent intent = new Intent(context, NotificationPreviewActivity.class);
        intent.putExtra(EXTRA_OBJECT, obj);
        intent.putExtra(EXTRA_FROM_NOTIF, from_notif);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_preview);
    }
}