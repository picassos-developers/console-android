package com.picassos.mint.console.android.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.ResetPasswordEmailBottomSheetModal;
import com.picassos.mint.console.android.sheets.TwoFactorAuthBottomSheetModal;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    Bundle bundle;

    ConsolePreferences consolePreferences;
    RequestDialog requestDialog;

    private EditText email;
    private EditText password;

    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_login);

        // initialize bundle
        bundle = new Bundle();

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // validate data
        if (getIntent().getStringExtra("add_account") == null) {
            if (!consolePreferences.loadToken().equals("exception:error?token")) {
                if (!consolePreferences.loadSecretAPIKey().equals("exception:error?sak")) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(LoginActivity.this, ProjectsActivity.class));
                    finishAffinity();
                }
            }
        }

        // email
        email = findViewById(R.id.email);
        email.addTextChangedListener(validateLogin);

        // password
        password = findViewById(R.id.password);
        password.addTextChangedListener(validateLogin);


        // login
        login = findViewById(R.id.login);
        login.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(password.getText().toString())) {
                requestLogin();
            } else {
                Toasto.show_toast(this, getString(R.string.all_fields_are_required), 0, 2);
            }
        });

        // Login with QR
        findViewById(R.id.qr_login).setOnClickListener(v -> {
            Intent qrLogin = new Intent(this, QrLoginActivity.class);
            startActivityForResult.launch(qrLogin);
        });

        // Forgot Password
        TextView forgot_password = findViewById(R.id.forgot_password);
        forgot_password.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(email.getText().toString())) {
                requestSendResetEmail();
            } else {
                Toasto.show_toast(this, getString(R.string.email_empty), 1, 1);
            }
        });
    }

    /**
     * request send email
     * with verification code
     */
    private void requestSendResetEmail() {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_SEND_RESET_EMAIL,
                response -> {
                    switch (response) {
                        case "200":
                            bundle.putString("email", email.getText().toString());
                            ResetPasswordEmailBottomSheetModal resetPasswordEmailBottomSheetModal = new ResetPasswordEmailBottomSheetModal();
                            resetPasswordEmailBottomSheetModal.setArguments(bundle);
                            resetPasswordEmailBottomSheetModal.show(getSupportFragmentManager(), "TAG");
                            break;
                        case "403":
                            Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
                            break;
                        case "404":
                            Toasto.show_toast(this, getString(R.string.no_users_found), 1, 1);
                            break;
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email.getText().toString());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request login
     */
    private void requestLogin() {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_LOGIN,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("account");
                        JSONObject details = root.getJSONObject("details");
                        JSONObject responseCode = root.getJSONObject("response");

                        switch (responseCode.getInt("code")) {
                            case 200:
                                // save account details
                                consolePreferences.setSecretAPIKey("exception:error?sak");
                                consolePreferences.setToken(details.getString("token"));
                                consolePreferences.setUsername(details.getString("username"));
                                consolePreferences.setEmail(details.getString("email_address"));
                                startActivity(new Intent(LoginActivity.this, ProjectsActivity.class));
                                finishAffinity();
                                break;
                            case 404:
                                Toasto.show_toast(this, getString(R.string.no_users_found), 1, 1);
                                break;
                            case 403:
                                Toasto.show_toast(this, getString(R.string.wrong_password), 1, 1);
                                break;
                            case 201:
                                bundle.putString("TOKEN", details.getString("token"));
                                bundle.putString("USERNAME", details.getString("username"));
                                bundle.putString("EMAIL", details.getString("email_address"));
                                TwoFactorAuthBottomSheetModal twoFactorAuthBottomSheetModal = new TwoFactorAuthBottomSheetModal();
                                twoFactorAuthBottomSheetModal.setArguments(bundle);
                                twoFactorAuthBottomSheetModal.show(getSupportFragmentManager(), "TAG");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(this, "exception:error?" + error.getMessage(), 1, 1);
        }){
          @Override
          protected Map<String, String> getParams() {
              Map<String, String> params = new HashMap<>();
              params.put("email", email.getText().toString());
              params.put("password", password.getText().toString());
              return params;
          }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request login with qr code
     * @param token for token
     */
    private void requestLoginViaQR(String token) {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_LOGIN_WITH_QR,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("account");
                        JSONObject details = root.getJSONObject("details");
                        JSONObject responseCode = root.getJSONObject("response");

                        switch (responseCode.getInt("code")) {
                            case 200:
                                consolePreferences.setToken(details.getString("token"));
                                consolePreferences.setUsername(details.getString("username"));
                                consolePreferences.setEmail(details.getString("email_address"));
                                startActivity(new Intent(LoginActivity.this, ProjectsActivity.class));
                                finish();
                                break;
                            case 404:
                                Toasto.show_toast(this, getString(R.string.no_users_found), 1, 1);
                                break;
                            case 403:
                                Toasto.show_toast(this, getString(R.string.wrong_password), 1, 1);
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private TextWatcher validateLogin = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            login.setEnabled(!email.getText().toString().equals("") && !password.getText().toString().equals(""));
        }
    };

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result != null && result.getResultCode() == RESULT_OK) {
            requestLoginViaQR(Objects.requireNonNull(result.getData()).getStringExtra("token"));
        }
    });
}
