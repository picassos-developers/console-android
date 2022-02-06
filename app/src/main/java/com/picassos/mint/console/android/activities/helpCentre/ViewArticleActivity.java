package com.picassos.mint.console.android.activities.helpCentre;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.ArticleOptionsBottomSheetModal;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewArticleActivity extends AppCompatActivity {

    // context
    Context context;

    // bundle
    Bundle bundle;

    // console preferences
    private ConsolePreferences consolePreferences;

    // request dialog
    private RequestDialog requestDialog;

    // details
    private TextView articleTitle;
    private TextView lastUpdate;
    private SimpleDraweeView authorImage;
    private TextView authorName;

    // webview used for details
    public RelativeLayout webviewContainer;
    private WebView webView;

    private int articleId;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // get context context
        context = getApplicationContext();

        // Check Connection
        // On Startup After
        // Webview Assigned
        try {
            webView = new WebView(context);
        }
        catch (Resources.NotFoundException e) {
            // Some older devices can crash when instantiating a WebView, due to a Resources$NotFoundException
            // Creating with the application Context fixes this, but is not generally recommended for view creation
            webView = new WebView(context);
        }

        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_view_article);

        // initialize bundle
        bundle = new Bundle();

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // data
        Intent data = getIntent();
        articleId = data.getIntExtra("article_id", 0);

        // article details
        articleTitle = findViewById(R.id.article_title);
        lastUpdate = findViewById(R.id.last_update);
        authorImage = findViewById(R.id.author_image);
        authorName = findViewById(R.id.author_name);

        /*---------------- Web View & Built In Browser (Start) ---------------*/
        webviewContainer = findViewById(R.id.webview_container);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        // Webview Settings
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        if (consolePreferences.loadDarkMode() == 2) {
            webView.setBackgroundColor(Color.parseColor("#121212"));
        } else {
            webView.setBackgroundColor(Color.parseColor("#F8F8F8"));
        }

        // Webview Client
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(request.getUrl().toString())));
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
        });

        webView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        webviewContainer.addView(webView);

        /*---------------- Web View & Built In Browser (End) ---------------*/

        // request article
        requestArticle(articleId);

        // Refresh Layout
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestArticle(articleId);
        });
    }

    /**
     * request article details
     * @param id for article id
     */
    private void requestArticle(int id) {
        findViewById(R.id.article_container).setVisibility(View.VISIBLE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_ARTICLE,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        JSONArray array = obj.getJSONArray("article");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            articleTitle.setText(object.getString("title"));
                            lastUpdate.setText(getString(R.string.last_update) + " " + object.getString("last_update"));
                            authorName.setText(object.getString("author_name"));
                            authorImage.setController(
                                    Fresco.newDraweeControllerBuilder()
                                            .setTapToRetryEnabled(true)
                                            .setUri(object.getString("author_image_url"))
                                            .build());
                            requestInitWebView(object.getString("description"));
                            // Article options
                            findViewById(R.id.article_options).setOnClickListener(v -> {
                                try {
                                    bundle.putInt("article_id", object.getInt("article_id"));
                                    bundle.putString("article_title", object.getString("title"));
                                    bundle.putString("article_author", object.getString("author_name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ArticleOptionsBottomSheetModal articleOptionsBottomSheetModal = new ArticleOptionsBottomSheetModal();
                                articleOptionsBottomSheetModal.setArguments(bundle);
                                articleOptionsBottomSheetModal.show(getSupportFragmentManager(), "TAG");
                            });
                            // Article comments
                            findViewById(R.id.article_comments).setOnClickListener(v -> {
                                try {
                                    Intent intent = new Intent(ViewArticleActivity.this, CommentsActivity.class);
                                    intent.putExtra("article_id", object.getInt("article_id"));
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.article_container).setVisibility(View.GONE);
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestArticle(articleId));
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("article_id", String.valueOf(id));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void requestInitWebView(String content) {
        String html_data =
                "<style>" +
                        "@font-face {font-family: 'Poppins'; src: url(\"file:///android_res/font/poppins_regular.ttf\");}" +
                        "@font-face {font-family: 'Poppins Bold'; src: url(\"file:///android_res/font/poppins_bold.ttf\");}" +
                        "h4 {font-size: 18px !important; font-family: 'Poppins Bold' !important; line-height: 1.3 !important}" +
                        "h5 {font-size: 16px !important; font-family: 'Poppins Bold' !important; line-height: 1.3 !important}" +
                        "h6 {font-size: 15px !important; font-family: 'Poppins Bold' !important; line-height: 1.3 !important}" +
                        "p {font-size: 14px !important; font-family: 'Poppins' !important; line-height: 1.5 !important;}" +
                        "ol li {padding: 0 0 0 1.5 em !important; margin: 0 0 1.35em !important}" +
                        "a {color: #007bff !important}" +
                        "</style> ";
        if (consolePreferences.loadDarkMode() == 2) {
            html_data += "<style>body{color: #f2f2f2;}</style> ";
        }

        html_data += content;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            webView.loadDataWithBaseURL(null, html_data, "text/html; charset=UTF-8", "utf-8", null);
        } else {
            webView.loadData(html_data, "text/html; charset=UTF-8", null);
        }
    }
}