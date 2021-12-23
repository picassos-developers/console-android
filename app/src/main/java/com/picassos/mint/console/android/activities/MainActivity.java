package com.picassos.mint.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.picassos.mint.console.android.App;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.manageAds.ManageAdsActivity;
import com.picassos.mint.console.android.bottomNavigation.BottomNavigationViewBehavior;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.fragments.HelpCentreFragment;
import com.picassos.mint.console.android.fragments.StoreFragment;
import com.picassos.mint.console.android.libraries.showcaseview.GuideView;
import com.picassos.mint.console.android.libraries.showcaseview.config.DismissType;
import com.picassos.mint.console.android.libraries.showcaseview.config.Gravity;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.fragments.HomeFragment;
import com.picassos.mint.console.android.fragments.NavigationFragment;
import com.picassos.mint.console.android.fragments.NotificationsFragment;
import com.picassos.mint.console.android.sheets.ProjectOptionsBottomSheetModal;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ProjectOptionsBottomSheetModal.OnEditListener {

    App app;
    String topic = "app";

    Bundle bundle;
    ConsolePreferences consolePreferences;

    // fragment manager
    FragmentManager fragmentManager;

    final Fragment homeFragment = new HomeFragment();
    final Fragment navigationsFragment = new NavigationFragment();
    final Fragment notificationsFragment = new NotificationsFragment();
    final Fragment powerStoreFragment = new StoreFragment();
    final Fragment helpCentreFragment = new HelpCentreFragment();

    // active fragment
    Fragment active = homeFragment;

    // navigation
    BottomNavigationView bottomNavigationView;
    public DrawerLayout drawer;
    public NavigationView navigationView;

    // fonts
    private Typeface title, content;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_main);

        // get instance
        app = App.getInstance();

        // request update visits
        requestUpdateVisits();

        // subscribe to topic
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    String msg = "Subscribed to " + topic.toUpperCase();
                    if (!task.isSuccessful()) {
                        msg = "Error while subscribing to " + topic.toUpperCase();
                    }
                    Log.d("TAG", msg);
                });

        // initialize bundle
        bundle = new Bundle();

        title = Typeface.createFromAsset(getAssets(), "fonts/poppins_bold.ttf");
        content = Typeface.createFromAsset(getAssets(), "fonts/poppins_regular.ttf");

        // initialize bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        fragmentManager = getSupportFragmentManager();

        // fragment manager
        fragmentManager.beginTransaction().add(R.id.fragment_container, helpCentreFragment, "5").hide(helpCentreFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, powerStoreFragment, "4").hide(powerStoreFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, notificationsFragment, "3").hide(notificationsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, navigationsFragment, "2").hide(navigationsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, homeFragment, "1").commit();

        // bottom navigation on item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    fragmentManager.beginTransaction().hide(active).show(homeFragment).commit();
                    active = homeFragment;
                    return true;
                case R.id.navigation:
                    fragmentManager.beginTransaction().hide(active).show(navigationsFragment).commit();
                    active = navigationsFragment;
                    return true;
                case R.id.notification:
                    fragmentManager.beginTransaction().hide(active).show(notificationsFragment).commit();
                    active = notificationsFragment;
                    return true;
                case R.id.power_store:
                    fragmentManager.beginTransaction().hide(active).show(powerStoreFragment).commit();
                    if (!consolePreferences.loadStoreGuide()) {
                        findViewById(R.id.skip_store_tour_container).setVisibility(View.VISIBLE);
                        findViewById(R.id.skip_store_tour_container).setOnClickListener(v -> Log.e("TAG", "Empty Click"));
                        findViewById(R.id.explore_store).setOnClickListener(v -> {
                            findViewById(R.id.skip_store_tour_container).setVisibility(View.GONE);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            StoreFragment storeFragment = (StoreFragment) fragmentManager.findFragmentByTag("4");
                            Objects.requireNonNull(storeFragment).requestStoreGuideOne();
                        });
                    }
                    active = powerStoreFragment;
                    return true;
                case R.id.help_centre:
                    fragmentManager.beginTransaction().hide(active).show(helpCentreFragment).commit();
                    active = helpCentreFragment;
                    return true;
            }
            return false;
        });

        // bottom navigation on item reselected
        bottomNavigationView.setOnNavigationItemReselectedListener(MenuItem::getItemId);

        // nested scrollview
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());

        if (!consolePreferences.loadGuide()) {
            new Handler().postDelayed(() -> {
                // show guide
                findViewById(R.id.skip_tour_container).setVisibility(View.VISIBLE);

                // tour function
                findViewById(R.id.skip_tour_container).setOnClickListener(v -> Log.e("TAG", "Empty Click"));
                findViewById(R.id.skip_tour).setOnClickListener(v -> {
                    findViewById(R.id.skip_tour_container).setVisibility(View.GONE);
                    consolePreferences.setGuide(true);
                });
                findViewById(R.id.btn_get_started).setOnClickListener(v -> {
                    findViewById(R.id.skip_tour_container).setVisibility(View.GONE);
                    requestGuide();
                });
            }, 3000);
        }

        // initialize drawer
        drawer = findViewById(R.id.drawer_layout);

        /* navigation drawer start **/
        navigationView = findViewById(R.id.navigation_drawer);
        // manage project
        TextView projectName = navigationView.findViewById(R.id.project_name);
        projectName.setText(consolePreferences.loadProjectName().substring(0, 1).toUpperCase());
        navigationView.findViewById(R.id.project_options).setOnClickListener(v -> {
            ProjectOptionsBottomSheetModal projectOptionsBottomSheetModal = new ProjectOptionsBottomSheetModal();
            projectOptionsBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });
        // notifications
        navigationView.findViewById(R.id.notifications).setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
        // download data
        navigationView.findViewById(R.id.downloads).setOnClickListener(v -> startActivity(new Intent(this, DownloadsActivity.class)));
        // check status
        navigationView.findViewById(R.id.check_status).setOnClickListener(v -> startActivity(new Intent(this, CheckStatusActivity.class)));
        // navigation tabs
        navigationView.findViewById(R.id.menu_manage_users).setOnClickListener(v -> {
            startActivity(new Intent(this, ManageUsersActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        });
        navigationView.findViewById(R.id.menu_manage_login_providers).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginProvidersActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        });
        navigationView.findViewById(R.id.menu_wizards).setOnClickListener(v -> {
            startActivity(new Intent(this, WizardsActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        });
        navigationView.findViewById(R.id.menu_manage_updates).setOnClickListener(v -> {
            startActivity(new Intent(this, ManageUpdatesActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        });
        navigationView.findViewById(R.id.menu_manage_ads).setOnClickListener(v -> {
            startActivity(new Intent(this, ManageAdsActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        });
        navigationView.findViewById(R.id.menu_custom_css).setOnClickListener(v -> {
            Intent customCSS = new Intent(this, ControllerActivity.class);
            customCSS.putExtra("request", "customcss");
            startActivity(customCSS);
            drawer.closeDrawer(GravityCompat.START);
        });
        navigationView.findViewById(R.id.menu_dark_mode).setOnClickListener(v -> {
            Intent darkMode = new Intent(this, ControllerActivity.class);
            darkMode.putExtra("request", "darkmode");
            startActivity(darkMode);
            drawer.closeDrawer(GravityCompat.START);
        });
        navigationView.findViewById(R.id.menu_privacy_policy).setOnClickListener(v -> {
            Intent privacyPolicy = new Intent(this, PoliciesActivity.class);
            privacyPolicy.putExtra("request", "privacy");
            startActivity(privacyPolicy);
            drawer.closeDrawer(GravityCompat.START);
        });
        navigationView.findViewById(R.id.menu_terms_of_use).setOnClickListener(v -> {
            Intent termsOfUse = new Intent(this, PoliciesActivity.class);
            termsOfUse.putExtra("request", "terms");
            startActivity(termsOfUse);
            drawer.closeDrawer(GravityCompat.START);
        });
        /* navigation drawer end **/
    }

    /**
     * request update project visits
     */
    private void requestUpdateVisits() {
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_PROJECT_VISITS,
                response -> Log.e("TAG", "Project Visits Response Code: " + response), error -> Log.e("TAG", "Couldn't update project visits")) {

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
    public void onBackPressed() {
        if (active != homeFragment) {
            fragmentManager.beginTransaction().hide(active).show(homeFragment).commit();
            active = homeFragment;
            bottomNavigationView.setSelectedItemId(R.id.home);
        } else {
            super.onBackPressed();
            this.finishAffinity();
            System.exit(0);
        }
    }

    /**
     * open navigation drawer
     */
    public void openNavigation() {
        drawer.openDrawer(GravityCompat.START);
    }

    /* guide start **/
    private void requestGuide() {
        new GuideView.Builder(this)
                .setTitle(getString(R.string.guide_one_title))
                .setContentText(getString(R.string.guide_one_description))
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(findViewById(R.id.home))
                .setTitleTypeFace(title)
                .setContentTypeFace(content)
                .setContentTextSize(12)
                .setTitleTextSize(13)
                .setGuideListener(view -> requestGuideTwo())
                .build()
                .show();
    }

    private void requestGuideTwo() {
        switchFragment(navigationsFragment);
        new GuideView.Builder(this)
                .setTitle(getString(R.string.guide_two_title))
                .setContentText(getString(R.string.guide_two_description))
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(findViewById(R.id.navigation))
                .setTitleTypeFace(title)
                .setContentTypeFace(content)
                .setContentTextSize(12)
                .setTitleTextSize(13)
                .setGuideListener(view -> requestGuideThree())
                .build()
                .show();
    }

    private void requestGuideThree() {
        switchFragment(notificationsFragment);
        new GuideView.Builder(this)
                .setTitle(getString(R.string.guide_three_title))
                .setContentText(getString(R.string.guide_three_description))
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(findViewById(R.id.notification))
                .setTitleTypeFace(title)
                .setContentTypeFace(content)
                .setContentTextSize(12)
                .setTitleTextSize(13)
                .setGuideListener(view -> requestGuideFive())
                .build()
                .show();
    }

    private void requestGuideFive() {
        switchFragment(helpCentreFragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentByTag("1");

        new GuideView.Builder(this)
                .setTitle(getString(R.string.guide_five_title))
                .setContentText(getString(R.string.guide_five_description))
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(findViewById(R.id.help_centre))
                .setTitleTypeFace(title)
                .setContentTypeFace(content)
                .setContentTextSize(12)
                .setTitleTextSize(13)
                .setGuideListener(view -> {
                    switchFragment(homeFragment);
                    Objects.requireNonNull(homeFragment).requestGuideSix();
                })
                .build()
                .show();
    }

    private void switchFragment(Fragment fragment) {
        fragmentManager.beginTransaction().hide(active).show(fragment).commit();
        active = fragment;
        if (homeFragment.equals(fragment)) {
            bottomNavigationView.setSelectedItemId(R.id.home);
        } else if (navigationsFragment.equals(fragment)) {
            bottomNavigationView.setSelectedItemId(R.id.navigation);
        } else if (notificationsFragment.equals(fragment)) {
            bottomNavigationView.setSelectedItemId(R.id.notification);
        } else if (helpCentreFragment.equals(fragment)) {
            bottomNavigationView.setSelectedItemId(R.id.help_centre);
        }
    }
    /* guide end **/

    @Override
    public void onEditListener(boolean edited) {
        Toasto.show_toast(this, getString(R.string.project_data_updated), 1, 0);
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        finishAffinity();
    }
}