package app.mynta.console.android.activities.about;

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

import app.mynta.console.android.R;
import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.utils.Helper;

public class LegalActivity extends AppCompatActivity {

    // context
    Context context;

    // console preferences
    private ConsolePreferences consolePreferences;

    // webview used for details
    public RelativeLayout webviewContainer;
    private WebView webView;

    // request
    private String request;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        try {
            webView = new WebView(context);
        }
        catch (Resources.NotFoundException e) {
            webView = new WebView(context);
        }
        super.onCreate(savedInstanceState);

        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_legal);

        // request
        request = getIntent().getStringExtra("request");

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // toolbar
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        if (request.equals("privacy")) {
            toolbarTitle.setText(getString(R.string.privacy_policy));
        } else {
            toolbarTitle.setText(getString(R.string.terms_of_use));
        }

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

        requestInitWebView();

        /*---------------- Web View & Built In Browser (End) ---------------*/

        // Refresh Layout
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestInitWebView();
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void requestInitWebView() {
        String content;
        if (request.equals("privacy")) {
            content = "<p>Privacy Policy</p>";
        } else {
            content = "<p>Terms of Use</p>";
        }

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