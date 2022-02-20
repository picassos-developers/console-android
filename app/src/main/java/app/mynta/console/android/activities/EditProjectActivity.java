package app.mynta.console.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

public class EditProjectActivity extends AppCompatActivity {

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    // project details
    EditText projectName, projectPackagename, projectFirebaseAccessKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_edit_project);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // toolbar title
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(consolePreferences.loadProjectName());

        // project details
        projectName = findViewById(R.id.project_name);
        projectPackagename = findViewById(R.id.project_packagename);
        projectFirebaseAccessKey = findViewById(R.id.project_firebase_access_key);

        // set details
        projectName.setText(consolePreferences.loadProjectName());
        projectPackagename.setText(consolePreferences.loadPackageName());
        if (!consolePreferences.loadFirebaseAccessToken().equals("exception:error?fak")) {
            projectFirebaseAccessKey.setText(consolePreferences.loadFirebaseAccessToken());
        }

        // update project
        Button updateProject = findViewById(R.id.update_project);
        updateProject.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(projectName.getText().toString()) && !TextUtils.isEmpty(projectPackagename.getText().toString()) && !TextUtils.isEmpty(projectFirebaseAccessKey.getText().toString())) {
                requestUpdateProject();
            } else {
                Toasto.show_toast(this, getString(R.string.all_fields_are_required), 1, 2);
            }
        });
    }

    /**
     * request update project
     */
    private void requestUpdateProject() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_PROJECT,
                    response -> {
                        if (response.equals("200")) {
                            consolePreferences.setProjectName(projectName.getText().toString());
                            consolePreferences.setPackageName(projectPackagename.getText().toString());
                            consolePreferences.setFirebaseAccessToken(projectFirebaseAccessKey.getText().toString());
                            Intent intent = new Intent();
                            intent.putExtra("request", "update");
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                            Toasto.show_toast(this, getString(R.string.project_data_updated), 1, 0);
                        } else {
                            Toasto.show_toast(this, getString(R.string.unknown_issue), 0, 2);
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(this, getString(R.string.unknown_issue), 0, 2);
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("token", consolePreferences.loadToken());
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("project_name", projectName.getText().toString());
                    params.put("project_packagename", projectPackagename.getText().toString());
                    params.put("project_firebase_access_key", projectFirebaseAccessKey.getText().toString());
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }
}