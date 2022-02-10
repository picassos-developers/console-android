package com.picassos.mint.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;

public class AppSettingsActivity extends AppCompatActivity {
    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_app_settings);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());


    }
}