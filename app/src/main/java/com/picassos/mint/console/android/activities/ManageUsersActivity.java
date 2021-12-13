package com.picassos.mint.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.adapter.UsersAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.models.Users;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.UserDetailsBottomSheetModal;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageUsersActivity extends AppCompatActivity implements UserDetailsBottomSheetModal.OnDeleteListener {

    Bundle bundle;

    // request dialog
    RequestDialog requestDialog;

    private ConsolePreferences consolePreferences;

    // Manage Users
    private final List<Users> usersList = new ArrayList<>();
    private UsersAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_manage_users);

        // initialize bundle
        bundle = new Bundle();

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // Initialize users recyclerview
        RecyclerView usersRecyclerview = findViewById(R.id.recycler_users);

        // Search adapter
        usersAdapter = new UsersAdapter(usersList, users -> {
            bundle.putInt("identifier", users.getUser_id());
            UserDetailsBottomSheetModal userDetailsBottomSheetModal = new UserDetailsBottomSheetModal();
            userDetailsBottomSheetModal.setArguments(bundle);
            userDetailsBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });

        usersRecyclerview.setAdapter(usersAdapter);
        usersRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        // request users
        requestUsers();

        // Refresh Layout
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            usersList.clear();
            usersAdapter.notifyDataSetChanged();
            requestUsers();
        });
    }

    /**
     * request users
     */
    private void requestUsers() {
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_USERS,
                response -> {

                    try {
                        JSONObject obj = new JSONObject(response);

                        JSONArray array = obj.getJSONArray("users");

                        // Check if data are empty
                        if (array.length() == 0) {
                           findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                        } else {
                           findViewById(R.id.no_items).setVisibility(View.GONE);
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Users users = new Users(object.getInt("user_id"), object.getString("username"), object.getString("email"), object.getString("verified"));
                            usersList.add(users);
                            usersAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestUsers());
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


    @Override
    public void onDeleteListener(int requestCode) {
        if (requestCode == UserDetailsBottomSheetModal.REQUEST_DELETE_USER_CODE) {
            usersList.clear();
            usersAdapter.notifyDataSetChanged();
            requestUsers();
        }
    }
}