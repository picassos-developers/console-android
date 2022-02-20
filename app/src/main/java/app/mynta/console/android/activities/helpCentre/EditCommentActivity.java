package app.mynta.console.android.activities.helpCentre;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.mynta.console.android.R;

import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.constants.RequestCodes;
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class EditCommentActivity extends AppCompatActivity {

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    // comment id
    private int id;

    private EditText descriptionField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_edit_comment);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // get id
        id = getIntent().getIntExtra("comment_id", 0);

        Toast.makeText(this, id + " ", Toast.LENGTH_SHORT).show();

        // comment description
        String description = getIntent().getStringExtra("comment_description");

        // comment details
        descriptionField = findViewById(R.id.comment_description);
        descriptionField.setText(description);

        // update comment
        Button updateComment = findViewById(R.id.update_comment);
        updateComment.setOnClickListener(v -> {
            // Validate comment data
            if (!TextUtils.isEmpty(descriptionField.getText().toString())) {
                requestUpdateComment();
            } else {
                Toasto.show_toast(this, getString(R.string.comment_empty), 0, 2);
            }
        });
    }

    /**
     *  request update comment
     */
    private void requestUpdateComment() {
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_COMMENT,
                response -> {
                    if (response.equals("200")) {
                        Intent intent = new Intent();
                        setResult(RequestCodes.REQUEST_UPDATE_COMMENT_CODE, intent);
                        finish();
                    } else {
                        Toast.makeText(this, getString(R.string.unknown_issue), Toast.LENGTH_SHORT).show();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toast.makeText(this, getString(R.string.unknown_issue), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", consolePreferences.loadToken());
                params.put("comment_description", descriptionField.getText().toString());
                params.put("comment_id", String.valueOf(id));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}