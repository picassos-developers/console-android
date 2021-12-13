package com.picassos.mint.console.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.picassos.mint.console.android.Config;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.MainActivity;
import com.picassos.mint.console.android.activities.ProjectsActivity;
import com.picassos.mint.console.android.activities.about.PurchasesActivity;
import com.picassos.mint.console.android.activities.about.GenerateAuthLoginActivity;
import com.picassos.mint.console.android.activities.LoginActivity;
import com.picassos.mint.console.android.activities.about.AboutConsoleActivity;
import com.picassos.mint.console.android.activities.about.ManageAccountActivity;
import com.picassos.mint.console.android.activities.about.MyTicketsActivity;
import com.picassos.mint.console.android.fragments.HomeFragment;
import com.picassos.mint.console.android.room.APP_DATABASE;
import com.picassos.mint.console.android.room.DAO;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.ManageAccountsBottomSheetModal;

public class AboutDialog extends Dialog {
    private final Activity activity;
    public AboutDialog(@NonNull Context context, Activity activity) {
        super(context);
        this.activity = activity;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConsolePreferences consolePreferences = new ConsolePreferences(getContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_about);

        // set cancelable
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        // dialog close
        ImageView dialogClose = findViewById(R.id.dialog_close);
        dialogClose.setOnClickListener(v -> dismiss());

        // username icon
        TextView usernameIcon = findViewById(R.id.username_profile);
        usernameIcon.setText(consolePreferences.loadUsername().substring(0, 1).toUpperCase());
        
        // account username
        TextView username = findViewById(R.id.username);
        username.setText(consolePreferences.loadUsername().substring(0, 1).toUpperCase() + consolePreferences.loadUsername().substring(1));

        // manage account
        findViewById(R.id.manage_account).setOnClickListener(v -> getContext().startActivity(new Intent(getContext(), ManageAccountActivity.class)));
        // switch account
        findViewById(R.id.switch_account).setOnClickListener(v -> {
            HomeFragment.getInstance().switchAccount();
        });

        // switch project
        RelativeLayout switchProject = findViewById(R.id.switch_project);
        switchProject.setOnClickListener(v -> getContext().startActivity(new Intent(getContext(), ProjectsActivity.class)));

        // copy token
        findViewById(R.id.copy_token).setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData data = ClipData.newPlainText("Customer Token: ", consolePreferences.loadToken());
            clipboardManager.setPrimaryClip(data);
            Toasto.show_toast(getContext(), getContext().getString(R.string.token_copied), 0, 0);
        });

        // qr login
        findViewById(R.id.qr_login).setOnClickListener(v -> getContext().startActivity(new Intent(getContext(), GenerateAuthLoginActivity.class)));

        // my tickets
        findViewById(R.id.my_tickets).setOnClickListener(v -> getContext().startActivity(new Intent(getContext(), MyTicketsActivity.class)));

        // purchases & history
        findViewById(R.id.purchases).setOnClickListener(v -> getContext().startActivity(new Intent(getContext(), PurchasesActivity.class)));

        // appearance
        findViewById(R.id.appearance).setOnClickListener(v -> {
            Dialog darkModeDialog = new Dialog(getContext());

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

        // sign out from account
        findViewById(R.id.logout).setOnClickListener(v -> {
            DAO dao = APP_DATABASE.requestDatabase(getContext()).requestDAO();
            dao.requestDeleteAccount(consolePreferences.loadToken());

            consolePreferences.setUsername("exception:error?username");
            consolePreferences.setEmail("exception:error?email");
            consolePreferences.setPackageName("exception:error?package_name");
            consolePreferences.setSecretAPIKey("exception:error?sak");
            consolePreferences.setToken("exception:error?token");

            getContext().startActivity(new Intent(getContext(), LoginActivity.class));
            activity.finish();
        });

        // privacy policy
        findViewById(R.id.privacy_policy).setOnClickListener(v -> IntentHandler.handleWeb(getContext(), Config.PRIVACY_POLICY_URL));

        // terms of use
        findViewById(R.id.terms_of_use).setOnClickListener(v -> IntentHandler.handleWeb(getContext(), Config.TERMS_OF_USE_URL));
        
        // about mint console
        findViewById(R.id.about_console).setOnClickListener(view -> getContext().startActivity(new Intent(getContext(), AboutConsoleActivity.class)));

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            Window window = getWindow();
            WindowManager.LayoutParams WLP = window.getAttributes();
            WLP.gravity = Gravity.CENTER;
            window.setAttributes(WLP);
        }
    }

    private void restartContext() {
        getContext().startActivity(new Intent(getContext(), MainActivity.class));
        activity.finish();
    }
}
