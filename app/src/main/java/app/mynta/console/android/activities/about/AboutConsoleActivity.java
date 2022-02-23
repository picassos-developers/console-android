package app.mynta.console.android.activities.about;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import app.mynta.console.android.R;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AboutConsoleActivity extends AppCompatActivity {

    RequestDialog requestDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Helper.darkMode(this);

        setContentView(R.layout.activity_about_console);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // set application version
        TextView version = findViewById(R.id.version);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // privacy policy
        findViewById(R.id.privacy_policy).setOnClickListener(v -> {
            Intent intent = new Intent(this, LegalActivity.class);
            intent.putExtra("request", "privacy");
            startActivity(intent);
        });

        // terms of use
        findViewById(R.id.terms_of_use).setOnClickListener(v -> {
            Intent intent = new Intent(this, LegalActivity.class);
            intent.putExtra("request", "terms");
            startActivity(intent);
        });

        // check for updates
        findViewById(R.id.check_updates).setOnClickListener(v -> requestCheckUpdates());
    }

    /**
     * check for updates
     */
    private void requestCheckUpdates() {
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_VERSION,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("app");
                        JSONObject version = root.getJSONObject("version");

                        Helper.checkVersion(this, version.getInt("code"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("request", "version");
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}