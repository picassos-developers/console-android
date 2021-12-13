package com.picassos.mint.console.android.activities;

import android.app.Activity;
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
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class EditWizardActivity extends AppCompatActivity {

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    // wizard id
    private int id;

    // wizard details
    EditText title, description, thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_edit_wizard);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // get id
        id = getIntent().getIntExtra("wizard_id", 0);

        // get wizard details
        String wizardTitle = getIntent().getStringExtra("wizard_title");
        String wizardDescription = getIntent().getStringExtra("wizard_description");
        String wizardThumbnail = getIntent().getStringExtra("wizard_thumbnail");

        // wizard details
        title = findViewById(R.id.wizard_title_input);
        description = findViewById(R.id.wizard_description_input);
        thumbnail = findViewById(R.id.wizard_thumbnail_input);

        // set details
        title.setText(wizardTitle);
        description.setText(wizardDescription);
        thumbnail.setText(wizardThumbnail);

        // update wizard
        Button updateWizard = findViewById(R.id.update_wizard);
        updateWizard.setOnClickListener(v -> {
            // validate wizard data
            if (!TextUtils.isEmpty(title.getText().toString())
                    && !TextUtils.isEmpty(description.getText().toString())
                    && !TextUtils.isEmpty(thumbnail.getText().toString())) {
                requestUpdateWizard();
            }
        });
    }

    private void requestUpdateWizard() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_WIZARD,
                    response -> {
                        if (response.contains("wizard:callback?success")) {
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