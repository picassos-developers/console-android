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
import com.picassos.mint.console.android.adapter.wordpress.CategoryStylesAdapter;
import com.picassos.mint.console.android.adapter.wordpress.PostStylesAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.constants.AuthAPI;
import com.picassos.mint.console.android.models.wordpress.CategoryDesigns;
import com.picassos.mint.console.android.models.wordpress.PostDesigns;
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

public class WordpressActivity extends AppCompatActivity {

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;
    private String categoryAuth;
    private String postAuth;

    private EditText baseUrl;
    private EditText perPage;
    private TextView categoryDesign;
    private TextView postDesign;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_wordpress);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // category design chooser
        CardView categoryDesignChooser = findViewById(R.id.category_design_chooser);
        categoryDesignChooser.setOnClickListener(v -> categoryDesignChooserDialog());

        // post design chooser
        CardView postDesignChooser = findViewById(R.id.post_design_chooser);
        postDesignChooser.setOnClickListener(v -> postDesignChooserDialog());

        // design data
        categoryDesign = findViewById(R.id.category_design);
        postDesign = findViewById(R.id.post_design);

        // wordpress data
        baseUrl = findViewById(R.id.base_url);
        perPage = findViewById(R.id.per_page);

        // request data
        requestData();

        // save data
        Button save = findViewById(R.id.update_wordpress);
        save.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(baseUrl.getText().toString())
                    && !TextUtils.isEmpty(perPage.getText().toString())
                    && !TextUtils.isEmpty(categoryDesign.getText().toString())
                    && !TextUtils.isEmpty(postDesign.getText().toString())) {
                requestUpdateWordpress();
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
     * request wordpress data
     */
    @SuppressLint("SetTexti18n")
    private void requestData() {
        findViewById(R.id.wordpress_container).setVisibility(View.VISIBLE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_WORDPRESS_DETAILS,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("wordpress");
                        // wordpress data
                        if (root.getString("base_url").equals("exception:error?wordpress_base_url")) {
                            baseUrl.setText("");
                        } else {
                            baseUrl.setText(root.getString("base_url"));
                        }
                        if (root.getString("per_page").equals("exception:error?wordpress_per_page")) {
                            perPage.setText("");
                        } else {
                            perPage.setText(root.getString("per_page"));
                        }
                        categoryAuth = root.getString("category_design");
                        postAuth = root.getString("post_design");
                        switch (root.getString("category_design")) {
                            case AuthAPI.WORD_PRESS_CATEGORY_STYLE_ONE:
                                categoryDesign.setText(getString(R.string.category_design_one));
                                break;
                            case AuthAPI.WORD_PRESS_CATEGORY_STYLE_TWO:
                                categoryDesign.setText(getString(R.string.category_design_two));
                                break;
                            case AuthAPI.WORD_PRESS_CATEGORY_STYLE_THREE:
                                categoryDesign.setText(getString(R.string.category_design_three));
                                break;
                            case AuthAPI.WORD_PRESS_CATEGORY_STYLE_FOUR:
                                categoryDesign.setText(getString(R.string.category_design_four));
                                break;
                        }
                        switch (root.getString("post_design")) {
                            case AuthAPI.WORD_PRESS_POST_STYLE_ONE:
                                postDesign.setText(getString(R.string.post_design) + " 1");
                                break;
                            case AuthAPI.WORD_PRESS_POST_STYLE_TWO:
                                postDesign.setText(getString(R.string.post_design) + " 2");
                                break;
                            case AuthAPI.WORD_PRESS_POST_STYLE_THREE:
                                postDesign.setText(getString(R.string.post_design) + " 3");
                                break;
                            case AuthAPI.WORD_PRESS_POST_STYLE_FOUR:
                                postDesign.setText(getString(R.string.post_design) + " 4");
                                break;
                            case AuthAPI.WORD_PRESS_POST_STYLE_FIVE:
                                postDesign.setText(getString(R.string.post_design) + " 5");
                                break;
                            case AuthAPI.WORD_PRESS_POST_STYLE_SIX:
                                postDesign.setText(getString(R.string.post_design) + " 6");
                                break;
                            case AuthAPI.WORD_PRESS_POST_STYLE_SEVEN:
                                postDesign.setText(getString(R.string.post_design) + " 7");
                                break;
                            case AuthAPI.WORD_PRESS_POST_STYLE_EIGHT:
                                postDesign.setText(getString(R.string.post_design) + " 8");
                                break;
                            case AuthAPI.WORD_PRESS_POST_STYLE_NINE:
                                postDesign.setText(getString(R.string.post_design) + " 9");
                                break;
                            case AuthAPI.WORD_PRESS_POST_STYLE_TEN:
                                postDesign.setText(getString(R.string.post_design) + " 10");
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.wordpress_container).setVisibility(View.GONE);
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
    private void requestUpdateWordpress() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_WORDPRESS,
                    response -> {
                        if (response.contains("exception:configuration?success")) {
                            Toasto.show_toast(this, getString(R.string.wordpress_updated), 0, 0);
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("base_url", baseUrl.getText().toString());
                    params.put("per_page", perPage.getText().toString());
                    params.put("category_design", categoryAuth);
                    params.put("post_design", postAuth);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }

    /**
     * open category design chooser dialog
     */
    @SuppressLint("NotifyDataSetChanged")
    private void categoryDesignChooserDialog() {
        Dialog designsDialog = new Dialog(this);

        designsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        designsDialog.setContentView(R.layout.dialog_designs);

        // enable dialog cancel
        designsDialog.setCancelable(true);
        designsDialog.setOnCancelListener(dialog -> designsDialog.dismiss());

        // close dialog
        ImageView dialogClose = designsDialog.findViewById(R.id.dialog_close);
        dialogClose.setOnClickListener(v -> designsDialog.dismiss());

        List<CategoryDesigns> designsList = new ArrayList<>();
        RecyclerView stylesRecyclerview = designsDialog.findViewById(R.id.recycler_styles);

        CategoryStylesAdapter categoryStylesAdapter = new CategoryStylesAdapter(this, designsList, click -> {
            categoryDesign.setText(click.getTitle());
            categoryAuth = click.getAuth();
            designsDialog.dismiss();
        });

        stylesRecyclerview.setAdapter(categoryStylesAdapter);
        stylesRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        designsList.add(new CategoryDesigns(0, AuthAPI.WORD_PRESS_CATEGORY_STYLE_ONE, getString(R.string.category_design_one)));
        designsList.add(new CategoryDesigns(1, AuthAPI.WORD_PRESS_CATEGORY_STYLE_TWO, getString(R.string.category_design_two)));
        designsList.add(new CategoryDesigns(2, AuthAPI.WORD_PRESS_CATEGORY_STYLE_THREE, getString(R.string.category_design_three)));
        designsList.add(new CategoryDesigns(3, AuthAPI.WORD_PRESS_CATEGORY_STYLE_FOUR, getString(R.string.category_design_four)));
        categoryStylesAdapter.notifyDataSetChanged();

        if (designsDialog.getWindow() != null) {
            designsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            designsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        designsDialog.show();
    }

    /**
     * open post design chooser dialog
     */
    @SuppressLint("NotifyDataSetChanged")
    private void postDesignChooserDialog() {
        Dialog designsDialog = new Dialog(this);

        designsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        designsDialog.setContentView(R.layout.dialog_designs);

        // enable dialog cancel
        designsDialog.setCancelable(true);
        designsDialog.setOnCancelListener(dialog -> designsDialog.dismiss());

        // close dialog
        ImageView dialogClose = designsDialog.findViewById(R.id.dialog_close);
        dialogClose.setOnClickListener(v -> designsDialog.dismiss());

        List<PostDesigns> designsList = new ArrayList<>();
        RecyclerView stylesRecyclerview = designsDialog.findViewById(R.id.recycler_styles);

        PostStylesAdapter postStylesAdapter = new PostStylesAdapter(this, designsList, postDesigns -> {
            postAuth = postDesigns.getAuth();
            postDesign.setText(postDesigns.getTitle());
            designsDialog.dismiss();
        });

        stylesRecyclerview.setAdapter(postStylesAdapter);
        stylesRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        designsList.add(new PostDesigns(0, AuthAPI.WORD_PRESS_POST_STYLE_ONE, getString(R.string.post_design) + " 1"));
        designsList.add(new PostDesigns(1, AuthAPI.WORD_PRESS_POST_STYLE_TWO, getString(R.string.post_design) + " 2"));
        designsList.add(new PostDesigns(2, AuthAPI.WORD_PRESS_POST_STYLE_THREE, getString(R.string.post_design) + " 3"));
        designsList.add(new PostDesigns(3, AuthAPI.WORD_PRESS_POST_STYLE_FOUR, getString(R.string.post_design) + " 4"));
        designsList.add(new PostDesigns(4, AuthAPI.WORD_PRESS_POST_STYLE_FIVE, getString(R.string.post_design) + " 5"));
        designsList.add(new PostDesigns(5, AuthAPI.WORD_PRESS_POST_STYLE_SIX, getString(R.string.post_design) + " 6"));
        designsList.add(new PostDesigns(6, AuthAPI.WORD_PRESS_POST_STYLE_SEVEN, getString(R.string.post_design) + " 7"));
        designsList.add(new PostDesigns(7, AuthAPI.WORD_PRESS_POST_STYLE_EIGHT, getString(R.string.post_design) + " 8"));
        designsList.add(new PostDesigns(8, AuthAPI.WORD_PRESS_POST_STYLE_NINE, getString(R.string.post_design) + " 9"));
        designsList.add(new PostDesigns(9, AuthAPI.WORD_PRESS_POST_STYLE_TEN, getString(R.string.post_design) + " 10"));
        postStylesAdapter.notifyDataSetChanged();

        if (designsDialog.getWindow() != null) {
            designsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            designsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        designsDialog.show();
    }
}