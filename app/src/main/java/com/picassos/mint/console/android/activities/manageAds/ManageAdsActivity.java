package com.picassos.mint.console.android.activities.manageAds;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.sheets.ManageAdsWizardBottomSheetModal;
import com.picassos.mint.console.android.utils.Helper;

public class ManageAdsActivity extends AppCompatActivity {

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        setContentView(R.layout.activity_manage_ads);

        // initialize bundle
        bundle = new Bundle();

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // manage ads
        findViewById(R.id.manage_ads).setOnClickListener(v -> {
            ManageAdsWizardBottomSheetModal manageAdsWizardBottomSheetModal = new ManageAdsWizardBottomSheetModal();
            manageAdsWizardBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });
    }
}