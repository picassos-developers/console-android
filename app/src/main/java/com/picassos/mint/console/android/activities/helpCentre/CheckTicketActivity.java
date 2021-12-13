package com.picassos.mint.console.android.activities.helpCentre;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.about.MyTicketsActivity;
import com.picassos.mint.console.android.utils.Helper;

public class CheckTicketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        setContentView(R.layout.activity_check_ticket);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // my tickets
        findViewById(R.id.my_tickets).setOnClickListener(v -> startActivity(new Intent(CheckTicketActivity.this, MyTicketsActivity.class)));
    }
}