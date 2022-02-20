package app.mynta.console.android.activities.store.powerups.customdarkmode;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

import java.util.HashMap;
import java.util.Map;

public class SetupCustomDarkModeActivity extends AppCompatActivity {

    ConsolePreferences consolePreferences;
    RequestDialog requestDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_setup_custom_dark_mode);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // links
        EditText links = findViewById(R.id.links);

        // set up custom dark mode
        findViewById(R.id.setup_custom_darkmode).setOnClickListener(v -> {
            if (!TextUtils.isEmpty(links.getText().toString())) {
                requestSetCustomDarkmode(links.getText().toString());
            } else {
                Toasto.show_toast(this, getString(R.string.all_fields_are_required), 1, 2);
            }
        });
    }

    /**
     * request submit a ticket to set up custom dark mode for customer
     * @param links for the google drive link
     */
    @SuppressLint("SetTextI18n")
    private void requestSetCustomDarkmode(String links) {
        requestDialog.show();
        String format =
                        "Links:\n\n" + links + "\n\n" +
                        "SAK: " + consolePreferences.loadSecretAPIKey();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_SUBMIT_TICKET,
                response -> {
                    if (response.equals("200")) {
                        TextView responseMessage = findViewById(R.id.response);
                        responseMessage.setVisibility(View.VISIBLE);
                        responseMessage.setText(getString(R.string.custom_dark_mode_card_activated_successfully_expect_your_modified_version_of_the_code_to_be_ready_in_less_than_3_business_days_depending_on_the_number_of_pages_requested) + " " + consolePreferences.loadEmail());
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(this, getString(R.string.unknown_issue), 0, 1);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", consolePreferences.loadToken());
                params.put("username", consolePreferences.loadUsername());
                params.put("email", consolePreferences.loadEmail());
                params.put("request_subject", "Custom Dark Mode Request [Power-Up]");
                params.put("request_description", format);
                params.put("request_type", "customization");
                params.put("identifier", "powerup");
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}