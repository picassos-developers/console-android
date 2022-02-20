package app.mynta.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import app.mynta.console.android.R;
import app.mynta.console.android.utils.Helper;

public class GiftActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        setContentView(R.layout.activity_gift);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // claim gift
        findViewById(R.id.claim_gift).setOnClickListener(v -> startActivity(new Intent(GiftActivity.this, ClaimGiftActivity.class)));
    }
}