package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.ProjectsActivity;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class TwoFactorAuthBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    public TwoFactorAuthBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.two_factor_auth_bottom_sheet_modal, container, false);

        consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // verification code
        EditText verificationCode = view.findViewById(R.id.verification_code);

        // email verification
        TextView emailVerification = view.findViewById(R.id.email_verification);
        emailVerification.setText(getString(R.string.we_sent_verification_code_to) + "\n" + requireArguments().getString("EMAIL"));

        // verify
        Button verify = view.findViewById(R.id.verify);
        verify.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(verificationCode.getText().toString())) {
                requestVerify(verificationCode.getText().toString());
            } else {
                Toasto.show_toast(requireContext(), getString(R.string.verification_code_empty), 1, 2);
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request verify code
     */
    private void requestVerify(String code) {
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_TWO_FACTOR_AUTH,
                response -> {
                    switch (response) {
                        case "200":
                            consolePreferences.setToken(requireArguments().getString("TOKEN"));
                            consolePreferences.setUsername(requireArguments().getString("USERNAME"));
                            consolePreferences.setEmail(requireArguments().getString("EMAIL"));
                            startActivity(new Intent(requireContext(), ProjectsActivity.class));
                            requireActivity().finish();
                            break;
                        case "403":
                            Toasto.show_toast(requireContext(), getString(R.string.verification_code_expired), 1, 1);
                            break;
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 1, 1);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", consolePreferences.loadToken());
                params.put("verification_code", code);
                params.put("email", requireArguments().getString("EMAIL"));
                return params;
            }
        };

        Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
    }
}
