package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.EditNavigationActivity;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NavigationOptionsBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    public NavigationOptionsBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.navigation_options_bottom_sheet_modal, container, false);

        consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

        // get navigation details
        int navigationId = requireArguments().getInt("navigation_id");
        String navigationTitle = requireArguments().getString("navigation_title");
        String navigationLink = requireArguments().getString("navigation_link");
        String navigationIcon = requireArguments().getString("navigation_icon");
        String navigationBehavior = requireArguments().getString("navigation_behavior");
        String navigationPremium = requireArguments().getString("navigation_premium");

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // edit navigation
        view.findViewById(R.id.edit_navigation).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditNavigationActivity.class);
            intent.putExtra("navigation_id", navigationId);
            intent.putExtra("navigation_title", navigationTitle);
            intent.putExtra("navigation_link", navigationLink);
            intent.putExtra("navigation_icon", navigationIcon);
            intent.putExtra("navigation_behavior", navigationBehavior);
            intent.putExtra("navigation_premium", navigationPremium);
            startActivityForResult.launch(intent);
        });

        // delete navigation
        view.findViewById(R.id.delete_navigation).setOnClickListener(v -> {
            requestDeleteNavigation(navigationTitle, navigationLink);
            dismiss();
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request delete navigation tab
     *
     * @param title for title
     * @param link  for link
     */
    private void requestDeleteNavigation(String title, String link) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(requireContext(), getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_DELETE_NAVIGATION,
                    response -> {
                        if (response.contains("delete:callback?success")) {
                            Intent intent = new Intent();
                            intent.putExtra("request", "delete");
                            Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                            dismiss();
                        } else {
                            Toast.makeText(requireContext(), "exception:error?" + response, Toast.LENGTH_SHORT).show();
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toast.makeText(requireContext(), "exception:error?" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("title", title);
                    params.put("link", link);
                    return params;
                }
            };

            Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
        }
    }

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result != null && result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra("request", "delete");
            Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            dismiss();
        }
    });
}
