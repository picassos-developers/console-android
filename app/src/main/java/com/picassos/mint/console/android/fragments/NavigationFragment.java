package com.picassos.mint.console.android.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.adapter.NavigationsAdapter;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.models.Navigations;
import com.picassos.mint.console.android.sheets.AddNavigationBottomSheetModal;
import com.picassos.mint.console.android.sheets.ManageAccountsBottomSheetModal;
import com.picassos.mint.console.android.sheets.NavigationOptionsBottomSheetModal;
import com.picassos.mint.console.android.utils.AboutDialog;
import com.picassos.mint.console.android.utils.IntentHandler;
import com.picassos.mint.console.android.utils.RequestDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationFragment extends Fragment {

    View view;

    // Bundle
    private Bundle bundle;

    // REQUEST CODE
    public static final int REQUEST_NAVIGATION_ACTION = 1;

    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    // Navigations
    private final List<Navigations> navigationsList = new ArrayList<>();
    private NavigationsAdapter navigationsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_navigation, container, false);

        // initialize bundle
        // & console shared preferences
        bundle = new Bundle();
        consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

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
        view.findViewById(R.id.open_profile).setOnLongClickListener(v -> {
            ManageAccountsBottomSheetModal manageAccountsBottomSheetModal = new ManageAccountsBottomSheetModal();
            manageAccountsBottomSheetModal.show(getChildFragmentManager(), "TAG");
            return true;
        });

        // Initialize navigations recyclerview
        RecyclerView navigationsRecyclerview = view.findViewById(R.id.recycler_navigations);

        navigationsAdapter = new NavigationsAdapter(navigationsList, click -> IntentHandler.handleIntent(getContext(), click.getLink()), longClick -> {
            bundle.putInt("navigation_id", longClick.getId());
            bundle.putString("navigation_title", longClick.getTitle());
            bundle.putString("navigation_link", longClick.getLink());
            bundle.putString("navigation_icon", longClick.getIcon());
            bundle.putString("navigation_behavior", longClick.getBehavior());
            bundle.putString("navigation_premium", longClick.getPremium());

            NavigationOptionsBottomSheetModal navigationOptionsBottomSheetModal = new NavigationOptionsBottomSheetModal();
            navigationOptionsBottomSheetModal.setArguments(bundle);
            navigationOptionsBottomSheetModal.setTargetFragment(NavigationFragment.this, REQUEST_NAVIGATION_ACTION);
            navigationOptionsBottomSheetModal.show(requireFragmentManager(), "TAG");
        });

        navigationsRecyclerview.setAdapter(navigationsAdapter);
        navigationsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        // request navigations
        requestNavigations();

        // add navigation
        view.findViewById(R.id.add_navigation).setOnClickListener(v -> {
            AddNavigationBottomSheetModal addNavigationBottomSheetModal = new AddNavigationBottomSheetModal();
            addNavigationBottomSheetModal.setTargetFragment(NavigationFragment.this, REQUEST_NAVIGATION_ACTION);
            addNavigationBottomSheetModal.show(requireFragmentManager(), "TAG");
        });

        // Refresh Layout
        SwipeRefreshLayout refresh = view.findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            refreshFragment();
        });
    }

    /**
     * request navigations
     */
    private void requestNavigations() {
        view.findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_NAVIGATIONS,
                response -> {

                    try {
                        JSONObject obj = new JSONObject(response);

                        JSONArray array = obj.getJSONArray("navigations");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Navigations navigations = new Navigations(object.getInt("id"), object.getString("title"), object.getString("link"), object.getString("icon"), object.getString("behavior"), object.getString("premium"));
                            navigationsList.add(navigations);
                            navigationsAdapter.notifyDataSetChanged();
                        }
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

    public void refreshFragment() {
        navigationsList.clear();
        navigationsAdapter.notifyDataSetChanged();
        requireFragmentManager().beginTransaction().detach(this).attach(this).commit();
        requestNavigations();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NAVIGATION_ACTION) {
            if (resultCode == Activity.RESULT_OK) {
                refreshFragment();
            }
        }
    }

}
