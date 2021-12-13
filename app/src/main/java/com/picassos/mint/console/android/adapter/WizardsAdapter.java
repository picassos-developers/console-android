package com.picassos.mint.console.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.interfaces.OnWizardClickListener;
import com.picassos.mint.console.android.interfaces.OnWizardLongClickListener;
import com.picassos.mint.console.android.models.Wizard;

import java.util.List;

public class WizardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Wizard> wizardList;
    private final OnWizardClickListener listener;
    private final OnWizardLongClickListener longListener;

    public WizardsAdapter(List<Wizard> wizardList, OnWizardClickListener listener, OnWizardLongClickListener longListener) {
        this.wizardList = wizardList;
        this.listener = listener;
        this.longListener = longListener;
    }

    static class WizardHolder extends RecyclerView.ViewHolder {

        TextView title, description, icon;

        WizardHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.wizard_title);
            description = itemView.findViewById(R.id.wizard_description);
            icon = itemView.findViewById(R.id.wizard_icon);
        }

        public void setData(Wizard data) {
            title.setText(data.getTitle());
            description.setText(data.getDescription());
            icon.setText(data.getTitle().substring(0, 1).toUpperCase());
        }

        void bind(final Wizard item, final OnWizardClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        void longBind(final Wizard item, final OnWizardLongClickListener listener) {
            itemView.setOnLongClickListener(v -> {
                listener.onItemLongClick(item);
                return true;
            });
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wizard_listitem, parent, false);
        return new WizardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Wizard wizard = wizardList.get(position);
        WizardHolder wizardHolder = (WizardHolder) holder;
        wizardHolder.setData(wizard);
        wizardHolder.bind(wizardList.get(position), listener);
        wizardHolder.longBind(wizardList.get(position), longListener);
    }

    @Override
    public int getItemCount() {
        return wizardList.size();
    }

}
