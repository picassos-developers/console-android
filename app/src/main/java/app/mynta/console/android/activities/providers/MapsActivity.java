package app.mynta.console.android.activities.providers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import app.mynta.console.android.adapter.MapStylesAdapter;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.models.Maps;
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

public class MapsActivity extends AppCompatActivity implements NavigationOptionsBottomSheetModal.OnRemoveListener {

    private Bundle bundle;
    private Intent intent;
    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    private String request;
    private String mapValue;

    private EditText label, icon;
    private TextView mapStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_maps);

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

        // map style chooser
        CardView mapStyleChooser = findViewById(R.id.map_style_chooser);
        mapStyleChooser.setOnClickListener(v -> styleChooserDialog());

        // maps data
        label = findViewById(R.id.provider_label);
        icon = findViewById(R.id.provider_icon);
        mapStyle = findViewById(R.id.map_style);

        // request data
        if (request.equals("update")) {
            requestData();
        }

        // save data
        Button save = findViewById(R.id.update_maps);
        save.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(mapStyle.getText().toString())
                && !mapStyle.getText().toString().equals(getString(R.string.map_style))) {
                switch (request) {
                    case "add":
                        requestAddMaps();
                        break;
                    case "update":
                        requestUpdateMaps();
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
     * request maps data
     */
    @SuppressLint("SetTextI18n")
    private void requestData() {
        findViewById(R.id.maps_container).setVisibility(View.VISIBLE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_MAPS_DETAILS,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("maps");
                        JSONObject general = root.getJSONObject("general");

                        // label, icon
                        label.setText(general.getString("label"));
                        icon.setText(general.getString("icon"));

                        // maps data
                        switch (root.getString("style")) {
                            case "map_aubergine":
                                mapStyle.setText("Aubergine");
                                break;
                            case "map_black_map":
                                mapStyle.setText("Black Map");
                                break;
                            case "map_dark":
                                mapStyle.setText("Dark");
                                break;
                            case "map_garden_city":
                                mapStyle.setText("Garden City");
                                break;
                            case "map_gulaya":
                                mapStyle.setText("Gulaya");
                                break;
                            case "map_night":
                                mapStyle.setText("Night");
                                break;
                            case "map_pinky":
                                mapStyle.setText("Pinky");
                                break;
                            case "map_retro":
                                mapStyle.setText("Retro");
                                break;
                            case "map_sangaspano":
                                mapStyle.setText("Sangaspano");
                                break;
                            case "map_silver":
                                mapStyle.setText("Silver");
                                break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.maps_container).setVisibility(View.GONE);
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
     * request add maps
     */
    private void requestAddMaps() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1,0 );
        } else {
            requestDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_ADD_MAPS_PROVIDER,
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
                    params.put("style", mapValue);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }

    /**
     * request update maps
     */
    private void requestUpdateMaps() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1,0 );
        } else {
            requestDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_MAPS_PROVIDER,
                    response -> {
                        if (response.equals("200")) {
                            Toasto.show_toast(this, getString(R.string.maps_updated), 1, 0);
                            intent.putExtra("updated", true);
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
                    params.put("identifier", String.valueOf(getIntent().getIntExtra("identifier", 0)));
                    params.put("label", label.getText().toString());
                    params.put("icon", icon.getText().toString());
                    params.put("style", mapValue);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }

    /**
     * open map styles chooser dialog
     */
    @SuppressLint("NotifyDataSetChanged")
    private void styleChooserDialog() {
        Dialog stylesDialog = new Dialog(this);

        stylesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        stylesDialog.setContentView(R.layout.dialog_map_styles);

        // enable dialog cancel
        stylesDialog.setCancelable(true);
        stylesDialog.setOnCancelListener(dialog -> stylesDialog.dismiss());

        // close dialog
        ImageView dialogClose = stylesDialog.findViewById(R.id.dialog_close);
        dialogClose.setOnClickListener(v -> stylesDialog.dismiss());

        List<Maps> mapsList = new ArrayList<>();
        RecyclerView stylesRecyclerview = stylesDialog.findViewById(R.id.recycler_styles);

        MapStylesAdapter mapStylesAdapter = new MapStylesAdapter(this, mapsList, click -> {
            mapStyle.setText(click.getTitle());
            mapValue = click.getStyle();
            stylesDialog.dismiss();
        });

        stylesRecyclerview.setAdapter(mapStylesAdapter);
        stylesRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        mapsList.add(new Maps("Aubergine", "map_aubergine", "map_aubergine"));
        mapsList.add(new Maps("Black Map", "map_black_map", "map_black"));
        mapsList.add(new Maps("Dark", "map_dark", "map_dark"));
        mapsList.add(new Maps("Garden City", "map_garden_city", "map_garden_city"));
        mapsList.add(new Maps("Gulaya", "map_gulaya", "map_gulaya"));
        mapsList.add(new Maps("Night", "map_night", "map_night"));
        mapsList.add(new Maps("Pinky", "map_pinky", "map_pinky"));
        mapsList.add(new Maps("Retro", "map_retro", "map_retro"));
        mapsList.add(new Maps("Sangaspano", "map_sangaspano", "map_sangaspano"));
        mapsList.add(new Maps("Silver", "map_silver", "map_silver"));
        mapStylesAdapter.notifyDataSetChanged();

        if (stylesDialog.getWindow() != null) {
            stylesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            stylesDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        stylesDialog.show();
    }

    @Override
    public void onRemoveListener(int requestCode) {
        intent.putExtra("remove", true);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}