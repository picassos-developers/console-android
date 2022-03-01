package app.mynta.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

public class AddWalkthroughBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    EditText walkthroughTitle;
    EditText walkthroughDescription;
    EditText walkthroughThumbnail;

    public interface OnAddListener {
        void onAddListener(int requestCode);
    }

    OnAddListener onAddListener;

    public AddWalkthroughBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onAddListener = (OnAddListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context + " must implement onAddListener");
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_walkthrough_bottom_sheet_modal, container, false);

        consolePreferences = new ConsolePreferences(requireContext());

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // walkthrough fields
        walkthroughTitle = view.findViewById(R.id.walkthrough_title);
        walkthroughDescription = view.findViewById(R.id.walkthrough_description);
        walkthroughThumbnail = view.findViewById(R.id.walkthrough_thumbnail);

        walkthroughTitle.addTextChangedListener(onValueChange);
        walkthroughDescription.addTextChangedListener(onValueChange);
        walkthroughThumbnail.addTextChangedListener(onValueChange);

        // add walkthrough
        view.findViewById(R.id.add_walkthrough).setOnClickListener(v -> {
            // validate walkthrough data
            if (!TextUtils.isEmpty(walkthroughTitle.getText().toString())
                    && !TextUtils.isEmpty(walkthroughDescription.getText().toString())
                    && !TextUtils.isEmpty(walkthroughThumbnail.getText().toString())) {
                requestAddWalkthrough(walkthroughTitle.getText().toString(), walkthroughDescription.getText().toString(), walkthroughThumbnail.getText().toString());
                dismiss();
            } else {
                Toasto.show_toast(requireContext(), getString(R.string.all_fields_are_required), 1, 2);
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request add walkthrough page
     * @param title for heading
     * @param description for subtitle
     * @param thumbnail for walkthrough thumbnail
     */
    private void requestAddWalkthrough(String title, String description, String thumbnail) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(requireContext(), getString(R.string.demo_project), 1, 0);
        } else {
            requestDialog.show();

            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_ADD_WALKTHROUGH,
                    response -> {
                        if (response.equals("200")) {
                            onAddListener.onAddListener(RequestCodes.REQUEST_ADD_WALKTHROUGH_CODE);
                            dismiss();
                        } else {
                            Toasto.show_toast(requireContext(), getString(R.string.unknown_issue), 1, 1);
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(requireActivity().getApplicationContext(), "exception:error?" + error.getMessage(), 1, 1);
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

    /**
     * on fields value change, update
     * button activity
     */
    private final TextWatcher onValueChange = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @SuppressLint("SetTextI18n")
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            view.findViewById(R.id.add_walkthrough).setEnabled(!TextUtils.isEmpty(walkthroughTitle.getText().toString())
                    && !TextUtils.isEmpty(walkthroughDescription.getText().toString())
                    && !TextUtils.isEmpty(walkthroughThumbnail.getText().toString()));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
