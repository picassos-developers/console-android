package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.manageAds.SetFacebookNetworkActivity;
import com.picassos.mint.console.android.activities.manageAds.SetGoogleAdmobActivity;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class ManageAdsWizardBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    ConsolePreferences consolePreferences;
    RequestDialog requestDialog;

    public ManageAdsWizardBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.manage_ads_wizard_bottom_sheet_modal, container, false);

        // google admob
        view.findViewById(R.id.google_admob).setOnClickListener(v -> startActivity(new Intent(requireContext(), SetGoogleAdmobActivity.class)));

        // facebook network
        view.findViewById(R.id.facebook_network).setOnClickListener(v -> startActivity(new Intent(requireContext(), SetFacebookNetworkActivity.class)));

        // disable ads
        view.findViewById(R.id.disable_ads).setOnClickListener(v -> requestDisableAds());

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        consolePreferences = new ConsolePreferences(requireContext());
        requestDialog = new RequestDialog(requireContext());
    }

    /**
     * request disable ads
     */
    private void requestDisableAds() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(requireContext(), getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_AD_PROVIDER,
                    response -> {
                        Toasto.show_toast(requireContext(), getString(R.string.ads_disabled), 0, 0);
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 0, 2);
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("provider", "disabled");
                    return params;
                }
            };

            Volley.newRequestQueue(requireContext()).add(request);
        }
    }
}
