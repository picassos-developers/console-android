package com.picassos.mint.console.android.activities.about;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.adapter.PurchasesAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.models.Purchase;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.RedeemCodeBottomSheetModal;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchasesActivity extends AppCompatActivity implements RedeemCodeBottomSheetModal.OnRedeemedListener {
    private ConsolePreferences consolePreferences;
    private RequestDialog requestDialog;

    // purchases
    private final List<Purchase> purchaseList = new ArrayList<>();
    private PurchasesAdapter purchasesAdapter;

    private TextView totalSpent;
    private TextView totalCredits;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_purchases);

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // total spent & credits
        totalSpent = findViewById(R.id.total_spent);
        totalCredits = findViewById(R.id.total_credits);

        // Initialize purchases recyclerview
        RecyclerView purchasesRecyclerview = findViewById(R.id.purchases_list);

        purchasesAdapter = new PurchasesAdapter(this, purchaseList);

        purchasesRecyclerview.setAdapter(purchasesAdapter);
        purchasesRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        // request purchases
        requestPurchases();

        // Refresh Layout
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestPurchases();
        });

        findViewById(R.id.redeem_card).setOnClickListener(v -> {
            RedeemCodeBottomSheetModal redeemCodeBottomSheetModal = new RedeemCodeBottomSheetModal();
            redeemCodeBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });
    }

    /**
     * request purchases data
     */
    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void requestPurchases() {
        purchaseList.clear();
        purchasesAdapter.notifyDataSetChanged();
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_PURCHASES,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONObject root = obj.getJSONObject("purchases");
                        JSONArray array = root.getJSONArray("data");
                        totalSpent.setText("$" + root.getInt("spent"));
                        totalCredits.setText("$" + root.getInt("credits"));

                        if (root.getInt("spent") == 0) {
                            findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                            totalSpent.setText("$0");
                        }

                        if (array.length() != 0) {
                            findViewById(R.id.no_items).setVisibility(View.GONE);
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Purchase purchase = new Purchase(object.getInt("id"), object.getInt("purchase_id"), object.getString("product_id"), object.getString("product_prefix"), object.getInt("product_price"), object.getString("date"));
                            purchaseList.add(purchase);
                            purchasesAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                        totalSpent.setText("$0");
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestPurchases());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                params.put("token", consolePreferences.loadToken());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onRedeemedListener(Boolean isRedeemed) {
        if (isRedeemed) {
            requestPurchases();
        }
    }
}