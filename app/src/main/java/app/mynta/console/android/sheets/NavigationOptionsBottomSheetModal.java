package app.mynta.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import app.mynta.console.android.R;

import app.mynta.console.android.constants.RequestCodes;
import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class NavigationOptionsBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    RequestDialog requestDialog;
    ConsolePreferences consolePreferences;

    public interface OnRemoveListener {
        void onRemoveListener(int requestCode);
    }

    OnRemoveListener onRemoveListener;

    public NavigationOptionsBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onRemoveListener = (OnRemoveListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context + " must implement onRemoveListener");
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.navigation_options_bottom_sheet_modal, container, false);

        // get navigation details
        int identifier = requireArguments().getInt("identifier");

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // remove navigation
        view.findViewById(R.id.remove_navigation).setOnClickListener(v -> {
            requestRemoveNavigation(identifier);
            dismiss();
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        consolePreferences = new ConsolePreferences(requireContext());
    }

    /**
     * request remove navigation
     * @param id for navigation id
     */
    private void requestRemoveNavigation(int id) {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_REMOVE_NAVIGATION,
                response -> {
                    onRemoveListener.onRemoveListener(RequestCodes.REQUEST_REMOVE_NAVIGATION_CODE);
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 0, 2);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                params.put("identifier", String.valueOf(id));
                params.put("type", requireArguments().getString("type"));
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
