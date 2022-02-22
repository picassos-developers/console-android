package app.mynta.console.android.fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mynta.console.android.R;
import app.mynta.console.android.activities.providers.FacebookActivity;
import app.mynta.console.android.activities.providers.ImgurActivity;
import app.mynta.console.android.activities.providers.MapsActivity;
import app.mynta.console.android.activities.providers.PinterestActivity;
import app.mynta.console.android.activities.providers.VimeoActivity;
import app.mynta.console.android.activities.providers.WebviewActivity;
import app.mynta.console.android.activities.providers.WordpressActivity;
import app.mynta.console.android.activities.providers.YoutubeActivity;
import app.mynta.console.android.adapter.NavigationsAdapter;
import app.mynta.console.android.adapter.wordpress.PostStylesAdapter;
import app.mynta.console.android.constants.API;
import app.mynta.console.android.constants.AuthAPI;
import app.mynta.console.android.constants.RequestCodes;
import app.mynta.console.android.models.Navigations;
import app.mynta.console.android.models.viewModel.SharedViewModel;
import app.mynta.console.android.models.wordpress.PostDesigns;
import app.mynta.console.android.sharedPreferences.ConsolePreferences;
import app.mynta.console.android.sheets.ChooseDefaultNavigationBottomSheetModal;
import app.mynta.console.android.sheets.ChooseProviderBottomSheetModal;
import app.mynta.console.android.utils.AboutDialog;
import app.mynta.console.android.utils.RequestDialog;
import app.mynta.console.android.utils.Toasto;

public class AccountFragment extends Fragment {
    SharedViewModel sharedViewModel;

    Bundle bundle;
    View view;

    RequestDialog requestDialog;
    private ConsolePreferences consolePreferences;

    // navigations
    private final List<Navigations> navigationsList = new ArrayList<>();
    private NavigationsAdapter navigationsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);

        // initialize bundle & shared preferences
        bundle = new Bundle();
        consolePreferences = new ConsolePreferences(requireContext());

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getRequestCode().observe(requireActivity(), item -> {

        });

        // language
        view.findViewById(R.id.language).setOnClickListener(v -> {
            chooseLanguage();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize request dialog
        requestDialog = new RequestDialog(requireContext());


        // Refresh Layout
        SwipeRefreshLayout refresh = view.findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {
            if (refresh.isRefreshing()) {
                refresh.setRefreshing(false);
            }


        });
    }

    private void chooseLanguage() {
        Dialog designsDialog = new Dialog(requireContext());

        designsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        designsDialog.setContentView(R.layout.dialog_language);

        // enable dialog cancel
        designsDialog.setCancelable(true);
        designsDialog.setOnCancelListener(dialog -> designsDialog.dismiss());

        // close dialog
        designsDialog.findViewById(R.id.dialog_close).setOnClickListener(v -> designsDialog.dismiss());

        // english language
        designsDialog.findViewById(R.id.language_english).setOnClickListener(v -> {

        });

        // arabic language
        designsDialog.findViewById(R.id.language_arabic).setOnClickListener(v -> {

        });

        if (designsDialog.getWindow() != null) {
            designsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            designsDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        designsDialog.show();
    }
}
