package app.mynta.console.android.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import app.mynta.console.android.activities.providers.FacebookActivity;
import app.mynta.console.android.activities.providers.ImgurActivity;
import app.mynta.console.android.activities.providers.MapsActivity;
import app.mynta.console.android.activities.providers.PinterestActivity;
import app.mynta.console.android.activities.providers.VimeoActivity;
import app.mynta.console.android.activities.providers.WebviewActivity;
import app.mynta.console.android.activities.providers.WordpressActivity;
import app.mynta.console.android.activities.providers.YoutubeActivity;
import app.mynta.console.android.sharedPreferences.ConsolePreferences;

import app.mynta.console.android.R;
import app.mynta.console.android.adapter.NavigationsAdapter;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.constants.RequestCodes;
import app.mynta.console.android.models.Navigations;
import app.mynta.console.android.models.viewModel.SharedViewModel;
import app.mynta.console.android.sheets.ChooseProviderBottomSheetModal;
import app.mynta.console.android.sheets.ChooseDefaultNavigationBottomSheetModal;
import app.mynta.console.android.utils.AboutDialog;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationFragment extends Fragment {
    SharedViewModel sharedViewModel;

    Bundle bundle;
    View view;

    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    // navigations
    private final List<Navigations> navigationsList = new ArrayList<>();
    private NavigationsAdapter navigationsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_navigation, container, false);

        // initialize bundle & shared preferences
        bundle = new Bundle();
        consolePreferences = new ConsolePreferences(requireContext());

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getRequestCode().observe(requireActivity(), item -> {
            if (item == RequestCodes.REQUEST_UPDATE_NAVIGATIONS) {
                requestNavigations();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // username profile
        TextView usernameProfile = view.findViewById(R.id.username_profile);
        usernameProfile.setText(consolePreferences.loadUsername().substring(0, 1));
        // open profile
        view.findViewById(R.id.open_profile).setOnClickListener(v -> {
            AboutDialog aboutDialog = new AboutDialog(requireContext(), getActivity());
            aboutDialog.show();
        });

        // Initialize navigations recyclerview
        RecyclerView navigationsRecyclerview = view.findViewById(R.id.recycler_providers);

        // providers adapter
        navigationsAdapter = new NavigationsAdapter(navigationsList, click -> {
            Intent intent = new Intent();
            intent.putExtra("request", "update");
            intent.putExtra("identifier", click.getIdentifier());
            intent.putExtra("label", click.getLabel());
            intent.putExtra("type", click.getType());

            switch (click.getType()) {
                case "webview":
                    intent.setClass(requireContext(), WebviewActivity.class);
                    startActivityForResult.launch(intent);
                    break;
                case "wordpress":
                    intent.setClass(requireContext(), WordpressActivity.class);
                    startActivityForResult.launch(intent);
                    break;
                case "youtube":
                    intent.setClass(requireContext(), YoutubeActivity.class);
                    startActivityForResult.launch(intent);
                    break;
                case "vimeo":
                    intent.setClass(requireContext(), VimeoActivity.class);
                    startActivityForResult.launch(intent);
                    break;
                case "pinterest":
                    intent.setClass(requireContext(), PinterestActivity.class);
                    startActivityForResult.launch(intent);
                    break;
                case "facebook":
                    intent.setClass(requireContext(), FacebookActivity.class);
                    startActivityForResult.launch(intent);
                    break;
                case "imgur":
                    intent.setClass(requireContext(), ImgurActivity.class);
                    startActivityForResult.launch(intent);
                    break;
                case "google_maps":
                    intent.setClass(requireContext(), MapsActivity.class);
                    startActivityForResult.launch(intent);
                    break;
            }
        }, state -> {
            int currentState = -1; if (state.getEnabled() == 1) { currentState = 0; } else if (state.getEnabled() == 0) { currentState = 1; }
            requestUpdateNavigation(state.getIdentifier(), currentState);
        });

        navigationsRecyclerview.setAdapter(navigationsAdapter);
        navigationsRecyclerview.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // request navigations
        requestNavigations();

        // add navigation
        view.findViewById(R.id.add_navigation).setOnClickListener(v -> {
            ChooseProviderBottomSheetModal chooseProviderBottomSheetModal = new ChooseProviderBottomSheetModal();
            chooseProviderBottomSheetModal.show(getChildFragmentManager(), "TAG");
        });

        // Refresh Layout
        SwipeRefreshLayout refresh = view.findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestNavigations();
        });
    }

    /**
     * request navigations
     */
    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void requestNavigations() {
        view.findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        // clear, notify data changed
        navigationsList.clear();
        navigationsAdapter.notifyDataSetChanged();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_PROVIDER_MENUS,
                response -> {
                    Log.v("TEST", response);

                    try {
                        JSONObject obj = new JSONObject(response);

                        JSONObject root = obj.getJSONObject("navigations");
                        JSONArray array = root.getJSONArray("data");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Navigations menus = new Navigations(object.getInt("id"), object.getInt("identifier"), object.getInt("enabled"), object.getString("type"),  object.getString("label"), object.getString("icon"));
                            navigationsList.add(menus);
                            navigationsAdapter.notifyDataSetChanged();
                        }

                        // default provider
                        JSONObject default_provider = root.getJSONObject("default_provider");

                        ImageView defaultProviderIcon = view.findViewById(R.id.default_provider_icon);
                        switch (default_provider.getString("type")) {
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
                        defaultProvider.setText(getString(R.string.default_provider) + ": " + default_provider.getString("label").substring(0, 1).toUpperCase() + default_provider.getString("label").substring(1));
                        // set provider
                        view.findViewById(R.id.default_provider_container).setOnClickListener(v -> {
                            try {
                                bundle.putInt("identifier", default_provider.getInt("identifier"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            ChooseDefaultNavigationBottomSheetModal chooseDefaultNavigationBottomSheetModal = new ChooseDefaultNavigationBottomSheetModal();
                            chooseDefaultNavigationBottomSheetModal.setArguments(bundle);
                            chooseDefaultNavigationBottomSheetModal.show(getChildFragmentManager(), "TAG");
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
                requestDialog.dismiss();
                view.findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
                view.findViewById(R.id.try_again).setOnClickListener(v -> requestNavigations());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                return params;
            }
        };

        Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
    }

    /**
     * request update navigation
     */
    private void requestUpdateNavigation(int id, int state) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(requireContext(), getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_PROVIDERS,
                    response -> {
                        if (response.equals("200")) {
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
                    params.put("identifier", String.valueOf(id));
                    params.put("state", String.valueOf(state));
                    return params;
                }
            };

            Volley.newRequestQueue(requireContext()).add(request);
        }
    }

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result != null && result.getResultCode() == RESULT_OK) {
            requestNavigations();
        }
    });
}
