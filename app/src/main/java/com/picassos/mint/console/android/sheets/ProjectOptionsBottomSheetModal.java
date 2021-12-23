package com.picassos.mint.console.android.sheets;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.EditProjectActivity;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.Toasto;

public class ProjectOptionsBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    private ConsolePreferences consolePreferences;

    public interface OnEditListener {
        void onEditListener(boolean edited);
    }

    OnEditListener onEditListener;

    public ProjectOptionsBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onEditListener = (OnEditListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onEditListener");
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.project_options_bottom_sheet_modal, container, false);

        consolePreferences = new ConsolePreferences(requireContext());

        // edit project
        view.findViewById(R.id.project_options).setOnClickListener(v -> startActivityForResult.launch(new Intent(requireContext(), EditProjectActivity.class)));

        // manage credentials
        view.findViewById(R.id.manage_credentials).setOnClickListener(v -> {
            Dialog manageCredentialsDialog = new Dialog(requireContext());
            manageCredentialsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            manageCredentialsDialog.setContentView(R.layout.dialog_manage_credentials);

            // enable dialog cancel
            manageCredentialsDialog.setCancelable(true);
            manageCredentialsDialog.setOnCancelListener(dialog -> manageCredentialsDialog.dismiss());

            // close dialog
            ImageView dialogClose = manageCredentialsDialog.findViewById(R.id.dialog_close);
            dialogClose.setOnClickListener(close -> manageCredentialsDialog.dismiss());

            // copy credentials
            manageCredentialsDialog.findViewById(R.id.copy_api_key).setOnClickListener(v2 -> {
                if (consolePreferences.loadSecretAPIKey().equals("demo")) {
                    Toasto.show_toast(requireContext(), getString(R.string.demo_project), 1, 0);
                } else {
                    ClipboardManager clipboardManager = (ClipboardManager) requireContext().getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Secret API Key", consolePreferences.loadSecretAPIKey());
                    clipboardManager.setPrimaryClip(clipData);

                }
                Toasto.show_toast(requireContext(), getString(R.string.sak_copied), 1, 0);
            });

            if (manageCredentialsDialog.getWindow() != null) {
                manageCredentialsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                manageCredentialsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            }

            manageCredentialsDialog.show();
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result != null && result.getResultCode() == Activity.RESULT_OK) {
            onEditListener.onEditListener(true);
            dismiss();
        }
    });
}
