package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.LoginActivity;
import com.picassos.mint.console.android.activities.ProjectsActivity;
import com.picassos.mint.console.android.adapter.AccountsAdapter;
import com.picassos.mint.console.android.entities.AccountEntity;
import com.picassos.mint.console.android.room.APP_DATABASE;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;

import java.util.ArrayList;
import java.util.List;

public class ManageAccountsBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    ConsolePreferences consolePreferences;

    // adapter & accounts model
    private AccountsAdapter adapter;
    private List<AccountEntity> accounts;

    public ManageAccountsBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.manage_account_bottom_sheet_modal, container, false);

        // console preferences
        consolePreferences = new ConsolePreferences(requireContext());

        // accounts list initialize
        RecyclerView recyclerView = view.findViewById(R.id.recycler_accounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        // accounts list, adapter
        accounts = new ArrayList<>();
        adapter = new AccountsAdapter(requireContext(), accounts, item -> {
            if (!item.token.equals(consolePreferences.loadToken())) {
                consolePreferences.setSecretAPIKey("exception:error?sak");
                consolePreferences.setToken(item.token);
                consolePreferences.setUsername(item.username);
                consolePreferences.setEmail(item.email);
                startActivity(new Intent(requireContext(), ProjectsActivity.class));
                requireActivity().finish();
            } else {
                dismiss();
            }
        });
        recyclerView.setAdapter(adapter);

        requestAccounts();

        // add account
        view.findViewById(R.id.add_account).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.putExtra("add_account", "valid");
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request accounts from
     * DAO, accounts entity
     */
    private void requestAccounts() {
        @SuppressLint("StaticFieldLeak")
        class GetAccountsTask extends AsyncTask<Void, Void, List<AccountEntity>> {
            @Override
            protected List<AccountEntity> doInBackground(Void... voids) {
                return APP_DATABASE.requestDatabase(requireContext()).requestDAO().requestAllAccounts();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(List<AccountEntity> accounts_inline) {
                super.onPostExecute(accounts_inline);
                accounts.addAll(accounts_inline);
                adapter.notifyDataSetChanged();
            }

        }
        new GetAccountsTask().execute();
    }

}

