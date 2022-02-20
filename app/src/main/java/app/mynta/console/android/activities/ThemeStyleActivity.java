package app.mynta.console.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.mynta.console.android.R;

import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.adapter.ThemeStylesAdapter;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.libraries.smartcolorpicker.ColorPickerDialog;
import app.mynta.console.android.models.ThemeStyles;
import app.mynta.console.android.sheets.CustomThemeBottomSheetModal;
import app.mynta.console.android.utils.Helper;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThemeStyleActivity extends AppCompatActivity implements CustomThemeBottomSheetModal.OnChooseThemeListener {

    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_theme_style);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // close activity
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        List<ThemeStyles> themeStylesList = new ArrayList<>();
        RecyclerView themesRecyclerview = findViewById(R.id.recycler_themes);

        ThemeStylesAdapter themeStylesAdapter = new ThemeStylesAdapter(themeStylesList, click -> requestUpdateThemeStyle(click.getGradient_start(), click.getGradient_end()));
        themesRecyclerview.setAdapter(themeStylesAdapter);
        themesRecyclerview.setLayoutManager(new GridLayoutManager(this, 2));
        // preset gradient themes
        themeStylesList.add(new ThemeStyles("Gradient 1", "#ABDCFF", "#0396FF"));
        themeStylesList.add(new ThemeStyles("Gradient 2", "#FEB692", "#EA5455"));
        themeStylesList.add(new ThemeStyles("Gradient 3", "#90F7EC", "#32CCBC"));
        themeStylesList.add(new ThemeStyles("Gradient 4", "#CE9FFC", "#7367F0"));
        themeStylesList.add(new ThemeStyles("Gradient 5", "#FDEB71", "#F8D800"));
        themeStylesList.add(new ThemeStyles("Gradient 6", "#FEC163", "#DE4313"));
        themeStylesList.add(new ThemeStyles("Gradient 7", "#FAD7A1", "#E96D71"));
        themeStylesList.add(new ThemeStyles("Gradient 8", "#81FBB8", "#28C76F"));
        themeStylesList.add(new ThemeStyles("Gradient 9", "#F761A1", "#8C1BAB"));
        themeStylesList.add(new ThemeStyles("Gradient 10", "#FCCF31", "#F55555"));
        // preset solid themes
        themeStylesList.add(new ThemeStyles("Solid 1", "#55EFC4", "#55EFC4"));
        themeStylesList.add(new ThemeStyles("Solid 2", "#81ECEC", "#81ECEC"));
        themeStylesList.add(new ThemeStyles("Solid 3", "#74B9FF", "#74B9FF"));
        themeStylesList.add(new ThemeStyles("Solid 4", "#6C5CE7", "#6C5CE7"));
        themeStylesList.add(new ThemeStyles("Solid 5", "#FAB1A0", "#FAB1A0"));
        themeStylesList.add(new ThemeStyles("Solid 6", "#0984E3", "#0984E3"));
        themeStylesList.add(new ThemeStyles("Solid 7", "#E17055", "#E17055"));
        themeStylesList.add(new ThemeStyles("Solid 8", "#FDCB6E", "#FDCB6E"));
        themeStylesList.add(new ThemeStyles("Solid 9", "#E74C3C", "#E74C3C"));
        themeStylesList.add(new ThemeStyles("Solid 10", "#2ECC71", "#2ECC71"));
        themeStylesAdapter.notifyDataSetChanged();

        // request current theme
        requestThemeStyle();

        // Refresh Layout
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestThemeStyle();
        });

        // choose custom theme
        findViewById(R.id.custom_theme).setOnClickListener(v -> {
            CustomThemeBottomSheetModal customThemeBottomSheetModal = new CustomThemeBottomSheetModal();
            customThemeBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });
    }

    /**
     * request application theme style
     */
    private void requestThemeStyle() {
        findViewById(R.id.theme_style_container).setVisibility(View.VISIBLE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_THEME_STYLE,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONObject root = obj.getJSONObject("theme");
                        // set theme style
                        Helper.setGradientBackground(
                                findViewById(R.id.theme_thumbnail),
                                root.getString("color_start"),
                                root.getString("color_end"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.theme_style_container).setVisibility(View.GONE);
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestThemeStyle());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request update application theme style
     * @param color_start for color gradient start
     * @param color_end for color gradient end
     */
    private void requestUpdateThemeStyle(String color_start, String color_end) {
        if (consolePreferences.loadSecretAPIKey().equals("demo")) {
            Toasto.show_toast(this, getString(R.string.demo_project), 1, 0);
        } else {
            StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_UPDATE_THEME_STYLE,
                    response -> {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject root = obj.getJSONObject("theme");
                            String callback = obj.getString("callback");

                            if (callback.equals("200")) {
                                // set theme style
                                Helper.setGradientBackground(
                                        findViewById(R.id.theme_thumbnail),
                                        root.getString("color_start"),
                                        root.getString("color_end"));
                            } else {
                                Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        requestDialog.dismiss();
                    }, error -> {
                requestDialog.dismiss();
                Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                    params.put("color_start", color_start);
                    params.put("color_end", color_end);
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);
        }
    }

    @Override
    public void onChooseThemeListener(boolean isSolid) {
        String dialogTitle;
        if (isSolid) {
            dialogTitle = "Select Your Color";
        } else {
            dialogTitle = "Select Color 1";
        }

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, Color.BLACK, dialogTitle, new ColorPickerDialog.OnColorPickerListener() {
            @Override
            public void onCancel(ColorPickerDialog dialog) {

            }

            @Override
            public void onOk(ColorPickerDialog dialog, int color) {
                String formattedColor = "#" + Integer.toHexString(color).substring(2);
                if (isSolid) {
                    requestUpdateThemeStyle(formattedColor, formattedColor);
                } else {
                    chooseColorTwo(formattedColor);
                }
            }
        });
        colorPickerDialog.show();
    }

    private void chooseColorTwo(String color_one) {
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, Color.BLACK, "Select Color 2", new ColorPickerDialog.OnColorPickerListener() {
            @Override
            public void onCancel(ColorPickerDialog dialog) {

            }

            @Override
            public void onOk(ColorPickerDialog dialog, int color) {
                String color_two = "#" + Integer.toHexString(color).substring(2);
                requestUpdateThemeStyle(color_one, color_two);
            }
        });
        colorPickerDialog.show();
    }
}