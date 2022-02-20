package app.mynta.console.android.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;

public class IntentHandler {

    /**
     * handle intent activity
     * @param context for context
     * @param url for url
     */
    public static boolean handleIntent(Context context, String url) {
        if (url != null) {
            if (url.startsWith("mailto:")) {
                MailTo mailTo = MailTo.parse(url);
                handleEmail(context, mailTo.getTo(), mailTo.getSubject(), mailTo.getBody());
                return true;
            } else if (url.startsWith("tel:")) {
                handleCall(context, url);
                return true;
            } else if (url.startsWith("sms:") || url.startsWith("smsto:")
                    || url.startsWith("mms:") || url.startsWith("mmsto:")) {
                handleMessages(context, url);
                return true;
            } else if (url.startsWith("geo:")) {
                handleGeoMap(context, url);
                return true;
            } else if (url.startsWith("market://")) {
                handleWeb(context, url);
                return true;
            } else if (url.startsWith("fb://")) {
                handleWeb(context, url);
                return true;
            } else if (url.startsWith("twitter://")) {
                handleWeb(context, url);
                return true;
            } else if (url.startsWith("whatsapp://")) {
                handleWeb(context, url);
                return true;
            } else if (url.startsWith("vnd:youtube")) {
                handleWeb(context, url);
                return true;
            } else if (url.startsWith("https://") || url.startsWith("http://")) {
                handleWeb(context, url);
                return true;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * handle emails, email-to
     * @param context for context
     * @param email for email
     * @param subject for subject
     * @param text for text
     */
    public static void handleEmail(Context context, String email, String subject, String text) {
        try {
            String uri = "mailto:" + email;
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, text);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // can't start activity
            e.printStackTrace();
        }
    }

    /**
     * handle calls via dial
     * @param context for context
     * @param url for url
     */
    public static void handleCall(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // can't start activity
            e.printStackTrace();
        }
    }

    /**
     * handle messages
     * @param context for context
     * @param url for url
     */
    public static void handleMessages(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // can't start activity
            e.printStackTrace();
        }
    }

    /**
     * handle web browser
     * @param context for context
     * @param url for url
     */
    public static void handleWeb(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // can't start activity
            e.printStackTrace();
        }
    }

    /**
     * handle default maps app
     * @param context for context
     * @param url for url
     */
    public static void handleGeoMap(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // can't start activity
            e.printStackTrace();
        }
    }
}
