package app.mynta.console.android.activities.providers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.mynta.console.android.R;

import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.adapter.LoadingSpinnerAdapter;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.models.LoadingSpinners;
import app.mynta.console.android.sheets.NavigationOptionsBottomSheetModal;
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebviewActivity extends AppCompatActivity implements NavigationOptionsBottomSheetModal.OnRemoveListener {

    private Bundle bundle;
    private Intent intent;
    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    private String request;
    private String spinner;
    private int launch_browser, bookmarks, downloads, qrscanner;

    private TextView loadingSpinner;
    private EditText label, icon, baseUrl;
    private SwitchCompat launchBrowser, bookmarksPage, downloadsPage, qrCodeScanner;

    @SuppressLint({"WrongViewCast", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_webview);

        bundle = new Bundle();
        intent = new Intent();

        // get request type
        request = getIntent().getStringExtra("request");

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // more options
        findViewById(R.id.more_options).setOnClickListener(v -> {
            bundle.putInt("identifier", getIntent().getIntExtra("identifier", 0));
            bundle.putString("type", getIntent().getStringExtra("type"));
            NavigationOptionsBottomSheetModal navigationOptionsBottomSheetModal = new NavigationOptionsBottomSheetModal();
            navigationOptionsBottomSheetModal.setArguments(bundle);
            navigationOptionsBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });
        if (request.equals("add")) {
            findViewById(R.id.more_options).setVisibility(View.GONE);
        }

        // toolbar title
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        if (request.equals("update")) {
            toolbarTitle.setText(getIntent().getStringExtra("label"));
        }

        // general data
        label = findViewById(R.id.provider_label);
        icon = findViewById(R.id.provider_icon);

        // webview data
        baseUrl = findViewById(R.id.base_url);
        loadingSpinner = findViewById(R.id.loading_spinner);
        launchBrowser = findViewById(R.id.system_default_browser);
        bookmarksPage = findViewById(R.id.bookmarks_page);
        downloadsPage = findViewById(R.id.downloads_page);
        qrCodeScanner = findViewById(R.id.qr_code_scanner);

        // set progress loader
        findViewById(R.id.loading_spinner_container).setOnClickListener(v -> {
            Dialog spinnersDialog = new Dialog(this);

            spinnersDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            spinnersDialog.setContentView(R.layout.dialog_loading_spinners);

            // enable dialog cancel
            spinnersDialog.setCancelable(true);
            spinnersDialog.setOnCancelListener(dialog -> spinnersDialog.dismiss());

            // close dialog
            ImageView dialogClose = spinnersDialog.findViewById(R.id.dialog_close);
            dialogClose.setOnClickListener(v1 -> spinnersDialog.dismiss());

            List<LoadingSpinners> spinnersList = new ArrayList<>();
            RecyclerView stylesRecyclerview = spinnersDialog.findViewById(R.id.recycler_spinners);

            @SuppressLint("SetTextI18n") LoadingSpinnerAdapter spinnerAdapter = new LoadingSpinnerAdapter(spinnersList, click -> {
                spinner = click.getStyle();
                loadingSpinner.setText(getString(R.string.loading_spinner) + ": " + click.getStyle());
                spinnersDialog.dismiss();
            });

            stylesRecyclerview.setAdapter(spinnerAdapter);
            stylesRecyclerview.setLayoutManager(new LinearLayoutManager(this));

            spinnersList.add(new LoadingSpinners("Disabled", "Disabled"));
            spinnersList.add(new LoadingSpinners("Native", "Native"));
            spinnersList.add(new LoadingSpinners("Shimmer", "Shimmer"));
            spinnersList.add(new LoadingSpinners("ThreeBounce", "ThreeBounce"));
            spinnersList.add(new LoadingSpinners("RotatingPlane", "RotatingPlane"));
            spinnersList.add(new LoadingSpinners("DoubleBounce", "DoubleBounce"));
            spinnersList.add(new LoadingSpinners("Wave", "Wave"));
            spinnersList.add(new LoadingSpinners("WanderingCubes", "WanderingCubes"));
            spinnersList.add(new LoadingSpinners("ChasingDots", "ChasingDots"));
            spinnersList.add(new LoadingSpinners("Circle", "Circle"));
            spinnersList.add(new LoadingSpinners("CubeGrid", "CubeGrid"));
            spinnersList.add(new LoadingSpinners("FadingCircle", "FadingCircle"));
            spinnersList.add(new LoadingSpinners("FoldingCube", "FoldingCube"));
            spinnersList.add(new LoadingSpinners("RotatingCircle", "RotatingCircle"));
            spinnerAdapter.notifyDataSetChanged();

            if (spinnersDialog.getWindow() != null) {
                spinnersDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                spinnersDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            }

            spinnersDialog.show();
        });

        // request data
        if (request.equals("update")) {
            requestData();
        }

        // save data
        Button save = findViewById(R.id.update_webview);
        save.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(baseUrl.getText().toString()) && spinner != null) {
                if (launchBrowser.isChecked()) { launch_browser = 1; } else { launch_browser = 0; }
                if (bookmarksPage.isChecked()) { bookmarks = 1; } else { bookmarks = 0; }
                if (downloadsPage.isChecked()) { downloads = 1; } else { downloads = 0; }
                if (qrCodeScanner.isChecked()) { qrscanner = 1; } else { qrscanner = 0; }

                switch (request) {
                    case "add":
                        requestAddWebView();
                        break;
                    case "update":
                        requestUpdateWebView();
                        break;
                }
            } else {
                Toasto.show_toast(this, getString(R.string.all_fields_are_required), 0, 2);
            }
        });

        // refresh
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestData();
        });
        if (request.equals("add")) {
            refresh.setEnabled(false);
        }
    }

    /**
     * request webview data
     */
    @SuppressLint("SetTextI18n")
    private void requestData() {
        findViewById(R.id.webview_container).setVisibility(View.VISIBLE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_WEBVIEW_DETAILS,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("webview");
                        JSONObject general = root.getJSONObject("general");

                        // label, icon
                        label.setText(general.getString("label"));
                        icon.setText(general.getString("icon"));

                        // base url
                        baseUrl.setText(root.getString("base_url"));

                        // default browser
                        switch (root.getInt("default_browser")) {
                            case 1:
                                launchBrowser.setChecked(true);
                                break;
                            case 0:
                                launchBrowser.setChecked(false);
                                break;
                        }

                        // bookmarks page
                        switch (root.getInt("bookmarks_page")) {
                            case 1:
                                bookmarksPage.setChecked(true);
                                break;
                            case 0:
                                bookmarksPage.setChecked(false);
                                break;
                        }
                        // downloads page
                        switch (root.getInt("downloads_page")) {
                            case 1:
                                downloadsPage.setChecked(true);
                                break;
                            case 0:
                                downloadsPage.setChecked(false);
                                break;
                        }
                        // qr code scanner
                        switch (root.getInt("qr_code_scanner")) {
                            case 1:
                                qrCodeScanner.setChecked(true);
                                break;
                            case 0:
                                qrCodeScanner.setChecked(false);
                                break;
                        }

                        spinner = root.getString("loading_spinner");
                        loadingSpinner.setText(getString(R.string.loading_spinner) + ": " + root.getString("loading_spinner"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.webview_container).setVisibility(View.GONE);
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestData());
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                params.put("identifier", String.valueOf(getIntent().getIntExtra("identifier", 0)));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request add webview provider
     */
    private void requestAddWebView() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_ADD_WEBVIEW_PROVIDER,
                    response -> {
                        if (response.equals("200")) {
                            intent.putExtra("added", true);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
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
                    params.put("label", label.getText().toString());
                    params.put("icon", icon.getText().toString());
                    params.put("base_url", baseUrl.getText().toString());
                    params.put("loading_spinner", spinner);
                    params.put("launch_browser", String.valueOf(launch_browser));
                    params.put("bookmarks_page", String.valueOf(bookmarks));
                    params.put("downloads_page", String.valueOf(downloads));
                    params.put("qr_code_scanner", String.valueOf(qrscanner));
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }

    /**
     * request update webview
     */
    private void requestUpdateWebView() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_WEBVIEW,
                    response -> {
                        if (response.equals("200")) {
                            Toasto.show_toast(this, getString(R.string.webview_updated), 0, 0);
                            intent.putExtra("updated", true);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(this, getString(R.string.unknown_issue), 0, 1);
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("identifier", String.valueOf(getIntent().getIntExtra("identifier", 0)));
                    params.put("label", label.getText().toString());
                    params.put("icon", icon.getText().toString());
                    params.put("base_url", baseUrl.getText().toString());
                    params.put("loading_spinner", spinner);
                    params.put("default_browser", String.valueOf(launch_browser));
                    params.put("bookmarks_page", String.valueOf(bookmarks));
                    params.put("downloads_page", String.valueOf(downloads));
                    params.put("qr_code_scanner", String.valueOf(qrscanner));
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }

    @Override
    public void onRemoveListener(int requestCode) {
        intent.putExtra("removed", true);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}