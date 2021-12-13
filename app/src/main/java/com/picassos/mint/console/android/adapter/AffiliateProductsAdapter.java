package com.picassos.mint.console.android.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.interfaces.OnAffiliateProductClickListener;
import com.picassos.mint.console.android.models.ProductAffiliate;

import java.util.List;

public class AffiliateProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ProductAffiliate> productList;
    private final OnAffiliateProductClickListener listener;

    public AffiliateProductsAdapter(List<ProductAffiliate> productList, OnAffiliateProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    static class ProductHolder extends RecyclerView.ViewHolder {

        TextView title, price;
        SimpleDraweeView thumbnail;

        ProductHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.product_title);
            price = itemView.findViewById(R.id.product_price);
            thumbnail = itemView.findViewById(R.id.product_thumbnail);
        }

        @SuppressLint("SetTextI18n")
        public void setData(ProductAffiliate data) {
            title.setText(data.getTitle());
            price.setText("$" + data.getPrice() / 100);
            thumbnail.setController(
                    Fresco.newDraweeControllerBuilder()
                            .setTapToRetryEnabled(true)
                            .setUri(data.getImage_preview())
                            .build());
        }

        void bind(final ProductAffiliate item, final OnAffiliateProductClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_affiliate, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductAffiliate product = productList.get(position);
        ProductHolder productHolder = (ProductHolder) holder;
        productHolder.setData(product);
        productHolder.bind(productList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}
