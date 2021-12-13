package com.picassos.mint.console.android.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.adapter.WizardsAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.models.Wizard;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.AddWizardBottomSheetModal;
import com.picassos.mint.console.android.sheets.WizardOptionsBottomSheetModal;
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

public class WizardsActivity extends AppCompatActivity implements AddWizardBottomSheetModal.OnAddListener,
        WizardOptionsBottomSheetModal.OnDeleteListener, WizardOptionsBottomSheetModal.OnEditListener {

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;
    private Bundle bundle;

    // Wizard
    private final List<Wizard> wizardList = new ArrayList<>();
    private WizardsAdapter wizardsAdapter;

    // Data
    private String data = "";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_wizards);

        // initialize bundle
        bundle = new Bundle();

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // Initialize wizards recyclerview
        RecyclerView wizardsRecyclerview = findViewById(R.id.recycler_wizards);

        wizardsAdapter = new WizardsAdapter(wizardList, click -> Log.e("Wizard", click.getTitle()), longClick -> {
            bundle.putInt("wizard_id", longClick.getId());
            bundle.putString("wizard_title", longClick.getTitle());
            bundle.putString("wizard_description", longClick.getDescription());
            bundle.putString("wizard_thumbnail", longClick.getThumbnail());
            WizardOptionsBottomSheetModal wizardOptionsBottomSheetModal = new WizardOptionsBottomSheetModal();
            wizardOptionsBottomSheetModal.setArguments(bundle);
            wizardOptionsBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });

        wizardsRecyclerview.setAdapter(wizardsAdapter);
        wizardsRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        // request wizards
        requestData();

        // add wizard
        CardView addWizard = findViewById(R.id.add_wizard);
        addWizard.setOnClickListener(v -> {
            AddWizardBottomSheetModal addWizardBottomSheetModal = new AddWizardBottomSheetModal();
            addWizardBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });

        // preview wizards
        findViewById(R.id.preview_wizards).setOnClickListener(v -> {
            if (wizardsAdapter.getItemCount() == 0) {
                Toasto.show_toast(this, getString(R.string.wizards_empty), 1, 2);
            } else {
                Intent intent = new Intent(WizardsActivity.this, PreviewWizardsActivity.class);
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
     * request wizards data
     */
    @SuppressLint("NotifyDataSetChanged")
    private void requestData() {
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_WIZARDS,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        JSONArray array = obj.getJSONArray("wizards");

                        // Check if data are empty
                        if (array.length() == 0) {
                            findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                            findViewById(R.id.no_items).setOnClickListener(v -> {
                                AddWizardBottomSheetModal addWizardBottomSheetModal = new AddWizardBottomSheetModal();
                                addWizardBottomSheetModal.show(getSupportFragmentManager(), "TAG");
                            });
                        } else {
                            findViewById(R.id.no_items).setVisibility(View.GONE);
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Wizard wizard = new Wizard(object.getInt("id"), object.getString("title"), object.getString("description"), object.getString("thumbnail"), object.getString("date"));
                            wizardList.add(wizard);
                            wizardsAdapter.notifyDataSetChanged();
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

    @SuppressLint("NotifyDataSetChanged")
    public void refresh() {
        wizardList.clear();
        wizardsAdapter.notifyDataSetChanged();
        requestData();
    }

    @Override
    public void onAddListener(int requestCode) {
        if (requestCode == AddWizardBottomSheetModal.REQUEST_ADD_WIZARD_CODE) {
            refresh();
            Toasto.show_toast(this, getString(R.string.wizard_added), 0, 0);
        }
    }

    @Override
    public void onDeleteListener(boolean delete) {
        if (delete) {
            refresh();
            Toasto.show_toast(this, getString(R.string.wizard_deleted), 0, 1);
        }
    }

    @Override
    public void onEditListener(boolean edit) {
        if (edit) {
            refresh();
            Toasto.show_toast(this, getString(R.string.wizard_edited), 0, 0);
        }
    }
}