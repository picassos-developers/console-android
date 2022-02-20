package app.mynta.console.android.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.mynta.console.android.R;

import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ShareContentActivity extends AppCompatActivity {
    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    private EditText shareContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_share_content);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // request share content
        requestShareContent();

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // share content
        shareContent = findViewById(R.id.share_content);

        // update share content
        findViewById(R.id.update_share_content).setOnClickListener(v -> requestUpdateShareContent(shareContent.getText().toString()));

        // refresh
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestShareContent();
        });
    }

    /**
     * request share content
     */
    private void requestShareContent() {
        findViewById(R.id.share_content_container).setVisibility(View.VISIBLE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_SHARE_CONTENT,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("share");
                        // set share content
                        shareContent.setText(root.getString("rendered"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.share_content_container).setVisibility(View.GONE);
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestShareContent());
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request update share content
     */
    private void requestUpdateShareContent(String content) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_SHARE_CONTENT,
                    response -> {
                        if (response.equals("200")) {
                            Toasto.show_toast(this, getString(R.string.share_content_updated), 0, 0);
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(this, getString(R.string.unknown_issue), 0, 0);
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("content", content);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }

}