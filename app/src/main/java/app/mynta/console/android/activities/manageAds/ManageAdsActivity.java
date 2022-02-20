package app.mynta.console.android.activities.manageAds;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import app.mynta.console.android.R;

import app.mynta.console.android.sheets.ManageAdsWizardBottomSheetModal;
import app.mynta.console.android.utils.Helper;

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