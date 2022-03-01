package app.mynta.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import app.mynta.console.android.BuildConfig;
import app.mynta.console.android.activities.about.MyTicketsActivity;
import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.R;

import app.mynta.console.android.constants.API;
import app.mynta.console.android.entities.NotificationEntity;
import app.mynta.console.android.room.APP_DATABASE;
import app.mynta.console.android.room.DAO;
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    ConsolePreferences consolePreferences;
    private String deeplink = null;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("NEW_INTENT", "On new intent started");

        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d("GOOGLE_FIREBASE", "Extras received at onCreate: Key: " + key + " Value: " + value);
            }
            String title = extras.getString("title");
            String message = extras.getString("body");
            if (message != null && message.length() > 0) {
                getIntent().removeExtra("body");
                saveNotification(title, message);
                if (extras.getString("url") != null) {
                    deeplink = extras.getString("url");
                    getIntent().removeExtra("url");
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d("GOOGLE_FIREBASE", "Extras received at onCreate: Key: " + key + " Value: " + value);
            }
            String title = extras.getString("title");
            String message = extras.getString("body");
            if (message != null && message.length() > 0) {
                getIntent().removeExtra("body");
                saveNotification(title, message);
                if (extras.getString("url") != null) {
                    deeplink = extras.getString("url");
                    getIntent().removeExtra("url");
                }
            }
        }

        setContentView(R.layout.activity_splash);

        Uri data = intent.getData();
        if (data != null) {
            if (data.toString().equals("https://console.themintapp.com/tickets")) {
                startActivity(new Intent(SplashActivity.this, MyTicketsActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(SplashActivity.this, GetStartedActivity.class));
            finish();
        }
    }

    /**
     * request save notification into room db
     * @param title for notification title
     * @param message for notification message
     */
    private void saveNotification(String title, String message) {
        DAO dao = APP_DATABASE.requestDatabase(this).requestDAO();
        NotificationEntity notification = new NotificationEntity();
        notification.title = title;
        notification.content = message;
        notification.id = System.currentTimeMillis();
        notification.created_at = System.currentTimeMillis();
        notification.read = false;
        dao.requestInsertNotification(notification);
    }

    /**
     * request launch main instance and
     * skip splash screen activity
     */
    private void requestLaunchMainInstance() {
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_VERSION,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("app");
                        JSONObject version = root.getJSONObject("version");

                        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

                        // check if version is greater than the app version
                        // then request user to update the app from PlayStore
                        int appVersion = packageInfo.versionCode;

                        // check version
                        if (version.getInt("code") > appVersion && version.getInt("force_update") == 1) {
                            Dialog dialogUpdate = new Dialog(this);
                            dialogUpdate.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialogUpdate.setContentView(R.layout.dialog_update);

                            // enable dialog cancel
                            dialogUpdate.setCancelable(true);
                            dialogUpdate.setOnCancelListener(dialog -> {
                                dialogUpdate.dismiss();
                                finishAffinity();
                                System.exit(0);
                            });
                            dialogUpdate.setOnDismissListener(dialog -> {
                                dialogUpdate.dismiss();
                                finishAffinity();
                                System.exit(0);
                            });

                            // update app
                            dialogUpdate.findViewById(R.id.update_app).setOnClickListener(v -> {
                                Intent openGooglePlay = new Intent(Intent.ACTION_VIEW);
                                openGooglePlay.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));
                                openGooglePlay.setPackage("com.android.vending");
                                startActivity(openGooglePlay);
                            });

                            if (dialogUpdate.getWindow() != null) {
                                dialogUpdate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialogUpdate.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                            }

                            dialogUpdate.show();
                        } else {
                            if (deeplink != null) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(deeplink)));
                                finishAffinity();
                            } else {
                                startActivity(new Intent(SplashActivity.this, GetStartedActivity.class));
                                finish();
                            }
                        }
                    } catch (PackageManager.NameNotFoundException | JSONException e) {
                        e.printStackTrace();
                    }

                    findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
                }, error -> {
            findViewById(R.id.progress_bar).setVisibility(View.INVISIBLE);
            startActivity(new Intent(SplashActivity.this, GetStartedActivity.class));
            finish();
            Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("request", "version");
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}