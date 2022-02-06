package com.picassos.mint.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.adapter.PreviewWalkthroughAdapter;
import com.picassos.mint.console.android.models.Guide;
import com.picassos.mint.console.android.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PreviewWalkthroughActivity extends AppCompatActivity {

    private ViewPager screenPager;
    List<Guide> walkthroughList = new ArrayList<>();
    PreviewWalkthroughAdapter previewWalkthroughAdapter;
    TabLayout tabIndicator;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        setContentView(R.layout.activity_preview_walkthrough);

        // close preview
        findViewById(R.id.close_preview).setOnClickListener(v -> finish());

        // next page
        findViewById(R.id.next).setOnClickListener(v -> {
            position = screenPager.getCurrentItem();
            if (position < walkthroughList.size()) {
                position++;
                screenPager.setCurrentItem(position);
            }
        });

        // tab indicator
        tabIndicator = findViewById(R.id.tab_indicator);
        tabIndicator.setupWithViewPager(screenPager);

        // screen pager, adapter
        screenPager = findViewById(R.id.guide_viewpager);
        previewWalkthroughAdapter = new PreviewWalkthroughAdapter(this, walkthroughList);
        screenPager.setAdapter(previewWalkthroughAdapter);

        // set preview data
        setPreview(getIntent().getStringExtra("data"));
    }

    /**
     * request set preview data
     * @param response for volley response
     */
    private void setPreview(String response) {
        try {
            JSONObject obj = new JSONObject(response);

            JSONObject root = obj.getJSONObject("walkthrough");
            JSONArray array = root.getJSONArray("rendered");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                Guide guide = new Guide(object.getString("title"), object.getString("description"), 0);
                walkthroughList.add(guide);
                previewWalkthroughAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}