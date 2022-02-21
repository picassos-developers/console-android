package app.mynta.console.android.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
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
import app.mynta.console.android.activities.addProject.fragments.NameAppFragment;
import app.mynta.console.android.activities.store.powerups.policies.FinishPoliciesSetupActivity;
import app.mynta.console.android.activities.store.powerups.policies.SetupPoliciesActivity;
import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.utils.Helper;
import com.shuhart.stepview.StepView;

import java.util.Objects;

public class AddProjectActivity extends AppCompatActivity {
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
    final Fragment fragment4 = new AddFirstContentProviderFragment();

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
                .stepsNumber(4)
                .commit();

        stepText = findViewById(R.id.current_step);
        stepText.setText(getString(R.string.step) + " 1 " + getString(R.string.of_four));

        fragmentManager = getSupportFragmentManager();

        // add fragments
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
        if (currentStep == 3) {
            findViewById(R.id.skip).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.skip).setVisibility(View.INVISIBLE);
        }

        stepText.setText(getString(R.string.step) + " " + (step) + " " + getString(R.string.of_four));
    }

    @SuppressLint("SetTextI18n")
    private void goBack() {
        if (currentStep > 0) {
            currentStep--;
            step--;
        } else {
            finish();
        }
        if (currentStep == 3) {
            findViewById(R.id.skip).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.skip).setVisibility(View.INVISIBLE);
        }
        stepView.done(false);
        stepView.go(currentStep, true);

        Fragment activeFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(stepView.getCurrentStep()));
        Fragment nextFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(currentStep));
        fragmentManager.beginTransaction().hide(Objects.requireNonNull(activeFragment)).show(Objects.requireNonNull(nextFragment)).commit();

        stepText.setText(getString(R.string.step) + " " + (step) + " " + getString(R.string.of_four));
    }

    /**
     * request show progress dialog
     */
    private void showProgress() {
        Dialog designsDialog = new Dialog(this);

        designsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        designsDialog.setContentView(R.layout.dialog_loading_bar);

        // set cancelable
        designsDialog.setCancelable(false);
        designsDialog.setCanceledOnTouchOutside(false);

        TextView loadingTitle = designsDialog.findViewById(R.id.loading_title);
        loadingTitle.setText("Creating" + " " + applicationName);

        TextView loadingDescription = designsDialog.findViewById(R.id.loading_description);
        Handler handler = new Handler();
        for (int i = 100; i <= 3500; i=i+100) {
            handler.postDelayed(() -> {
                if(i%300 == 0){
                    loadingDescription.setText("Loading.");
                }else if(i%200 == 0){
                    loadingDescription.setText("Loading..");
                }else if(i%100 == 0){
                    loadingDescription.setText("Loading...");
                }
            }, i);
        }

        new Handler().postDelayed(() -> {
            // TODO: Add here please
        }, 4000);

        if (designsDialog.getWindow() != null) {
            designsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            designsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        designsDialog.show();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}