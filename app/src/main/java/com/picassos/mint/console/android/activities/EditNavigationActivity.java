package com.picassos.mint.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class EditNavigationActivity extends AppCompatActivity {

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    // navigation tab id
    private int id;

    // navigation tab details
    EditText title, link, icon;
    SwitchCompat behavior, premium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_edit_navigation);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // get id
        id = getIntent().getIntExtra("navigation_id", 0);

        // get navigation tab details
        String navigationTitle = getIntent().getStringExtra("navigation_title");
        String navigationLink = getIntent().getStringExtra("navigation_link");
        String navigationIcon = getIntent().getStringExtra("navigation_icon");
        String navigationBehavior = getIntent().getStringExtra("navigation_behavior");
        String navigationPremium = getIntent().getStringExtra("navigation_premium");

        // navigation details
        title = findViewById(R.id.navigation_title_input);
        link = findViewById(R.id.navigation_link_input);
        icon = findViewById(R.id.navigation_icon_input);
        behavior = findViewById(R.id.navigation_behavior_switch);
        premium = findViewById(R.id.navigation_premium_switch);

        // set details
        title.setText(navigationTitle);
        link.setText(navigationLink);
        icon.setText(navigationIcon);

        // navigation behavior
        if (navigationBehavior.equals("EXTERNAL")) {
            behavior.setChecked(true);
        } else if (navigationBehavior.equals("INAPP")) {
            behavior.setChecked(false);
        }

        // navigation premium
        if (navigationPremium.equals("true")) {
            premium.setChecked(true);
        } else if (navigationPremium.equals("false")) {
            premium.setChecked(false);
        }

        // update navigation
        Button updateNavigation = findViewById(R.id.update_navigation);
        updateNavigation.setOnClickListener(v -> {
            // Check if navigation title is empty
            if (TextUtils.isEmpty(title.getText().toString())) {
                title.setError(getString(R.string.navigation_title_required));
                title.requestFocus();
                updateNavigation.setEnabled(true);
                return;
            }

            // Check if navigation link is empty
            if (TextUtils.isEmpty(link.getText().toString())) {
                link.setError(getString(R.string.navigation_link_required));
                link.requestFocus();
                updateNavigation.setEnabled(true);
                return;
            }

            // Validate navigation data
            if (!TextUtils.isEmpty(title.getText().toString()) && !TextUtils.isEmpty(link.getText().toString())) {
                requestUpdateNavigation();
            }
        });
    }

    private void requestUpdateNavigation() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();

            String app_behavior;
            if (behavior.isChecked()) {
                app_behavior = "EXTERNAL";
            } else {
                app_behavior = "INAPP";
            }

            String app_premium;
            if (premium.isChecked()) {
                app_premium = "true";
            } else {
                app_premium = "false";
            }

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_NAVIGATION,
                    response -> {
                        if (response.contains("navigation:callback?success")) {
                            Intent intent = new Intent();
                            intent.putExtra("request", "update");
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            Toasto.show_toast(this, getString(R.string.unknown_issue), 0, 2);
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(this, getString(R.string.unknown_issue), 0, 2);
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("title", title.getText().toString());
                    params.put("url", link.getText().toString());
                    params.put("behavior", app_behavior);
                    params.put("premium", app_premium);
                    params.put("icon", icon.getText().toString());
                    params.put("id", String.valueOf(id));
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }
}