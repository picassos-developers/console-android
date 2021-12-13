package com.picassos.mint.console.android.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class AddProjectActivity extends AppCompatActivity {

    // console preferences
    ConsolePreferences consolePreferences;

    // Request Dialog
    private RequestDialog requestDialog;

    private EditText projectName;
    private EditText projectPurchaseCode;
    private EditText projectPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_add_project);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // initialize fields
        projectName = findViewById(R.id.project_name);
        projectPurchaseCode = findViewById(R.id.project_purchasecode);
        projectPackageName = findViewById(R.id.project_packagename);

        // add project
        findViewById(R.id.add_project).setOnClickListener(v -> {
            if (!TextUtils.isEmpty(projectName.getText().toString())
                    && !TextUtils.isEmpty(projectPurchaseCode.getText().toString())
                    && !TextUtils.isEmpty(projectPackageName.getText().toString())) {
                requestAddProject();
            } else {
                Toasto.show_toast(this, getString(R.string.all_fields_are_required), 1, 2);
            }
        });

        // visit product
        TextView visitProduct = findViewById(R.id.visit_product);
        visitProduct.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * request add project
     */
    private void requestAddProject() {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_ADD_PROJECT,
                response -> {
                    switch (response) {
                        case "exception:configuration?success":
                            showProgress();
                            break;
                        case "exception:error?already_exists":
                            Toasto.show_toast(this, getString(R.string.project_exists), 1, 2);
                            break;
                        case "exception:error?purchasecode_not_valid":
                            Toasto.show_toast(this, getString(R.string.purchase_code_not_valid), 1, 1);
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
                params.put("name", projectName.getText().toString().trim());
                params.put("purchasecode", projectPurchaseCode.getText().toString().trim());
                params.put("packagename", projectPackageName.getText().toString().trim());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request show progress dialog
     * for creating project.
     */
    private void showProgress() {
        Dialog designsDialog = new Dialog(this);

        designsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        designsDialog.setContentView(R.layout.dialog_loading_bar);

        // set cancelable
        designsDialog.setCancelable(false);
        designsDialog.setCanceledOnTouchOutside(false);

        TextView loadingTitle = designsDialog.findViewById(R.id.loading_title);
        loadingTitle.setText(getString(R.string.creating_your_project));

        TextView loadingDescription = designsDialog.findViewById(R.id.loading_description);
        loadingDescription.setText(getString(R.string.we_are_setting_up_your_project));

        new Handler().postDelayed(() -> {
            designsDialog.dismiss();
            Intent intent = new Intent();
            intent.putExtra("request", "add");
            setResult(Activity.RESULT_OK);
            finish();
        }, 4000);

        if (designsDialog.getWindow() != null) {
            designsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            designsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        designsDialog.show();
    }

}