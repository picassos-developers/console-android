package app.mynta.console.android.activities.addProject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import app.mynta.console.android.R;

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

    }
}
