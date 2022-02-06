package com.picassos.mint.console.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.interfaces.OnAppCategoryClickListener;
import com.picassos.mint.console.android.models.AppCategory;
import com.picassos.mint.console.android.utils.Helper;

import java.util.List;

public class AppCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<AppCategory> appCategoryList;
    private final OnAppCategoryClickListener listener;

    public AppCategoryAdapter(Context context, List<AppCategory> appCategoryList, OnAppCategoryClickListener listener) {
        this.context = context;
        this.appCategoryList = appCategoryList;
        this.listener = listener;
    }

    class AppCategoryHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView thumbnail;

        AppCategoryHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.app_category_title);
            thumbnail = itemView.findViewById(R.id.app_category_thumbnail);
        }

        public void setData(AppCategory data) {
            title.setText(data.getTitle());
            thumbnail.setImageResource(Helper.getDrawableByName(context, data.getThumbnail()));
        }

        void bind(final AppCategory item, final OnAppCategoryClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_category_listitem, parent, false);
        return new AppCategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AppCategory appCategory = appCategoryList.get(position);
        AppCategoryHolder appCategoryHolder = (AppCategoryHolder) holder;
        appCategoryHolder.setData(appCategory);
        appCategoryHolder.bind(appCategoryList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return appCategoryList.size();
    }
}