package com.picassos.mint.console.android.activities.helpCentre;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.picassos.mint.console.android.adapter.TicketTypeAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.models.TicketType;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmitTicketActivity extends AppCompatActivity {

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    private EditText ticketSubject;
    private EditText ticketDescription;
    private TextView ticketType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_submit_ticket);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // ticket type chooser
        CardView ticketTypeChooser = findViewById(R.id.ticket_type_chooser);
        ticketTypeChooser.setOnClickListener(v -> ticketTypeChooserDialog());

        // ticket type data
        ticketType = findViewById(R.id.ticket_type);

        // ticket data
        ticketSubject = findViewById(R.id.ticket_subject);
        ticketDescription = findViewById(R.id.ticket_description);

        // submit request
        Button openTicket = findViewById(R.id.open_ticket);
        openTicket.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(ticketSubject.getText().toString())
                    && !TextUtils.isEmpty(ticketDescription.getText().toString())
                    && !TextUtils.isEmpty(ticketType.getText().toString())
                    && !ticketType.getText().toString().equals(getString(R.string.ticket_type))) {
                requestOpenTicket();
            } else {
                Toasto.show_toast(this, getString(R.string.all_fields_are_required), 0, 2);
            }
        });
    }

    /**
     * request open ticket
     */
    private void requestOpenTicket() {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_SUBMIT_TICKET,
                response -> {
                    if (response.equals("200")) {
                        startActivity(new Intent(SubmitTicketActivity.this, CheckTicketActivity.class));
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(this, getString(R.string.unknown_issue), 0, 1);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", consolePreferences.loadToken());
                params.put("username", consolePreferences.loadUsername());
                params.put("email", consolePreferences.loadEmail());
                params.put("request_subject", ticketSubject.getText().toString());
                params.put("request_description", ticketDescription.getText().toString());
                params.put("request_type", ticketType.getText().toString());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * open ticket type chooser dialog
     */
    @SuppressLint("NotifyDataSetChanged")
    private void ticketTypeChooserDialog() {
        Dialog typeDialog = new Dialog(this);

        typeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        typeDialog.setContentView(R.layout.dialog_ticket_types);

        // enable dialog cancel
        typeDialog.setCancelable(true);
        typeDialog.setOnCancelListener(dialog -> typeDialog.dismiss());

        // close dialog
        ImageView dialogClose = typeDialog.findViewById(R.id.dialog_close);
        dialogClose.setOnClickListener(v -> typeDialog.dismiss());

        List<TicketType> ticketTypeList = new ArrayList<>();
        RecyclerView stylesRecyclerview = typeDialog.findViewById(R.id.recycler_types);

        TicketTypeAdapter ticketTypeAdapter = new TicketTypeAdapter(ticketTypeList, click -> {
            ticketType.setText(click.getType());
            typeDialog.dismiss();
        });

        stylesRecyclerview.setAdapter(ticketTypeAdapter);
        stylesRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        ticketTypeList.add(new TicketType("Support"));
        ticketTypeList.add(new TicketType("Report a Bug"));
        ticketTypeList.add(new TicketType("Customization"));
        ticketTypeList.add(new TicketType("Upgrade Request"));
        ticketTypeList.add(new TicketType("Other"));
        ticketTypeAdapter.notifyDataSetChanged();

        if (typeDialog.getWindow() != null) {
            typeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            typeDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        typeDialog.show();
    }
}