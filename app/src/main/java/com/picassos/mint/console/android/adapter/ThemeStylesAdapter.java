package com.picassos.mint.console.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.interfaces.OnThemeStyleClickListener;
import com.picassos.mint.console.android.models.ThemeStyles;
import com.picassos.mint.console.android.utils.Helper;

import java.util.List;

public class ThemeStylesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ThemeStyles> themeStylesList;
    private final OnThemeStyleClickListener listener;

    public ThemeStylesAdapter(List<ThemeStyles> themeStylesList, OnThemeStyleClickListener listener) {
        this.themeStylesList = themeStylesList;
        this.listener = listener;
    }

    static class ThemeHolder extends RecyclerView.ViewHolder {

        TextView title;
        View thumbnail;

        ThemeHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.theme_title);
            thumbnail = itemView.findViewById(R.id.theme_thumbnail);
        }

        public void setData(ThemeStyles data) {
            title.setText(data.getTitle());
            Helper.setGradientBackground(thumbnail, data.getGradient_start(), data.getGradient_end());
        }

        void bind(final ThemeStyles item, final OnThemeStyleClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theme_style_listitem, parent, false);
        return new ThemeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ThemeStyles themeStyles = themeStylesList.get(position);
        ThemeHolder themeHolder = (ThemeHolder) holder;
        themeHolder.setData(themeStyles);
        themeHolder.bind(themeStylesList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return themeStylesList.size();
    }
}