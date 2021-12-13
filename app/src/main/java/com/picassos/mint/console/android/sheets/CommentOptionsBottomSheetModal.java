package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.helpCentre.EditCommentActivity;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import java.util.HashMap;
import java.util.Map;

public class CommentOptionsBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    public interface OnRemoveListener {
        void onRemoveListener(int requestCode);
    }

    public interface OnUpdateListener {
        void onUpdateListener(int requestCode);
    }

    OnRemoveListener onRemoveListener;
    OnUpdateListener onUpdateListener;

    public static final int REQUEST_REMOVE_COMMENT_CODE = 1;
    public static final int REQUEST_UPDATE_COMMENT_CODE = 2;

    public CommentOptionsBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onRemoveListener = (OnRemoveListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onRemoveListener");
        }
        try {
            onUpdateListener = (OnUpdateListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onUpdateListener");
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.comment_options_bottom_sheet_modal, container, false);

        consolePreferences = new ConsolePreferences(requireActivity().getApplicationContext());

        // get comment details
        int commentId = requireArguments().getInt("comment_id");
        String commentDescription = requireArguments().getString("comment_description");

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // edit comment
        view.findViewById(R.id.edit_comment).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditCommentActivity.class);
            intent.putExtra("comment_id", commentId);
            intent.putExtra("comment_description", commentDescription);
            startActivityForResult(intent, REQUEST_UPDATE_COMMENT_CODE);
        });

        // remove comment
        view.findViewById(R.id.remove_comment).setOnClickListener(v -> {
            requestRemoveComment(commentId);
            dismiss();
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request remove comment
     *
     * @param id for comment id
     */
    private void requestRemoveComment(int id) {
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_REMOVE_COMMENT,
                response -> {
                    if (response.equals("200")) {
                        onRemoveListener.onRemoveListener(REQUEST_REMOVE_COMMENT_CODE);
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
                params.put("comment_id", String.valueOf(id));
                return params;
            }
        };

        Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_UPDATE_COMMENT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                onUpdateListener.onUpdateListener(REQUEST_UPDATE_COMMENT_CODE);
                dismiss();
            }
        }
    }
}