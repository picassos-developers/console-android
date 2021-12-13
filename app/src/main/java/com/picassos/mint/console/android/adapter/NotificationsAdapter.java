package com.picassos.mint.console.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.entities.NotificationEntity;
import com.picassos.mint.console.android.interfaces.OnNotificationClickListener;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<NotificationEntity> notifications;
    private final OnNotificationClickListener notificationListener;

    public NotificationsAdapter(Context context, List<NotificationEntity> notifications, OnNotificationClickListener notificationListener) {
        this.notifications = notifications;
        this.notificationListener = notificationListener;
    }

    class NotificationsHolder extends RecyclerView.ViewHolder {

        View isRead;
        TextView title, description;

        NotificationsHolder(@NonNull View notification) {
            super(notification);
            isRead = notification.findViewById(R.id.notification_is_read);
            title = notification.findViewById(R.id.notification_title);
            description = notification.findViewById(R.id.notification_description);
        }

        public void setData(NotificationEntity data) {
            title.setText(data.title);
            description.setText(data.content);
            // check if is read
            if (!data.read) {
                isRead.setVisibility(View.VISIBLE);
            } else {
                isRead.setVisibility(View.GONE);
            }
        }

        void bind(final NotificationEntity item, final OnNotificationClickListener listener) {
            itemView.setOnClickListener(v -> listener.onNotificationClicked(item));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_listitem, parent, false);
        return new NotificationsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NotificationEntity notification = notifications.get(position);
        NotificationsHolder notificationsHolder = (NotificationsHolder) holder;
        notificationsHolder.bind(notifications.get(position), notificationListener);
        notificationsHolder.setData(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

}
