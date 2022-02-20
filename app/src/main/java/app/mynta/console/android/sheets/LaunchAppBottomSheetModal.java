package app.mynta.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import app.mynta.console.android.R;

import app.mynta.console.android.activities.EditProjectActivity;
import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

public class LaunchAppBottomSheetModal extends BottomSheetDialogFragment {

    View view;
    RequestDialog requestDialog;
    ConsolePreferences consolePreferences;

    public LaunchAppBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.launch_app_bottom_sheet_modal, container, false);

        // providers
        view.findViewById(R.id.normal_process).setOnClickListener(v -> requestLaunchActivity("main"));
        view.findViewById(R.id.wordpress_provider).setOnClickListener(v -> requestLaunchActivity("wordpress"));
        view.findViewById(R.id.webview_provider).setOnClickListener(v -> requestLaunchActivity("webview"));
        view.findViewById(R.id.youtube_provider).setOnClickListener(v -> requestLaunchActivity("youtube"));
        view.findViewById(R.id.vimeo_provider).setOnClickListener(v -> requestLaunchActivity("vimeo"));
        view.findViewById(R.id.facebook_provider).setOnClickListener(v -> requestLaunchActivity("facebook"));
        view.findViewById(R.id.pinterest_provider).setOnClickListener(v -> requestLaunchActivity("pinterest"));
        view.findViewById(R.id.maps_provider).setOnClickListener(v -> requestLaunchActivity("maps"));
        view.findViewById(R.id.imgur_provider).setOnClickListener(v -> requestLaunchActivity("imgur"));

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestDialog = new RequestDialog(requireContext());
        consolePreferences = new ConsolePreferences(requireContext());
    }

    /**
     * request launch activity
     * @param activity for activity
     */
    private void requestLaunchActivity(String activity) {
        try {
            Intent intent = requireContext().getPackageManager().getLaunchIntentForPackage("com.picassos.mint");
            intent.putExtra("activity", activity);
            startActivity(intent);
        } catch (NullPointerException e) {
            Toasto.show_toast(requireContext(), requireContext().getString(R.string.invalid_package_name), 1, 1);
            startActivity(new Intent(requireContext(), EditProjectActivity.class));
        }
    }
}
