package com.picassos.mint.console.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.constants.RequestCodes;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class EditWalkthroughActivity extends AppCompatActivity {

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    // walkthrough id
    private int id;

    // walkthrough details
    EditText title, description, thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_edit_walkthrough);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // get id
        id = getIntent().getIntExtra("walkthrough_id", 0);

        // get walkthrough details
        String walkthroughTitle = getIntent().getStringExtra("walkthrough_title");
        String walkthroughDescription = getIntent().getStringExtra("walkthrough_description");
        String walkthroughThumbnail = getIntent().getStringExtra("walkthrough_thumbnail");

        // walkthrough details
        title = findViewById(R.id.walkthrough_title_input);
        description = findViewById(R.id.walkthrough_description_input);
        thumbnail = findViewById(R.id.walkthrough_thumbnail_input);

        // set details
        title.setText(walkthroughTitle);
        description.setText(walkthroughDescription);
        thumbnail.setText(walkthroughThumbnail);

        // update walkthrough
        Button updateWalkthrough = findViewById(R.id.update_walkthrough);
        updateWalkthrough.setOnClickListener(v -> {
            // validate walkthrough data
            if (!TextUtils.isEmpty(title.getText().toString())
                    && !TextUtils.isEmpty(description.getText().toString())
                    && !TextUtils.isEmpty(thumbnail.getText().toString())) {
                requestUpdateWalkthrough();
            }
        });
    }

    private void requestUpdateWalkthrough() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_WALKTHROUGH,
                    response -> {
                        if (response.contains("walkthrough:callback?success")) {
                            Intent intent = new Intent();
                            setResult(RequestCodes.REQUEST_UPDATE_WALKTRHOUGH_CODE, intent);
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
                    params.put("description", description.getText().toString());
                    params.put("thumbnail", thumbnail.getText().toString());
                    params.put("id", String.valueOf(id));
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }
}