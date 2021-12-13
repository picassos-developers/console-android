package com.picassos.mint.console.android.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;

import com.picassos.mint.console.android.BuildConfig;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {
    /**
     * a method that enables dark
     * mode when option is toggled
     * @param context for context
     */
    public static void darkMode(Context context) {
        ConsolePreferences consolePreferences;
        consolePreferences = new ConsolePreferences(context);
        // check if night mode @dark_mode is
        // enabled by user.
        if (consolePreferences.loadDarkMode() == 1) {
            context.setTheme(R.style.AppTheme);
        } else if (consolePreferences.loadDarkMode() == 2) {
            context.setTheme(R.style.DarkTheme);
        } else if (consolePreferences.loadDarkMode() == 3) {
            int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    context.setTheme(R.style.DarkTheme);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    context.setTheme(R.style.AppTheme);
                    break;
            }
        }
    }

    /**
     * use @get_formatted_date to format date
     * @param date_time for date time
     * @return formatted date
     */
    public static String getFormattedDate(Long date_time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yy hh:mm");
        return newFormat.format(new Date(date_time));
    }
    public static String getFormattedDateString(String date_time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(date_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return "";
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat convertDateFormat = new SimpleDateFormat("dd MMM yy, hh:mm");
        return convertDateFormat.format(date);
    }

    /**
     * check application version and
     * check for updates
     * @param version for version code
     */
    public static void checkVersion(Context context, int version) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            // check if version is greater than the app version
            // then request user to update the app from PlayStore
            int appVersion = packageInfo.versionCode;

            // check version
            if (version > appVersion) {
                Intent openGooglePlay = new Intent(Intent.ACTION_VIEW);
                openGooglePlay.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));
                openGooglePlay.setPackage("com.android.vending");
                context.startActivity(openGooglePlay);
            } else {
                Toasto.show_toast(context, context.getString(R.string.using_latest_update), 1, 0);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
