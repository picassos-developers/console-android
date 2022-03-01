package app.mynta.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import app.mynta.console.android.R;

import app.mynta.console.android.activities.EditWalkthroughActivity;
import app.mynta.console.android.constants.RequestCodes;
import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.utils.RequestDialog;

import java.util.HashMap;
import java.util.Map;

public class WalkthroughOptionsBottomSheetModal extends BottomSheetDialogFragment {
    View view;

    RequestDialog requestDialog;
    ConsolePreferences consolePreferences;

    public interface OnDeleteListener {
        void onDeleteListener(boolean delete);
    }

    OnDeleteListener onDeleteListener;

    public interface OnEditListener {
        void onEditListener(boolean edit);
    }

    OnEditListener onEditListener;

    public WalkthroughOptionsBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onDeleteListener = (OnDeleteListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context + " must implement onDeleteListener");
        }

        try {
            onEditListener = (OnEditListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context + "must implement onEditListener");
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.walkthrough_options_bottom_sheet_modal, container, false);

        consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

        // get walkthrough details
        int walkthroughId = requireArguments().getInt("walkthrough_id");
        String walkthroughTitle = requireArguments().getString("walkthrough_title");
        String walkthroughDescription = requireArguments().getString("walkthrough_description");
        String walkthroughThumbnail = requireArguments().getString("walkthrough_thumbnail");

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // edit walkthrough
        view.findViewById(R.id.edit_walkthrough).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditWalkthroughActivity.class);
            intent.putExtra("walkthrough_id", walkthroughId);
            intent.putExtra("walkthrough_title", walkthroughTitle);
            intent.putExtra("walkthrough_description", walkthroughDescription);
            intent.putExtra("walkthrough_thumbnail", walkthroughThumbnail);
            startActivityForResult.launch(intent);
        });

        // delete walkthrough
        view.findViewById(R.id.delete_walkthrough).setOnClickListener(v -> {
            requestDeleteWalkthrough(walkthroughTitle, walkthroughDescription, walkthroughThumbnail);
            dismiss();
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request delete walkthrough
     *
     * @param title for walkthrough title
     * @param description for walkthrough description
     * @param thumbnail for walkthrough thumbnail
     */
    private void requestDeleteWalkthrough(String title, String description, String thumbnail) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toast.makeText(requireContext(), getString(R.string.demo_project), Toast.LENGTH_SHORT).show();
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_REMOVE_WALKTHROUGH,
                    response -> {
                        if (response.equals("200")) {
                            onDeleteListener.onDeleteListener(true);
                            dismiss();
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.unknown_issue), Toast.LENGTH_SHORT).show();
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toast.makeText(requireContext(), getString(R.string.unknown_issue), Toast.LENGTH_SHORT).show();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("title", title);
                    params.put("description", description);
                    params.put("thumbnail", thumbnail);
                    return params;
                }
            };

            Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
        }
    }

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result != null) {
                if (result.getResultCode() == RequestCodes.REQUEST_UPDATE_WALKTRHOUGH_CODE) {
                    onEditListener.onEditListener(true);
                    dismiss();
                }
            }
        }
    });
}
