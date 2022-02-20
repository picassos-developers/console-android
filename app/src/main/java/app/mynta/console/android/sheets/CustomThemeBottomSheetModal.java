package app.mynta.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import app.mynta.console.android.R;

public class CustomThemeBottomSheetModal extends BottomSheetDialogFragment {

    View view;

    public interface OnChooseThemeListener {
        void onChooseThemeListener(boolean isSolid);
    }

    OnChooseThemeListener onChooseThemeListener;

    public CustomThemeBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onChooseThemeListener = (OnChooseThemeListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onChooseThemeListener");
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.custom_theme_bottom_sheet_modal, container, false);

        // solid theme
        view.findViewById(R.id.solid_theme).setOnClickListener(v -> {
            onChooseThemeListener.onChooseThemeListener(true);
            dismiss();
        });

        // gradient theme
        view.findViewById(R.id.gradient_theme).setOnClickListener(v -> {
            onChooseThemeListener.onChooseThemeListener(false);
            dismiss();
        });

        return view;
    }

}
