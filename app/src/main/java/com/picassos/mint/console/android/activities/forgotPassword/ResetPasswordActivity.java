package com.picassos.mint.console.android.activities.forgotPassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.LoginActivity;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {

    // console preferences
    ConsolePreferences consolePreferences;

    // request dialog
    private RequestDialog requestDialog;

    // fields
    private EditText newPassword;
    private EditText confirmPassword;

    // update password
    private Button updatePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_reset_password);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // new password
        newPassword = findViewById(R.id.new_password);
        newPassword.addTextChangedListener(onPasswordValueChange);

        // confirm password
        confirmPassword = findViewById(R.id.confirm_password);
        confirmPassword.addTextChangedListener(onPasswordValueChange);

        // change password
        updatePassword = findViewById(R.id.update_password);
        updatePassword.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(newPassword.getText().toString()) && !TextUtils.isEmpty(confirmPassword.getText().toString())) {
                if (newPassword.getText().toString().length() >= 8) {
                    if (newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                        requestChangePassword();
                    } else {
                        Toasto.show_toast(this, getString(R.string.new_confirm_password_not_same), 0, 2);
                    }
                } else {
                    Toasto.show_toast(this, getString(R.string.short_password), 0, 2);
                }
            } else {
                Toasto.show_toast(this, getString(R.string.new_confirm_password_empty), 0, 2);
            }
        });
    }

    /**
     * request update password
     */
    private void requestChangePassword() {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_RESET_PASSWORD,
                response -> {
                    switch (response) {
                        case "200":
                            Toasto.show_toast(this, getString(R.string.password_updated_successfully), 0, 0);
                            startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                            finishAffinity();
                            break;
                        case "0":
                        case "403":
                            Toasto.show_toast(this, getString(R.string.unknown_issue), 0, 1);
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
                params.put("token", getIntent().getStringExtra("token"));
                params.put("email", getIntent().getStringExtra("email"));
                params.put("new_password", newPassword.getText().toString().trim());
                params.put("confirm_password", confirmPassword.getText().toString().trim());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * on password field value change, update
     * password button status
     */
    private final TextWatcher onPasswordValueChange = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            validatePassword();
        }
    };

    /**
     * validate password
     */
    private void validatePassword() {
        if(TextUtils.isEmpty(newPassword.getText().toString())
                || TextUtils.isEmpty(confirmPassword.getText().toString())){
            updatePassword.setVisibility(View.GONE);
        } else {
            updatePassword.setVisibility(View.VISIBLE);
        }
    }
}