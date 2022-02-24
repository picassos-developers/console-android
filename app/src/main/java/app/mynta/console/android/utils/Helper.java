package app.mynta.console.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;

import app.mynta.console.android.BuildConfig;
import app.mynta.console.android.activities.MainActivity;
import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.R;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

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
     * restart app context
     * @param context for application
     * @param activity for activity
     */
    public static void restartContext(Context context, Activity activity) {
        context.startActivity(new Intent(context, MainActivity.class));
        activity.finish();
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

    /**
     * set gradient background for view
     * @param view for view
     * @param color_start for color gradient start
     * @param color_end for color gradient end
     */
    public static void setGradientBackground(View view, String color_start, String color_end) {
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] {Color.parseColor(color_start), Color.parseColor(color_end)});
        gradientDrawable.setCornerRadius(0f);
        view.setBackground(gradientDrawable);
    }

    /**
     *
     * @param context for context
     * @param image_name for name of image in string format
     * @return drawable
     */
    public static int getDrawableByName(Context context, String image_name) {
        return context.getResources().getIdentifier(image_name, "drawable", context.getPackageName());
    }

    /**
     * convert text to packagename format
     * @param package_name for package name
     * @return converted app package name
     */
    public static String toPackagenameFormat(String package_name) {
        String nonWhiteSpace = Pattern.compile("[\\s]").matcher(package_name).replaceAll("_");
        String normalized = Normalizer.normalize(nonWhiteSpace, Normalizer.Form.NFD);
        String slug = Pattern.compile("[^\\w-]").matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH).replaceAll("-", "_");
    }

    /**
     * check if package name is valid
     * @param package_name for package name
     * @return check if package name is valid
     */
    public static boolean validatePackagename(String package_name) {
        return package_name.matches("^([A-Za-z]{1}[A-Za-z\\d_]*\\.)+[A-Za-z][A-Za-z\\d_]*$");
    }

    /**
     * check if package name is valid
     * @param purchasecode for envato purchase code
     * @return check if code is valid
     */
    public static boolean validatePurchaseCode(String purchasecode) {
        return purchasecode.matches("^([a-f0-9]{8})-(([a-f0-9]{4})-){3}([a-f0-9]{12})*$");
    }

    /**
     * check if email address is valid
     * @param email for email
     * @return check if email is valid
     */
    public static boolean validateEmailAddress(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * get json from assets folder
     * @param context for content
     * @param path for path in assets
     * @return json in string format
     */
    public static String getJsonFromAssets(Context context, String path) {
        String json;
        try {
            InputStream is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static void setLocale(Context context, String language) {
        Locale myLocale = new Locale(language);
        Resources resources = context.getResources();
        DisplayMetrics display = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = myLocale;
        resources.updateConfiguration(configuration, display);

    }
}
