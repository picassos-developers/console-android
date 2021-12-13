package com.picassos.mint.console.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.interfaces.OnSectionClickListener;
import com.picassos.mint.console.android.models.Sections;

import java.util.List;

public class SectionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Sections> sectionsList;
    private final OnSectionClickListener listener;

    public SectionsAdapter(List<Sections> sectionsList, OnSectionClickListener listener) {
        this.sectionsList = sectionsList;
        this.listener = listener;
    }

    static class SectionsHolder extends RecyclerView.ViewHolder {

        TextView title, description;

        SectionsHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.section_title);
            description = itemView.findViewById(R.id.section_description);
        }

        public void setData(Sections data) {
            title.setText(data.getSection_title());
            description.setText(data.getSection_description());
        }

        void bind(final Sections item, final OnSectionClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section_listitem, parent, false);
        return new SectionsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Sections sections = sectionsList.get(position);
        SectionsHolder sectionsHolder = (SectionsHolder) holder;
        sectionsHolder.setData(sections);
        sectionsHolder.bind(sectionsList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return sectionsList.size();
    }

}
