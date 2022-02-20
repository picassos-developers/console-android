package app.mynta.console.android.adapter;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import app.mynta.console.android.R;

import app.mynta.console.android.interfaces.OnProductClickListener;
import app.mynta.console.android.models.Product;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final boolean isHorizontal;
    private final List<Product> productList;
    private final OnProductClickListener listener;

    public ProductsAdapter(boolean isHorizontal, List<Product> productList, OnProductClickListener listener) {
        this.isHorizontal = isHorizontal;
        this.productList = productList;
        this.listener = listener;
    }

    static class ProductHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView price;
        TextView discount;
        SimpleDraweeView thumbnail;

        ProductHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.product_title);
            price = itemView.findViewById(R.id.product_price);
            discount = itemView.findViewById(R.id.product_discount);
            thumbnail = itemView.findViewById(R.id.product_thumbnail);
        }

        @SuppressLint("SetTextI18n")
        public void setData(Product data) {
            title.setText(data.getTitle());
            price.setText("$" + data.getPrice());
            discount.setText("$" + data.getDiscount());
            discount.setPaintFlags(discount.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            thumbnail.setController(
                    Fresco.newDraweeControllerBuilder()
                            .setTapToRetryEnabled(true)
                            .setUri(data.getThumbnail())
                            .build());
        }

        void bind(final Product item, final OnProductClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (isHorizontal) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_horizontal, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        }
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = productList.get(position);
        ProductHolder productHolder = (ProductHolder) holder;
        productHolder.setData(product);
        productHolder.bind(productList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

}
