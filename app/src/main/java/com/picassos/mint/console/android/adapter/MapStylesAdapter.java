package com.picassos.mint.console.android.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.interfaces.OnMapClickListener;
import com.picassos.mint.console.android.models.Maps;

import java.util.List;

public class MapStylesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Maps> mapsList;
    private final OnMapClickListener listener;

    public MapStylesAdapter(Context context, List<Maps> mapsList, OnMapClickListener listener) {
        this.context = context;
        this.mapsList = mapsList;
        this.listener = listener;
    }

    class MapsHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView design;

        MapsHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.map_style);
            design = itemView.findViewById(R.id.design);
        }

        public void setData(Maps data) {
            title.setText(data.getTitle());
            int resource = context.getResources().getIdentifier(context.getPackageName() + ":drawable/" + data.getDesign(), null, null);
            design.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), resource));
        }

        void bind(final Maps item, final OnMapClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map_style, parent, false);
        return new MapsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Maps maps = mapsList.get(position);
        MapsHolder mapsHolder = (MapsHolder) holder;
        mapsHolder.setData(maps);
        mapsHolder.bind(mapsList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return mapsList.size();
    }

}
