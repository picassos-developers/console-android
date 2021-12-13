package com.picassos.mint.console.android.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.entities.AccountEntity;
import com.picassos.mint.console.android.interfaces.OnAccountClickListener;

import java.util.List;

public class StepAccountsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<AccountEntity> accounts;
    private final OnAccountClickListener accountListener;

    public StepAccountsAdapter(List<AccountEntity> accounts, OnAccountClickListener accountListener) {
        this.accounts = accounts;
        this.accountListener = accountListener;
    }

    static class AccountsHolder extends RecyclerView.ViewHolder {

        TextView avatar;
        TextView username;

        AccountsHolder(@NonNull View account) {
            super(account);
            avatar = account.findViewById(R.id.account_avatar);
            username = account.findViewById(R.id.account_username);
        }

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        public void setData(AccountEntity data) {
            avatar.setText(data.getUsername().substring(0, 1).toUpperCase());
            username.setText(data.getUsername().substring(0, 1).toUpperCase() + data.getUsername().substring(1));
        }

        void bind(final AccountEntity item, final OnAccountClickListener listener) {
            itemView.setOnClickListener(v -> listener.onAccountClicked(item));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step_account, parent, false);
        return new AccountsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AccountEntity accountEntity = accounts.get(position);
        AccountsHolder accountsHolder = (AccountsHolder) holder;
        accountsHolder.bind(accounts.get(position), accountListener);
        accountsHolder.setData(accountEntity);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }
}
