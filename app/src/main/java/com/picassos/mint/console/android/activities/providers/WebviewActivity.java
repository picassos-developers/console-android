package com.picassos.mint.console.android.activities.providers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WebviewActivity extends AppCompatActivity {

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    private EditText baseUrl;
    private SwitchCompat bookmarksPage, downloadsPage;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_webview);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // webview data
        baseUrl = findViewById(R.id.base_url);
        bookmarksPage = findViewById(R.id.bookmarks_page);
        downloadsPage = findViewById(R.id.downloads_page);

        // request data
        requestData();

        // save data
        Button save = findViewById(R.id.update_webview);
        save.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(baseUrl.getText().toString())) {
                requestUpdateWebView();
            } else {
                Toasto.show_toast(this, getString(R.string.base_url_required), 0, 2);
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
    }

    /**
     * request webview data
     */
    private void requestData() {
        findViewById(R.id.webview_container).setVisibility(View.VISIBLE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_WEBVIEW_DETAILS,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("webview");
                        // base url
                        baseUrl.setText(root.getString("base_url"));

                        // bookmarks page
                        switch (root.getString("bookmarks_page")) {
                            case "true":
                                bookmarksPage.setChecked(true);
                                break;
                            case "false":
                                bookmarksPage.setChecked(false);
                                break;
                        }
                        // downloads page
                        switch (root.getString("downloads_page")) {
                            case "true":
                                downloadsPage.setChecked(true);
                                break;
                            case "false":
                                downloadsPage.setChecked(false);
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.webview_container).setVisibility(View.GONE);
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestData());
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
     * request update webview
     */
    private void requestUpdateWebView() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();
            String bookmarks;
            if (bookmarksPage.isChecked()) {
                bookmarks = "true";
            } else {
                bookmarks = "false";
            }

            String downloads;
            if (downloadsPage.isChecked()) {
                downloads = "true";
            } else {
                downloads = "false";
            }

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_WEBVIEW,
                    response -> {
                        if (response.contains("exception:configuration?success")) {
                            Toasto.show_toast(this, getString(R.string.webview_updated), 0, 0);
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(this, "exception:error?" + error.getMessage(), 0, 1);
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("base_url", baseUrl.getText().toString());
                    params.put("bookmarks_page", bookmarks);
                    params.put("downloads_page", downloads);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }
}