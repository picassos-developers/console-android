package com.picassos.mint.console.android.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.EditProjectActivity;
import com.picassos.mint.console.android.activities.GiftActivity;
import com.picassos.mint.console.android.activities.MainActivity;
import com.picassos.mint.console.android.adapter.LoadingSpinnerAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.libraries.showcaseview.GuideView;
import com.picassos.mint.console.android.libraries.showcaseview.config.DismissType;
import com.picassos.mint.console.android.libraries.showcaseview.config.Gravity;
import com.picassos.mint.console.android.models.LoadingSpinners;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.EditEmailBottomSheetModal;
import com.picassos.mint.console.android.sheets.LaunchAppBottomSheetModal;
import com.picassos.mint.console.android.sheets.ManageAccountsBottomSheetModal;
import com.picassos.mint.console.android.sheets.ProvidersBottomSheetModal;
import com.picassos.mint.console.android.utils.AboutDialog;
import com.picassos.mint.console.android.utils.PromotionDialog;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    private static HomeFragment instance;

    View view;
    Bundle bundle;

    private RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;
    private Dialog spinnersDialog;

    // request codes
    private static final int REQUEST_EDIT_EMAIL_CODE = 1;
    private static final int REQUEST_SELECT_PROVIDER = 2;

    // providers
    private SwitchCompat wordpressProvider;
    private SwitchCompat webviewProvider;
    private SwitchCompat youtubeProvider;
    private SwitchCompat vimeoProvider;
    private SwitchCompat pinterestProvider;
    private SwitchCompat imgurProvider;
    private SwitchCompat facebookProvider;
    private SwitchCompat mapsProvider;

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

        requestConfiguration();

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

        /* providers start **/
        wordpressProvider = view.findViewById(R.id.wordpress_provider);
        webviewProvider = view.findViewById(R.id.webview_provider);
        youtubeProvider = view.findViewById(R.id.youtube_provider);
        vimeoProvider = view.findViewById(R.id.vimeo_provider);
        pinterestProvider = view.findViewById(R.id.pinterest_provider);
        facebookProvider = view.findViewById(R.id.facebook_provider);
        imgurProvider = view.findViewById(R.id.imgur_provider);
        mapsProvider = view.findViewById(R.id.maps_provider);

        webviewProvider.setOnCheckedChangeListener((compoundButton, b) -> requestUpdateProviders());
        wordpressProvider.setOnCheckedChangeListener((compoundButton, b) -> requestUpdateProviders());
        youtubeProvider.setOnCheckedChangeListener((compoundButton, b) -> requestUpdateProviders());
        vimeoProvider.setOnCheckedChangeListener((compoundButton, b) -> requestUpdateProviders());
        pinterestProvider.setOnCheckedChangeListener((compoundButton, b) -> requestUpdateProviders());
        facebookProvider.setOnCheckedChangeListener((compoundButton, b) -> requestUpdateProviders());
        imgurProvider.setOnCheckedChangeListener((compoundButton, b) -> requestUpdateProviders());
        mapsProvider.setOnCheckedChangeListener((compoundButton, b) -> requestUpdateProviders());
        /* providers end **/

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
    }

    /**
     * request configuration
     */
    @SuppressLint({"SetTexti18n", "NotifyDataSetChanged"})
    private void requestConfiguration() {
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
                        JSONObject providers = root.getJSONObject("providers");

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

                        // set providers
                        wordpressProvider.setChecked(getState(providers.getInt("wordpress_provider")));
                        webviewProvider.setChecked(getState(providers.getInt("webview_provider")));
                        youtubeProvider.setChecked(getState(providers.getInt("youtube_provider")));
                        vimeoProvider.setChecked(getState(providers.getInt("vimeo_provider")));
                        pinterestProvider.setChecked(getState(providers.getInt("pinterest_provider")));
                        facebookProvider.setChecked(getState(providers.getInt("facebook_provider")));
                        imgurProvider.setChecked(getState(providers.getInt("imgur_provider")));
                        mapsProvider.setChecked(getState(providers.getInt("maps_provider")));

                        // set preferences
                        screenSleep.setChecked(getStringState(configuration.getString("screen_sleep")));
                        fullScreen.setChecked(getStringState(configuration.getString("full_screen")));
                        forceFullscreen.setChecked(getStringState(configuration.getString("force_fullscreen")));
                        getStartedPage.setChecked(getStringState(configuration.getString("get_started_page")));
                        downloadViaWifi.setChecked(getStringState(configuration.getString("download_via_wifi")));

                        // set email address
                        TextView emailAddress = view.findViewById(R.id.email_address);
                        emailAddress.setText(configuration.getString("email_address"));

                        // default provider
                        ImageView defaultProviderIcon = view.findViewById(R.id.default_provider_icon);
                        switch (configuration.getString("default_provider")) {
                            case "wordpress":
                                defaultProviderIcon.setImageResource(R.drawable.icon_wordpress);
                                break;
                            case "webview":
                                defaultProviderIcon.setImageResource(R.drawable.icon_webview);
                                break;
                            case "youtube":
                                defaultProviderIcon.setImageResource(R.drawable.icon_youtube);
                                break;
                            case "vimeo":
                                defaultProviderIcon.setImageResource(R.drawable.icon_vimeo);
                                break;
                            case "facebook":
                                defaultProviderIcon.setImageResource(R.drawable.icon_facebook);
                                break;
                            case "pinterest":
                                defaultProviderIcon.setImageResource(R.drawable.icon_pinterest);
                                break;
                            case "maps":
                                defaultProviderIcon.setImageResource(R.drawable.icon_maps);
                                break;
                            case "imgur":
                                defaultProviderIcon.setImageResource(R.drawable.icon_imgur);
                                break;
                        }
                        TextView defaultProvider = view.findViewById(R.id.default_provider);
                        defaultProvider.setText(getString(R.string.default_provider) + ": " + configuration.getString("default_provider").substring(0, 1).toUpperCase() + configuration.getString("default_provider").substring(1));
                        // set provider
                        view.findViewById(R.id.default_provider_container).setOnClickListener(v -> {
                            ProvidersBottomSheetModal providersBottomSheetModal = new ProvidersBottomSheetModal();
                            providersBottomSheetModal.setArguments(bundle);
                            providersBottomSheetModal.setTargetFragment(HomeFragment.this, REQUEST_SELECT_PROVIDER);
                            providersBottomSheetModal.show(requireFragmentManager(), "TAG");
                        });

                        // loading spinner
                        TextView loadingSpinner = view.findViewById(R.id.loading_spinner);
                        loadingSpinner.setText(getString(R.string.loading_spinner) + ": " + configuration.getString("loading_spinner"));
                        // set progress loader
                        view.findViewById(R.id.loading_spinner_container).setOnClickListener(v -> {
                            spinnersDialog = new Dialog(requireContext());

                            spinnersDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            spinnersDialog.setContentView(R.layout.dialog_loading_spinners);

                            // enable dialog cancel
                            spinnersDialog.setCancelable(true);
                            spinnersDialog.setOnCancelListener(dialog -> spinnersDialog.dismiss());

                            // close dialog
                            ImageView dialogClose = spinnersDialog.findViewById(R.id.dialog_close);
                            dialogClose.setOnClickListener(v1 -> spinnersDialog.dismiss());

                            List<LoadingSpinners> spinnersList = new ArrayList<>();
                            RecyclerView stylesRecyclerview = spinnersDialog.findViewById(R.id.recycler_spinners);

                            LoadingSpinnerAdapter spinnerAdapter = new LoadingSpinnerAdapter(spinnersList, click -> requestUpdateSpinner(click.getStyle()));

                            stylesRecyclerview.setAdapter(spinnerAdapter);
                            stylesRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));

                            spinnersList.add(new LoadingSpinners("Disabled", "Disabled"));
                            spinnersList.add(new LoadingSpinners("Native", "Native"));
                            spinnersList.add(new LoadingSpinners("Shimmer", "Shimmer"));
                            spinnersList.add(new LoadingSpinners("ThreeBounce", "ThreeBounce"));
                            spinnersList.add(new LoadingSpinners("RotatingPlane", "RotatingPlane"));
                            spinnersList.add(new LoadingSpinners("DoubleBounce", "DoubleBounce"));
                            spinnersList.add(new LoadingSpinners("Wave", "Wave"));
                            spinnersList.add(new LoadingSpinners("WanderingCubes", "WanderingCubes"));
                            spinnersList.add(new LoadingSpinners("ChasingDots", "ChasingDots"));
                            spinnersList.add(new LoadingSpinners("Circle", "Circle"));
                            spinnersList.add(new LoadingSpinners("CubeGrid", "CubeGrid"));
                            spinnersList.add(new LoadingSpinners("FadingCircle", "FadingCircle"));
                            spinnersList.add(new LoadingSpinners("FoldingCube", "FoldingCube"));
                            spinnersList.add(new LoadingSpinners("RotatingCircle", "RotatingCircle"));
                            spinnerAdapter.notifyDataSetChanged();

                            if (spinnersDialog.getWindow() != null) {
                                spinnersDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                spinnersDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                            }

                            spinnersDialog.show();
                        });
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
     * request update providers
     */
    private void requestUpdateProviders() {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(requireContext(), getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_PROVIDERS,
                    response -> {
                        if (response.contains("exception:configuration?success")) {
                            requestDialog.dismiss();
                        }
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 1, 2);
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("webview", String.valueOf(setState(webviewProvider.isChecked())));
                    params.put("wordpress", String.valueOf(setState(wordpressProvider.isChecked())));
                    params.put("youtube", String.valueOf(setState(youtubeProvider.isChecked())));
                    params.put("vimeo", String.valueOf(setState(vimeoProvider.isChecked())));
                    params.put("facebook", String.valueOf(setState(facebookProvider.isChecked())));
                    params.put("pinterest", String.valueOf(setState(pinterestProvider.isChecked())));
                    params.put("imgur", String.valueOf(setState(imgurProvider.isChecked())));
                    params.put("maps", String.valueOf(setState(mapsProvider.isChecked())));
                    return params;
                }
            };

            Volley.newRequestQueue(requireContext()).add(request);
        }
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
     * request update spinner
     * @param spinner for style
     */
    private void requestUpdateSpinner(String spinner) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(requireContext(), getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_SPINNER,
                    response -> {
                        requestConfiguration();
                        spinnersDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(requireActivity().getApplicationContext(), getString(R.string.unknown_issue), 1, 1);
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("spinner", spinner);
                    return params;
                }
            };

            Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
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
     * @param value for int value 0 or 1
     * @return boolean
     */
    private boolean getState(int value) {
        return value == 1;
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
     * @return value in integer type
     */
    private int setState(boolean value) {
        if (value) {
            return 1;
        } else {
            return 0;
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_EMAIL_CODE || requestCode == REQUEST_SELECT_PROVIDER) {
            if (resultCode == Activity.RESULT_OK) {
                requestConfiguration();
            }
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