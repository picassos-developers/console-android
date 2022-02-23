package app.mynta.console.android.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.mynta.console.android.R;

import app.mynta.console.android.activities.store.ViewProductActivity;
import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.adapter.ProductsAdapter;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.models.Product;
import app.mynta.console.android.utils.RequestDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreFragment extends Fragment {

    View view;

    Bundle bundle;
    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    // Product
    private final List<Product> productList = new ArrayList<>();
    private ProductsAdapter productsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_store, container, false);

        // initialize console shared preferences
        consolePreferences = new ConsolePreferences(requireContext());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize bundle
        bundle = new Bundle();

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());

        // Initialize store recyclerview
        RecyclerView storeRecyclerview = view.findViewById(R.id.recycler_store);

        productsAdapter = new ProductsAdapter(false, productList, click -> {
            Intent intent = new Intent(requireContext(), ViewProductActivity.class);
            intent.putExtra("product_id", click.getProductId());
            startActivity(intent);
        });

        storeRecyclerview.setAdapter(productsAdapter);
        storeRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));

        // request products
        requestProducts();

        // Refresh Layout
        SwipeRefreshLayout refresh = view.findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }
            requestProducts();
        });
    }

    /**
     * request products
     */
    @SuppressLint("NotifyDataSetChanged")
    private void requestProducts() {
        productList.clear();
        productsAdapter.notifyDataSetChanged();
        view.findViewById(R.id.store_container).setVisibility(View.VISIBLE);
        view.findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_PRODUCTS,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        JSONArray array = obj.getJSONArray("products");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            Product product = new Product(object.getInt("id"), object.getString("product_id"), object.getString("url"), object.getString("thumbnail"), object.getString("prefix"), object.getString("title"), object.getString("description"), object.getInt("price"), object.getInt("discount"));
                            productList.add(product);
                            productsAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
                requestDialog.dismiss();
                view.findViewById(R.id.store_container).setVisibility(View.GONE);
                view.findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
                view.findViewById(R.id.try_again).setOnClickListener(v -> requestProducts());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                return params;
            }
        };

        Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
    }

    /* guide start **/
    public void requestStoreGuideOne() {
     /*   new GuideView.Builder(requireContext())
                .setTitle(getString(R.string.guide_store_title_one))
                .setContentText(getString(R.string.guide_store_description_one))
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(view.findViewById(R.id.products_container))
                .setTitleTypeFace(title)
                .setContentTypeFace(content)
                .setContentTextSize(12)
                .setTitleTextSize(13)
                .setGuideListener(view -> requestStoreGuideTwo())
                .build()
                .show();*/
    }
    public void requestStoreGuideTwo() {
      /*  new GuideView.Builder(requireContext())
                .setTitle(getString(R.string.guide_store_title_two))
                .setContentText(getString(R.string.guide_store_description_two))
                .setGravity(Gravity.auto)
                .setDismissType(DismissType.anywhere)
                .setTargetView(view.findViewById(R.id.power_store_container))
                .setTitleTypeFace(title)
                .setContentTypeFace(content)
                .setContentTextSize(12)
                .setTitleTextSize(13)
                .setGuideListener(view -> consolePreferences.setStoreGuide(true))
                .build()
                .show();*/
    }
}
