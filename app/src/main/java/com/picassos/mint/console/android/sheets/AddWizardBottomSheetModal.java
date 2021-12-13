package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class AddWizardBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    public interface OnAddListener {
        void onAddListener(int requestCode);
    }

    OnAddListener onAddListener;

    public static final int REQUEST_ADD_WIZARD_CODE = 1;

    public AddWizardBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onAddListener = (OnAddListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onAddListener");
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_wizard_bottom_sheet_modal, container, false);

        consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // wizard fields
        EditText wizardTitle = view.findViewById(R.id.wizard_title);
        EditText wizardDescription = view.findViewById(R.id.wizard_description);
        EditText wizardThumbnail = view.findViewById(R.id.wizard_thumbnail);

        // add wizard
        Button addWizard = view.findViewById(R.id.add_wizard_button);
        addWizard.setOnClickListener(v -> {
            // Validate wizard data
            if (!TextUtils.isEmpty(wizardTitle.getText().toString())
                    && !TextUtils.isEmpty(wizardDescription.getText().toString())
                    && !TextUtils.isEmpty(wizardThumbnail.getText().toString())) {
                requestAddWizard(wizardTitle.getText().toString(), wizardDescription.getText().toString(), wizardThumbnail.getText().toString());
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void requestAddWizard(String title, String description, String thumbnail) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(requireContext(), getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_ADD_WIZARD,
                    response -> {
                        if (response.contains("wizard:callback?success")) {
                            onAddListener.onAddListener(REQUEST_ADD_WIZARD_CODE);
                            dismiss();
                        } else {
                            Toasto.show_toast(requireContext(), "exception:error?" + response, 1, 1);
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(requireActivity().getApplicationContext(), "exception:error?" + error.getMessage(), 1, 1);
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("title", title);
                    params.put("description", description);
                    params.put("thumbnail", thumbnail);
                    return params;
                }
            };

            Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
        }
    }
}
