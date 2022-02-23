package app.mynta.console.android.fragments;

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
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import app.mynta.console.android.R;
import app.mynta.console.android.activities.GetStartedActivity;
import app.mynta.console.android.activities.LoginActivity;
import app.mynta.console.android.activities.ProjectsActivity;
import app.mynta.console.android.activities.about.AboutConsoleActivity;
import app.mynta.console.android.activities.about.GenerateAuthLoginActivity;
import app.mynta.console.android.activities.about.MyTicketsActivity;
import app.mynta.console.android.activities.about.PurchasesActivity;
import app.mynta.console.android.models.viewModel.SharedViewModel;
import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.RequestDialog;

public class AccountFragment extends Fragment {
    SharedViewModel sharedViewModel;

    Bundle bundle;
    View view;

    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);

        // initialize bundle & shared preferences
        bundle = new Bundle();
        consolePreferences = new ConsolePreferences(requireContext());

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getRequestCode().observe(requireActivity(), item -> {

        });

        // switch project
        view.findViewById(R.id.switch_project).setOnClickListener(v -> startActivity(new Intent(requireContext(), ProjectsActivity.class)));

        // my tickets
        view.findViewById(R.id.my_tickets).setOnClickListener(v -> startActivity(new Intent(requireContext(), MyTicketsActivity.class)));

        // qr login
        view.findViewById(R.id.qr_login).setOnClickListener(v -> startActivity(new Intent(requireContext(), GenerateAuthLoginActivity.class)));

        // purchases & history
        view.findViewById(R.id.purchases).setOnClickListener(v -> startActivity(new Intent(requireContext(), PurchasesActivity.class)));

        // appearance
        view.findViewById(R.id.appearance).setOnClickListener(v -> {
            Dialog darkModeDialog = new Dialog(requireContext());

            darkModeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            darkModeDialog.setContentView(R.layout.dialog_appearance);

            // enable dialog cancel
            darkModeDialog.setCancelable(true);
            darkModeDialog.setOnCancelListener(dialog -> darkModeDialog.dismiss());

            // close dialog
            ImageView close = darkModeDialog.findViewById(R.id.dialog_close);
            close.setOnClickListener(v1 -> darkModeDialog.dismiss());

            // light mode
            darkModeDialog.findViewById(R.id.light_mode).setOnClickListener(v1 -> {
                consolePreferences.setDarkMode(1);
                Helper.restartContext(requireContext(), requireActivity());
            });

            // dark mode
            darkModeDialog.findViewById(R.id.dark_mode).setOnClickListener(v1 -> {
                consolePreferences.setDarkMode(2);
                Helper.restartContext(requireContext(), requireActivity());
            });

            // system default
            darkModeDialog.findViewById(R.id.system_default).setOnClickListener(v1 -> {
                consolePreferences.setDarkMode(3);
                Helper.restartContext(requireContext(), requireActivity());
            });

            if (darkModeDialog.getWindow() != null) {
                darkModeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                darkModeDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            }

            darkModeDialog.show();
        });

        // language
        view.findViewById(R.id.language).setOnClickListener(v -> chooseLanguage());

        // about app
        view.findViewById(R.id.about_console).setOnClickListener(view -> startActivity(new Intent(requireContext(), AboutConsoleActivity.class)));

        // sign out from account
        view.findViewById(R.id.logout).setOnClickListener(v -> {
            consolePreferences.setUsername("exception:error?username");
            consolePreferences.setEmail("exception:error?email");
            consolePreferences.setPackageName("exception:error?package_name");
            consolePreferences.setSecretAPIKey("exception:error?sak");
            consolePreferences.setToken("exception:error?token");

            startActivity(new Intent(requireContext(), GetStartedActivity.class));
            requireActivity().finishAffinity();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());


        // Refresh Layout
        SwipeRefreshLayout refresh = view.findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }
        });
    }

    /**
     * change app language
     */
    private void chooseLanguage() {
        Dialog designsDialog = new Dialog(requireContext());

        designsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        designsDialog.setContentView(R.layout.dialog_language);

        // enable dialog cancel
        designsDialog.setCancelable(true);
        designsDialog.setOnCancelListener(dialog -> designsDialog.dismiss());

        // close dialog
        designsDialog.findViewById(R.id.dialog_close).setOnClickListener(v -> designsDialog.dismiss());

        // english language
        designsDialog.findViewById(R.id.language_english).setOnClickListener(v -> {

        });

        // arabic language
        designsDialog.findViewById(R.id.language_arabic).setOnClickListener(v -> {

        });

        if (designsDialog.getWindow() != null) {
            designsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            designsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        designsDialog.show();
    }
}
