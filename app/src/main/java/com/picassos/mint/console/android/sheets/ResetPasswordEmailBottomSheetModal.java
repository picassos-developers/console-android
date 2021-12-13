package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.forgotPassword.EnterVerificationActivity;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordEmailBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    RequestDialog requestDialog;

    public ResetPasswordEmailBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reset_password_email_bottom_sheet_modal, container, false);

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // agree
        view.findViewById(R.id.agree).setOnClickListener(v -> {
            dismiss();
            Intent intent = new Intent(requireContext(), EnterVerificationActivity.class);
            intent.putExtra("email", requireArguments().getString("email"));
            startActivity(intent);
        });

        // email verification
        TextView emailVerification = view.findViewById(R.id.email_verification);
        emailVerification.setText(getString(R.string.we_sent_verification_code_to) + " " + requireArguments().getString("email"));

        // resend email
        view.findViewById(R.id.resend_again).setOnClickListener(v -> requestSendResetEmail());

        return view;
    }

    /**
     * request send email
     * with verification code
     */
    private void requestSendResetEmail() {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_SEND_RESET_EMAIL,
                response -> {
                    switch (response) {
                        case "200":
                            Toasto.show_toast(requireContext(), getString(R.string.email_resent), 1, 2);
                            break;
                        case "403":
                            Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 1, 1);
                            break;
                        case "404":
                            Toasto.show_toast(requireContext(), getString(R.string.no_users_found), 1, 1);
                            break;
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(requireContext(), error.getMessage(), 1, 1);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", requireArguments().getString("email"));
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

}
