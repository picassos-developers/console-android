package com.picassos.mint.console.android.activities.addProject.fragments;

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
import androidx.fragment.app.Fragment;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.activities.providers.FacebookActivity;
import com.picassos.mint.console.android.activities.providers.ImgurActivity;
import com.picassos.mint.console.android.activities.providers.MapsActivity;
import com.picassos.mint.console.android.activities.providers.PinterestActivity;
import com.picassos.mint.console.android.activities.providers.VimeoActivity;
import com.picassos.mint.console.android.activities.providers.WebviewActivity;
import com.picassos.mint.console.android.activities.providers.WordpressActivity;
import com.picassos.mint.console.android.activities.providers.YoutubeActivity;

public class AddFirstContentProviderFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_first_content_provider, container, false);

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

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result != null && result.getResultCode() == Activity.RESULT_OK) {

        }
    });
}
