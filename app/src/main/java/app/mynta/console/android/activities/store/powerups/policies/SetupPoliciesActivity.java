package app.mynta.console.android.activities.store.powerups.policies;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.mynta.console.android.R;

import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.sheets.LearnMoreBottomSheetModal;
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SetupPoliciesActivity extends AppCompatActivity {

    RequestDialog requestDialog;
    ConsolePreferences consolePreferences;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_setup_policies);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // policies learn more
        findViewById(R.id.policies_learnmore).setOnClickListener(v -> showLearnMore(getString(R.string.policies_learnmore_title), getString(R.string.policies_learnmore_description), ""));

        // fields
        EditText applicationName = findViewById(R.id.application_name);
        EditText companyName = findViewById(R.id.company_name);
        EditText companyAddress = findViewById(R.id.company_address);
        EditText emailAddress = findViewById(R.id.email_address);
        EditText country = findViewById(R.id.your_country);

        // policy effective date
        TextView policyEffectiveDate = findViewById(R.id.policy_effective_date);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(date);
        policyEffectiveDate.setText(getString(R.string.policy_effective_date) + " " + formattedDate);

        findViewById(R.id.learn_more).setOnClickListener(v -> showLearnMore(getString(R.string.what_is_policy_effective_date), getString(R.string.effective_date_description), ""));

        // generate policies
        findViewById(R.id.generate_policies).setOnClickListener(v -> {
            if (!TextUtils.isEmpty(applicationName.getText().toString())
                    && !TextUtils.isEmpty(companyName.getText().toString()) && !TextUtils.isEmpty(companyAddress.getText().toString())
                    && !TextUtils.isEmpty(emailAddress.getText().toString()) && !TextUtils.isEmpty(country.getText().toString())) {
                requestGeneratePolicies(applicationName.getText().toString(), companyName.getText().toString(), companyAddress.getText().toString(), emailAddress.getText().toString(), country.getText().toString());
            } else {
                Toasto.show_toast(this, getString(R.string.all_fields_are_required), 1, 2);
            }
        });
    }

    /**
     * request generate policies for customer
     * @param applicationName for application name
     * @param companyName for company name
     * @param companyAddress for company address
     * @param email for email address
     * @param country for country
     */
    private void requestGeneratePolicies(String applicationName, String companyName, String companyAddress, String email, String country) {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_SETUP_POLICIES,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("policies");
                        JSONObject privacy = root.getJSONObject("privacy");
                        JSONObject terms = root.getJSONObject("terms");
                        JSONObject responseCode = root.getJSONObject("response");

                        switch (responseCode.getInt("code")) {
                            case 200:
                                showProgress(privacy.getString("rendered"), terms.getString("rendered"));
                                break;
                            case 403:
                                Toasto.show_toast(this, getString(R.string.in_trouble_exception), 1, 1);
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
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                params.put("application_name", applicationName);
                params.put("company_name", companyName);
                params.put("company_address", companyAddress);
                params.put("email", email);
                params.put("country", country);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request show learn more dialog
     * @param title for title
     * @param description for description
     * @param url for url
     */
    public void showLearnMore(String title, String description, String url) {
        Bundle learnmore = new Bundle();
        learnmore.putString("title", title);
        learnmore.putString("description", description);
        learnmore.putString("url", url);
        LearnMoreBottomSheetModal learnMoreBottomSheetModal = new LearnMoreBottomSheetModal();
        learnMoreBottomSheetModal.setArguments(learnmore);
        learnMoreBottomSheetModal.show(getSupportFragmentManager(), "TAG");
    }

    /**
     * request show progress dialog
     * for setting up policies
     * @param privacy for privacy policy
     * @param terms for terms of use
     */
    private void showProgress(String privacy, String terms) {
        Dialog designsDialog = new Dialog(this);

        designsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        designsDialog.setContentView(R.layout.dialog_loading_bar);

        // set cancelable
        designsDialog.setCancelable(false);
        designsDialog.setCanceledOnTouchOutside(false);

        TextView loadingTitle = designsDialog.findViewById(R.id.loading_title);
        loadingTitle.setText(getString(R.string.generating_your_policies));

        TextView loadingDescription = designsDialog.findViewById(R.id.loading_description);
        loadingDescription.setText(getString(R.string.applying_your_privacy_policy_terms_of_use));

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SetupPoliciesActivity.this, FinishPoliciesSetupActivity.class);
            intent.putExtra("privacy", privacy);
            intent.putExtra("terms", terms);
            startActivity(intent);
            finish();
        }, 4000);

        if (designsDialog.getWindow() != null) {
            designsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            designsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        designsDialog.show();
    }
}