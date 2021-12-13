package com.picassos.mint.console.android.activities.helpCentre;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.adapter.CommentsAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.models.Comments;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.CommentOptionsBottomSheetModal;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity implements CommentOptionsBottomSheetModal.OnRemoveListener, CommentOptionsBottomSheetModal.OnUpdateListener {

    Bundle bundle;

    ConsolePreferences consolePreferences;

    // Comments
    private final List<Comments> commentsList = new ArrayList<>();
    // request dialog
    RequestDialog requestDialog;
    private CommentsAdapter commentsAdapter;

    private int articleId;
    private EditText commentField;
    
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        
        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_comments);

        // initialize bundle
        bundle = new Bundle();

        // request data
        Intent data = getIntent();
        articleId = data.getIntExtra("article_id", 0);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // Initialize comments recyclerview
        RecyclerView commentsRecyclerview = findViewById(R.id.recycler_comments);

        // comments adapter
        commentsAdapter = new CommentsAdapter(this, commentsList, comment -> {
            if (comment.getToken().equals(consolePreferences.loadToken())) {
                bundle.putInt("comment_id", comment.getId());
                bundle.putString("comment_description", comment.getDescription());
                CommentOptionsBottomSheetModal commentOptionsBottomSheetModal = new CommentOptionsBottomSheetModal();
                commentOptionsBottomSheetModal.setArguments(bundle);
                commentOptionsBottomSheetModal.show(getSupportFragmentManager(), "TAG");
            }
        });

        commentsRecyclerview.setAdapter(commentsAdapter);
        commentsRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        // request comments
        requestComments(articleId);

        // Refresh Layout
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            commentsList.clear();
            commentsAdapter.notifyDataSetChanged();
            requestComments(articleId);
        });

        // add comment
        commentField = findViewById(R.id.comment_field);
        findViewById(R.id.post_comment).setOnClickListener(v -> {
            if (!TextUtils.isEmpty(commentField.getText().toString())) {
                requestAddComment(commentField.getText().toString().trim());
            } else {
                Toasto.show_toast(this, getString(R.string.comment_empty), 0, 2);
            }
        });
    }

    /**
     * request comments
     */
    @SuppressLint("NotifyDataSetChanged")
    private void requestComments(int id) {
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_COMMENTS,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        JSONArray array = obj.getJSONArray("comments");

                        // Check if data are empty
                        if (array.length() == 0) {
                            findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.no_items).setVisibility(View.GONE);
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Comments comments = new Comments(object.getString("token"), object.getInt("comment_id"), object.getString("comment_username"), object.getString("comment_email"), object.getString("comment_description"), object.getString("comment_date"), object.getInt("comment_article"), object.getInt("comment_votes"), object.getInt("is_edited"));
                            commentsList.add(comments);
                            commentsAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestComments(articleId));
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("article", String.valueOf(id));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request add comment
     * @param comment for comment
     */
    @SuppressLint("NotifyDataSetChanged")
    private void requestAddComment(String comment) {
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_POST_COMMENT,
                response -> {
                    if (response.equals("200")) {
                        Toasto.show_toast(this, getString(R.string.comment_added), 0, 0);
                        commentField.setText("");
                        commentsList.clear();
                        commentsAdapter.notifyDataSetChanged();
                        requestComments(articleId);
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(this, getString(R.string.unknown_issue), 0, 2);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", consolePreferences.loadToken());
                params.put("username", consolePreferences.loadUsername());
                params.put("email", consolePreferences.loadEmail());
                params.put("comment", comment);
                params.put("article_id", String.valueOf(articleId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRemoveListener(int requestCode) {
        if (requestCode == CommentOptionsBottomSheetModal.REQUEST_REMOVE_COMMENT_CODE) {
            commentsList.clear();
            commentsAdapter.notifyDataSetChanged();
            requestComments(articleId);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onUpdateListener(int requestCode) {
        if (requestCode == CommentOptionsBottomSheetModal.REQUEST_UPDATE_COMMENT_CODE) {
            commentsList.clear();
            commentsAdapter.notifyDataSetChanged();
            requestComments(articleId);
        }
    }
}