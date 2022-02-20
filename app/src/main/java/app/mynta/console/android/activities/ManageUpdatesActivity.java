package app.mynta.console.android.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.mynta.console.android.R;

import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.adapter.ForceUpdatesAdapter;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.models.ForceUpdates;
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageUpdatesActivity extends AppCompatActivity {

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;
    private int isForce;

    private EditText latestUpdate;
    private TextView forceUpdate;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_manage_updates);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // force update chooser
        CardView forceUpdateChooser = findViewById(R.id.force_update_chooser);
        forceUpdateChooser.setOnClickListener(v -> forceUpdateChooserDialog());

        // force update data
        forceUpdate = findViewById(R.id.force_update);

        // latest update
        latestUpdate = findViewById(R.id.latest_update);

        // request data
        requestData();

        // save data
        Button save = findViewById(R.id.update_configuration);
        save.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(latestUpdate.getText().toString())
                    && !TextUtils.isEmpty(forceUpdate.getText().toString())) {
                requestUpdateManageUpdates();
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
    }

    /**
     * request manage updates
     */
    @SuppressLint("SetTexti18n")
    private void requestData() {
        findViewById(R.id.force_update_container).setVisibility(View.VISIBLE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_MANAGE_UPDATES,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("manage_updates");
                        // manage updates data
                        latestUpdate.setText(String.valueOf(root.getInt("latest_update")));
                        isForce = root.getInt("force_update");
                        switch (root.getInt("force_update")) {
                            case 1:
                                forceUpdate.setText(getString(R.string.force_update));
                                break;
                            case 0:
                                forceUpdate.setText(getString(R.string.dont_force_update));
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.force_update_container).setVisibility(View.GONE);
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestData());
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request update youtube
     */
    private void requestUpdateManageUpdates() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_MANAGE_UPDATES,
                    response -> {
                        if (response.contains("exception:configuration?success")) {
                            Toasto.show_toast(this, getString(R.string.data_updated), 0, 0);
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
                    params.put("latest_update", latestUpdate.getText().toString());
                    params.put("force_update", String.valueOf(isForce));
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }

    /**
     * open category design chooser dialog
     */
    @SuppressLint("NotifyDataSetChanged")
    private void forceUpdateChooserDialog() {
        Dialog designsDialog = new Dialog(this);

        designsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        designsDialog.setContentView(R.layout.dialog_force_update);

        // enable dialog cancel
        designsDialog.setCancelable(true);
        designsDialog.setOnCancelListener(dialog -> designsDialog.dismiss());

        // close dialog
        ImageView dialogClose = designsDialog.findViewById(R.id.dialog_close);
        dialogClose.setOnClickListener(v -> designsDialog.dismiss());

        List<ForceUpdates> forceUpdates = new ArrayList<>();
        RecyclerView forceUpdatesRecyclerview = designsDialog.findViewById(R.id.recycler_force_update);

        ForceUpdatesAdapter forceUpdatesAdapter = new ForceUpdatesAdapter(forceUpdates, click -> {
            forceUpdate.setText(click.getTitle());
            isForce = click.getId();
            designsDialog.dismiss();
        });

        forceUpdatesRecyclerview.setAdapter(forceUpdatesAdapter);
        forceUpdatesRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        forceUpdates.add(new ForceUpdates(0, getString(R.string.dont_force_update)));
        forceUpdates.add(new ForceUpdates(1, getString(R.string.force_update)));
        forceUpdatesAdapter.notifyDataSetChanged();

        if (designsDialog.getWindow() != null) {
            designsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            designsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        designsDialog.show();

    }
}