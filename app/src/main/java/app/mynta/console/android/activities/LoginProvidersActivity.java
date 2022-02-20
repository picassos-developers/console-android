package app.mynta.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;

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

public class LoginProvidersActivity extends AppCompatActivity {

    RequestDialog requestDialog;
    ConsolePreferences consolePreferences;

    // providers
    private SwitchCompat donut;
    private SwitchCompat google;
    private SwitchCompat facebook;
    private SwitchCompat firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_login_providers);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // initialize providers
        donut = findViewById(R.id.donut_login);
        google = findViewById(R.id.google_login);
        facebook = findViewById(R.id.facebook_login);
        firebase = findViewById(R.id.firebase_login);

        // request providers
        requestProviders();

        // refresh
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestProviders();
        });

        // update login providers
        findViewById(R.id.update_login_providers).setOnClickListener(v -> requestUpdateProviders());
    }

    /**
     * request login providers
     */
    private void requestProviders() {
        findViewById(R.id.login_providers_container).setVisibility(View.VISIBLE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_LOGIN_PROVIDERS,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("login_providers");

                        // set providers
                        donut.setChecked(getState(root.getInt("donut")));
                        google.setChecked(getState(root.getInt("google")));
                        facebook.setChecked(getState(root.getInt("facebook")));
                        firebase.setChecked(getState(root.getInt("firebase")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.login_providers_container).setVisibility(View.GONE);
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestProviders());
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
     * request update providers
     */
    private void requestUpdateProviders() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_LOGIN_PROVIDERS,
                    response -> {
                        if (response.equals("exception:configuration?success")) {
                            Toasto.show_toast(this, getString(R.string.login_providers_updated), 1, 0);
                        } else {
                            Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("donut", String.valueOf(setState(donut.isChecked())));
                    params.put("google", String.valueOf(setState(google.isChecked())));
                    params.put("facebook", String.valueOf(setState(facebook.isChecked())));
                    params.put("firebase", String.valueOf(setState(firebase.isChecked())));
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }

    /**
     * get state
     * @param value for int value 0 or 1
     * @return boolean
     */
    private boolean getState(int value) {
        return value == 1;
    }

    /**
     * set state
     * @param value for boolean true or false
     * @return value in integer type
     */
    private int setState(boolean value) {
        if (value) {
            return 1;
        } else {
            return 0;
        }
    }
}