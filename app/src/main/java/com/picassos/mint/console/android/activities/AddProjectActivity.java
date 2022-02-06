package com.picassos.mint.console.android.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.addProject.fragments.ChooseAppCategoryFragment;
import com.picassos.mint.console.android.activities.addProject.fragments.NameAppFragment;
import com.picassos.mint.console.android.activities.addProject.fragments.AddFirstContentProviderFragment;
import com.picassos.mint.console.android.activities.addProject.fragments.Fragment4;
import com.picassos.mint.console.android.activities.addProject.fragments.Fragment5;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.Helper;
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
    final Fragment fragment3 = new AddFirstContentProviderFragment();
    final Fragment fragment4 = new Fragment4();
    final Fragment fragment5 = new Fragment5();

    TextView steptext;

    // project details
    public String appCategory;
    public String applicationName;
    public String packageName;

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

        steptext = findViewById(R.id.current_step);
        steptext.setText(getString(R.string.step) + " 1 " + getString(R.string.of_five));

        fragmentManager = getSupportFragmentManager();

        // add fragments
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment5, "4").hide(fragment5).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment4, "3").hide(fragment4).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment3, "2").hide(fragment3).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment2, "1").hide(fragment2).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment1, "0").commit();

        findViewById(R.id.go_back).setOnClickListener(v -> goBack());
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

        steptext.setText(getString(R.string.step) + " " + (step) + " " + getString(R.string.of_five));
    }

    @SuppressLint("SetTextI18n")
    private void goBack() {
        if (currentStep > 0) {
            currentStep--;
            step--;
        } else {
            finish();
        }
        stepView.done(false);
        stepView.go(currentStep, true);

        Fragment activeFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(stepView.getCurrentStep()));
        Fragment nextFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(currentStep));
        fragmentManager.beginTransaction().hide(Objects.requireNonNull(activeFragment)).show(Objects.requireNonNull(nextFragment)).commit();

        steptext.setText(getString(R.string.step) + " " + (step) + " " + getString(R.string.of_five));
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}