package com.picassos.mint.console.android.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.interfaces.OnNavigationClickListener;
import com.picassos.mint.console.android.interfaces.OnNavigationLongClickListener;
import com.picassos.mint.console.android.models.Navigations;

import java.util.List;

public class NavigationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Navigations> navigationsList;
    private final OnNavigationClickListener listener;
    private final OnNavigationLongClickListener longListener;

    public NavigationsAdapter(List<Navigations> navigationsList, OnNavigationClickListener listener, OnNavigationLongClickListener longListener) {
        this.navigationsList = navigationsList;
        this.listener = listener;
        this.longListener = longListener;
    }

    static class NavigationsHolder extends RecyclerView.ViewHolder {

        TextView title, link, icon, behavior, premium;
        ImageView premiumIcon;

        NavigationsHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.navigation_title);
            link = itemView.findViewById(R.id.navigation_link);
            icon = itemView.findViewById(R.id.navigation_icon);
            behavior = itemView.findViewById(R.id.navigation_behavior);
            premium = itemView.findViewById(R.id.navigation_premium);
            premiumIcon = itemView.findViewById(R.id.premium);
        }

        public void setData(Navigations data) {
            title.setText(data.getTitle());
            link.setText(data.getLink());
            icon.setText(data.getIcon());
            behavior.setText(data.getBehavior());
            premium.setText(data.getPremium());
            if (data.getPremium().equals("false") || TextUtils.isEmpty(data.getPremium())) {
                premiumIcon.setVisibility(View.GONE);
            }
        }

        void bind(final Navigations item, final OnNavigationClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        void longBind(final Navigations item, final OnNavigationLongClickListener listener) {
            itemView.setOnLongClickListener(v -> {
                listener.onItemLongClick(item);
                return true;
            });
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_navigation_listitem, parent, false);
        return new NavigationsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Navigations navigations = navigationsList.get(position);
        NavigationsHolder navigationsHolder = (NavigationsHolder) holder;
        navigationsHolder.setData(navigations);
        navigationsHolder.bind(navigationsList.get(position), listener);
        navigationsHolder.longBind(navigationsList.get(position), longListener);
    }

    @Override
    public int getItemCount() {
        return navigationsList.size();
    }

}
