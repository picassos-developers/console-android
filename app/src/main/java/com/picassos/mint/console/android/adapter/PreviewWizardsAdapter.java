/*
 * Mint APP, Mint Console, Design (except icons and some open-source libraries) and Donut API is copyright of Rixhion, inc. - © Rixhion, inc 2019. All rights reserved.
 *
 * Any redistribution or reproduction of part or all of the contents in any form is prohibited other than the following:
 *  - YOU MAY USE, EDIT DATA INSIDE MINT ONLY.
 *
 * You may not, except with our express written permission, distribute or commercially exploit the content. Nor may you transmit it or store it in any other website & app or other form of electronic retrieval system.
 *
 */

package com.picassos.mint.console.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.models.Guide;

import java.util.List;

public class PreviewWizardsAdapter extends PagerAdapter {

    private final Context context;
    private final List<Guide> listScreen;

    public PreviewWizardsAdapter(Context context, List<Guide> listScreen) {
        this.context = context;
        this.listScreen = listScreen;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View layoutScreen = inflater.inflate(R.layout.item_wizard_preview, null);

        // identify items as title, description, image
        TextView title = layoutScreen.findViewById(R.id.intro_title);
        TextView description = layoutScreen.findViewById(R.id.intro_description);

        // set content by position
        title.setText(listScreen.get(position).getTitle());
        description.setText(listScreen.get(position).getDescription());

        container.addView(layoutScreen);

        return layoutScreen;
    }

    @Override
    public int getCount() {
        return listScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

}
