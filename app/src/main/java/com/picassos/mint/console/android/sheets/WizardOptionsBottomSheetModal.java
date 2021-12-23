package com.picassos.mint.console.android.sheets;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.EditWizardActivity;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.RequestDialog;

import java.util.HashMap;
import java.util.Map;

public class WizardOptionsBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    public interface OnDeleteListener {
        void onDeleteListener(boolean delete);
    }

    OnDeleteListener onDeleteListener;

    public interface OnEditListener {
        void onEditListener(boolean edit);
    }

    OnEditListener onEditListener;

    public WizardOptionsBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onDeleteListener = (OnDeleteListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onDeleteListener");
        }

        try {
            onEditListener = (OnEditListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + "mus implement onEditListener");
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.wizard_options_bottom_sheet_modal, container, false);

        consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

        // get wizard details
        int wizardId = requireArguments().getInt("wizard_id");
        String wizardTitle = requireArguments().getString("wizard_title");
        String wizardDescription = requireArguments().getString("wizard_description");
        String wizardThumbnail = requireArguments().getString("wizard_thumbnail");

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // edit wizard
        view.findViewById(R.id.edit_wizard).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditWizardActivity.class);
            intent.putExtra("wizard_id", wizardId);
            intent.putExtra("wizard_title", wizardTitle);
            intent.putExtra("wizard_description", wizardDescription);
            intent.putExtra("wizard_thumbnail", wizardThumbnail);
            startActivityForResult.launch(intent);
        });

        // delete wizard
        view.findViewById(R.id.delete_wizard).setOnClickListener(v -> {
            requestDeleteWizard(wizardTitle, wizardDescription, wizardThumbnail);
            dismiss();
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request delete wizard
     *
     * @param title for wizard title
     * @param description for wizard description
     * @param thumbnail for wizard thumbnail
     */
    private void requestDeleteWizard(String title, String description, String thumbnail) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toast.makeText(requireContext(), getString(R.string.demo_project), Toast.LENGTH_SHORT).show();
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_DELETE_WIZARD,
                    response -> {
                        if (response.contains("delete:callback?success")) {
                            onDeleteListener.onDeleteListener(true);
                            dismiss();
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.unknown_issue), Toast.LENGTH_SHORT).show();
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toast.makeText(requireContext(), getString(R.string.unknown_issue), Toast.LENGTH_SHORT).show();
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

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result != null && result.getResultCode() == RESULT_OK) {
                onEditListener.onEditListener(true);
                dismiss();
            }
        }
    });
}
