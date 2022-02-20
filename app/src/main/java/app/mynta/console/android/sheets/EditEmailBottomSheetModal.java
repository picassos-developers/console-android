package app.mynta.console.android.sheets;

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

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import app.mynta.console.android.R;

import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditEmailBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    public EditEmailBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit_email_bottom_sheet_modal, container, false);

        consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // email address
        EditText emailAddress = view.findViewById(R.id.email_address);

        // save email
        Button saveEmail = view.findViewById(R.id.save);
        saveEmail.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(emailAddress.getText().toString())) {
                requestSaveEmail(emailAddress.getText().toString());
            } else {
                Toasto.show_toast(requireContext(), getString(R.string.email_empty), 1, 2);
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request save email address
     */
    private void requestSaveEmail(String email) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(requireContext(), getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_EMAIL,
                    response -> {
                        Intent intent = new Intent();
                        intent.putExtra("request", "save");
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
                    params.put("email", email);
                    return params;
                }
            };

            Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
        }
    }
}
