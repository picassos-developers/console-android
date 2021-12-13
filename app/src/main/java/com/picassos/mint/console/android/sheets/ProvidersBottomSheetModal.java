package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProvidersBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    RequestDialog requestDialog;
    ConsolePreferences consolePreferences;

    public ProvidersBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.providers_bottom_sheet_modal, container, false);

        // providers
        view.findViewById(R.id.wordpress_provider).setOnClickListener(v -> requestSelectProvider("wordpress"));
        view.findViewById(R.id.webview_provider).setOnClickListener(v -> requestSelectProvider("webview"));
        view.findViewById(R.id.youtube_provider).setOnClickListener(v -> requestSelectProvider("youtube"));
        view.findViewById(R.id.vimeo_provider).setOnClickListener(v -> requestSelectProvider("vimeo"));
        view.findViewById(R.id.facebook_provider).setOnClickListener(v -> requestSelectProvider("facebook"));
        view.findViewById(R.id.pinterest_provider).setOnClickListener(v -> requestSelectProvider("pinterest"));
        view.findViewById(R.id.maps_provider).setOnClickListener(v -> requestSelectProvider("maps"));
        view.findViewById(R.id.imgur_provider).setOnClickListener(v -> requestSelectProvider("imgur"));

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestDialog = new RequestDialog(requireContext());
        consolePreferences = new ConsolePreferences(requireContext());
    }

    /**
     * request select default provider
     * @param provider for provider
     */
    private void requestSelectProvider(String provider) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(requireContext(), getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_PROVIDER,
                    response -> {
                        Intent intent = new Intent();
                        intent.putExtra("request", "update");
                        Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        requestDialog.dismiss();
                        dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(requireActivity().getApplicationContext(), getString(R.string.unknown_issue), 1, 1);
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("provider", provider);
                    return params;
                }
            };

            Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
        }
    }

}
