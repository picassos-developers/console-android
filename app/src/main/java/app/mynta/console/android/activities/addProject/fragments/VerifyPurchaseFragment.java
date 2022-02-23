package app.mynta.console.android.activities.addProject.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import app.mynta.console.android.R;
import app.mynta.console.android.activities.addProject.AddProjectActivity;
import app.mynta.console.android.utils.Helper;

public class VerifyPurchaseFragment extends Fragment {
    View view;
    EditText purchaseCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_verify_purchase, container, false);

        // description
        TextView description = view.findViewById(R.id.description);
        description.setMovementMethod(LinkMovementMethod.getInstance());

        // purchase code
        purchaseCode = view.findViewById(R.id.purchasecode);
        purchaseCode.addTextChangedListener(onUpdatePurchaseCode);

        // next
        view.findViewById(R.id.next).setOnClickListener(v -> {
            ((AddProjectActivity) requireActivity()).purchaseCode = purchaseCode.getText().toString();
            ((AddProjectActivity) requireActivity()).goForward();
        });

        return view;
    }

    /**
     * check if purchase code is
     * valid to verify project
     */
    private final TextWatcher onUpdatePurchaseCode = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressLint("SetTextI18n")
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (Helper.validatePurchaseCode(purchaseCode.getText().toString().trim())) {
                view.findViewById(R.id.next).setVisibility(View.VISIBLE);
                view.findViewById(R.id.response).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.next).setVisibility(View.GONE);
                view.findViewById(R.id.response).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
