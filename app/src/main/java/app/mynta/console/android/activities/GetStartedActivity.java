package app.mynta.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import app.mynta.console.android.R;
import app.mynta.console.android.adapter.GetStartedAdapter;
import app.mynta.console.android.models.Guide;
import app.mynta.console.android.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetStartedActivity extends AppCompatActivity {

    GetStartedAdapter getStartedAdapter;
    TabLayout tabIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // when this activity is about to be launch we need to check if its opened before or not

        if (restorePrefData()) {

            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
            finish();

        }

        setContentView(R.layout.activity_get_started);

        tabIndicator = findViewById(R.id.tab_indicator);
        // fill list screen

        final List<Guide> guide_list = new ArrayList<>();

        try {
            JSONObject obj = new JSONObject(Objects.requireNonNull(Helper.getJsonFromAssets(this, "adapter/wizards.json")));
            JSONArray wizards = obj.getJSONArray("wizards");

            for (int i = 0; i < wizards.length(); i++) {
                JSONObject wizardsObject = wizards.getJSONObject(i);
                String title = wizardsObject.getString("title");
                String description = wizardsObject.getString("description");
                String thumbnail = wizardsObject.getString("thumbnail");

                guide_list.add(new Guide(title, description, getResources().getIdentifier(thumbnail, "drawable", getPackageName())));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // setup viewpager
        ViewPager screenPager = findViewById(R.id.guide_viewpager);
        getStartedAdapter = new GetStartedAdapter(this, guide_list);
        screenPager.setAdapter(getStartedAdapter);

        // setup tab layout with viewpager

        tabIndicator.setupWithViewPager(screenPager);

        // tab layout add change listener

        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        findViewById(R.id.get_started).setOnClickListener(v -> startActivity(new Intent(GetStartedActivity.this, RegisterActivity.class)));
        findViewById(R.id.login).setOnClickListener(v -> startActivity(new Intent(GetStartedActivity.this, LoginActivity.class)));
    }

    private boolean restorePrefData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Shared Preferences",MODE_PRIVATE);
        return pref.getBoolean("isGuideOpened",false);

    }

    private void savePrefsData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Shared Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isGuideOpened", true);
        editor.apply();

    }
}