package app.mynta.console.android.sheets;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import app.mynta.console.android.R;
import app.mynta.console.android.utils.IntentHandler;

public class LearnMoreBottomSheetModal extends BottomSheetDialogFragment {
    View view;

    public LearnMoreBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.learn_more_bottom_sheet_modal, container, false);

        // learn more title
        TextView title = view.findViewById(R.id.learnmore_title);
        title.setText(requireArguments().getString("title"));

        // learn more description
        TextView description = view.findViewById(R.id.learnmore_description);
        description.setText(requireArguments().getString("description"));

        // dismiss
        view.findViewById(R.id.button_dismiss).setOnClickListener(v -> dismiss());

        // learn more
        if (requireArguments().getString("url").equals("")) {
            view.findViewById(R.id.button_learnmore).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.button_learnmore).setOnClickListener(v -> IntentHandler.handleWeb(requireContext(), requireArguments().getString("url")));
        }

        return view;
    }
}