package com.picassos.mint.console.android.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.interfaces.OnNavigationClickListener;
import com.picassos.mint.console.android.interfaces.OnNavigationStateListener;
import com.picassos.mint.console.android.models.Navigations;

import java.util.List;

public class NavigationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Navigations> navigations;
    private final OnNavigationClickListener listener;
    private final OnNavigationStateListener stateListener;

    public NavigationsAdapter(List<Navigations> navigations, OnNavigationClickListener listener, OnNavigationStateListener stateListener) {
        this.navigations = navigations;
        this.listener = listener;
        this.stateListener = stateListener;
    }

    static class NavigationsHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView label;
        SwitchCompat action;

        NavigationsHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.provider_icon);
            label = itemView.findViewById(R.id.provider_label);
            action = itemView.findViewById(R.id.provider_action);
        }

        @SuppressLint("SetTextI18n")
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
            if (data.getEnabled() == 1) { action.setChecked(true); } else if (data.getEnabled() == 0) { action.setChecked(false); }
        }

        void bind(final Navigations item, final OnNavigationClickListener listener) {
            icon.setOnClickListener(v -> listener.onItemClick(item));
            label.setOnClickListener(v -> listener.onItemClick(item));
        }

        void bindState(final Navigations item, final OnNavigationStateListener stateListener) {
            action.setOnCheckedChangeListener((compoundButton, b) -> stateListener.onItemClick(item));
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
        Navigations Navigations = navigations.get(position);
        NavigationsHolder navigationsHolder = (NavigationsHolder) holder;
        navigationsHolder.setData(Navigations);
        navigationsHolder.bind(navigations.get(position), listener);
        navigationsHolder.bindState(navigations.get(position), stateListener);
    }

    @Override
    public int getItemCount() {
        return navigations.size();
    }

}
