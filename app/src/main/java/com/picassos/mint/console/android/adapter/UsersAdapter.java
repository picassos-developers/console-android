package com.picassos.mint.console.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.interfaces.OnUserClickListener;
import com.picassos.mint.console.android.models.Users;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Users> usersList;
    private final OnUserClickListener listener;

    public UsersAdapter(List<Users> usersList, OnUserClickListener listener) {
        this.usersList = usersList;
        this.listener = listener;
    }

    static class UsersHolder extends RecyclerView.ViewHolder {

        TextView icon, username, email;

        UsersHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.user_icon);
            username = itemView.findViewById(R.id.user_username);
            email = itemView.findViewById(R.id.user_email);
        }

        public void setData(Users data) {
            String getUsername = data.getUsername();
            String getEmail = data.getEmail();

            icon.setText(getUsername.substring(0, 1).toUpperCase());
            username.setText(getUsername);
            email.setText(getEmail);
        }

        void bind(final Users item, final OnUserClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_listitem, parent, false);
        return new UsersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Users users = usersList.get(position);
        UsersHolder usersHolder = (UsersHolder) holder;
        usersHolder.setData(users);
        usersHolder.bind(usersList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

}
