package com.picassos.mint.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

public class ClaimGiftActivity extends AppCompatActivity {

    RequestDialog requestDialog;
    ConsolePreferences consolePreferences;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_claim_gift);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // gift steps
        TextView giftSteps = findViewById(R.id.gift_steps);
        giftSteps.setMovementMethod(LinkMovementMethod.getInstance());
        
        // fields
        EditText applicationName = findViewById(R.id.application_name);
        EditText packageName = findViewById(R.id.package_name);
        EditText driveLink = findViewById(R.id.drive_link);
        
        // submit request
        findViewById(R.id.submit_ticket).setOnClickListener(v -> {
            if (TextUtils.isEmpty(applicationName.getText().toString())
                    || TextUtils.isEmpty(packageName.getText().toString())
                    || TextUtils.isEmpty(driveLink.getText().toString())) {
                Toasto.show_toast(this, getString(R.string.all_fields_are_required), 1, 2);
            } else {
                if (driveLink.getText().toString().contains("drive.google.com")) {
                    requestSubmitRequest(applicationName.getText().toString(), packageName.getText().toString(), driveLink.getText().toString());
                } else {
                    Toasto.show_toast(this, getString(R.string.enter_valid_drive_link), 1, 2);
                }
            }
        });

        // terms of use
        TextView termsOfUse = findViewById(R.id.agreement_notice);
        termsOfUse.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * request submit request
     * @param applicationName for application name
     * @param packageName for package name
     * @param driveLink for drive link
     */
    @SuppressLint("SetTextI18n")
    private void requestSubmitRequest(String applicationName, String packageName, String driveLink) {
        requestDialog.show();
        String format =
                "Application Name: " + applicationName + "\n\n" +
                "Package Name: " + packageName + "\n\n" +
                "Drive Link: " + driveLink + "\n\n" +
                "SAK: " + consolePreferences.loadSecretAPIKey();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_SUBMIT_TICKET,
                response -> {
                    if (response.equals("200")) {
                        findViewById(R.id.agreement_notice).setVisibility(View.GONE);
                        TextView responseMessage = findViewById(R.id.response);
                        responseMessage.setVisibility(View.VISIBLE);
                        responseMessage.setText(getString(R.string.request_submitted_successfully_we_will_verify_your_data_and_send_you_the_customized_source_code_to) + " " + consolePreferences.loadEmail());
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(this, getString(R.string.unknown_issue), 0, 1);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", consolePreferences.loadToken());
                params.put("username", consolePreferences.loadUsername());
                params.put("email", consolePreferences.loadEmail());
                params.put("request_subject", "Customization Request [Gift]");
                params.put("request_description", format);
                params.put("request_type", "customization");
                params.put("identifier", "gift");
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}