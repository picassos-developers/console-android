package com.picassos.mint.console.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.interfaces.OnTicketTypeClickListener;
import com.picassos.mint.console.android.models.TicketType;

import java.util.List;

public class TicketTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<TicketType> ticketTypeList;
    private final OnTicketTypeClickListener listener;

    public TicketTypeAdapter(List<TicketType> ticketTypeList, OnTicketTypeClickListener listener) {
        this.ticketTypeList = ticketTypeList;
        this.listener = listener;
    }

    static class TicketTypeHolder extends RecyclerView.ViewHolder {

        TextView title;

        TicketTypeHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.ticket_type);
        }

        public void setData(TicketType data) {
            title.setText(data.getType());
        }

        void bind(final TicketType item, final OnTicketTypeClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket_type, parent, false);
        return new TicketTypeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TicketType type = ticketTypeList.get(position);
        TicketTypeHolder ticketTypeHolder = (TicketTypeHolder) holder;
        ticketTypeHolder.setData(type);
        ticketTypeHolder.bind(ticketTypeList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return ticketTypeList.size();
    }

}
