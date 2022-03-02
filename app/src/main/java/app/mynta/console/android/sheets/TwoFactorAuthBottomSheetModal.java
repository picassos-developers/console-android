package app.mynta.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import app.mynta.console.android.R;

import app.mynta.console.android.activities.projects.ProjectsActivity;
import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

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
        verificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                view.findViewById(R.id.verify).setEnabled(charSequence.length() >= 4);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // email verification
        TextView emailVerification = view.findViewById(R.id.email_verification);
        emailVerification.setText(getString(R.string.we_sent_verification_code_to) + " " + requireArguments().getString("EMAIL"));

        // verify
        view.findViewById(R.id.verify).setOnClickListener(v -> {
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
