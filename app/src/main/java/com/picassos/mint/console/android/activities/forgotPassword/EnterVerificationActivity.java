package com.picassos.mint.console.android.activities.forgotPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EnterVerificationActivity extends AppCompatActivity {
    RequestDialog requestDialog;

    Button verify;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        setContentView(R.layout.activity_enter_verification);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // get email
        TextView emailVerification = findViewById(R.id.email_verification);
        emailVerification.setText(getString(R.string.please_enter_the_verification_code_sent_to) + " " + getIntent().getStringExtra("email"));

        // verification code
        PinView verificationCode = findViewById(R.id.verification_code);
        verificationCode.addTextChangedListener(onVerificationCodeEnter);

        // resend again timer
        resendAgain();

        // verify
        verify = findViewById(R.id.verify);
        verify.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(Objects.requireNonNull(verificationCode.getText()).toString())) {
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
                            Toasto.show_toast(this, getString(R.string.email_resent), 1, 2);
                            resendAgain();
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
                params.put("email", getIntent().getStringExtra("email"));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * on verification code field value change,
     * enable verification button if not empty
     */
    private final TextWatcher onVerificationCodeEnter = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressLint("SetTextI18n")
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 4) {
                verify.setVisibility(View.VISIBLE);
            } else {
                verify.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    /**
     * request resend code again
     */
    private void resendAgain() {
        findViewById(R.id.resend_again).setVisibility(View.GONE);
        new CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                TextView timer = findViewById(R.id.resend_again_timer);
                timer.setText(getString(R.string.resend_code_in) + millisUntilFinished / 1000);
            }
            public void onFinish() {
                findViewById(R.id.resend_again).setVisibility(View.VISIBLE);
                findViewById(R.id.resend_again).setOnClickListener(v -> requestSendResetEmail());
            }
        }.start();
    }
}