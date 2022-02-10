package com.picassos.mint.console.android.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.libraries.passwordstrength.PasswordStrength;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Bundle bundle;

    ConsolePreferences consolePreferences;
    RequestDialog requestDialog;

    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_register);

        // initialize bundle
        bundle = new Bundle();

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // agreement notice
        TextView agreementNotice = findViewById(R.id.agreement_notice);
        agreementNotice.setMovementMethod(LinkMovementMethod.getInstance());

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        password.addTextChangedListener(onPasswordValueChange);
        confirmPassword = findViewById(R.id.confirm_password);

        final Button register = findViewById(R.id.register);

        register.setOnClickListener(v -> {

            if (TextUtils.isEmpty(username.getText().toString())) {
                username.setError(getString(R.string.username_empty));
                username.requestFocus();
                register.setEnabled(true);
                return;
            }

            if (TextUtils.isEmpty(email.getText().toString())) {
                email.setError(getString(R.string.email_empty));
                email.requestFocus();
                register.setEnabled(true);
                return;
            }

            if (TextUtils.isEmpty(password.getText().toString()) || TextUtils.isEmpty(confirmPassword.getText().toString())) {
                Toasto.show_toast(this, getString(R.string.passwords_empty), 1, 1);
            } else {
                if (password.getText().toString().length() >= 8) {
                    if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                        Toasto.show_toast(this, getString(R.string.passwords_not_match), 1, 1);
                    } else {
                        if (!TextUtils.isEmpty(username.getText().toString())
                                && !TextUtils.isEmpty(email.getText().toString())) {
                            requestRegister();
                        }
                    }
                } else {
                    Toasto.show_toast(this, getString(R.string.short_password), 1, 1);
                }
            }

        });
    }

    /**
     * on password field value change, check
     * for strength of password entered
     */
    private final TextWatcher onPasswordValueChange = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @SuppressLint("SetTextI18n")
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkPasswordStrength(s.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void checkPasswordStrength(String password) {
        PasswordStrength passwordStrength = PasswordStrength.validate(password);
        ProgressBar progressBar = findViewById(R.id.password_strength_indicator);
        progressBar.setProgress(passwordStrength.callback);
    }

    /**
     * request register
     */
    private void requestRegister() {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_REGISTER,
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
                                startActivity(new Intent(RegisterActivity.this, ProjectsActivity.class));
                                finishAffinity();
                                break;
                            case 0:
                                Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
                                break;
                            case 403:
                                Toasto.show_toast(this, getString(R.string.user_exists), 1, 1);
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
              params.put("username", username.getText().toString().trim());
              params.put("email", email.getText().toString().trim());
              params.put("password", password.getText().toString().trim());
              return params;
          }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
