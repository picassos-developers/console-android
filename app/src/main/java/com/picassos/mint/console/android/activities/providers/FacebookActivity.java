package com.picassos.mint.console.android.activities.providers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
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
import com.picassos.mint.console.android.sheets.ProviderOptionsBottomSheetModal;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.InputFilterRange;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FacebookActivity extends AppCompatActivity implements ProviderOptionsBottomSheetModal.OnRemoveListener {
    private Bundle bundle;
    private Intent intent;

    private RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    private String request;

    private EditText label, icon,
            accessToken, perPage, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_facebook);

        bundle = new Bundle();
        intent = new Intent();

        // get request type
        request = getIntent().getStringExtra("request");

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // more options
        findViewById(R.id.more_options).setOnClickListener(v -> {
            bundle.putInt("identifier", getIntent().getIntExtra("identifier", 0));
            bundle.putString("type", getIntent().getStringExtra("type"));
            ProviderOptionsBottomSheetModal providerOptionsBottomSheetModal = new ProviderOptionsBottomSheetModal();
            providerOptionsBottomSheetModal.setArguments(bundle);
            providerOptionsBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });
        if (request.equals("add")) {
            findViewById(R.id.more_options).setVisibility(View.GONE);
        }

        // toolbar title
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        if (request.equals("update")) {
            toolbarTitle.setText(getIntent().getStringExtra("label"));
        }

        // facebook data
        label = findViewById(R.id.provider_label);
        icon = findViewById(R.id.provider_icon);
        accessToken = findViewById(R.id.access_token);
        username = findViewById(R.id.username);
        perPage = findViewById(R.id.per_page);
        perPage.setFilters(new InputFilter[]{ new InputFilterRange("1", "25")});

        // request data
        if (request.equals("update")) {
            requestData();
        }

        // save data
        Button save = findViewById(R.id.update_facebook);
        save.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(accessToken.getText().toString())
                    && !TextUtils.isEmpty(username.getText().toString())
                    && !TextUtils.isEmpty(perPage.getText().toString())) {
                switch (request) {
                    case "add":
                        requestAddFacebook();
                        break;
                    case "update":
                        requestUpdateFacebook();
                        break;
                }
            } else {
                Toasto.show_toast(this, getString(R.string.all_fields_are_required), 0, 2);
            }
        });

        // refresh
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestData();
        });
        if (request.equals("add")) {
            refresh.setEnabled(false);
        }
    }

    /**
     * request facebook data
     */
    private void requestData() {
        findViewById(R.id.facebook_container).setVisibility(View.VISIBLE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_FACEBOOK_DETAILS,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("facebook");
                        JSONObject general = root.getJSONObject("general");

                        // label, icon
                        label.setText(general.getString("label"));
                        icon.setText(general.getString("icon"));

                        accessToken.setText(root.getString("access_token"));
                        username.setText(root.getString("username"));
                        perPage.setText(root.getString("per_page"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.facebook_container).setVisibility(View.GONE);
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestData());
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                params.put("identifier", String.valueOf(getIntent().getIntExtra("identifier", 0)));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request add facebook provider
     */
    private void requestAddFacebook() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_ADD_FACEBOOK_PROVIDER,
                    response -> {
                        if (response.equals("200")) {
                            intent.putExtra("added", true);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("label", label.getText().toString());
                    params.put("icon", icon.getText().toString());
                    params.put("access_token", accessToken.getText().toString());
                    params.put("username", username.getText().toString());
                    params.put("per_page", perPage.getText().toString());
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }

    /**
     * request update facebook
     */
    private void requestUpdateFacebook() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_FACEBOOK_PROVIDER,
                    response -> {
                        if (response.equals("200")) {
                            Toasto.show_toast(this, getString(R.string.facebook_updated), 1, 0);
                            intent.putExtra("updated", true);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("identifier", String.valueOf(getIntent().getIntExtra("identifier", 0)));
                    params.put("label", label.getText().toString());
                    params.put("icon", icon.getText().toString());
                    params.put("access_token", accessToken.getText().toString());
                    params.put("username", username.getText().toString());
                    params.put("per_page", perPage.getText().toString());
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }

    @Override
    public void onRemoveListener(int requestCode) {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}