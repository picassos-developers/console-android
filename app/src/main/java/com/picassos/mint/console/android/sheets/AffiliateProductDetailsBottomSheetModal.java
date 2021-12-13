package com.picassos.mint.console.android.sheets;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.utils.IntentHandler;

public class AffiliateProductDetailsBottomSheetModal extends BottomSheetDialogFragment {

    View view;

    public AffiliateProductDetailsBottomSheetModal() {

    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.affiliate_product_details_sheet_modal, container, false);

        // product thumbnail
        SimpleDraweeView thumbnail = view.findViewById(R.id.product_thumbnail);
        thumbnail.setController(
                Fresco.newDraweeControllerBuilder()
                        .setTapToRetryEnabled(true)
                        .setUri(requireArguments().getString("thumbnail"))
                        .build());

        // product title
        TextView title = view.findViewById(R.id.product_title);
        title.setText(requireArguments().getString("title"));

        // product rating
        RatingBar rating = view.findViewById(R.id.product_rating);
        rating.setRating(requireArguments().getFloat("rating"));

        // product price
        TextView price = view.findViewById(R.id.product_price);
        price.setText("$" + requireArguments().getInt("price"));

        // visit product
        view.findViewById(R.id.visit_product).setOnClickListener(v -> IntentHandler.handleWeb(requireContext(), requireArguments().getString("url")));

        return view;
    }

}
