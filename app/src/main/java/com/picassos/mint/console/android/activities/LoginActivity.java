package com.picassos.mint.console.android.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.adapter.StepAccountsAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.entities.AccountEntity;
import com.picassos.mint.console.android.room.APP_DATABASE;
import com.picassos.mint.console.android.room.DAO;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.ManageAccountsBottomSheetModal;
import com.picassos.mint.console.android.sheets.ResetPasswordEmailBottomSheetModal;
import com.picassos.mint.console.android.sheets.TwoFactorAuthBottomSheetModal;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    Bundle bundle;

    ConsolePreferences consolePreferences;
    RequestDialog requestDialog;

    private EditText email;
    private EditText password;

    private String email_value;
    private String password_value;

    // adapter & accounts model
    private RecyclerView recyclerView;
    private StepAccountsAdapter adapter;
    private List<AccountEntity> accounts;

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

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        final Button login = findViewById(R.id.login);

        login.setOnClickListener(v -> {

            email_value = email.getText().toString();
            password_value = password.getText().toString();

            if (TextUtils.isEmpty(email_value)) {
                email.setError(getString(R.string.email_empty));
                email.requestFocus();
                login.setEnabled(true);
                return;
            }

            if (TextUtils.isEmpty(password_value)) {
                password.setError(getString(R.string.password_empty));
                password.requestFocus();
                login.setEnabled(true);
                return;
            }

            // checking if password length is smaller than 8 chars
            if (password_value.length() <= 8) {
                password.setError(getString(R.string.short_password));
                password.requestFocus();
                login.setEnabled(true);
                return;
            }

            if (!TextUtils.isEmpty(email_value) || !TextUtils.isEmpty(password_value)) {
                requestLogin();
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

        // Register Button
        TextView register = findViewById(R.id.register);
        register.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        register.setOnLongClickListener(v -> {
            ManageAccountsBottomSheetModal manageAccountsBottomSheetModal = new ManageAccountsBottomSheetModal();
            manageAccountsBottomSheetModal.show(getSupportFragmentManager(), "TAG");
            return true;
        });

        // accounts list initialize
        recyclerView = findViewById(R.id.recycler_accounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // accounts list, adapter
        accounts = new ArrayList<>();
        adapter = new StepAccountsAdapter(accounts, item -> {
            if (!item.token.equals(consolePreferences.loadToken())) {

            }
        });
        recyclerView.setAdapter(adapter);

        requestAccounts();
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
                                // save account details on accounts center
                                DAO dao = APP_DATABASE.requestDatabase(this).requestDAO();
                                AccountEntity account = new AccountEntity();
                                if (dao.requestAccountsExists(details.getString("token")) == 0) {
                                    account.token = details.getString("token");
                                    account.username = details.getString("username");
                                    account.email = details.getString("email_address");
                                    dao.requestInsertAccount(account);
                                }
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
              params.put("email", email_value);
              params.put("password", password_value);
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

    /**
     * request accounts from
     * DAO, accounts entity
     */
    private void requestAccounts() {
        if (APP_DATABASE.requestDatabase(getApplicationContext()).requestDAO().requestAccountsCount() != 0) {
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }

        @SuppressLint("StaticFieldLeak")
        class GetAccountsTask extends AsyncTask<Void, Void, List<AccountEntity>> {
            @Override
            protected List<AccountEntity> doInBackground(Void... voids) {
                return APP_DATABASE.requestDatabase(getApplicationContext()).requestDAO().requestAllAccounts();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(List<AccountEntity> accounts_inline) {
                super.onPostExecute(accounts_inline);
                accounts.addAll(accounts_inline);
                adapter.notifyDataSetChanged();
            }

        }
        new GetAccountsTask().execute();
    }

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result != null && result.getResultCode() == RESULT_OK) {
            requestLoginViaQR(Objects.requireNonNull(result.getData()).getStringExtra("token"));
        }
    });
}
