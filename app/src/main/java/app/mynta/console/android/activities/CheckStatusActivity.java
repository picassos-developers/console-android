package app.mynta.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.mynta.console.android.R;
import app.mynta.console.android.adapter.ServerStatusAdapter;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.models.ServerStatus;
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.RequestDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckStatusActivity extends AppCompatActivity {
    private RequestDialog requestDialog;

    // checkups
    private final List<ServerStatus> statusList = new ArrayList<>();
    private ServerStatusAdapter serverStatusAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        setContentView(R.layout.activity_check_status);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // Initialize checkups recyclerview
        RecyclerView checkupsRecyclerview = findViewById(R.id.checkup_list);

        serverStatusAdapter = new ServerStatusAdapter(this, statusList);

        checkupsRecyclerview.setAdapter(serverStatusAdapter);
        checkupsRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        // request checkups
        requestCheckups();

        // Refresh Layout
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestCheckups();
        });
    }

    /**
     * request checkups data
     */
    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void requestCheckups() {
        statusList.clear();
        serverStatusAdapter.notifyDataSetChanged();
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_SERVER_STATUS,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONObject root = obj.getJSONObject("server");
                        JSONArray status = root.getJSONArray("status");
                        // set overall status
                        TextView up = findViewById(R.id.overall_uptime);
                        up.setText(String.valueOf(root.getInt("up")));
                        TextView down = findViewById(R.id.overall_down);
                        down.setText(String.valueOf(root.getInt("down")));

                        findViewById(R.id.status_container).setVisibility(View.VISIBLE);
                        LottieAnimationView statusLottie = findViewById(R.id.animation_view);
                        TextView statusText = findViewById(R.id.status);
                        if (root.getString("now").equals("up")) {
                            statusLottie.setAnimation(R.raw.circle_pulse_success);
                            statusText.setText(getString(R.string.all_systems_operational));
                        } else if (root.getString("now").equals("down")) {
                            statusLottie.setAnimation(R.raw.circle_pulse_danger);
                            statusText.setText(getString(R.string.server_is_down));
                        }
                        statusLottie.playAnimation();

                        for (int i = 0; i < status.length(); i++) {
                            JSONObject object = status.getJSONObject(i);
                            ServerStatus serverStatus = new ServerStatus(object.getInt("id"), object.getString("status"), object.getString("date"));
                            statusList.add(serverStatus);
                            serverStatusAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.status_container).setVisibility(View.GONE);
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestCheckups());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("request", "server_status");
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}