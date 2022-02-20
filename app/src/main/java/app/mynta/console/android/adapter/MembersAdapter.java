package app.mynta.console.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.mynta.console.android.R;

import app.mynta.console.android.interfaces.OnMemberClickListener;
import app.mynta.console.android.models.Members;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Members> membersList;
    private final OnMemberClickListener listener;
    private final Boolean isHome;

    public MembersAdapter(List<Members> membersList, OnMemberClickListener listener, Boolean isHome) {
        this.membersList = membersList;
        this.listener = listener;
        this.isHome = isHome;
    }

    static class MembersHolder extends RecyclerView.ViewHolder {

        TextView icon, username, email;

        MembersHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.member_icon);
            username = itemView.findViewById(R.id.member_username);
            email = itemView.findViewById(R.id.member_email);
        }

        public void setData(Members data) {
            String getUsername = data.getUsername();
            String getEmail = data.getEmail();

            icon.setText(getUsername.substring(0, 1).toUpperCase());
            username.setText(getUsername);
            email.setText(getEmail);
        }

        void bind(final Members item, final OnMemberClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (isHome) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_member_listitem, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_listitem, parent, false);
        }
        return new MembersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Members members = membersList.get(position);
        MembersHolder membersHolder = (MembersHolder) holder;
        membersHolder.setData(members);
        membersHolder.bind(membersList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

}
