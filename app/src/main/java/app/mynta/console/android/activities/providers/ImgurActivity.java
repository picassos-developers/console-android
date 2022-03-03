package app.mynta.console.android.activities.providers;

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
import app.mynta.console.android.R;

import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.sheets.NavigationOptionsBottomSheetModal;
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.InputFilterRange;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ImgurActivity extends AppCompatActivity implements NavigationOptionsBottomSheetModal.OnRemoveListener {

    private Bundle bundle;
    private Intent intent;
    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    private String request;

    private EditText label, icon, accessToken, perPage, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_imgur);

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
            NavigationOptionsBottomSheetModal navigationOptionsBottomSheetModal = new NavigationOptionsBottomSheetModal();
            navigationOptionsBottomSheetModal.setArguments(bundle);
            navigationOptionsBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });
        if (request.equals("add")) {
            findViewById(R.id.more_options).setVisibility(View.GONE);
        }

        // toolbar title
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        if (request.equals("update")) {
            toolbarTitle.setText(getIntent().getStringExtra("label"));
        }

        // imgur data
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
        Button save = findViewById(R.id.update_imgur);
        save.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(accessToken.getText().toString())
                    && !TextUtils.isEmpty(username.getText().toString())
                    && !TextUtils.isEmpty(perPage.getText().toString())) {
                switch (request) {
                    case "add":
                        requestAddImgur();
                        break;
                    case "update":
                        requestUpdateImgur();
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
     * request imgur data
     */
    private void requestData() {
        findViewById(R.id.imgur_container).setVisibility(View.VISIBLE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_IMGUR_DETAILS,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("imgur");
                        JSONObject general = root.getJSONObject("general");

                        // label, icon
                        label.setText(general.getString("label"));
                        icon.setText(general.getString("icon"));

                        accessToken.setText(root.getString("access_token"));
                        username.setText(root.getString("username"));
                        perPage.setText(String.valueOf(root.getInt("per_page")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.imgur_container).setVisibility(View.GONE);
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
     * request add imgur provider
     */
    private void requestAddImgur() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_ADD_IMGUR_PROVIDER,
                    response -> {
                        if (response.equals("200")) {
                            intent.putExtra("added", true);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(this, getString(R.string.unknown_issue), 0, 1);
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
     * request update imgur
     */
    private void requestUpdateImgur() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_IMGUR_PROVIDER,
                    response -> {
                        if (response.equals("200")) {
                            Toasto.show_toast(this, getString(R.string.imgur_updated), 0, 0);
                            intent.putExtra("updated", true);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(this, getString(R.string.unknown_issue), 0, 1);
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
        intent.putExtra("remove", true);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}