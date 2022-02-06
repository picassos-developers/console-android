package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.constants.RequestCodes;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MemberDetailsBottomSheetModal extends BottomSheetDialogFragment {

    public interface OnDeleteListener {
        void onDeleteListener(int requestCode);
    }

    OnDeleteListener onDeleteListener;

    View view;
    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;
    private TextView username;
    private TextView email;
    private TextView icon;
    private Button deleteMember;

    // member id
    private int memberId;

    public MemberDetailsBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onDeleteListener = (OnDeleteListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onDeleteListener");
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.member_details_bottom_sheet_modal, container, false);

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

        // member details
        icon = view.findViewById(R.id.member_icon);
        username = view.findViewById(R.id.member_username);
        email = view.findViewById(R.id.member_email);

        // request member details
        requestMemberDetails();

        // delete member
        deleteMember = view.findViewById(R.id.delete_member);
        deleteMember.setEnabled(false);
        deleteMember.setOnClickListener(v -> requestDeleteMember(memberId));

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request delete member
     * @param id for member id
     */
    private void requestDeleteMember(int id) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(requireContext(), getString(R.string.demo_project), 1, 0);
        } else {
            view.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_DELETE_MEMBER,
                    response -> {
                        if (response.equals("delete:callback?success")) {
                            onDeleteListener.onDeleteListener(RequestCodes.REQUEST_REMOVE_MEMBER_CODE);
                            dismiss();
                        }
                        view.findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    }, error -> {
                view.findViewById(R.id.progress_bar).setVisibility(View.GONE);
                Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 0, 1);
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("member_id", String.valueOf(id));
                    return params;
                }
            };

            Volley.newRequestQueue(requireContext()).add(request);
        }
    }

    /**
     * request member details
     */
    private void requestMemberDetails() {
        icon.setVisibility(View.GONE);
        view.findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_MEMBER_DETAILS,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("member");

                        memberId = root.getInt("member_id");
                        deleteMember.setEnabled(true);
                        icon.setText(root.getString("username").substring(0, 1).toUpperCase());
                        username.setText(root.getString("username"));
                        email.setText(root.getString("email"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    view.findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    icon.setVisibility(View.VISIBLE);
                }, error -> {
            view.findViewById(R.id.progress_bar).setVisibility(View.GONE);
            icon.setVisibility(View.VISIBLE);
            Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 0, 1);
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                params.put("member_id", String.valueOf(requireArguments().getInt("identifier", 0)));
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requestDialog.dismiss();
    }
}
