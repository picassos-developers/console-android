package com.picassos.mint.console.android.activities.about;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.ResetPasswordEmailBottomSheetModal;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class UpdatePasswordActivity extends AppCompatActivity {
    Bundle bundle;

    // console preferences
    ConsolePreferences consolePreferences;

    // request dialog
    private RequestDialog requestDialog;

    // fields
    private EditText currentPassword;
    private EditText newPassword;
    private EditText confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_update_password);

        // initialize bundle
        bundle = new Bundle();

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // initialize fields
        currentPassword = findViewById(R.id.current_password);
        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.confirm_password);

        // change password
        findViewById(R.id.change_password).setOnClickListener(v -> {
            if (!TextUtils.isEmpty(currentPassword.getText().toString())) {
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
            } else {
                Toasto.show_toast(this, getString(R.string.current_password_empty), 0, 2);
            }
        });

        // forget password
        findViewById(R.id.forgot_password).setOnClickListener(v -> requestSendResetEmail());
    }

    /**
     * request change account password
     */
    private void requestChangePassword() {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_PASSWORD,
                response -> {
                    switch (response) {
                        case "200":
                            Toasto.show_toast(this, getString(R.string.password_updated_successfully), 0, 0);
                            finish();
                            break;
                        case "403":
                            Toasto.show_toast(this, getString(R.string.current_password_wrong), 0, 1);
                            break;
                        case "0":
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
                params.put("token", consolePreferences.loadToken());
                params.put("current_password", currentPassword.getText().toString().trim());
                params.put("new_password", newPassword.getText().toString().trim());
                params.put("confirm_password", confirmPassword.getText().toString().trim());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
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
                            bundle.putString("email", consolePreferences.loadEmail());
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
                params.put("email", consolePreferences.loadEmail());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}