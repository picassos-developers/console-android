package com.picassos.mint.console.android.activities;

import androidx.annotation.Nullable;
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
import com.picassos.mint.console.android.adapter.ProjectsAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.models.Projects;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectsActivity extends AppCompatActivity {

    ConsolePreferences consolePreferences;

    // request dialog
    RequestDialog requestDialog;

    // Projects
    private final List<Projects> projectsList = new ArrayList<>();
    private ProjectsAdapter projectsAdapter;

    // REQUEST CODES
    public static final int REQUEST_ADD_PROJECT_CODE = 1;
    
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);
        
        consolePreferences = new ConsolePreferences(this);
        
        setContentView(R.layout.activity_projects);

        if (consolePreferences.loadToken().equals("exception:error?token")) {
            finish();
        }

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // add project
        findViewById(R.id.add_project).setOnClickListener(v -> startActivityForResult(new Intent(ProjectsActivity.this, AddProjectActivity.class), REQUEST_ADD_PROJECT_CODE));

        // demo project
        findViewById(R.id.demo_project).setOnClickListener(v -> {
            consolePreferences.setProjectName("Demo Project");
            consolePreferences.setSecretAPIKey("demo");
            consolePreferences.setPackageName("com.app.demo");
            consolePreferences.setFirebaseAccessToken("demo");
            startActivity(new Intent(ProjectsActivity.this, MainActivity.class));
            finishAffinity();
        });

        // Initialize projects recyclerview
        RecyclerView projectsRecyclerview = findViewById(R.id.recycler_projects);

        // projects adapter
        projectsAdapter = new ProjectsAdapter(projectsList, project -> {
            consolePreferences.setProjectName(project.getName());
            consolePreferences.setSecretAPIKey(project.getSak());
            consolePreferences.setPackageName(project.getPackagename());
            consolePreferences.setFirebaseAccessToken(project.getFirebase_access_key());
            startActivity(new Intent(ProjectsActivity.this, MainActivity.class));
            finishAffinity();
        });

        projectsRecyclerview.setAdapter(projectsAdapter);
        projectsRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        // request projects
        requestProjects();

        // Refresh Layout
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            projectsList.clear();
            projectsAdapter.notifyDataSetChanged();
            requestProjects();
        });
    }

    /**
     * request projects
     */
    @SuppressLint("NotifyDataSetChanged")
    private void requestProjects() {
        projectsList.clear();
        projectsAdapter.notifyDataSetChanged();
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_PROJECTS,
                response -> {

                    try {
                        JSONObject obj = new JSONObject(response);

                        JSONArray array = obj.getJSONArray("projects");

                        // Check if data are empty
                        if (array.length() == 0) {
                            findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.no_items).setVisibility(View.GONE);
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Projects projects = new Projects(object.getInt("id"), object.getString("sak"), object.getString("name"), object.getString("purchasecode"), object.getString("packagename"), object.getString("firebase_access_key"));
                            projectsList.add(projects);
                            projectsAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestProjects());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", consolePreferences.loadToken());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_PROJECT_CODE) {
            requestProjects();
        }
    }
}