package app.mynta.console.android.sheets;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import app.mynta.console.android.R;
import app.mynta.console.android.activities.LoginActivity;
import app.mynta.console.android.activities.MainActivity;
import app.mynta.console.android.activities.about.AboutConsoleActivity;
import app.mynta.console.android.models.viewModel.SharedViewModel;
import app.mynta.console.android.sharedPreferences.ConsolePreferences;

public class AccountSettingsBottomSheetModal extends BottomSheetDialogFragment {
    SharedViewModel sharedViewModel;

    View view;
    private ConsolePreferences consolePreferences;

    public AccountSettingsBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.account_settings_bottom_sheet_modal, container, false);

        consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

        // language
        view.findViewById(R.id.language).setOnClickListener(v -> {

        });

        // appearance
        view.findViewById(R.id.appearance).setOnClickListener(v -> {
            Dialog darkModeDialog = new Dialog(getContext());

            darkModeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            darkModeDialog.setContentView(R.layout.dialog_appearance);

            // enable dialog cancel
            darkModeDialog.setCancelable(true);
            darkModeDialog.setOnCancelListener(dialog -> darkModeDialog.dismiss());

            // close dialog
            darkModeDialog.findViewById(R.id.dialog_close).setOnClickListener(v1 -> darkModeDialog.dismiss());

            // light mode
            darkModeDialog.findViewById(R.id.light_mode).setOnClickListener(v1 -> {
                consolePreferences.setDarkMode(1);
                restartContext();
            });

            // dark mode
            darkModeDialog.findViewById(R.id.dark_mode).setOnClickListener(v1 -> {
                consolePreferences.setDarkMode(2);
                restartContext();
            });

            // system default
            darkModeDialog.findViewById(R.id.system_default).setOnClickListener(v1 -> {
                consolePreferences.setDarkMode(3);
                restartContext();
            });

            if (darkModeDialog.getWindow() != null) {
                darkModeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                darkModeDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            }

            darkModeDialog.show();
        });

        // about app
        view.findViewById(R.id.about_console).setOnClickListener(v -> startActivity(new Intent(requireContext(), AboutConsoleActivity.class)));

        // sign out from account
        view.findViewById(R.id.logout).setOnClickListener(v -> {
            consolePreferences.setUsername("exception:error?username");
            consolePreferences.setEmail("exception:error?email");
            consolePreferences.setPackageName("exception:error?package_name");
            consolePreferences.setSecretAPIKey("exception:error?sak");
            consolePreferences.setToken("exception:error?token");

            requireContext().startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    private void restartContext() {
        requireContext().startActivity(new Intent(getContext(), MainActivity.class));
        requireActivity().finishAffinity();
    }
}
