package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationsPreferencesBottomSheetModal extends BottomSheetDialogFragment {
    View view;

    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    // notification preferences
    private SwitchCompat notificationSound;
    private SwitchCompat notificationVibration;

    private boolean firstTime = true;

    public NotificationsPreferencesBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.notifications_preferences_bottom_sheet_modal, container, false);

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

        // initialize preferences
        notificationSound = view.findViewById(R.id.notification_sound);
        notificationVibration = view.findViewById(R.id.notification_vibration);

        notificationSound.setOnCheckedChangeListener((buttonView, isChecked) -> requestUpdateNotificationPreferences());
        notificationVibration.setOnCheckedChangeListener((buttonView, isChecked) -> requestUpdateNotificationPreferences());

        // request preferences
        requestNotificationPreferences();

        return view;
    }

    /**
     * request notification preferences
     */
    private void requestNotificationPreferences() {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_NOTIFICATIONS_PREFERENCES,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("notifications");
                        JSONObject preferences = root.getJSONObject("preferences");
                        // notification sound option
                        switch (preferences.getString("sound")) {
                            case "true":
                                notificationSound.setChecked(true);
                                break;
                            case "false":
                                notificationSound.setChecked(false);
                                break;
                        }
                        // notification vibration option
                        switch (preferences.getString("vibration")) {
                            case "true":
                                notificationVibration.setChecked(true);
                                break;
                            case "false":
                                notificationVibration.setChecked(false);
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 0, 1);
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    /**
     * request notification preferences
     */
    private void requestUpdateNotificationPreferences() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(requireContext(), getString(R.string.demo_project), 1, 0);
        } else {
            String sound;
            if (notificationSound.isChecked()) {
                sound = "true";
            } else {
                sound = "false";
            }

            String vibration;
            if (notificationVibration.isChecked()) {
                vibration = "true";
            } else {
                vibration = "false";
            }

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_NOTIFICATION_PREFERENCES_OPTION,
                    response -> {
                        if (response.contains("exception:configuration?success")) {
                            if (firstTime) {
                                firstTime = false;
                            } else {
                                Toasto.show_toast(requireContext(), getString(R.string.notification_preferences_updated), 0, 0);
                            }
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 0, 1);
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("notification_sound", sound);
                    params.put("notification_vibration", vibration);
                    return params;
                }
            };

            Volley.newRequestQueue(requireContext()).add(request);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requestDialog.dismiss();
    }
}
