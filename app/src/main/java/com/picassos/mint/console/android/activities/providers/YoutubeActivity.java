package com.picassos.mint.console.android.activities.providers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.adapter.VideoStylesAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.constants.AuthAPI;
import com.picassos.mint.console.android.models.VideoDesigns;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YoutubeActivity extends AppCompatActivity {

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    private EditText accessToken;
    private EditText channelId;
    private EditText perPage;
    private TextView design;
    private String auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_youtube);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // design chooser
        CardView designChooser = findViewById(R.id.video_design_chooser);
        designChooser.setOnClickListener(v -> designChooserDialog());

        // design data
        design = findViewById(R.id.video_design);

        // youtube data
        accessToken = findViewById(R.id.access_token);
        channelId = findViewById(R.id.channel_id);
        perPage = findViewById(R.id.per_page);

        // request data
        requestData();

        // save data
        Button save = findViewById(R.id.update_youtube);
        save.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(accessToken.getText().toString())
                    && !TextUtils.isEmpty(channelId.getText().toString())
                    && !TextUtils.isEmpty(perPage.getText().toString())
                    && !TextUtils.isEmpty(design.getText().toString())) {
                requestUpdateYoutube();
            } else {
                Toasto.show_toast(this, getString(R.string.all_fields_are_required), 0, 2);
            }
        });

        // refresh
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestData();
        });
    }

    /**
     * request youtube data
     */
    @SuppressLint("SetTexti18n")
    private void requestData() {
        findViewById(R.id.youtube_container).setVisibility(View.VISIBLE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_YOUTUBE_DETAILS,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("youtube");
                        // youtube data
                        if (root.getString("access_token").equals("exception:error?youtube_api_key")) {
                            accessToken.setText("");
                        } else {
                            accessToken.setText(root.getString("access_token"));
                        }
                        if (root.getString("channel_id").equals("exception:error?youtube_channel_id")) {
                            channelId.setText("");
                        } else {
                            channelId.setText(root.getString("channel_id"));
                        }
                        if (root.getString("per_page").equals("exception:error?youtube_per_page")) {
                            perPage.setText("");
                        } else {
                            perPage.setText(root.getString("per_page"));
                        }

                        auth = root.getString("video_design");
                        switch (root.getString("video_design")) {
                            case AuthAPI.YOUTUBE_VIDEO_STYLE_ONE:
                                design.setText(getString(R.string.video_design) + " 1");
                                break;
                            case AuthAPI.YOUTUBE_VIDEO_STYLE_TWO:
                                design.setText(getString(R.string.video_design) + " 2");
                                break;
                            case AuthAPI.YOUTUBE_VIDEO_STYLE_THREE:
                                design.setText(getString(R.string.video_design) + " 3");
                                break;
                            case AuthAPI.YOUTUBE_VIDEO_STYLE_FOUR:
                                design.setText(getString(R.string.video_design) + " 4");
                                break;
                            case AuthAPI.YOUTUBE_VIDEO_STYLE_FIVE:
                                design.setText(getString(R.string.video_design) + " 5");
                                break;
                            case AuthAPI.YOUTUBE_VIDEO_STYLE_SIX:
                                design.setText(getString(R.string.video_design) + " 6");
                                break;
                            case AuthAPI.YOUTUBE_VIDEO_STYLE_SEVEN:
                                design.setText(getString(R.string.video_design) + " 7");
                                break;
                            case AuthAPI.YOUTUBE_VIDEO_STYLE_EIGHT:
                                design.setText(getString(R.string.video_design) + " 8");
                                break;
                            case AuthAPI.YOUTUBE_VIDEO_STYLE_NINE:
                                design.setText(getString(R.string.video_design) + " 9");
                                break;
                            case AuthAPI.YOUTUBE_VIDEO_STYLE_TEN:
                                design.setText(getString(R.string.video_design) + " 10");
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.youtube_container).setVisibility(View.GONE);
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestData());
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
     * request update youtube
     */
    private void requestUpdateYoutube() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_YOUTUBE,
                    response -> {
                        if (response.contains("exception:configuration?success")) {
                            Toasto.show_toast(this, getString(R.string.youtube_updated), 0, 0);
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(this, "exception:error?" + error.getMessage(), 0, 1);
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("access_token", accessToken.getText().toString());
                    params.put("channel_id", channelId.getText().toString());
                    params.put("per_page", perPage.getText().toString());
                    params.put("video_design", auth);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }

    /**
     * open video design chooser dialog
     */
    @SuppressLint("NotifyDataSetChanged")
    private void designChooserDialog() {
        Dialog designsDialog = new Dialog(this);

        designsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        designsDialog.setContentView(R.layout.dialog_designs);

        // enable dialog cancel
        designsDialog.setCancelable(true);
        designsDialog.setOnCancelListener(dialog -> designsDialog.dismiss());

        // close dialog
        ImageView dialogClose = designsDialog.findViewById(R.id.dialog_close);
        dialogClose.setOnClickListener(v -> designsDialog.dismiss());

        List<VideoDesigns> designsList = new ArrayList<>();
        RecyclerView stylesRecyclerview = designsDialog.findViewById(R.id.recycler_styles);

        VideoStylesAdapter videoStylesAdapter = new VideoStylesAdapter(this, designsList, click -> {
            design.setText(click.getTitle());
            auth = click.getAuth();
            designsDialog.dismiss();
        });

        stylesRecyclerview.setAdapter(videoStylesAdapter);
        stylesRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        designsList.add(new VideoDesigns(0, AuthAPI.YOUTUBE_VIDEO_STYLE_ONE, getString(R.string.video_design) + " 1"));
        designsList.add(new VideoDesigns(1, AuthAPI.YOUTUBE_VIDEO_STYLE_TWO, getString(R.string.video_design) + " 2"));
        designsList.add(new VideoDesigns(2, AuthAPI.YOUTUBE_VIDEO_STYLE_THREE, getString(R.string.video_design) + " 3"));
        designsList.add(new VideoDesigns(3, AuthAPI.YOUTUBE_VIDEO_STYLE_FOUR, getString(R.string.video_design) + " 4"));
        designsList.add(new VideoDesigns(4, AuthAPI.YOUTUBE_VIDEO_STYLE_FIVE, getString(R.string.video_design) + " 5"));
        designsList.add(new VideoDesigns(5, AuthAPI.YOUTUBE_VIDEO_STYLE_SIX, getString(R.string.video_design) + " 6"));
        designsList.add(new VideoDesigns(6, AuthAPI.YOUTUBE_VIDEO_STYLE_SEVEN, getString(R.string.video_design) + " 7"));
        designsList.add(new VideoDesigns(7, AuthAPI.YOUTUBE_VIDEO_STYLE_EIGHT, getString(R.string.video_design) + " 8"));
        designsList.add(new VideoDesigns(8, AuthAPI.YOUTUBE_VIDEO_STYLE_NINE, getString(R.string.video_design) + " 9"));
        designsList.add(new VideoDesigns(9, AuthAPI.YOUTUBE_VIDEO_STYLE_TEN, getString(R.string.video_design) + " 10"));
        videoStylesAdapter.notifyDataSetChanged();

        if (designsDialog.getWindow() != null) {
            designsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            designsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        designsDialog.show();
    }
}