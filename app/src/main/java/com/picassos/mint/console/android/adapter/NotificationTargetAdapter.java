package com.picassos.mint.console.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.interfaces.OnNotificationTargetClickListener;
import com.picassos.mint.console.android.models.NotificationTarget;

import java.util.List;

public class NotificationTargetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<NotificationTarget> notificationTargetList;
    private final OnNotificationTargetClickListener listener;

    public NotificationTargetAdapter(List<NotificationTarget> notificationTargetList, OnNotificationTargetClickListener listener) {
        this.notificationTargetList = notificationTargetList;
        this.listener = listener;
    }

    static class NotificationTargetHolder extends RecyclerView.ViewHolder {

        TextView title;

        NotificationTargetHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_target);
        }

        public void setData(NotificationTarget data) {
            title.setText(data.getTitle());
        }

        void bind(final NotificationTarget item, final OnNotificationTargetClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_target_listitem, parent, false);
        return new NotificationTargetHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NotificationTarget target = notificationTargetList.get(position);
        NotificationTargetHolder notificationTargetHolder = (NotificationTargetHolder) holder;
        notificationTargetHolder.setData(target);
        notificationTargetHolder.bind(notificationTargetList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return notificationTargetList.size();
    }

}
