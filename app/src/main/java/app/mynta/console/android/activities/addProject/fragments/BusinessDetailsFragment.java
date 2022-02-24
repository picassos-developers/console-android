package app.mynta.console.android.activities.addProject.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import app.mynta.console.android.R;
import app.mynta.console.android.activities.addProject.AddProjectActivity;
import app.mynta.console.android.adapter.CountriesAdapter;
import app.mynta.console.android.models.Countries;
import app.mynta.console.android.utils.Helper;

public class BusinessDetailsFragment extends Fragment {
    View view;

    private EditText businessName;
    private EditText email;
    private TextView location;
    private String code;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_business_details, container, false);

        // business name
        businessName = view.findViewById(R.id.business);
        businessName.addTextChangedListener(onOrganizationValueChange);

        // email
        email = view.findViewById(R.id.email_address);
        email.addTextChangedListener(onOrganizationValueChange);

        // choose location
        view.findViewById(R.id.location_chooser).setOnClickListener(v -> locationChooserDialog());
        location = view.findViewById(R.id.location);

        // next
        view.findViewById(R.id.next).setOnClickListener(v -> {
            ((AddProjectActivity) requireActivity()).businessName = businessName.getText().toString();
            ((AddProjectActivity) requireActivity()).countryCode = code;
            ((AddProjectActivity) requireActivity()).goForward();
        });

        return view;
    }

    /**
     * open location dialog
     */
    @SuppressLint("NotifyDataSetChanged")
    private void locationChooserDialog() {
        Dialog locationDialog = new Dialog(requireContext());

        locationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        locationDialog.setContentView(R.layout.dialog_location);

        // enable dialog cancel
        locationDialog.setCancelable(true);
        locationDialog.setOnCancelListener(dialog -> locationDialog.dismiss());

        // close dialog
        locationDialog.findViewById(R.id.dialog_close).setOnClickListener(v -> locationDialog.dismiss());

        // countries
        List<Countries> countriesList = new ArrayList<>();
        RecyclerView countriesRecyclerview = locationDialog.findViewById(R.id.recycler_countries);

        CountriesAdapter countriesAdapter = new CountriesAdapter(requireContext(), countriesList, click -> {
            location.setText(click.getTitle());
            code = click.getCode();
            businessName.setText(businessName.getText().toString());
            locationDialog.dismiss();
        });
        countriesRecyclerview.setAdapter(countriesAdapter);
        countriesRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        try {
            JSONObject obj = new JSONObject(Objects.requireNonNull(Helper.getJsonFromAssets(requireContext(), "countries/countries.json")));
            JSONArray countries = obj.getJSONArray("countries");

            for (int i = 0; i < countries.length(); i++) {
                JSONObject countriesObject = countries.getJSONObject(i);
                String name = countriesObject.getString("name");
                String code = countriesObject.getString("code");
                String flag = countriesObject.getString("flag");

                countriesList.add(new Countries(name, code, flag));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        countriesAdapter.notifyDataSetChanged();

        if (locationDialog.getWindow() != null) {
            locationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            locationDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        locationDialog.show();
    }

    /**
     * on business details value change, update
     * next button visibility if valid.
     */
    private final TextWatcher onOrganizationValueChange = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressLint("SetTextI18n")
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(businessName.getText().toString())
                    || TextUtils.isEmpty(email.getText().toString())
                    || location.getText().toString().equals(getString(R.string.where_is_your_business_located))) {
                view.findViewById(R.id.next).setVisibility(View.GONE);
            } else {
                if (Helper.validateEmailAddress(email.getText().toString().trim())) {
                    view.findViewById(R.id.next).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.next).setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
