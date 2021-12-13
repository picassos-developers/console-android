package com.picassos.mint.console.android.activities.helpCentre;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class UpdateTicketActivity extends AppCompatActivity {

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    // ticket id
    private int id;

    private EditText descriptionField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_update_ticket);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // get id
        id = getIntent().getIntExtra("ticket_id", 0);

        // ticket description
        String description = getIntent().getStringExtra("ticket_description");

        // ticket details
        descriptionField = findViewById(R.id.ticket_description);
        descriptionField.setText(description);

        // update ticket
        Button updateTicket = findViewById(R.id.update_ticket);
        updateTicket.setOnClickListener(v -> {
            // Validate ticket data
            if (!TextUtils.isEmpty(descriptionField.getText().toString())) {
                requestUpdateTicket();
            } else {
                Toasto.show_toast(this, getString(R.string.all_fields_are_required), 0, 2);
            }
        });
    }

    /**
     *  request update ticket
     */
    private void requestUpdateTicket() {
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_TICKET,
                response -> {
                    if (response.equals("200")) {
                        Intent intent = new Intent();
                        intent.putExtra("request", "update");
                        setResult(Activity.RESULT_OK, intent);
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
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                params.put("ticket_id", String.valueOf(id));
                params.put("ticket_description", descriptionField.getText().toString());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}