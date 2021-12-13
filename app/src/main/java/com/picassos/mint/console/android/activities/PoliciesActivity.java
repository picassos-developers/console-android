package com.picassos.mint.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PoliciesActivity extends AppCompatActivity {

    // request
    private String REQUEST;

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    private EditText policiesContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_policies);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // get request type
        REQUEST = getIntent().getStringExtra("request");

        // request policies
        requestPolicies();

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // policies content
        // update policies
        policiesContent = findViewById(R.id.policies_content);
        Button updatePolicies = findViewById(R.id.update_policies);
        updatePolicies.setOnClickListener(v -> requestUpdatePolicies(policiesContent.getText().toString()));

        // set policies type
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        TextView policiesTitle = findViewById(R.id.policies_title);
        switch (Objects.requireNonNull(REQUEST)) {
            case "privacy":
                toolbarTitle.setText(getString(R.string.privacy_policy));
                policiesTitle.setText(getString(R.string.privacy_policy));
                break;
            case "terms":
                toolbarTitle.setText(getString(R.string.terms_of_use));
                policiesTitle.setText(getString(R.string.terms_of_use));
        }

        // refresh
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestPolicies();
        });
    }

    /**
     * request policies
     */
    private void requestPolicies() {
        findViewById(R.id.policies_container).setVisibility(View.VISIBLE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();
        String requestUrl = "";

        switch (REQUEST) {
            case "privacy":
                requestUrl = API.REQUEST_PRIVACY_POLICY;
                break;
            case "terms":
                requestUrl = API.REQUEST_TERMS_OF_USE;
        }

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + requestUrl,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject(REQUEST);
                        // set policies content
                        policiesContent.setText(root.getString("rendered"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.policies_container).setVisibility(View.GONE);
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestPolicies());
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request update policies
     */
    private void requestUpdatePolicies(String content) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();
            String requestUrl = "";

            switch (REQUEST) {
                case "privacy":
                    requestUrl = API.REQUEST_UPDATE_PRIVACY_POLICY;
                    break;
                case "terms":
                    requestUrl = API.REQUEST_UPDATE_TERMS_OF_USE;
            }

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + requestUrl,
                    response -> {
                        if (response.contains("200")) {
                            Toasto.show_toast(this, getString(R.string.policies_updated), 0, 0);
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(this, getString(R.string.unknown_issue), 0, 0);
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("content", content);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }

}