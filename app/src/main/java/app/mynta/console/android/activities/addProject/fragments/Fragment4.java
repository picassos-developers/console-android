package app.mynta.console.android.activities.addProject.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import app.mynta.console.android.activities.AddProjectActivity;
import app.mynta.console.android.adapter.CountriesAdapter;
import app.mynta.console.android.models.Countries;
import app.mynta.console.android.utils.Helper;

public class Fragment4 extends Fragment {
    View view;

    private EditText organization;
    private TextView location;
    private String code;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment4, container, false);

        // organization
        organization = view.findViewById(R.id.organization);
        organization.addTextChangedListener(onOrganizationValueChange);

        // choose location
        view.findViewById(R.id.location_chooser).setOnClickListener(v -> locationChooserDialog());
        location = view.findViewById(R.id.location);

        // next
        view.findViewById(R.id.next).setOnClickListener(v -> {
            ((AddProjectActivity) requireActivity()).organization = organization.getText().toString();
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
        ImageView dialogClose = locationDialog.findViewById(R.id.dialog_close);
        dialogClose.setOnClickListener(v -> locationDialog.dismiss());

        // countries
        List<Countries> countriesList = new ArrayList<>();
        RecyclerView countriesRecyclerview = locationDialog.findViewById(R.id.recycler_countries);

        CountriesAdapter countriesAdapter = new CountriesAdapter(countriesList, click -> {
            location.setText(click.getTitle());
            code = click.getCode();
            organization.setText(organization.getText().toString());
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

                countriesList.add(new Countries(name, code));
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
     * on organization field value change, update
     * next button visibility if valid.
     */
    private final TextWatcher onOrganizationValueChange = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @SuppressLint("SetTextI18n")
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0 || location.getText().toString().equals(getString(R.string.location))) {
                view.findViewById(R.id.next).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.next).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
