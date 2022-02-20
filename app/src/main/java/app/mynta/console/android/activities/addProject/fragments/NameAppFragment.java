package app.mynta.console.android.activities.addProject.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import app.mynta.console.android.R;
import app.mynta.console.android.activities.AddProjectActivity;
import app.mynta.console.android.models.viewModel.SharedViewModel;
import app.mynta.console.android.sheets.EditPackageNameCodeBottomSheetModal;
import app.mynta.console.android.utils.Helper;

public class NameAppFragment extends Fragment {
    SharedViewModel sharedViewModel;

    View view;

    // fields
    EditText applicationName;
    TextView packageName;
    TextView length;

    // next step
    Button nextStep;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_name_app, container, false);

        // application name
        applicationName = view.findViewById(R.id.project_name);
        applicationName.addTextChangedListener(onUpdatePackageName);

        // package name
        packageName = view.findViewById(R.id.project_packagename);
        view.findViewById(R.id.edit_packagename).setOnClickListener(v -> {
            EditPackageNameCodeBottomSheetModal editPackageNameCodeBottomSheetModal = new EditPackageNameCodeBottomSheetModal();
            editPackageNameCodeBottomSheetModal.show(getChildFragmentManager(), "TAG");
        });

        // initialize length
        length = view.findViewById(R.id.length);

        // next step
        nextStep = view.findViewById(R.id.next);
        nextStep.setOnClickListener(v -> {
            ((AddProjectActivity) requireActivity()).applicationName = applicationName.getText().toString();
            ((AddProjectActivity) requireActivity()).packageName = packageName.getText().toString();
            ((AddProjectActivity) requireActivity()).goForward();
        });

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getPackageName().observe(requireActivity(), item -> packageName.setText(item));

        return view;
    }

    /**
     * check if package name is
     * not empty to process.
     */
    private final TextWatcher onUpdatePackageName = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressLint("SetTextI18n")
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            length.setText(s.length() + " / 30");
            if (!TextUtils.isEmpty(applicationName.getText().toString())) {
                nextStep.setVisibility(View.VISIBLE);
                view.findViewById(R.id.edit_packagename).setVisibility(View.VISIBLE);
                // package name format
                packageName.setText("com." + Helper.toPackagenameFormat(applicationName.getText().toString()) + ".app");
            } else {
                nextStep.setVisibility(View.GONE);
                view.findViewById(R.id.edit_packagename).setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
