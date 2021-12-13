package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.mint.console.android.R;

public class ChooseCustomDarkmodePackageAppBottomSheetModal extends BottomSheetDialogFragment {

    View view;

    public interface OnSelectPackageListener {
        void onSelectPackageListener(String productId, int pages);
    }

    OnSelectPackageListener onSelectPackageListener;

    public ChooseCustomDarkmodePackageAppBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onSelectPackageListener = (OnSelectPackageListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSelectPackageListener");
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.choose_custom_darkmode_package_bottom_sheet_modal, container, false);

        // package one
        Button packageOne = view.findViewById(R.id.package_one);
        packageOne.setText("1 " + getString(R.string.page_package) + " ($3)");
            packageOne.setOnClickListener(v -> selectPackage("pu_0001_custom_darkmode_package1", 1));

        // package two
        Button packageTwo = view.findViewById(R.id.package_two);
        packageTwo.setText("2 " + getString(R.string.pages_package) + " ($6)");
        packageTwo.setOnClickListener(v -> selectPackage("pu_0001_custom_darkmode_package2", 2));

        // package three
        Button packageThree = view.findViewById(R.id.package_three);
        packageThree.setText("3 " + getString(R.string.pages_package) + " ($9)");
        packageThree.setOnClickListener(v -> selectPackage("pu_0001_custom_darkmode_package3", 3));

        // package four
        Button packageFour = view.findViewById(R.id.package_four);
        packageFour.setText("4 " + getString(R.string.pages_package) + " ($12)");
        packageFour.setOnClickListener(v -> selectPackage("pu_0001_custom_darkmode_package4", 4));

        // package five
        Button packageFive = view.findViewById(R.id.package_five);
        packageFive.setText("5 " + getString(R.string.pages_package) + " ($15)");
        packageFive.setOnClickListener(v -> selectPackage("pu_0001_custom_darkmode_package5", 5));

        // package six
        Button packageSix = view.findViewById(R.id.package_six);
        packageSix.setText("6 " + getString(R.string.pages_package) + " ($18)");
        packageSix.setOnClickListener(v -> selectPackage("pu_0001_custom_darkmode_package6", 6));

        // package seven
        Button packageSeven = view.findViewById(R.id.package_seven);
        packageSeven.setText("7 " + getString(R.string.pages_package) + " ($21)");
        packageSeven.setOnClickListener(v -> selectPackage("pu_0001_custom_darkmode_package7", 7));

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * select package
     * @param id for product id
     * @param pages for pages count
     */
    private void selectPackage(String id, int pages) {
        onSelectPackageListener.onSelectPackageListener(id, pages);
        dismiss();
    }
}
