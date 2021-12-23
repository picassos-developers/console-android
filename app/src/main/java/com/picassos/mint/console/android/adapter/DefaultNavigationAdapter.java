package com.picassos.mint.console.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.interfaces.OnNavigationClickListener;
import com.picassos.mint.console.android.models.Navigations;

import java.util.List;

public class DefaultNavigationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Navigations> navigations;
    private final OnNavigationClickListener listener;
    private final int defaultNavigation;

    public DefaultNavigationAdapter(Context context, List<Navigations> navigations, int defaultNavigation, OnNavigationClickListener listener) {
        this.context = context;
        this.navigations = navigations;
        this.defaultNavigation = defaultNavigation;
        this.listener = listener;
    }

    class DefaultNavigationHolder extends RecyclerView.ViewHolder {

        View providerContainer;
        ImageView icon;
        TextView label;

        DefaultNavigationHolder(@NonNull View itemView) {
            super(itemView);
            providerContainer = itemView.findViewById(R.id.provider_container);
            icon = itemView.findViewById(R.id.provider_icon);
            label = itemView.findViewById(R.id.provider_label);
        }

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        public void setData(Navigations data) {
            switch (data.getType()) {
                case "webview":
                    icon.setImageResource(R.drawable.icon_webview);
                    break;
                case "wordpress":
                    icon.setImageResource(R.drawable.icon_wordpress);
                    break;
                case "youtube":
                    icon.setImageResource(R.drawable.icon_youtube);
                    break;
                case "vimeo":
                    icon.setImageResource(R.drawable.icon_vimeo);
                    break;
                case "facebook":
                    icon.setImageResource(R.drawable.icon_facebook);
                    break;
                case "pinterest":
                    icon.setImageResource(R.drawable.icon_pinterest);
                    break;
                case "imgur":
                    icon.setImageResource(R.drawable.icon_imgur);
                    break;
                case "google_maps":
                    icon.setImageResource(R.drawable.icon_maps);
                    break;
            }
            label.setText(data.getLabel());

            if (data.getIdentifier() == defaultNavigation) {
                providerContainer.setBackground(context.getDrawable(R.drawable.item_background_darker_selected));
            } else {
                providerContainer.setBackground(context.getDrawable(R.drawable.item_background_darker));
            }
        }

        void bind(final Navigations item, final OnNavigationClickListener listener) {
            providerContainer.setOnClickListener(v -> listener.onItemClick(item));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_default_navigation_listitem, parent, false);
        return new DefaultNavigationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Navigations Navigations = navigations.get(position);
        DefaultNavigationHolder defaultNavigationHolder = (DefaultNavigationHolder) holder;
        defaultNavigationHolder.setData(Navigations);
        defaultNavigationHolder.bind(navigations.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return navigations.size();
    }

}
