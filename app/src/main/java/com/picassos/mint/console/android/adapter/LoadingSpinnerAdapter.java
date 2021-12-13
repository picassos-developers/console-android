package com.picassos.mint.console.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.interfaces.OnSpinnerClickListener;
import com.picassos.mint.console.android.models.LoadingSpinners;

import java.util.List;

public class LoadingSpinnerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<LoadingSpinners> loadingSpinnersList;
    private final OnSpinnerClickListener listener;

    public LoadingSpinnerAdapter(List<LoadingSpinners> loadingSpinnersList, OnSpinnerClickListener listener) {
        this.loadingSpinnersList = loadingSpinnersList;
        this.listener = listener;
    }

    static class SpinnerHolder extends RecyclerView.ViewHolder {

        TextView title;

        SpinnerHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.loading_spinner);
        }

        public void setData(LoadingSpinners data) {
            title.setText(data.getTitle());
        }

        void bind(final LoadingSpinners item, final OnSpinnerClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading_spinner, parent, false);
        return new SpinnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LoadingSpinners loadingSpinners = loadingSpinnersList.get(position);
        SpinnerHolder spinnerHolder = (SpinnerHolder) holder;
        spinnerHolder.setData(loadingSpinners);
        spinnerHolder.bind(loadingSpinnersList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return loadingSpinnersList.size();
    }

}
