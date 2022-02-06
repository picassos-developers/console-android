package com.picassos.mint.console.android.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.helpCentre.ArticlesActivity;
import com.picassos.mint.console.android.activities.helpCentre.SearchArticlesActivity;
import com.picassos.mint.console.android.activities.helpCentre.SubmitTicketActivity;
import com.picassos.mint.console.android.adapter.SectionsAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.models.Sections;
import com.picassos.mint.console.android.utils.RequestDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpCentreFragment extends Fragment {

    View view;

    // request dialog
    RequestDialog requestDialog;

    // sections
    private final List<Sections> sectionsList = new ArrayList<>();
    private SectionsAdapter sectionsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_help_centre, container, false);

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // Initialize sections recyclerview
        RecyclerView sectionsRecyclerview = view.findViewById(R.id.recycler_sections);

        // sections adapter
        sectionsAdapter = new SectionsAdapter(sectionsList, section -> {
            Intent intent = new Intent(requireContext(), ArticlesActivity.class);
            intent.putExtra("section_id", section.getSection_id());
            intent.putExtra("section_title", section.getSection_title());
            intent.putExtra("section_description", section.getSection_description());
            startActivity(intent);
        });

        sectionsRecyclerview.setAdapter(sectionsAdapter);
        sectionsRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));

        // request sections
        requestSections();

        // Refresh Layout
        SwipeRefreshLayout refresh = view.findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            sectionsList.clear();
            sectionsAdapter.notifyDataSetChanged();
            requestSections();
        });

        // submit a ticket
        view.findViewById(R.id.submit_ticket).setOnClickListener(v -> startActivity(new Intent(requireContext(), SubmitTicketActivity.class)));

        // search articles
        view.findViewById(R.id.search_articles).setOnClickListener(v -> startActivity(new Intent(requireContext(), SearchArticlesActivity.class)));
    }

    /**
     * request help centre sections
     */
    @SuppressLint("NotifyDataSetChanged")
    private void requestSections() {
        view.findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_HC_SECTIONS,
                response -> {

                    try {
                        JSONObject obj = new JSONObject(response);

                        JSONArray array = obj.getJSONArray("sections");

                        // Check if data are empty
                        if (array.length() == 0) {
                            view.findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                        } else {
                            view.findViewById(R.id.no_items).setVisibility(View.GONE);
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Sections sections = new Sections(object.getInt("section_id"), object.getString("title"), object.getString("description"));
                            sectionsList.add(sections);
                            sectionsAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            view.findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            view.findViewById(R.id.try_again).setOnClickListener(v -> requestSections());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("request", "sections");
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

}
