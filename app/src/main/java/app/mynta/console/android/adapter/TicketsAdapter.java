package app.mynta.console.android.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.mynta.console.android.R;

import app.mynta.console.android.interfaces.OnTicketClickListener;
import app.mynta.console.android.interfaces.OnTicketLongClickListener;
import app.mynta.console.android.models.Tickets;

import java.util.List;

public class TicketsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Tickets> ticketsList;
    private final OnTicketClickListener listener;
    private final OnTicketLongClickListener longListener;

    public TicketsAdapter(List<Tickets> ticketsList, OnTicketClickListener listener, OnTicketLongClickListener longListener) {
        this.ticketsList = ticketsList;
        this.listener = listener;
        this.longListener = longListener;
    }

    static class TicketsHolder extends RecyclerView.ViewHolder {

        TextView title, description;

        TicketsHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.ticket_title);
            description = itemView.findViewById(R.id.ticket_description);
        }

        @SuppressLint("SetTextI18n")
        public void setData(Tickets data) {
            title.setText("[" + data.getTicketId() + "] - " + data.getTicketSubject());
            description.setText(data.getTicketMessage());
        }

        void bind(final Tickets item, final OnTicketClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        void longBind(final Tickets item, final OnTicketLongClickListener listener) {
            itemView.setOnLongClickListener(v -> {
                listener.onItemLongClick(item);
                return true;
            });
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket_listitem, parent, false);
        return new TicketsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Tickets tickets = ticketsList.get(position);
        TicketsHolder ticketsHolder = (TicketsHolder) holder;
        ticketsHolder.setData(tickets);
        ticketsHolder.bind(ticketsList.get(position), listener);
        ticketsHolder.longBind(ticketsList.get(position), longListener);
    }

    @Override
    public int getItemCount() {
        return ticketsList.size();
    }

}
