package com.picassos.mint.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.adapter.PreviewWizardsAdapter;
import com.picassos.mint.console.android.models.Guide;
import com.picassos.mint.console.android.utils.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PreviewWizardsActivity extends AppCompatActivity {

    private ViewPager screenPager;
    List<Guide> wizardList = new ArrayList<>();
    PreviewWizardsAdapter previewWizardsAdapter;
    TabLayout tabIndicator;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        setContentView(R.layout.activity_preview_wizards);

        // close preview
        findViewById(R.id.close_preview).setOnClickListener(v -> finish());

        // next page
        findViewById(R.id.next).setOnClickListener(v -> {
            position = screenPager.getCurrentItem();
            if (position < wizardList.size()) {
                position++;
                screenPager.setCurrentItem(position);
            }
        });

        // tab indicator
        tabIndicator = findViewById(R.id.tab_indicator);
        tabIndicator.setupWithViewPager(screenPager);

        // screen pager, adapter
        screenPager = findViewById(R.id.guide_viewpager);
        previewWizardsAdapter = new PreviewWizardsAdapter(this, wizardList);
        screenPager.setAdapter(previewWizardsAdapter);

        // set preview data
        setPreview(getIntent().getStringExtra("data"));
    }

    /**
     * request set preview data
     * @param response for response
     */
    private void setPreview(String response) {
        try {
            JSONObject obj = new JSONObject(response);

            JSONArray array = obj.getJSONArray("wizards");

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                Guide guide = new Guide(object.getString("title"), object.getString("description"), 0);
                wizardList.add(guide);
                previewWizardsAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}