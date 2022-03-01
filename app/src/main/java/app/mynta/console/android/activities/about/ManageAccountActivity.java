package app.mynta.console.android.activities.about;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

public class ManageAccountActivity extends AppCompatActivity {

    // console preferences
    ConsolePreferences consolePreferences;

    // request dialog
    RequestDialog requestDialog;

    // account details
    private EditText username2;
    private SwitchCompat mintConsoleNewsletter;
    private SwitchCompat picassosNewsletter;
    private SwitchCompat _2fa;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_manage_account);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // profile
        TextView usernameProfile = findViewById(R.id.username_profile);
        TextView username = findViewById(R.id.member_username);
        TextView email = findViewById(R.id.email);
        usernameProfile.setText(consolePreferences.loadUsername().substring(0, 1).toUpperCase());
        username.setText(consolePreferences.loadUsername().substring(0, 1).toUpperCase() + consolePreferences.loadUsername().substring(1));
        email.setText(consolePreferences.loadEmail());

        // account details
        username2 = findViewById(R.id.username);
        mintConsoleNewsletter = findViewById(R.id.mint_console_newsletter);
        picassosNewsletter = findViewById(R.id.picassos_newsletter);
        _2fa = findViewById(R.id.two_factor_auth);

        // request account details
        requestAccountDetails();

        // refresh account details
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestAccountDetails();
        });

        // save button
        findViewById(R.id.save).setOnClickListener(v -> {
            if (!TextUtils.isEmpty(username2.getText().toString())) {
                requestSaveAccountDetails();
            } else {
                Toasto.show_toast(this, getString(R.string.all_fields_are_required), 0, 2);
            }
        });

        // change password
        findViewById(R.id.change_password).setOnClickListener(v -> startActivity(new Intent(this, UpdatePasswordActivity.class)));
    }

    /**
     * request account details
     */
    private void requestAccountDetails() {
        findViewById(R.id.account_container).setVisibility(View.VISIBLE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_ACCOUNT_DETAILS,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("account");
                        JSONObject details = root.getJSONObject("details");
                        JSONObject responseCode = root.getJSONObject("response");
                        JSONObject newsletter = details.getJSONObject("newsletter");

                        if (responseCode.getInt("code") == 200) {
                            username2.setText(details.getString("username"));
                            consolePreferences.setUsername(details.getString("username"));
                            mintConsoleNewsletter.setChecked(getChecked(newsletter.getInt("newsletter")));
                            picassosNewsletter.setChecked(getChecked(newsletter.getInt("picassos_newsletter")));
                            _2fa.setChecked(getChecked(details.getInt("2fa")));
                        } else {
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.account_container).setVisibility(View.GONE);
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestAccountDetails());
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", consolePreferences.loadToken());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request save account details
     */
    private void requestSaveAccountDetails() {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_ACCOUNT,
                response -> {
                    if (response.equals("200")) {
                        Toasto.show_toast(this, getString(R.string.account_updated_successfully), 0, 0);
                        requestAccountDetails();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", consolePreferences.loadToken());
                params.put("username", username2.getText().toString());
                params.put("newsletter", String.valueOf(getState(mintConsoleNewsletter)));
                params.put("picassos_newsletter", String.valueOf(getState(picassosNewsletter)));
                params.put("two_factor_authentication", String.valueOf(getState(_2fa)));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * get switch compat state
     * @param value for switch compat checked
     * @return state in string format
     */
    private int getState(SwitchCompat value) {
        if (value.isChecked()) {
            return 1;
        } else {
            return 0;
        }
    }

    private Boolean getChecked(int value) {
        return value == 1;
    }
}