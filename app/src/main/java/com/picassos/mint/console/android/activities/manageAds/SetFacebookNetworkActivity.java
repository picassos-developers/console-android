package com.picassos.mint.console.android.activities.manageAds;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
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

public class SetFacebookNetworkActivity extends AppCompatActivity {

    ConsolePreferences consolePreferences;

    // request dialog
    private RequestDialog requestDialog;

    // fields
    private EditText nativeId;
    private EditText interstitialId;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_set_facebook_network);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // fields
        nativeId = findViewById(R.id.facebook_native_ad_id);
        interstitialId = findViewById(R.id.facebook_interstitial_ad_id);

        // update provider
        findViewById(R.id.update_provider).setOnClickListener(v -> {
            if (!TextUtils.isEmpty(nativeId.getText().toString()) && !TextUtils.isEmpty(interstitialId.getText().toString())) {
                requestUpdateAdProvider();
            } else {
                Toasto.show_toast(this, getString(R.string.all_fields_are_required), 1, 2);
            }
        });
    }

    /**
     * request update ad provider
     */
    private void requestUpdateAdProvider() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_AD_PROVIDER,
                    response -> {
                        Toasto.show_toast(this, getString(R.string.facebook_network), 0, 0);
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(this, getString(R.string.unknown_issue), 0, 2);
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("provider", "facebook");
                    params.put("facebook_native_ad_id", nativeId.getText().toString());
                    params.put("facebook_interstitial_ad_id", interstitialId.getText().toString());
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }
}