package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.helpCentre.UpdateTicketActivity;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.constants.RequestCodes;
import com.picassos.mint.console.android.models.viewModel.SharedViewModel;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class TicketOptionsBottomSheetModal extends BottomSheetDialogFragment {
    SharedViewModel sharedViewModel;

    View view;
    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    public TicketOptionsBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ticket_options_bottom_sheet_modal, container, false);

        consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

        // get ticket details
        int ticketId = requireArguments().getInt("ticket_id");
        String ticketDescription = requireArguments().getString("ticket_description");

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // update ticket
        view.findViewById(R.id.update_ticket).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UpdateTicketActivity.class);
            intent.putExtra("ticket_id", ticketId);
            intent.putExtra("ticket_description", ticketDescription);
            startActivityForResult.launch(intent);
        });

        // close ticket
        view.findViewById(R.id.close_ticket).setOnClickListener(v -> {
            requestCloseTicket(ticketId);
            dismiss();
        });
        if (!requireArguments().getBoolean("is_opened")) {
            view.findViewById(R.id.close_ticket).setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    /**
     * request close ticket
     *
     * @param id for ticket id
     */
    private void requestCloseTicket(int id) {
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_CLOSE_TICKET,
                response -> {
                    if (response.equals("200")) {
                        sharedViewModel.setRequestCode(RequestCodes.REQUEST_CLOSE_TICKET_CODE);
                        dismiss();
                    } else {
                        Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 0, 2);
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 0, 2);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", consolePreferences.loadToken());
                params.put("ticket_id", String.valueOf(id));
                return params;
            }
        };

        Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
    }

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result != null && result.getResultCode() == RequestCodes.REQUEST_UPDATE_TICKET_CODE) {
            sharedViewModel.setRequestCode(RequestCodes.REQUEST_UPDATE_TICKET_CODE);
            dismiss();
        }
    });
}
