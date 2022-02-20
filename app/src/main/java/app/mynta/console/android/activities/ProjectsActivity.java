package app.mynta.console.android.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import app.mynta.console.android.R;

import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.adapter.ProjectsAdapter;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.models.Projects;
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.RequestDialog;

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
        findViewById(R.id.add_project).setOnClickListener(v -> startActivityForResult.launch(new Intent(ProjectsActivity.this, AddProjectActivity.class)));

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

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result != null && result.getResultCode() == RESULT_OK) {
            requestProjects();
        }
    });
}