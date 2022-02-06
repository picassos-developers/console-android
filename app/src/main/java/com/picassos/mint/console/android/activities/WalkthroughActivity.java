package com.picassos.mint.console.android.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.adapter.WalkthroughAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.constants.RequestCodes;
import com.picassos.mint.console.android.models.Walkthrough;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.AddWalkthroughBottomSheetModal;
import com.picassos.mint.console.android.sheets.WalkthroughOptionsBottomSheetModal;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalkthroughActivity extends AppCompatActivity implements AddWalkthroughBottomSheetModal.OnAddListener,
        WalkthroughOptionsBottomSheetModal.OnDeleteListener, WalkthroughOptionsBottomSheetModal.OnEditListener {

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;
    private Bundle bundle;

    // walkthrough
    private final List<Walkthrough> walkthroughList = new ArrayList<>();
    private WalkthroughAdapter walkthroughAdapter;

    // walkthrough option
    private SwitchCompat walkthroughOption;

    // data
    private String data = "";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_walkthrough);

        // initialize bundle
        bundle = new Bundle();

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // enable walkthrough option
        walkthroughOption = findViewById(R.id.walkthrough_option);
        walkthroughOption.setOnCheckedChangeListener((compoundButton, isChecked) -> requestUpdateWalkthroughOption(isChecked));

        // Initialize walkthrough recyclerview
        RecyclerView walkthroughRecyclerview = findViewById(R.id.recycler_walkthrough);

        walkthroughAdapter = new WalkthroughAdapter(walkthroughList, click -> Log.e("walkthrough", click.getTitle()), longClick -> {
            bundle.putInt("walkthrough_id", longClick.getId());
            bundle.putString("walkthrough_title", longClick.getTitle());
            bundle.putString("walkthrough_description", longClick.getDescription());
            bundle.putString("walkthrough_thumbnail", longClick.getThumbnail());
            WalkthroughOptionsBottomSheetModal walkthroughOptionsBottomSheetModal = new WalkthroughOptionsBottomSheetModal();
            walkthroughOptionsBottomSheetModal.setArguments(bundle);
            walkthroughOptionsBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });

        walkthroughRecyclerview.setAdapter(walkthroughAdapter);
        walkthroughRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        // request walkthrough
        requestData();

        // add walkthrough
        CardView addWalkthrough = findViewById(R.id.add_walkthrough);
        addWalkthrough.setOnClickListener(v -> {
            AddWalkthroughBottomSheetModal addWalkthroughBottomSheetModal = new AddWalkthroughBottomSheetModal();
            addWalkthroughBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });

        // preview walkthrough
        findViewById(R.id.preview_walkthrough).setOnClickListener(v -> {
            if (walkthroughAdapter.getItemCount() == 0) {
                Toasto.show_toast(this, getString(R.string.walkthrough_empty), 1, 2);
            } else {
                Intent intent = new Intent(WalkthroughActivity.this, PreviewWalkthroughActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
            }
        });

        // Refresh Layout
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            refresh();
        });
    }

    /**
     * request walkthrough data
     */
    @SuppressLint("NotifyDataSetChanged")
    private void requestData() {
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_WALKTHROUGH,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONObject root = obj.getJSONObject("walkthrough");
                        JSONArray array = root.getJSONArray("rendered");

                        switch (root.getString("option")) {
                            case "true":
                                walkthroughOption.setChecked(true);
                                break;
                            case "false":
                                walkthroughOption.setChecked(false);
                                break;
                        }

                        // Check if data are empty
                        if (array.length() == 0) {
                            findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                            findViewById(R.id.no_items).setOnClickListener(v -> {
                                AddWalkthroughBottomSheetModal addWalkthroughBottomSheetModal = new AddWalkthroughBottomSheetModal();
                                addWalkthroughBottomSheetModal.show(getSupportFragmentManager(), "TAG");
                            });
                        } else {
                            findViewById(R.id.no_items).setVisibility(View.GONE);
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Walkthrough walkthrough = new Walkthrough(object.getInt("id"), object.getString("title"), object.getString("description"), object.getString("thumbnail"), object.getString("date"));
                            walkthroughList.add(walkthrough);
                            walkthroughAdapter.notifyDataSetChanged();
                            data = response;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
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
     * request update walkthrough page option
     * @param isChecked to check whether page is enabled or not.
     */
    private void requestUpdateWalkthroughOption(boolean isChecked) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            String status;
            if (isChecked) {
                status = "true";
            } else {
                status = "false";
            }
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_WALKTHROUGH_OPTION,
                    response -> {
                        if (!response.equals("200")) {
                            Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
                        }
                    }, error -> Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1)){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("status", status);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refresh() {
        walkthroughList.clear();
        walkthroughAdapter.notifyDataSetChanged();
        requestData();
    }

    @Override
    public void onAddListener(int requestCode) {
        if (requestCode == RequestCodes.REQUEST_ADD_WALKTHROUGH_CODE) {
            refresh();
            Toasto.show_toast(this, getString(R.string.walkthrough_added), 0, 0);
        }
    }

    @Override
    public void onDeleteListener(boolean delete) {
        if (delete) {
            refresh();
            Toasto.show_toast(this, getString(R.string.walkthrough_deleted), 0, 1);
        }
    }

    @Override
    public void onEditListener(boolean edit) {
        if (edit) {
            refresh();
            Toasto.show_toast(this, getString(R.string.walkthrough_edited), 0, 0);
        }
    }
}