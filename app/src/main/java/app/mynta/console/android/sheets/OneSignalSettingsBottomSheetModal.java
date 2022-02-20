package app.mynta.console.android.sheets;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.mynta.console.android.R;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

public class OneSignalSettingsBottomSheetModal extends BottomSheetDialogFragment {
    View view;
    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    private EditText appId;
    private EditText restApiKey;

    public OneSignalSettingsBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.onesignal_settings_bottom_sheet_modal, container, false);

        consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // onesignal fields
        appId = view.findViewById(R.id.onesignal_app_id);
        restApiKey = view.findViewById(R.id.onesignal_rest_api_key);

        // save settings
        view.findViewById(R.id.save_settings).setOnClickListener(v -> {
            // validate onesignal settings
            if (!TextUtils.isEmpty(appId.getText().toString())
                    && !TextUtils.isEmpty(restApiKey.getText().toString())) {
                requestSaveSettings(appId.getText().toString(), restApiKey.getText().toString());
                dismiss();
            } else {
                Toasto.show_toast(requireContext(), getString(R.string.all_fields_are_required), 1, 2);
            }
        });

        // request settings
        requestSettings();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request onesignal settings
     */
    private void requestSettings() {
        view.findViewById(R.id.save_settings).setEnabled(false);
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_ONESIGNAL_SETTINGS,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("settings");

                        appId.setText(root.getString("app_id"));
                        restApiKey.setText(root.getString("rest_api_key"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    view.findViewById(R.id.save_settings).setEnabled(true);
                }, error -> {
            view.findViewById(R.id.save_settings).setEnabled(true);
            Toasto.show_toast(requireActivity().getApplicationContext(), getString(R.string.unknown_issue), 1, 1);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                return params;
            }
        };

        Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
    }

    /**
     * request save onesignal settings
     * @param app_id for application id
     * @param rest_api_key for rest api key
     */
    private void requestSaveSettings(String app_id, String rest_api_key) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(requireContext(), getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_SAVE_ONESIGNAL_SETTINGS,
                    response -> {
                        if (response.equals("200")) {
                            Toasto.show_toast(requireContext(), getString(R.string.settings_saved), 0, 0);
                            dismiss();
                        } else {
                            Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 1, 1);
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(requireActivity().getApplicationContext(), getString(R.string.unknown_issue), 1, 1);
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("app_id", app_id);
                    params.put("rest_api_key", rest_api_key);
                    return params;
                }
            };

            Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
        }
    }
}
