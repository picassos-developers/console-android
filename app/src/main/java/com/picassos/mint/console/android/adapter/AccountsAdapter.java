package com.picassos.mint.console.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.entities.AccountEntity;
import com.picassos.mint.console.android.interfaces.OnAccountClickListener;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;

import java.util.List;

public class AccountsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private ConsolePreferences consolePreferences;
    private final List<AccountEntity> accounts;
    private final OnAccountClickListener accountListener;

    public AccountsAdapter(Context context, List<AccountEntity> accounts, OnAccountClickListener accountListener) {
        this.context = context;
        this.accounts = accounts;
        this.accountListener = accountListener;
    }

    class AccountsHolder extends RecyclerView.ViewHolder {

        LinearLayout accountContainer;
        View active;
        TextView avatar;
        TextView token;
        TextView username;

        AccountsHolder(@NonNull View account) {
            super(account);
            accountContainer = account.findViewById(R.id.account_container);
            active = account.findViewById(R.id.account_status);
            avatar = account.findViewById(R.id.account_avatar);
            token = account.findViewById(R.id.account_token);
            username = account.findViewById(R.id.account_username);
        }

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        public void setData(AccountEntity data) {
            if (consolePreferences.loadToken().equals(data.getToken())) {
                accountContainer.setBackground(context.getDrawable(R.drawable.item_background_active));
                active.setVisibility(View.VISIBLE);
            }
            avatar.setText(data.getUsername().substring(0, 1).toUpperCase());
            token.setText(data.getToken());
            username.setText(data.getUsername().substring(0, 1).toUpperCase() + data.getUsername().substring(1));
        }

        void bind(final AccountEntity item, final OnAccountClickListener listener) {
            itemView.setOnClickListener(v -> listener.onAccountClicked(item));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_listitem, parent, false);
        consolePreferences = new ConsolePreferences(parent.getContext());
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
