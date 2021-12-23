package com.picassos.mint.console.android.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.EditProjectActivity;
import com.picassos.mint.console.android.activities.GiftActivity;
import com.picassos.mint.console.android.activities.MainActivity;
import com.picassos.mint.console.android.activities.PushNotificationsActivity;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.libraries.showcaseview.GuideView;
import com.picassos.mint.console.android.libraries.showcaseview.config.DismissType;
import com.picassos.mint.console.android.libraries.showcaseview.config.Gravity;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.EditEmailBottomSheetModal;
import com.picassos.mint.console.android.sheets.LaunchAppBottomSheetModal;
import com.picassos.mint.console.android.sheets.ManageAccountsBottomSheetModal;
import com.picassos.mint.console.android.utils.AboutDialog;
import com.picassos.mint.console.android.utils.PromotionDialog;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    private static HomeFragment instance;

    View view;
    Bundle bundle;

    private RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    // request codes
    private static final int REQUEST_EDIT_EMAIL_CODE = 1;

    // preferences
    private SwitchCompat screenSleep;
    private SwitchCompat fullScreen;
    private SwitchCompat forceFullscreen;
    private SwitchCompat getStartedPage;
    private SwitchCompat downloadViaWifi;

    // fonts
    private Typeface title, content;

    // profile
    private TextView username_profile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        instance = this;

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());
        consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bundle = new Bundle();

        title = Typeface.createFromAsset(requireActivity().getAssets(), "fonts/poppins_bold.ttf");
        content = Typeface.createFromAsset(requireActivity().getAssets(), "fonts/poppins_regular.ttf");

        // check promotion
        requestPromotion();

        // open navigation
        view.findViewById(R.id.open_navigation).setOnClickListener(v -> ((MainActivity) requireActivity()).openNavigation());

        // profile
        username_profile = view.findViewById(R.id.username_profile);
        username_profile.setText(consolePreferences.loadUsername().substring(0, 1));
        // open profile
        view.findViewById(R.id.open_profile).setOnClickListener(v -> {
            AboutDialog aboutDialog = new AboutDialog(requireContext(), getActivity());
            aboutDialog.show();
        });
        view.findViewById(R.id.open_profile).setOnLongClickListener(v -> {
            ManageAccountsBottomSheetModal manageAccountsBottomSheetModal = new ManageAccountsBottomSheetModal();
            manageAccountsBottomSheetModal.show(getChildFragmentManager(), "TAG");
            return true;
        });

        // push notifications
        view.findViewById(R.id.push_notifications_container).setOnClickListener(v -> startActivity(new Intent(requireContext(), PushNotificationsActivity.class)));

        /* preferences start **/
        screenSleep = view.findViewById(R.id.screen_sleep);
        fullScreen = view.findViewById(R.id.fullscreen);
        forceFullscreen = view.findViewById(R.id.force_fullscreen);
        getStartedPage = view.findViewById(R.id.get_started);
        downloadViaWifi = view.findViewById(R.id.download_via_wifi);

        screenSleep.setOnCheckedChangeListener((compoundButton, b) -> requestUpdatePreferences());
        fullScreen.setOnCheckedChangeListener((compoundButton, b) -> requestUpdatePreferences());
        forceFullscreen.setOnCheckedChangeListener((compoundButton, b) -> requestUpdatePreferences());
        getStartedPage.setOnCheckedChangeListener((compoundButton, b) -> requestUpdatePreferences());
        downloadViaWifi.setOnCheckedChangeListener((compoundButton, b) -> requestUpdatePreferences());
        /* preferences end **/

        // Refresh Layout
        SwipeRefreshLayout refresh = view.findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestConfiguration();
        });

        // launch application
        view.findViewById(R.id.launch_app).setOnClickListener(v -> {
            if (consolePreferences.loadPackageName().equals("exception:error?package_name")) {
                startActivity(new Intent(requireContext(), EditProjectActivity.class));
            } else {
                try {
                    LaunchAppBottomSheetModal launchAppBottomSheetModal = new LaunchAppBottomSheetModal();
                    launchAppBottomSheetModal.show(requireFragmentManager(), "TAG");
                } catch (NullPointerException e) {
                    Toasto.show_toast(requireContext(), requireContext().getString(R.string.invalid_package_name), 1, 1);
                    startActivity(new Intent(requireContext(), EditProjectActivity.class));
                }
            }
        });

        // email address container
        view.findViewById(R.id.email_address_container).setOnClickListener(v -> {
            bundle.putString("request", "edit_email");
            EditEmailBottomSheetModal editEmailBottomSheetModal = new EditEmailBottomSheetModal();
            editEmailBottomSheetModal.setArguments(bundle);
            editEmailBottomSheetModal.setTargetFragment(HomeFragment.this, REQUEST_EDIT_EMAIL_CODE);
            editEmailBottomSheetModal.show(requireFragmentManager(), "TAG");
        });

        requestConfiguration();
    }

    /**
     * request configuration
     */
    @SuppressLint({"SetTexti18n", "NotifyDataSetChanged"})
    public void requestConfiguration() {
        view.findViewById(R.id.dashboard_container).setVisibility(View.VISIBLE);
        view.findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_CONFIGURATION,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("donut");
                        JSONObject statistics = root.getJSONObject("statistics");
                        JSONObject configuration = root.getJSONObject("configuration");

                        // customization card
                        if (root.getInt("review") == 0) {
                            view.findViewById(R.id.customization_card).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.customization_card).setOnClickListener(v -> startActivity(new Intent(requireContext(), GiftActivity.class)));
                        } else {
                            view.findViewById(R.id.customization_card).setVisibility(View.GONE);
                        }

                        // database size
                        TextView database_size = view.findViewById(R.id.database_size);
                        database_size.setText(statistics.getString("database_size").replace("B", ""));

                        // mint console visits
                        TextView visits = view.findViewById(R.id.visits);
                        visits.setText(statistics.getString("console_visits"));

                        // total users
                        TextView users = view.findViewById(R.id.total_users);
                        users.setText(statistics.getString("active_users"));

                        // total notifications
                        TextView notifications = view.findViewById(R.id.notifications);
                        notifications.setText(statistics.getString("notifications"));

                        // set preferences
                        screenSleep.setChecked(getStringState(configuration.getString("screen_sleep")));
                        fullScreen.setChecked(getStringState(configuration.getString("full_screen")));
                        forceFullscreen.setChecked(getStringState(configuration.getString("force_fullscreen")));
                        getStartedPage.setChecked(getStringState(configuration.getString("get_started_page")));
                        downloadViaWifi.setChecked(getStringState(configuration.getString("download_via_wifi")));

                        // set email address
                        TextView emailAddress = view.findViewById(R.id.email_address);
                        emailAddress.setText(configuration.getString("email_address"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            view.findViewById(R.id.dashboard_container).setVisibility(View.GONE);
            view.findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            view.findViewById(R.id.try_again).setOnClickListener(v -> requestConfiguration());
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", consolePreferences.loadToken());
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    /**
     * request update preferences
     */
    private void requestUpdatePreferences() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(requireContext(), getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_PREFERENCES,
                    response -> {
                        if (response.contains("exception:configuration?success")) {
                            requestDialog.dismiss();
                        }
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 1, 2);
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("screen_sleep", setStringState(screenSleep.isChecked()));
                    params.put("full_screen", setStringState(fullScreen.isChecked()));
                    params.put("download_via_wifi", setStringState(downloadViaWifi.isChecked()));
                    params.put("force_fullscreen", setStringState(forceFullscreen.isChecked()));
                    params.put("get_started_page", setStringState(getStartedPage.isChecked()));
                    return params;
                }
            };

            Volley.newRequestQueue(requireContext()).add(request);
        }
    }

    /**
     * request promotion
     */
    private void requestPromotion() {
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_PROMOTION,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("app");
                        JSONObject promotion = root.getJSONObject("promotion");

                        if (promotion.getInt("enabled") == 1) {
                            if (!promotion.getString("identifier").equals(consolePreferences.loadPromotionID())) {
                                PromotionDialog promotionDialog = new PromotionDialog(requireContext());
                                promotionDialog.show();
                                consolePreferences.setPromotionID(promotion.getString("identifier"));
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("PROMOTION", "Failed to load promotion request")) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("request", "promotion");
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    /**
     * get state
     * @param value for String
     * @return boolean
     */
    private boolean getStringState(String value) {
        return value.equals("true");
    }

    /**
     * set state
     * @param value for boolean true or false
     * @return value in String type
     */
    private String setStringState(boolean value) {
        if (value) {
            return "true";
        } else {
            return "false";
        }
    }

    /* guide start **/
    public void requestGuideSix() {
        new GuideView.Builder(requireContext())
                .setTitle(getString(R.string.guide_six_title))
                .setContentText(getString(R.string.guide_six_description))
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(username_profile)
                .setTitleTypeFace(title)
                .setContentTypeFace(content)
                .setContentTextSize(12)
                .setTitleTextSize(13)
                .setGuideListener(view -> requestGuideSeven())
                .build()
                .show();
    }

    private void requestGuideSeven() {
        new GuideView.Builder(requireContext())
                .setTitle(getString(R.string.guide_seven_title))
                .setContentText(getString(R.string.guide_seven_description))
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(view.findViewById(R.id.launch_app))
                .setTitleTypeFace(title)
                .setContentTypeFace(content)
                .setContentTextSize(12)
                .setTitleTextSize(13)
                .setGuideListener(view -> requestGuideEight())
                .build()
                .show();
    }

    private void requestGuideEight() {
        new GuideView.Builder(requireContext())
                .setTitle(getString(R.string.guide_eight_title))
                .setContentText(getString(R.string.guide_eight_description))
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(view.findViewById(R.id.open_navigation))
                .setTitleTypeFace(title)
                .setContentTypeFace(content)
                .setContentTextSize(12)
                .setTitleTextSize(13)
                .setGuideListener(view -> consolePreferences.setGuide(true))
                .build()
                .show();
    }
    /* guide end **/

    public static HomeFragment getInstance() {
        return instance;
    }

    public void switchAccount() {
        ManageAccountsBottomSheetModal manageAccountsBottomSheetModal = new ManageAccountsBottomSheetModal();
        manageAccountsBottomSheetModal.show(getChildFragmentManager(), "TAG");
    }
}