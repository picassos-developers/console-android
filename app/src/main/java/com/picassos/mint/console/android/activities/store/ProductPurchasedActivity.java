package com.picassos.mint.console.android.activities.store;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.utils.Helper;

public class ProductPurchasedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        setContentView(R.layout.activity_product_purchased);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // power ups
        findViewById(R.id.my_power_ups).setOnClickListener(v -> finish());
    }
}