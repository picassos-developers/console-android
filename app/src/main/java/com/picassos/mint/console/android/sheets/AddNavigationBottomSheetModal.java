package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

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

public class AddNavigationBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    RequestDialog requestDialog;
    // navigation tab preferences
    String app_behavior;
    String app_premium;
    private ConsolePreferences consolePreferences;

    public AddNavigationBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_navigation_bottom_sheet_modal, container, false);

        consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // navigation fields
        EditText navigationTitle = view.findViewById(R.id.navigation_title_input);
        EditText navigationLink = view.findViewById(R.id.navigation_link_input);
        EditText navigationIcon = view.findViewById(R.id.navigation_icon_input);

        // navigation open external
        SwitchCompat openExternal = view.findViewById(R.id.navigation_behavior_switch);
        openExternal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                app_behavior = "EXTERNAL";
            } else {
                app_behavior = "INAPP";
            }
        });

        if (openExternal.isChecked()) {
            app_behavior = "EXTERNAL";
        } else {
            app_behavior = "INAPP";
        }

        // navigation premium
        SwitchCompat isPremium = view.findViewById(R.id.navigation_premium_switch);
        isPremium.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                app_premium = "true";
            } else {
                app_premium = "false";
            }
        });

        if (isPremium.isChecked()) {
            app_premium = "true";
        } else {
            app_premium = "false";
        }

        // add navigation
        Button addNavigation = view.findViewById(R.id.add_navigation_button);
        addNavigation.setOnClickListener(v -> {

            // Check if navigation title is empty
            if (TextUtils.isEmpty(navigationTitle.getText().toString())) {
                navigationTitle.setError(getString(R.string.navigation_title_required));
                navigationTitle.requestFocus();
                addNavigation.setEnabled(true);
                return;
            }

            // Check if navigation link is empty
            if (TextUtils.isEmpty(navigationLink.getText().toString())) {
                navigationLink.setError(getString(R.string.navigation_link_required));
                navigationLink.requestFocus();
                addNavigation.setEnabled(true);
                return;
            }

            // Validate navigation data
            if (!TextUtils.isEmpty(navigationTitle.getText().toString()) && !TextUtils.isEmpty(navigationLink.getText().toString())) {
                requestAddNavigation(navigationTitle.getText().toString(), navigationLink.getText().toString(), navigationIcon.getText().toString(), app_behavior, app_premium);
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request add navigation
     *
     * @param title    for title
     * @param link     for link
     * @param icon     for icon
     * @param behavior for tab behavior
     * @param premium  for premium type
     */
    private void requestAddNavigation(String title, String link, String icon, String behavior, String premium) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(requireContext(), getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_ADD_NAVIGATION,
                    response -> {
                        if (response.contains("navigation:callback?success")) {
                            Intent intent = new Intent();
                            intent.putExtra("request", "add");
                            Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        } else {
                            Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 1, 1);
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 1, 1);
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("title", title);
                    params.put("link", link);
                    params.put("icon", icon);
                    params.put("behavior", behavior);
                    params.put("premium", premium);
                    return params;
                }
            };

            Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
        }
    }
}
