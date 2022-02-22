package app.mynta.console.android.activities.addProject.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import app.mynta.console.android.R;
import app.mynta.console.android.activities.addProject.AddProjectActivity;
import app.mynta.console.android.adapter.AppCategoryAdapter;
import app.mynta.console.android.models.AppCategory;

import java.util.ArrayList;
import java.util.List;

public class ChooseAppCategoryFragment extends Fragment {
    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_app_category, container, false);

        List<AppCategory> appCategoryList = new ArrayList<>();
        RecyclerView appCategoryRecyclerview = view.findViewById(R.id.recycler_app_category);
        appCategoryRecyclerview.setHasFixedSize(true);

        AppCategoryAdapter appCategoryAdapter = new AppCategoryAdapter(requireContext(), appCategoryList, click -> {
            ((AddProjectActivity) requireActivity()).appCategory = click.getTitle();
            ((AddProjectActivity) requireActivity()).goForward();
        });
        appCategoryRecyclerview.setAdapter(appCategoryAdapter);
        appCategoryRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(5, LinearLayout.HORIZONTAL));
        // app categories
        appCategoryList.add(new AppCategory(1, "Blog", "icon_pen"));
        appCategoryList.add(new AppCategory(2, "Social Profile", "icon_social_profile"));
        appCategoryList.add(new AppCategory(3, "Business", "icon_financial"));
        appCategoryList.add(new AppCategory(4, "Gaming", "icon_controller"));
        appCategoryList.add(new AppCategory(5, "Communication", "icon_communication"));
        appCategoryList.add(new AppCategory(6, "Online Store", "icon_shopping_basket"));
        appCategoryList.add(new AppCategory(7, "Education", "icon_book"));
        appCategoryList.add(new AppCategory(8, "Creativity", "icon_idea"));
        appCategoryList.add(new AppCategory(9, "Other", "icon_other"));
        appCategoryAdapter.notifyDataSetChanged();

        return view;
    }
}