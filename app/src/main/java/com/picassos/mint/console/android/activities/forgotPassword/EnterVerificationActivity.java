package com.picassos.mint.console.android.activities.forgotPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class EnterVerificationActivity extends AppCompatActivity {

    RequestDialog requestDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        setContentView(R.layout.activity_enter_verification);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // verification code
        EditText verificationCode = findViewById(R.id.verification_code);

        // verify
        findViewById(R.id.verify).setOnClickListener(v -> {
            if (!TextUtils.isEmpty(verificationCode.getText().toString())) {
                requestVerify(verificationCode.getText().toString());
            } else {
                Toasto.show_toast(this, getString(R.string.verification_code_empty), 2, 1);
            }
        });
    }

    /**
     * request verify code
     * @param code for verification code
     */
    private void requestVerify(String code) {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_VERIFY_CODE,
                response -> {
                    if (response.equals("403")) {
                        Toasto.show_toast(this, getString(R.string.verification_code_expired), 1, 1);
                    } else if (response.equals("404")) {
                        Toasto.show_toast(this, getString(R.string.no_users_found), 1, 1);
                    } else {
                        Intent intent = new Intent(EnterVerificationActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("email", getIntent().getStringExtra("email"));
                        intent.putExtra("token", response);
                        startActivity(intent);
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", getIntent().getStringExtra("email"));
                params.put("verification_code", code);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}