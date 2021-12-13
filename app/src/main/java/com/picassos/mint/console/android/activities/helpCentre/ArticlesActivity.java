package com.picassos.mint.console.android.activities.helpCentre;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.adapter.ArticlesAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.models.Articles;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticlesActivity extends AppCompatActivity {

    // Articles
    private final List<Articles> articlesList = new ArrayList<>();
    // request dialog
    RequestDialog requestDialog;
    private ArticlesAdapter articlesAdapter;

    private int sectionId;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        setContentView(R.layout.activity_articles);

        // request data
        Intent data = getIntent();
        sectionId = data.getIntExtra("section_id", 0);
        String sectionTitle = data.getStringExtra("section_title");

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // set section title
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(sectionTitle);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // Initialize articles recyclerview
        RecyclerView articlesRecyclerview = findViewById(R.id.recycler_articles);

        // articles adapter
        articlesAdapter = new ArticlesAdapter(articlesList, article -> {
            Intent intent = new Intent(ArticlesActivity.this, ViewArticleActivity.class);
            intent.putExtra("article_id", article.getArticle_id());
            startActivity(intent);
        });

        articlesRecyclerview.setAdapter(articlesAdapter);
        articlesRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        // request articles
        requestArticles(sectionId);

        // Refresh Layout
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            articlesList.clear();
            articlesAdapter.notifyDataSetChanged();
            requestArticles(sectionId);
        });
    }

    /**
     * request articles
     */
    private void requestArticles(int id) {
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_ARTICLES,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        JSONArray array = obj.getJSONArray("articles");

                        // Check if data are empty
                        if (array.length() == 0) {
                            findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.no_items).setVisibility(View.GONE);
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Articles articles = new Articles(object.getInt("article_id"), object.getString("title"));
                            articlesList.add(articles);
                            articlesAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestArticles(sectionId));
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("section", String.valueOf(id));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}