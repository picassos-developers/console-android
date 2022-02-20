package app.mynta.console.android.sheets;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import app.mynta.console.android.R;
import app.mynta.console.android.models.viewModel.SharedViewModel;
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.Toasto;

public class EditPackageNameCodeBottomSheetModal extends BottomSheetDialogFragment {
    SharedViewModel sharedViewModel;

    View view;

    EditText packageName;
    TextView agreementNotice;

    public EditPackageNameCodeBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit_packagename_bottom_sheet_modal, container, false);

        // code
        packageName = view.findViewById(R.id.project_packagename);
        packageName.addTextChangedListener(onCodeValueChanged);

        // agreement notice
        agreementNotice = view.findViewById(R.id.agreement_notice);
        agreementNotice.setMovementMethod(LinkMovementMethod.getInstance());

        // redeem code
        view.findViewById(R.id.update_packagename).setOnClickListener(v -> {
            if (!TextUtils.isEmpty(packageName.getText().toString())) {
                sharedViewModel.setPackageName(packageName.getText().toString());
                dismiss();
            } else {
                Toasto.show_toast(requireContext(), getString(R.string.redeem_code_empty), 1, 2);
            }
        });

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
     * on code field value change, enable or
     * disable redeem button
     */
    private final TextWatcher onCodeValueChanged = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @SuppressLint("SetTextI18n")
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            view.findViewById(R.id.update_packagename).setEnabled(Helper.validatePackagename(packageName.getText().toString()));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}

