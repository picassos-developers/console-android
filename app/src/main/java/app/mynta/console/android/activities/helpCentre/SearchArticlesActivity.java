package app.mynta.console.android.activities.helpCentre;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.mynta.console.android.R;
import app.mynta.console.android.adapter.ArticlesAdapter;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.models.Articles;
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SearchArticlesActivity extends AppCompatActivity {

    // request dialog
    RequestDialog requestDialog;

    // articles
    private final List<Articles> articlesList = new ArrayList<>();
    private ArticlesAdapter articlesAdapter;

    private EditText searchBar;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        setContentView(R.layout.activity_search_articles);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // Initialize articles recyclerview
        RecyclerView articlesRecyclerview = findViewById(R.id.recycler_articles);

        // articles adapter
        articlesAdapter = new ArticlesAdapter(articlesList, article -> {
            Intent intent = new Intent(SearchArticlesActivity.this, ViewArticleActivity.class);
            intent.putExtra("article_id", article.getArticle_id());
            startActivity(intent);
        });

        articlesRecyclerview.setAdapter(articlesAdapter);
        articlesRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        // search bar
        searchBar = findViewById(R.id.search_bar);
        searchBar.setImeActionLabel(getString(R.string.search), KeyEvent.KEYCODE_ENTER);
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                if (!TextUtils.isEmpty(searchBar.getText().toString())) {
                    articlesList.clear();
                    articlesAdapter.notifyDataSetChanged();
                    requestArticles(searchBar.getText().toString());
                } else {
                    Toasto.show_toast(this, getString(R.string.search_empty), 0, 2);
                }
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            }
            return false;
        });

        // Voice Search
        ImageView searchMic = findViewById(R.id.voice_search);
        searchMic.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_articles));
            try {
                startActivityForResult.launch(intent);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * request search articles
     * @param query for term (query)
     */
    @SuppressLint("NotifyDataSetChanged")
    private void requestArticles(String query) {
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_SEARCH_ARTICLES,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        JSONArray array = obj.getJSONArray("articles");

                        // check if articles are empty
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
            findViewById(R.id.try_again).setOnClickListener(v -> requestArticles(searchBar.getText().toString()));
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("query", query);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @SuppressLint("NotifyDataSetChanged")
    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result != null && result.getResultCode() == RESULT_OK) {
            ArrayList<String> callback = Objects.requireNonNull(result.getData()).getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (callback != null) {
                searchBar.setText(callback.get(0));
                requestArticles(searchBar.getText().toString());
                articlesList.clear();
                articlesAdapter.notifyDataSetChanged();
            }
        }
    });

}