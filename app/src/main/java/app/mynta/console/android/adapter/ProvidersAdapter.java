package app.mynta.console.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.mynta.console.android.R;

import app.mynta.console.android.interfaces.OnProviderClickListener;
import app.mynta.console.android.models.Providers;

import java.util.List;

public class ProvidersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Providers> providersList;
    private final OnProviderClickListener listener;

    public ProvidersAdapter(List<Providers> providersList, OnProviderClickListener listener) {
        this.providersList = providersList;
        this.listener = listener;
    }

    static class ProvidersHolder extends RecyclerView.ViewHolder {

        TextView title;

        ProvidersHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.provider);
        }

        public void setData(Providers data) {
            title.setText(data.getProvider());
        }

        void bind(final Providers item, final OnProviderClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_provider, parent, false);
        return new ProvidersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Providers providers = providersList.get(position);
        ProvidersHolder providersHolder = (ProvidersHolder) holder;
        providersHolder.setData(providers);
        providersHolder.bind(providersList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return providersList.size();
    }

}
