package com.picassos.mint.console.android.activities.helpCentre;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.adapter.TicketRespondsAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.models.TicketResponds;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewTicketActivity extends AppCompatActivity {

    private RequestDialog requestDialog;

    // ticket details
    private TextView ticketTitle;
    private TextView ticketDetails;
    private EditText ticketDescription;

    private int ticketId;

    // Wizard
    private final List<TicketResponds> respondsList = new ArrayList<>();
    private TicketRespondsAdapter ticketRespondsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        setContentView(R.layout.activity_view_ticket);

        // get data
        Intent data = getIntent();
        ticketId = data.getIntExtra("ticket_id", 0);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // initialize details
        ticketTitle = findViewById(R.id.ticket_title);
        ticketDetails = findViewById(R.id.ticket_details);
        ticketDescription = findViewById(R.id.ticket_description);

        // request ticket
        requestTicket(ticketId);

        // Initialize ticket responds recyclerview
        RecyclerView wizardsRecyclerview = findViewById(R.id.recycler_ticket_responds);

        // Refresh Layout
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestTicket(ticketId);
            requestResponds();
        });

        ticketRespondsAdapter = new TicketRespondsAdapter(respondsList);

        wizardsRecyclerview.setAdapter(ticketRespondsAdapter);
        wizardsRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        // request responds
        requestResponds();
    }

    /**
     * request ticket
     * @param id for ticket id
     */
    @SuppressLint("SetTexti18n")
    private void requestTicket(int id) {
        findViewById(R.id.ticket_container).setVisibility(View.VISIBLE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_TICKET,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        JSONArray array = obj.getJSONArray("ticket");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            ticketTitle.setText(object.getString("ticket_subject"));
                            ticketDetails.setText(getString(R.string.ticket_id) + " [" + object.getInt("ticket_id") + "] • " + object.getString("ticket_date"));
                            ticketDescription.setText(object.getString("ticket_message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.ticket_container).setVisibility(View.GONE);
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestTicket(ticketId));
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("ticket_id", String.valueOf(id));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request wizards data
     */
    @SuppressLint("NotifyDataSetChanged")
    private void requestResponds() {
        respondsList.clear();
        ticketRespondsAdapter.notifyDataSetChanged();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_TICKET_RESPONDS,
                response -> {

                    try {
                        JSONObject obj = new JSONObject(response);

                        JSONArray array = obj.getJSONArray("responds");

                        // Check if data are empty
                        if (array.length() == 0) {
                            findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.no_items).setVisibility(View.GONE);
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);

                            TicketResponds wizard = new TicketResponds(object.getInt("ticket_id"), object.getString("ticket_subject"), object.getString("ticket_message"), object.getString("ticket_owner_token"), object.getString("ticket_date"));
                            respondsList.add(wizard);
                            ticketRespondsAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.d("TAG", error.getMessage())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("ticket_id", String.valueOf(ticketId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}