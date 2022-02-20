package app.mynta.console.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.mynta.console.android.R;
import app.mynta.console.android.models.ServerStatus;

import java.util.List;

public class ServerStatusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<ServerStatus> statusList;

    public ServerStatusAdapter(Context context, List<ServerStatus> statusList) {
        this.context = context;
        this.statusList = statusList;
    }

    class ServerStatusHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView status, date;

        ServerStatusHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.checkup_icon);
            status = itemView.findViewById(R.id.checkup_status);
            date = itemView.findViewById(R.id.checkup_date);
        }

        @SuppressLint("SetTextI18n")
        public void setData(ServerStatus data) {
            if (data.getStatus().equals("up")) {
                icon.setImageResource(R.drawable.icon_up_green);
                status.setText(context.getString(R.string.all_systems_operational));
            } else if (data.getStatus().equals("down")) {
                icon.setImageResource(R.drawable.icon_down_red);
                status.setText(context.getString(R.string.server_is_down));
            }
            date.setText(data.getDate());
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_server_status_listitem, parent, false);
        return new ServerStatusHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ServerStatus purchase = statusList.get(position);
        ServerStatusHolder serverStatusHolder = (ServerStatusHolder) holder;
        serverStatusHolder.setData(purchase);
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }
}
