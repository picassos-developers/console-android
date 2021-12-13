package com.picassos.mint.console.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.models.Purchase;
import com.picassos.mint.console.android.utils.Helper;

import java.util.List;

public class PurchasesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Purchase> purchaseList;

    public PurchasesAdapter(Context context, List<Purchase> purchaseList) {
        this.purchaseList = purchaseList;
        this.context = context;
    }

    class PurchaseHolder extends RecyclerView.ViewHolder {

        TextView id,
                    title, price, date;

        PurchaseHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.purchase_id);
            title = itemView.findViewById(R.id.purchase_title);
            price = itemView.findViewById(R.id.purchase_price);
            date = itemView.findViewById(R.id.purchase_date);
        }

        @SuppressLint("SetTextI18n")
        public void setData(Purchase data) {
            id.setText(context.getString(R.string.id) + " " + data.getPurchase_id());
            title.setText(data.getProduct_prefix());
            price.setText("$" + data.getProduct_price());
            date.setText(Helper.getFormattedDateString(data.getDate()));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_listitem, parent, false);
        return new PurchaseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Purchase purchase = purchaseList.get(position);
        PurchaseHolder purchaseHolder = (PurchaseHolder) holder;
        purchaseHolder.setData(purchase);
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }
}
