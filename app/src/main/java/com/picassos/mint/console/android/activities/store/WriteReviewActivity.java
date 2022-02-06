package com.picassos.mint.console.android.activities.store;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class WriteReviewActivity extends AppCompatActivity {

    RequestDialog requestDialog;
    ConsolePreferences consolePreferences;

    // length
    private TextView length;

    // placeholders
    private int id = 0;
    private String review = "";
    private int rating = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_write_review);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // check request type
        if (getIntent().getStringExtra("action") != null) {
            if (getIntent().getStringExtra("action").equals("update")) {
                id = getIntent().getIntExtra("id", 0);
                review = getIntent().getStringExtra("review");
                rating = getIntent().getIntExtra("rating", 0);
            }
        } else {
            finish();
        }

        // review author
        TextView reviewAuthor = findViewById(R.id.review_author);
        reviewAuthor.setText(consolePreferences.loadUsername().substring(0, 1).toUpperCase() + consolePreferences.loadUsername().substring(1));
        TextView reviewAuthorIcon = findViewById(R.id.review_author_icon);
        reviewAuthorIcon.setText(consolePreferences.loadUsername().substring(0, 1).toUpperCase());

        // review field
        EditText reviewField = findViewById(R.id.review_field);
        length = findViewById(R.id.length);
        reviewField.addTextChangedListener(onReviewValueChange);
        reviewField.setText(review);

        // learn more
        TextView learnMore = findViewById(R.id.learn_more);
        learnMore.setMovementMethod(LinkMovementMethod.getInstance());

        // review rating
        RatingBar reviewRating = findViewById(R.id.review_rating);
        reviewRating.setRating(rating);
        reviewRating.setOnRatingBarChangeListener((ratingBar, v, b) -> {
            int rating = Math.round(ratingBar.getRating());
            switch (rating) {
                case 1:
                case 2:
                    findViewById(R.id.rating_callback).setVisibility(View.VISIBLE);
                    break;
                case 3:
                case 4:
                case 5:
                default:
                    findViewById(R.id.rating_callback).setVisibility(View.GONE);
                    break;
            }
        });

        // post review
        findViewById(R.id.post_review).setOnClickListener(v -> {
            if (Math.round(reviewRating.getRating()) >= 1 && Math.round(reviewRating.getRating()) <= 5 && !TextUtils.isEmpty(reviewField.getText().toString())) {
                requestPostReview(reviewField.getText().toString(), Math.round(reviewRating.getRating()), getIntent().getStringExtra("action"));
            } else {
                Toasto.show_toast(this, getString(R.string.all_fields_are_required), 1, 2);
            }
        });
    }

    /**
     * request post customer review
     * @param review for review
     * @param rating for rating
     * @param action for request action
     */
    private void requestPostReview(String review, int rating, String action) {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_POST_REVIEW,
                response -> {
                    switch (response) {
                        case "200":
                            Toasto.show_toast(this, getString(R.string.review_posted_successfully), 1, 0);
                            Intent intent = new Intent();
                            intent.putExtra("request", "update");
                            setResult(Activity.RESULT_OK);
                            finish();
                            break;
                        case "403":
                            Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 2);
                            break;
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("request", action);
                params.put("token", consolePreferences.loadToken());
                params.put("product_id", getIntent().getStringExtra("product_id"));
                params.put("review", review);
                params.put("author", consolePreferences.loadUsername());
                params.put("review_id", String.valueOf(id));
                params.put("rating", String.valueOf(rating));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * on review field value change, update
     * length to inform user with max length
     */
    private final TextWatcher onReviewValueChange = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @SuppressLint("SetTextI18n")
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            length.setText(s.length() + " / 250");
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}