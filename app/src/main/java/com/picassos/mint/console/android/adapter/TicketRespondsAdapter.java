package com.picassos.mint.console.android.adapter;

import android.annotation.SuppressLint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.interfaces.OnTicketClickListener;
import com.picassos.mint.console.android.interfaces.OnTicketLongClickListener;
import com.picassos.mint.console.android.models.TicketResponds;

import java.util.List;

public class TicketRespondsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<TicketResponds> ticketRespondsList;

    public TicketRespondsAdapter(List<TicketResponds> ticketRespondsList) {
        this.ticketRespondsList = ticketRespondsList;
    }

    static class TicketRespondsHolder extends RecyclerView.ViewHolder {

        TextView title, description;

        TicketRespondsHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.ticket_title);
            description = itemView.findViewById(R.id.ticket_description);
        }

        @SuppressLint("SetTextI18n")
        public void setData(TicketResponds data) {
            title.setText(data.getSubject());
            description.setText(Html.fromHtml(data.getMessage()));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket_listitem, parent, false);
        return new TicketRespondsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TicketResponds tickets = ticketRespondsList.get(position);
        TicketRespondsHolder ticketsHolder = (TicketRespondsHolder) holder;
        ticketsHolder.setData(tickets);
    }

    @Override
    public int getItemCount() {
        return ticketRespondsList.size();
    }

}
