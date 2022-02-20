package app.mynta.console.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.mynta.console.android.R;

import app.mynta.console.android.interfaces.OnWizardClickListener;
import app.mynta.console.android.interfaces.OnWizardLongClickListener;
import app.mynta.console.android.models.Walkthrough;

import java.util.List;

public class WalkthroughAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Walkthrough> walkthroughList;
    private final OnWizardClickListener listener;
    private final OnWizardLongClickListener longListener;

    public WalkthroughAdapter(List<Walkthrough> walkthroughList, OnWizardClickListener listener, OnWizardLongClickListener longListener) {
        this.walkthroughList = walkthroughList;
        this.listener = listener;
        this.longListener = longListener;
    }

    static class WizardHolder extends RecyclerView.ViewHolder {

        TextView title, description, icon;

        WizardHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.walkthrough_title);
            description = itemView.findViewById(R.id.walkthrough_description);
            icon = itemView.findViewById(R.id.walkthrough_icon);
        }

        public void setData(Walkthrough data) {
            title.setText(data.getTitle());
            description.setText(data.getDescription());
            icon.setText(data.getTitle().substring(0, 1).toUpperCase());
        }

        void bind(final Walkthrough item, final OnWizardClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        void longBind(final Walkthrough item, final OnWizardLongClickListener listener) {
            itemView.setOnLongClickListener(v -> {
                listener.onItemLongClick(item);
                return true;
            });
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_walkthrough_listitem, parent, false);
        return new WizardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Walkthrough walkthrough = walkthroughList.get(position);
        WizardHolder wizardHolder = (WizardHolder) holder;
        wizardHolder.setData(walkthrough);
        wizardHolder.bind(walkthroughList.get(position), listener);
        wizardHolder.longBind(walkthroughList.get(position), longListener);
    }

    @Override
    public int getItemCount() {
        return walkthroughList.size();
    }

}
