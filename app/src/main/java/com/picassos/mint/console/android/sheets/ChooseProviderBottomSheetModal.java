package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.providers.FacebookActivity;
import com.picassos.mint.console.android.activities.providers.ImgurActivity;
import com.picassos.mint.console.android.activities.providers.MapsActivity;
import com.picassos.mint.console.android.activities.providers.PinterestActivity;
import com.picassos.mint.console.android.activities.providers.VimeoActivity;
import com.picassos.mint.console.android.activities.providers.WebviewActivity;
import com.picassos.mint.console.android.activities.providers.WordpressActivity;
import com.picassos.mint.console.android.activities.providers.YoutubeActivity;
import com.picassos.mint.console.android.constants.RequestCodes;
import com.picassos.mint.console.android.models.viewModel.SharedViewModel;
import com.picassos.mint.console.android.sharedPreferences.ConsolePreferences;
import com.picassos.mint.console.android.utils.RequestDialog;

public class ChooseProviderBottomSheetModal extends BottomSheetDialogFragment {
    SharedViewModel sharedViewModel;

    View view;
    RequestDialog requestDialog;
    ConsolePreferences consolePreferences;

    public ChooseProviderBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.choose_provider_bottom_sheet_modal, container, false);

        // providers
        view.findViewById(R.id.wordpress_provider).setOnClickListener(v -> addProvider("wordpress"));
        view.findViewById(R.id.webview_provider).setOnClickListener(v -> addProvider("webview"));
        view.findViewById(R.id.youtube_provider).setOnClickListener(v -> addProvider("youtube"));
        view.findViewById(R.id.vimeo_provider).setOnClickListener(v -> addProvider("vimeo"));
        view.findViewById(R.id.facebook_provider).setOnClickListener(v -> addProvider("facebook"));
        view.findViewById(R.id.pinterest_provider).setOnClickListener(v -> addProvider("pinterest"));
        view.findViewById(R.id.maps_provider).setOnClickListener(v -> addProvider("google_maps"));
        view.findViewById(R.id.imgur_provider).setOnClickListener(v -> addProvider("imgur"));

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestDialog = new RequestDialog(requireContext());
        consolePreferences = new ConsolePreferences(requireContext());
    }

    private void addProvider(String activity) {
        Intent intent = new Intent();
        intent.putExtra("request", "add");

        switch (activity) {
            case "webview":
                intent.setClass(requireContext(), WebviewActivity.class);
                startActivityForResult.launch(intent);
                break;
            case "wordpress":
                intent.setClass(requireContext(), WordpressActivity.class);
                startActivityForResult.launch(intent);
                break;
            case "youtube":
                intent.setClass(requireContext(), YoutubeActivity.class);
                startActivityForResult.launch(intent);
                break;
            case "vimeo":
                intent.setClass(requireContext(), VimeoActivity.class);
                startActivityForResult.launch(intent);
                break;
            case "pinterest":
                intent.setClass(requireContext(), PinterestActivity.class);
                startActivityForResult.launch(intent);
                break;
            case "facebook":
                intent.setClass(requireContext(), FacebookActivity.class);
                startActivityForResult.launch(intent);
                break;
            case "imgur":
                intent.setClass(requireContext(), ImgurActivity.class);
                startActivityForResult.launch(intent);
                break;
            case "google_maps":
                intent.setClass(requireContext(), MapsActivity.class);
                startActivityForResult.launch(intent);
                break;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result != null && result.getResultCode() == Activity.RESULT_OK) {
            sharedViewModel.setRequestCode(RequestCodes.REQUEST_UPDATE_NAVIGATIONS);
            dismiss();
        }
    });
}
