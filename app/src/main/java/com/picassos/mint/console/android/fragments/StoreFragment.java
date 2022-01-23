package com.picassos.mint.console.android.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.picassos.mint.console.android.Config;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.store.ViewProductActivity;
import com.picassos.mint.console.android.adapter.AffiliateProductsAdapter;
import com.picassos.mint.console.android.adapter.ProductsAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.libraries.showcaseview.GuideView;
import com.picassos.mint.console.android.libraries.showcaseview.config.DismissType;
import com.picassos.mint.console.android.libraries.showcaseview.config.Gravity;
import com.picassos.mint.console.android.models.Product;
import com.picassos.mint.console.android.models.ProductAffiliate;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.AffiliateProductDetailsBottomSheetModal;
import com.picassos.mint.console.android.sheets.ManageAccountsBottomSheetModal;
import com.picassos.mint.console.android.utils.AboutDialog;
import com.picassos.mint.console.android.utils.RequestDialog;

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

    // Affiliate Product
    private final List<ProductAffiliate> productAffiliateList = new ArrayList<>();
    private AffiliateProductsAdapter affiliateProductsAdapter;

    // fonts
    private Typeface title, content;

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

        title = Typeface.createFromAsset(requireActivity().getAssets(), "fonts/poppins_bold.ttf");
        content = Typeface.createFromAsset(requireActivity().getAssets(), "fonts/poppins_regular.ttf");

        // username profile
        TextView usernameProfile = view.findViewById(R.id.username_profile);
        usernameProfile.setText(consolePreferences.loadUsername().substring(0, 1));
        // open profile
        view.findViewById(R.id.open_profile).setOnClickListener(v -> {
            AboutDialog aboutDialog = new AboutDialog(requireContext(), getActivity());
            aboutDialog.show();
        });
        view.findViewById(R.id.open_profile).setOnLongClickListener(v -> {
            ManageAccountsBottomSheetModal manageAccountsBottomSheetModal = new ManageAccountsBottomSheetModal();
            manageAccountsBottomSheetModal.show(getChildFragmentManager(), "TAG");
            return true;
        });

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

        // Initialize affiliate recyclerview
        RecyclerView affiliateRecyclerview = view.findViewById(R.id.recycler_affiliate);

        affiliateProductsAdapter = new AffiliateProductsAdapter(productAffiliateList, click -> {
            bundle.putString("thumbnail", click.getImage_preview());
            bundle.putString("title", click.getTitle());
            bundle.putFloat("rating", click.getRating());
            bundle.putInt("price", click.getPrice() / 100);
            bundle.putString("url", click.getUrl());
            AffiliateProductDetailsBottomSheetModal affiliateProductDetailsBottomSheetModal = new AffiliateProductDetailsBottomSheetModal();
            affiliateProductDetailsBottomSheetModal.setArguments(bundle);
            affiliateProductDetailsBottomSheetModal.show(getChildFragmentManager(), "TAG");
        });

        affiliateRecyclerview.setAdapter(affiliateProductsAdapter);
        affiliateRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        // request affiliate products
        requestAffiliateProducts();

        // Refresh Layout
        SwipeRefreshLayout refresh = view.findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }
            requestProducts();
            requestAffiliateProducts();
        });
    }

    /**
     * request affiliate products
     */
    @SuppressLint("NotifyDataSetChanged")
    private void requestAffiliateProducts() {
        productAffiliateList.clear();
        affiliateProductsAdapter.notifyDataSetChanged();
        requestDialog.show();
        view.findViewById(R.id.store_container).setVisibility(View.VISIBLE);
        view.findViewById(R.id.internet_connection).setVisibility(View.GONE);
        StringRequest request = new StringRequest(Request.Method.GET, "https://api.envato.com/v3/market/user/collection?id=" + Config.ENVATO_COLLECTION_ID,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray array = obj.getJSONArray("items");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);

                            ProductAffiliate productAffiliate = new ProductAffiliate(object.getInt("id"), object.getString("name"), object.getInt("number_of_sales"), object.getString("url"), object.getString("updated_at"), object.getString("description"), object.getInt("price_cents"), (float) object.getDouble("rating"), object.getInt("rating_count"), object.getString("published_at"), object.getJSONObject("previews").getJSONObject("landscape_preview").getString("landscape_url"));
                            productAffiliateList.add(productAffiliate);
                            affiliateProductsAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            view.findViewById(R.id.store_container).setVisibility(View.GONE);
            view.findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            view.findViewById(R.id.try_again).setOnClickListener(v -> {
                requestProducts();
                requestAffiliateProducts();
            });
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + Config.ENVATO_PERSONAL_TOKEN);
                return headers;
            }
        };

        Volley.newRequestQueue(requireActivity().getApplicationContext()).add(request);
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
                view.findViewById(R.id.try_again).setOnClickListener(v -> {
                    requestProducts();
                    requestAffiliateProducts();
                });
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
        new GuideView.Builder(requireContext())
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
                .show();
    }
    public void requestStoreGuideTwo() {
        new GuideView.Builder(requireContext())
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
                .show();
    }
}
