package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.PushNotificationsActivity;

public class ChooseNotificationProviderBottomSheetModal extends BottomSheetDialogFragment {
    View view;

    public ChooseNotificationProviderBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.choose_notification_provider_bottom_sheet_modal, container, false);

        // google firebase
        view.findViewById(R.id.google_firebase).setOnClickListener(v -> chooseNotificationProvider("google_firebase"));

        // onesignal
        view.findViewById(R.id.onesignal).setOnClickListener(v -> chooseNotificationProvider("onesignal"));

        return view;
    }

    /**
     * choose a notification provider
     * @param provider for provider
     */
    private void chooseNotificationProvider(String provider) {
        Intent intent = new Intent(requireContext(), PushNotificationsActivity.class);
        intent.putExtra("provider", provider);
        startActivity(intent);
        dismiss();
    }
}