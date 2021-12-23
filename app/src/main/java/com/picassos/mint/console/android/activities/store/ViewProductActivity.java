package com.picassos.mint.console.android.activities.store;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.about.PurchasesActivity;
import com.picassos.mint.console.android.activities.store.powerups.customdarkmode.SetupCustomDarkModeActivity;
import com.picassos.mint.console.android.activities.store.powerups.policies.SetupPoliciesActivity;
import com.picassos.mint.console.android.adapter.ProductsAdapter;
import com.picassos.mint.console.android.adapter.StoreReviewAdapter;
import com.picassos.mint.console.android.constants.API;
import com.picassos.mint.console.android.models.Product;
import com.picassos.mint.console.android.models.StoreReview;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.sheets.ChooseCustomDarkmodePackageAppBottomSheetModal;
import com.picassos.mint.console.android.sheets.PaymentMethodBottomSheetModal;
import com.picassos.mint.console.android.utils.Helper;
import com.picassos.mint.console.android.utils.RequestDialog;
import com.picassos.mint.console.android.utils.Toasto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewProductActivity extends AppCompatActivity implements
        ChooseCustomDarkmodePackageAppBottomSheetModal.OnSelectPackageListener,
        PaymentMethodBottomSheetModal.OnSelectListener, PurchasesUpdatedListener {

    // Products & Reviews
    private final List<Product> productList = new ArrayList<>();
    private final List<StoreReview> reviewList = new ArrayList<>();
    BillingClient billingClient;
    SkuDetails productInfo;
    private Bundle bundle;
    private RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;
    private boolean isInitialized = false;
    private int isPurchased;
    private String productId;
    // product details
    private int credits;
    private int price;
    private String prefix;
    private Button purchaseProduct;
    private SimpleDraweeView productThumbnail;
    private TextView productTitle;
    private TextView productDescription;
    private TextView productProcess;
    private TextView productDiscount;
    private ProductsAdapter productsAdapter;
    private StoreReviewAdapter storeReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.darkMode(this);

        consolePreferences = new ConsolePreferences(this);

        setContentView(R.layout.activity_view_product);

        // initialize bundle
        bundle = new Bundle();

        // initialize request dialog
        requestDialog = new RequestDialog(this);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // purchases
        findViewById(R.id.purchases).setOnClickListener(v -> startActivity(new Intent(ViewProductActivity.this, PurchasesActivity.class)));

        // product id
        productId = getIntent().getStringExtra("product_id");

        // initialize fields
        productThumbnail = findViewById(R.id.product_thumbnail);
        productTitle = findViewById(R.id.product_title);
        productDescription = findViewById(R.id.product_description);
        productProcess = findViewById(R.id.product_process);
        productDiscount = findViewById(R.id.product_discount);

        // purchase product
        purchaseProduct = findViewById(R.id.purchase_product);

        // initialize billing client
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(this)
                .build();
        connectPlayBilling();

        requestProduct(productId);

        // Initialize store recyclerview
        RecyclerView storeRecyclerview = findViewById(R.id.recycler_store);
        productsAdapter = new ProductsAdapter(true, productList, click -> {
            Intent intent = new Intent(this, ViewProductActivity.class);
            intent.putExtra("product_id", click.getProductId());
            startActivity(intent);
        });
        storeReviewAdapter = new StoreReviewAdapter(reviewList);

        storeRecyclerview.setAdapter(productsAdapter);
        storeRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // request products
        requestRecommendedProducts();

        // Initialize reviews recyclerview
        RecyclerView reviewsRecyclerview = findViewById(R.id.recycler_reviews);
        reviewsRecyclerview.setAdapter(storeReviewAdapter);

        reviewsRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // request reviews
        requestReviews();

        // Refresh Layout
        SwipeRefreshLayout refresh = findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }

            requestProduct(productId);
            requestRecommendedProducts();
            requestReviews();
        });
    }

    /**
     * request product details
     * @param id for product id
     */
    @SuppressLint("SetTextI18n")
    private void requestProduct(String id) {
        findViewById(R.id.product_container).setVisibility(View.GONE);
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_PRODUCT,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONObject root = obj.getJSONObject("product");
                        JSONArray details = root.getJSONArray("details");
                        isPurchased = root.getInt("is_purchased");
                        credits = root.getInt("credits");

                        for (int i = 0; i < details.length(); i++) {
                            JSONObject object = details.getJSONObject(i);
                            productTitle.setText(object.getString("title"));
                            productDescription.setText(object.getString("description"));
                            productProcess.setText(object.getString("process"));
                            productDiscount.setText(getString(R.string.instead_of) + " $" + object.getInt("discount"));
                            productDiscount.setPaintFlags(productDiscount.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                            productThumbnail.setController(
                                    Fresco.newDraweeControllerBuilder()
                                            .setTapToRetryEnabled(true)
                                            .setUri(object.getString("thumbnail"))
                                            .build());
                            price = object.getInt("price");
                            // prefix
                            prefix = object.getString("prefix");
                            TextView toolbarTitle = findViewById(R.id.toolbar_title);
                            toolbarTitle.setText(object.getString("prefix"));
                            initializePurchaseButton(isPurchased, object.getString("product_id"), object.getInt("price"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    findViewById(R.id.product_container).setVisibility(View.VISIBLE);
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.product_container).setVisibility(View.GONE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestProduct(id));
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("product_id", id);
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                params.put("token", consolePreferences.loadToken());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request initialize purchase button
     * @param isPurchased to check if product purchased
     * @param productId for product id
     *                  product id format: pu_0001_policies
     * @param price for product price
     */
    @SuppressLint("SetTextI18n")
    private void initializePurchaseButton(int isPurchased, String productId, int price) {
        if (isPurchased == 1) {
            if (productId.equals("pu_0001_policies")) {
                purchaseProduct.setText(getString(R.string.generate_policies));
                purchaseProduct.setOnClickListener(v -> startActivity(new Intent(ViewProductActivity.this, SetupPoliciesActivity.class)));
            } else if (productId.equals("pu_0002_custom_darkmode")) {
                purchaseProduct.setText(getString(R.string.request_custom_darkmode));
                purchaseProduct.setOnClickListener(v -> startActivity(new Intent(ViewProductActivity.this, SetupCustomDarkModeActivity.class)));
            }
        } else if (isPurchased == 0) {
            if (productId.equals("pu_0001_policies")) {
                purchaseProduct.setText(getString(R.string.continue_purchase) + " " + "($" + price + ")");
                purchaseProduct.setOnClickListener(v -> {
                    bundle.putInt("credits", credits);
                    bundle.putInt("price", price);
                    PaymentMethodBottomSheetModal paymentMethodBottomSheetModal = new PaymentMethodBottomSheetModal();
                    paymentMethodBottomSheetModal.setArguments(bundle);
                    paymentMethodBottomSheetModal.show(getSupportFragmentManager(), "TAG");
                });
            } else if (productId.equals("pu_0002_custom_darkmode")) {
                purchaseProduct.setText(getString(R.string.continue_purchase) + " " + "($" + price + " " + getString(R.string.per_page) + ")");
                purchaseProduct.setOnClickListener(v -> {
                    ChooseCustomDarkmodePackageAppBottomSheetModal chooseCustomDarkmodePackageAppBottomSheetModal = new ChooseCustomDarkmodePackageAppBottomSheetModal();
                    chooseCustomDarkmodePackageAppBottomSheetModal.show(getSupportFragmentManager(), "TAG");
                });
            }
        }
    }

    /**
     * request purchase product
     */
    private void requestPurchaseProduct(String method) {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_PURCHASE_PRODUCT,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject root = object.getJSONObject("purchase");
                        JSONObject details = root.getJSONObject("details");
                        JSONObject responseCode = root.getJSONObject("response");

                        switch (responseCode.getInt("code")) {
                            case 200:
                                initializePurchaseButton(1, details.getString("product_id"), details.getInt("product_price"));
                                startActivity(new Intent(ViewProductActivity.this, ProductPurchasedActivity.class));
                                break;
                            case 403:
                                Toasto.show_toast(this, getString(R.string.in_trouble_exception), 1, 1);
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(this, getString(R.string.in_trouble_exception), 1, 1);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                params.put("token", consolePreferences.loadToken());
                params.put("product_id", productId);
                params.put("product_prefix", prefix);
                params.put("product_price", String.valueOf(price));
                params.put("payment_method", method);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request products
     */
    @SuppressLint("NotifyDataSetChanged")
    private void requestRecommendedProducts() {
        productList.clear();
        productsAdapter.notifyDataSetChanged();
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_RECOMMENDED_PRODUCTS,
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
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestRecommendedProducts());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret_api_key", consolePreferences.loadSecretAPIKey());
                params.put("product_id", getIntent().getStringExtra("product_id"));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request reviews
     */
    @SuppressLint("NotifyDataSetChanged, SetTextI18n")
    private void requestReviews() {
        reviewList.clear();
        storeReviewAdapter.notifyDataSetChanged();
        findViewById(R.id.internet_connection).setVisibility(View.GONE);
        requestDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_STORE_REVIEWS,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONObject root = obj.getJSONObject("reviews");
                        JSONArray data = root.getJSONArray("data");
                        JSONObject customer_review = root.getJSONObject("customer_review");
                        int isReviewed = customer_review.getInt("is_reviewed");
                        // check if user reviewed the product
                        if (isReviewed == 1) {
                            findViewById(R.id.write_review).setVisibility(View.GONE);
                            findViewById(R.id.your_review_container).setVisibility(View.VISIBLE);
                            TextView reviewAuthorIcon = findViewById(R.id.review_author_icon);
                            reviewAuthorIcon.setText(customer_review.getString("review_author").substring(0, 1).toUpperCase());
                            TextView reviewAuthor = findViewById(R.id.review_author);
                            reviewAuthor.setText(customer_review.getString("review_author").substring(0, 1).toUpperCase() + customer_review.getString("review_author").substring(1));
                            TextView reviewDescription = findViewById(R.id.review_description);
                            reviewDescription.setText(customer_review.getString("review_description"));
                            TextView reviewDate = findViewById(R.id.review_date);
                            reviewDate.setText(Helper.getFormattedDateString(customer_review.getString("review_date")));
                            TextView reviewRating = findViewById(R.id.review_rating);
                            reviewRating.setText(String.valueOf(customer_review.getInt("review_rating")));
                            // edit review
                            findViewById(R.id.edit_review).setOnClickListener(v -> {
                                Intent intent = new Intent(ViewProductActivity.this, WriteReviewActivity.class);
                                intent.putExtra("product_id", productId);
                                try {
                                    intent.putExtra("action", "update");
                                    intent.putExtra("id", customer_review.getInt("review_id"));
                                    intent.putExtra("review", customer_review.getString("review_description"));
                                    intent.putExtra("rating", customer_review.getInt("review_rating"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivityForResult.launch(intent);
                            });
                            // delete review
                            findViewById(R.id.delete_review).setOnClickListener(v -> {
                                try {
                                    requestDeleteReview(customer_review.getInt("review_id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else if (isReviewed == 0) {
                            findViewById(R.id.your_review_container).setVisibility(View.GONE);
                            findViewById(R.id.write_review).setVisibility(View.VISIBLE);
                            findViewById(R.id.write_review).setOnClickListener(v -> {
                                Intent intent = new Intent(ViewProductActivity.this, WriteReviewActivity.class);
                                intent.putExtra("action", "post");
                                intent.putExtra("product_id", productId);
                                startActivityForResult.launch(intent);
                            });
                        } else if (isReviewed == -1) {
                            findViewById(R.id.your_review_container).setVisibility(View.GONE);
                            findViewById(R.id.write_review).setVisibility(View.GONE);
                        }

                        if (data.length() == 0) {
                            findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.no_items).setVisibility(View.GONE);
                        }

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            StoreReview product = new StoreReview(object.getInt("id"), object.getString("author"), object.getString("product_id"), object.getString("review"), object.getInt("rating"), object.getString("date"));
                            reviewList.add(product);
                            storeReviewAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            findViewById(R.id.internet_connection).setVisibility(View.VISIBLE);
            findViewById(R.id.try_again).setOnClickListener(v -> requestRecommendedProducts());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", consolePreferences.loadToken());
                params.put("product_id", productId);
                params.put("is_purchased", String.valueOf(isPurchased));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * request delete review
     * @param id for review id
     */
    private void requestDeleteReview(int id) {
        requestDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, API.API_URL + API.REQUEST_DELETE_REVIEW,
                response -> {
                    switch (response) {
                        case "200":
                            Toasto.show_toast(this, getString(R.string.review_deleted_successfully), 1, 1);
                            requestReviews();
                            break;
                        case "403":
                            Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 2);
                            break;
                    }
                    requestDialog.dismiss();
                }, error -> {
            requestDialog.dismiss();
            Toasto.show_toast(this, getString(R.string.unknown_issue), 1, 1);
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", consolePreferences.loadToken());
                params.put("review_id", String.valueOf(id));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onSelectPackageListener(String id, int pages) {
        productId = id;
        price = (pages * 3);
        connectPlayBilling();
        bundle.putInt("credits", credits);
        bundle.putInt("price", price);
        PaymentMethodBottomSheetModal paymentMethodBottomSheetModal = new PaymentMethodBottomSheetModal();
        paymentMethodBottomSheetModal.setArguments(bundle);
        paymentMethodBottomSheetModal.show(getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onSelectListener(String method) {
        switch (method) {
            case "google_pay":
                if (isInitialized) {
                    billingClient.launchBillingFlow(this, BillingFlowParams.newBuilder().setSkuDetails(productInfo).build());
                }
                break;
            case "credits":
                requestPurchaseProduct("credits");
                break;
        }
        if (productId.startsWith("pu_0001_custom_darkmode_package")) {
            productId = "pu_0002_custom_darkmode";
        }
    }

    /**
     * connect to google play billing
     */
    private void connectPlayBilling() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                connectPlayBilling();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    getProductDetails(productId);
                }
            }
        });
    }

    /**
     * get product details
     */
    private void getProductDetails(String id) {
        List<String> productIds = new ArrayList<> ();
        productIds.add(id);
        SkuDetailsParams getProductDetailsQuery = SkuDetailsParams
                .newBuilder()
                .setSkusList(productIds)
                .setType(BillingClient.SkuType.INAPP)
                .build();
        billingClient.querySkuDetailsAsync(
                getProductDetailsQuery,
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                        isInitialized = true;
                        productInfo = list.get(0);
                    }
                }
        );
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            for (Purchase purchase: list) {
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                    requestPurchaseProduct("google_pay");
                }
            }
        }
    }

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result != null && result.getResultCode() == RESULT_OK) {
            requestReviews();
        }
    });
}