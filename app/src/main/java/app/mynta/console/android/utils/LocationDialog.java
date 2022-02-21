package app.mynta.console.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import app.mynta.console.android.R;
import app.mynta.console.android.adapter.CountriesAdapter;
import app.mynta.console.android.models.Countries;

public class LocationDialog extends Dialog {
    private final Activity activity;
    public LocationDialog(@NonNull Context context, Activity activity) {
        super(context);
        this.activity = activity;
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_location);

        // set cancelable
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        // dialog close
        findViewById(R.id.dialog_close).setOnClickListener(v -> dismiss());

        // countries
        List<Countries> countriesList = new ArrayList<>();
        RecyclerView countriesRecyclerview = findViewById(R.id.recycler_countries);

        CountriesAdapter countriesAdapter = new CountriesAdapter(countriesList, click -> {

        });

        countriesRecyclerview.setAdapter(countriesAdapter);
        countriesRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
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

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getContext().getAssets().open("countries/countries.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
