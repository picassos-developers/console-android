package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
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
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class RedeemCodeBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    ConsolePreferences consolePreferences;
    RequestDialog requestDialog;

    public interface OnRedeemedListener {
        void onRedeemedListener(Boolean isRedeemed);
    }

    OnRedeemedListener onRedeemedListener;

    public RedeemCodeBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onRedeemedListener = (OnRedeemedListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onRedeemedListener");
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.redeem_code_bottom_sheet_modal, container, false);

        // console preferences
        consolePreferences = new ConsolePreferences(requireContext());

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // code
        EditText redeemCode = view.findViewById(R.id.redeem_code);
        redeemCode.addTextChangedListener(onCodeValueChanged);

        // agreement notice
        TextView agreementNotice = view.findViewById(R.id.agreement_notice);
        agreementNotice.setMovementMethod(LinkMovementMethod.getInstance());

        // redeem code
        view.findViewById(R.id.redeem).setOnClickListener(v -> {
            if (!TextUtils.isEmpty(redeemCode.getText().toString())) {
                requestRedeem(redeemCode.getText().toString());
            } else {
                Toasto.show_toast(requireContext(), getString(R.string.redeem_code_empty), 1, 2);
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request redeem code
     * @param code for redeem code
     */
    private void requestRedeem(String code) {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_REDEEM_PROMO_CODE,
                response -> {
                    switch (response) {
                        case "200":
                            onRedeemedListener.onRedeemedListener(true);
                            view.findViewById(R.id.response).setVisibility(View.VISIBLE);
                            new Handler().postDelayed(this::dismiss, 2000);
                            break;
                        case "403":
                            Toasto.show_toast(requireContext(), getString(R.string.code_redeemed), 1, 2);
                            break;
                        case "404":
                            Toasto.show_toast(requireContext(), getString(R.string.promo_code_not_work), 1, 2);
                            break;
                        case "0":
                            Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 1, 1);
                            break;
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 1, 1);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", consolePreferences.loadToken());
                params.put("code", code);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    /**
     * on code field value change, enable or
     * disable redeem button
     */
    private final TextWatcher onCodeValueChanged = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @SuppressLint("SetTextI18n")
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            view.findViewById(R.id.redeem).setEnabled(s.length() != 0);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}

