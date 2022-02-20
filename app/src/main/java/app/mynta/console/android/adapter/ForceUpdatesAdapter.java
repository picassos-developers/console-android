package app.mynta.console.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.mynta.console.android.R;

import app.mynta.console.android.interfaces.OnForceUpdateClickListener;
import app.mynta.console.android.models.ForceUpdates;

import java.util.List;

public class ForceUpdatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ForceUpdates> updatesList;
    private final OnForceUpdateClickListener listener;

    public ForceUpdatesAdapter(List<ForceUpdates> updatesList, OnForceUpdateClickListener listener) {
        this.updatesList = updatesList;
        this.listener = listener;
    }

    static class ForceUpdatesHolder extends RecyclerView.ViewHolder {

        TextView title;

        ForceUpdatesHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.force_update);
        }

        public void setData(ForceUpdates data) {
            title.setText(data.getTitle());
        }

        void bind(final ForceUpdates item, final OnForceUpdateClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_force_update, parent, false);
        return new ForceUpdatesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ForceUpdates updates = updatesList.get(position);
        ForceUpdatesHolder forceUpdatesHolder = (ForceUpdatesHolder) holder;
        forceUpdatesHolder.setData(updates);
        forceUpdatesHolder.bind(updatesList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return updatesList.size();
    }

}
