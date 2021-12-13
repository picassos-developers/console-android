package com.picassos.mint.console.android.activities.about.fragments;

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
import com.picassos.mint.console.android.activities.helpCentre.SubmitTicketActivity;
import com.picassos.mint.console.android.activities.helpCentre.ViewTicketActivity;
import com.picassos.mint.console.android.adapter.TicketsAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.models.Tickets;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.TicketOptionsBottomSheetModal;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClosedTicketsFragment extends Fragment {

    View view;
    Bundle bundle;

    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    // Closed Tickets
    private final List<Tickets> ticketsList = new ArrayList<>();
    private TicketsAdapter ticketsAdapter;

    private static final int REQUEST_TICKET_ACTION = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_closed_tickets, container, false);

        // initialize bundle
        bundle = new Bundle();

        // console shared preferences
        consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // go back
        view.findViewById(R.id.go_back).setOnClickListener(v -> requireActivity().finish());

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // initialize tickets recyclerview
        RecyclerView ticketsRecyclerview = view.findViewById(R.id.recycler_tickets);

        ticketsAdapter = new TicketsAdapter(ticketsList, ticket -> {
            Intent intent = new Intent(getContext(), ViewTicketActivity.class);
            intent.putExtra("ticket_id", ticket.getTicketId());
            startActivity(intent);
        }, ticket -> {
            bundle.putInt("ticket_id", ticket.getTicketId());
            bundle.putString("ticket_description", ticket.getTicketMessage());
            bundle.putBoolean("is_opened", false);
            TicketOptionsBottomSheetModal ticketOptionsBottomSheetModal = new TicketOptionsBottomSheetModal();
            ticketOptionsBottomSheetModal.setArguments(bundle);
            ticketOptionsBottomSheetModal.setTargetFragment(ClosedTicketsFragment.this, REQUEST_TICKET_ACTION);
            ticketOptionsBottomSheetModal.show(requireFragmentManager(), "TAG");
        });

        ticketsRecyclerview.setAdapter(ticketsAdapter);
        ticketsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        // request tickets
        requestTickets();

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
     * request tickets
     */
    private void requestTickets() {
        view.findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_CLOSED_TICKETS,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        JSONArray array = obj.getJSONArray("tickets");

                        // Check if data is empty
                        if (array.length() == 0) {
                            view.findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                        } else {
                            view.findViewById(R.id.no_items).setVisibility(View.GONE);
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Tickets tickets = new Tickets(object.getInt("id"), object.getInt("ticket_id"), object.getString("ticket_subject"), object.getString("ticket_message"), object.getString("ticket_owner_email"), object.getString("ticket_type"), object.getString("ticket_date"), object.getString("ticket_expire_date"), object.getString("status"));
                            ticketsList.add(tickets);
                            ticketsAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
                requestDialog.dismiss();
                view.findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
                view.findViewById(R.id.try_again).setOnClickListener(v -> refreshFragment());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", consolePreferences.loadToken());
                return params;
            }
        };

        Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
    }

    public void refreshFragment() {
        ticketsList.clear();
        ticketsAdapter.notifyDataSetChanged();
        requireFragmentManager().beginTransaction().detach(this).attach(this).commit();
        requestTickets();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TICKET_ACTION) {
            refreshFragment();
            if (data != null) {
                if (data.getStringExtra("request").equals("close")) {
                    Toasto.show_toast(requireContext(), getString(R.string.ticket_closed), 1, 0);
                } else if (data.getStringExtra("request").equals("update")) {
                    Toasto.show_toast(requireContext(), getString(R.string.ticket_updated), 0, 0);
                }
            }
        }
    }
}
