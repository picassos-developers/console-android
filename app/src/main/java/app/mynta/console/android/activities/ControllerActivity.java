package app.mynta.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ControllerActivity extends AppCompatActivity {

    // request
    private String REQUEST;

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    private EditText controllerContent;
    private SwitchCompat darkMode;

    private boolean firstTime = true;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_controller);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // get request type
        REQUEST = getIntent().getStringExtra("request");

        // request controller
        requestController();

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // update controller
        controllerContent = findViewById(R.id.controller_content);
        Button updateController = findViewById(R.id.update_controller);
        updateController.setOnClickListener(v -> requestUpdateController(controllerContent.getText().toString()));

        // set controller type
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        TextView controllerTitle = findViewById(R.id.controller_title);
        switch (Objects.requireNonNull(REQUEST)) {
            case "customcss":
                toolbarTitle.setText(getString(R.string.custom_css));
                controllerTitle.setText(getString(R.string.custom_css));
                findViewById(R.id.darkmode_container).setVisibility(View.GONE);
                break;
            case "darkmode":
                toolbarTitle.setText(getString(R.string.dark_mode));
                controllerTitle.setText(getString(R.string.dark_mode));
                darkMode = findViewById(R.id.dark_mode);
                darkMode.setOnCheckedChangeListener((buttonView, isChecked) -> requestUpdateDarkMode(isChecked));
        }

        // refresh account details
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestController();
        });
    }

    /**
     * request update dark mode option
     * @param isChecked for check status
     */
    private void requestUpdateDarkMode(boolean isChecked) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            String status;
            if (isChecked) {
                status = "true";
            } else {
                status = "false";
            }

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_DARK_MODE_OPTION,
                    response -> {
                        if (response.contains("exception:configuration?success")) {
                            if (firstTime) {
                                firstTime = false;
                            } else {
                                Toasto.show_toast(this, getString(R.string.dark_mode_data_updated), 0, 0);
                            }
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
                    params.put("status", status);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }

    /**
     * request controller
     */
    private void requestController() {
        findViewById(R.id.controller_container).setVisibility(View.VISIBLE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();
        String requestUrl = "";

        switch (REQUEST) {
            case "customcss":
                requestUrl = API.REQUEST_CUSTOM_CSS;
                break;
            case "darkmode":
                requestUrl = API.REQUEST_DARK_MODE;
        }

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + requestUrl,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject(REQUEST);
                        // set controller content
                        controllerContent.setText(root.getString("rendered"));
                        // dark mode option
                        switch (root.getString("option")) {
                            case "true":
                                darkMode.setChecked(true);
                                break;
                            case "false":
                                darkMode.setChecked(false);
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.controller_container).setVisibility(View.GONE);
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestController());
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
     * request update controller
     */
    private void requestUpdateController(String content) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();
            String requestUrl = "";

            switch (REQUEST) {
                case "customcss":
                    requestUrl = API.REQUEST_UPDATE_CUSTOM_CSS;
                    break;
                case "darkmode":
                    requestUrl = API.REQUEST_UPDATE_DARK_MODE;
            }

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + requestUrl,
                    response -> {
                        if (response.contains("exception:update?success")) {
                            Toasto.show_toast(this, getString(R.string.dark_mode_data_updated), 0, 0);
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
                    params.put("content", content);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }
}