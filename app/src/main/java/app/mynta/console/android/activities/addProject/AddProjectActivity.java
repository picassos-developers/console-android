package app.mynta.console.android.activities.addProject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import app.mynta.console.android.R;

import app.mynta.console.android.activities.addProject.fragments.AddFirstContentProviderFragment;
import app.mynta.console.android.activities.addProject.fragments.ChooseAppCategoryFragment;
import app.mynta.console.android.activities.addProject.fragments.Fragment4;
import app.mynta.console.android.activities.addProject.fragments.Fragment5;
import app.mynta.console.android.activities.addProject.fragments.NameAppFragment;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.Toasto;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shuhart.stepview.StepView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddProjectActivity extends AppCompatActivity {
    Dialog createProjectDialog;
    StepView stepView;

    private int currentStep = 0;
    private int step = 1;

    // console preferences
    ConsolePreferences consolePreferences;

    // fragment manager
    FragmentManager fragmentManager;

    final Fragment fragment1 = new ChooseAppCategoryFragment();
    final Fragment fragment2 = new NameAppFragment();
    final Fragment fragment3 = new Fragment4();
    final Fragment fragment4 = new Fragment5();
    final Fragment fragment5 = new AddFirstContentProviderFragment();

    TextView stepText;

    // project details
    public String appCategory;
    public String applicationName;
    public String packageName;
    public String organization;
    public String countryCode;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_add_project);

        stepView = findViewById(R.id.step_view);
        stepView.getState()
                .animationType(StepView.ANIMATION_ALL)
                .stepsNumber(5)
                .commit();

        stepText = findViewById(R.id.current_step);
        stepText.setText(getString(R.string.step) + " 1 " + getString(R.string.of_five));

        fragmentManager = getSupportFragmentManager();

        // add fragments
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment5, "4").hide(fragment4).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment4, "3").hide(fragment4).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment3, "2").hide(fragment3).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment2, "1").hide(fragment2).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment1, "0").commit();

        findViewById(R.id.go_back).setOnClickListener(v -> goBack());

        findViewById(R.id.skip).setOnClickListener(v -> showProgress());
    }

    @SuppressLint("SetTextI18n")
    public void goForward() {
        if (currentStep < stepView.getStepCount() - 1) {
            currentStep++;
            step++;
            stepView.go(currentStep, true);

            Fragment activeFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(stepView.getCurrentStep()));
            Fragment nextFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(currentStep));
            fragmentManager.beginTransaction().hide(Objects.requireNonNull(activeFragment)).show(Objects.requireNonNull(nextFragment)).commit();
        } else {
            stepView.done(true);

            finish();
        }
        if (currentStep == 4) {
            findViewById(R.id.skip).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.skip).setVisibility(View.INVISIBLE);
        }

        stepText.setText(getString(R.string.step) + " " + (step) + " " + getString(R.string.of_five));
    }

    @SuppressLint("SetTextI18n")
    private void goBack() {
        if (currentStep > 0) {
            currentStep--;
            step--;
        } else {
            finish();
        }
        if (currentStep == 4) {
            findViewById(R.id.skip).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.skip).setVisibility(View.INVISIBLE);
        }
        stepView.done(false);
        stepView.go(currentStep, true);

        Fragment activeFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(stepView.getCurrentStep()));
        Fragment nextFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(currentStep));
        fragmentManager.beginTransaction().hide(Objects.requireNonNull(activeFragment)).show(Objects.requireNonNull(nextFragment)).commit();

        stepText.setText(getString(R.string.step) + " " + (step) + " " + getString(R.string.of_five));
    }

    /**
     * show progress
     */
    @SuppressLint("SetTextI18n")
    private void showProgress() {
        createProjectDialog = new Dialog(this);

        createProjectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        createProjectDialog.setContentView(R.layout.dialog_loading_bar);

        // set cancelable
        createProjectDialog.setCancelable(false);
        createProjectDialog.setCanceledOnTouchOutside(false);

        // set loading title
        TextView loadingTitle = createProjectDialog.findViewById(R.id.loading_title);
        loadingTitle.setText("Creating" + " " + applicationName);

        // set loading description
        TextView loadingDescription = createProjectDialog.findViewById(R.id.loading_description);
        loadingDescription.setText("Applying your design...");
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int count = 0;
            @Override
            public void run() {
                count++;
                if (count == 1) { loadingDescription.setText("Applying your design...");
                } else if (count == 2) { loadingDescription.setText("Playing a little with project...");
                } else if (count == 3) { loadingDescription.setText(getString(R.string.adding_final_touches)); }
                if (count == 3) {count = 0; }
                handler.postDelayed(this, 3500);
            }
        };
        handler.postDelayed(runnable, 1000);

        new Handler().postDelayed(this::requestCreateProject, 10500);

        if (createProjectDialog.getWindow() != null) {
            createProjectDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            createProjectDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
        createProjectDialog.show();
    }

    private void requestCreateProject() {
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_SERVER_STATUS,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("project");

                        JSONObject callback = root.getJSONObject("callback");
                        if (callback.getString("code").equals("200")) {
                            Toasto.show_toast(this, "done", 1, 2);
                            createProjectDialog.dismiss();
                        } else {
                            Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
                            createProjectDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
                        createProjectDialog.dismiss();
                    }
                }, error -> {
            Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
            createProjectDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", consolePreferences.loadToken());
                params.put("category", appCategory);
                params.put("name", applicationName);
                params.put("packagename", packageName);
                params.put("country", countryCode);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}