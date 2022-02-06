package com.picassos.mint.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.adapter.MembersAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.constants.RequestCodes;
import com.picassos.mint.console.android.models.Members;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.MemberDetailsBottomSheetModal;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MembersActivity extends AppCompatActivity implements MemberDetailsBottomSheetModal.OnDeleteListener {

    Bundle bundle;

    // request dialog
    RequestDialog requestDialog;

    private ConsolePreferences consolePreferences;

    // members
    private final List<Members> membersList = new ArrayList<>();
    private MembersAdapter membersAdapter;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_members);

        // initialize bundle
        bundle = new Bundle();

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // manage login providers
        findViewById(R.id.manage_login_providers).setOnClickListener(v -> startActivity(new Intent(this, LoginProvidersActivity.class)));

        // Initialize members recyclerview
        RecyclerView membersRecyclerview = findViewById(R.id.recycler_members);

        // members adapter
        membersAdapter = new MembersAdapter(membersList, members -> {
            bundle.putInt("identifier", members.getUser_id());
            MemberDetailsBottomSheetModal memberDetailsBottomSheetModal = new MemberDetailsBottomSheetModal();
            memberDetailsBottomSheetModal.setArguments(bundle);
            memberDetailsBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        }, false);

        membersRecyclerview.setAdapter(membersAdapter);
        membersRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        // request members
        requestMembers();

        // Refresh Layout
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            membersList.clear();
            membersAdapter.notifyDataSetChanged();
            requestMembers();
        });
    }

    /**
     * request members
     */
    @SuppressLint("NotifyDataSetChanged")
    private void requestMembers() {
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_MEMBERS,
                response -> {

                    try {
                        JSONObject obj = new JSONObject(response);

                        JSONArray array = obj.getJSONArray("members");

                        // check if members are empty
                        if (array.length() == 0) {
                           findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                        } else {
                           findViewById(R.id.no_items).setVisibility(View.GONE);
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Members members = new Members(object.getInt("user_id"), object.getString("username"), object.getString("email"), object.getString("verified"));
                            membersList.add(members);
                            membersAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestMembers());
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
    @Override
    public void onDeleteListener(int requestCode) {
        if (requestCode == RequestCodes.REQUEST_REMOVE_MEMBER_CODE) {
            membersList.clear();
            membersAdapter.notifyDataSetChanged();
            requestMembers();
        }
    }
}