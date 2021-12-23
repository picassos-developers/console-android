package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.adapter.DefaultNavigationAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.models.Navigations;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.Toasto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChooseDefaultNavigationBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    ConsolePreferences consolePreferences;
    int defaultNavigation;

    // Navigations
    private final List<Navigations> navigationsList = new ArrayList<>();
    private DefaultNavigationAdapter defaultNavigationAdapter;

    public ChooseDefaultNavigationBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.choose_default_navigation_bottom_sheet_modal, container, false);

        // default navigation
        defaultNavigation = requireArguments().getInt("identifier", 0);

        // Initialize navigations recyclerview
        RecyclerView navigationsRecyclerview = view.findViewById(R.id.recycler_providers);

        // providers adapter
        defaultNavigationAdapter = new DefaultNavigationAdapter(requireContext(), navigationsList, defaultNavigation, click -> requestUpdateDefaultProvider(click.getIdentifier()));
        navigationsRecyclerview.setAdapter(defaultNavigationAdapter);
        navigationsRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));

        // request navigations
        requestNavigations();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        consolePreferences = new ConsolePreferences(requireContext());
    }

    /**
     * request navigations
     */
    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void requestNavigations() {
        view.findViewById(R.id.internet_connection).setVisibility(View.GONE);

        // clear, notify data changed
        navigationsList.clear();
        defaultNavigationAdapter.notifyDataSetChanged();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_PROVIDER_MENUS,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        JSONObject root = obj.getJSONObject("navigations");
                        JSONArray array = root.getJSONArray("data");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Navigations menus = new Navigations(object.getInt("id"), object.getInt("identifier"), object.getInt("enabled"), object.getString("type"),  object.getString("label"), object.getString("icon"));
                            navigationsList.add(menus);
                            defaultNavigationAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
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
     * request select default provider
     * @param identifier for identifier
     */
    private void requestUpdateDefaultProvider(int identifier) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(requireContext(), getString(R.string.demo_project), 1, 0);
        } else {
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_PROVIDER,
                    response -> {
                        //Intent intent = new Intent();
                      //  intent.putExtra("request", "update");
                       // Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);

                       // dismiss();
                    }, error -> Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 1, 1)) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("identifier", String.valueOf(identifier));
                    return params;
                }
            };

            Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
        }
    }

}
