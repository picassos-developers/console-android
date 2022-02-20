package app.mynta.console.android.adapter.wordpress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.mynta.console.android.R;

import app.mynta.console.android.constants.AuthAPI;
import app.mynta.console.android.interfaces.OnWordPressCategoryClickListener;
import app.mynta.console.android.models.wordpress.CategoryDesigns;

import java.util.List;

public class CategoryStylesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<CategoryDesigns> categoriesDesign;
    private final OnWordPressCategoryClickListener listener;
    private final Context context;

    public CategoryStylesAdapter(Context context, List<CategoryDesigns> postDesigns, OnWordPressCategoryClickListener listener) {
        this.categoriesDesign = postDesigns;
        this.listener = listener;
        this.context = context;
    }

    class CategoryDesignsHolder extends RecyclerView.ViewHolder {

        ImageView design;

        CategoryDesignsHolder(@NonNull View itemView) {
            super(itemView);
            design = itemView.findViewById(R.id.design);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void setData(CategoryDesigns data) {
            switch (data.getAuth()) {
                case AuthAPI.WORD_PRESS_CATEGORY_STYLE_ONE:
                    design.setImageDrawable(context.getDrawable(R.drawable.wordpress_category_style_one));
                    break;
                case AuthAPI.WORD_PRESS_CATEGORY_STYLE_TWO:
                    design.setImageDrawable(context.getDrawable(R.drawable.wordpress_category_style_two));
                    break;
                case AuthAPI.WORD_PRESS_CATEGORY_STYLE_THREE:
                    design.setImageDrawable(context.getDrawable(R.drawable.wordpress_category_style_three));
                    break;
                case AuthAPI.WORD_PRESS_CATEGORY_STYLE_FOUR:
                    design.setImageDrawable(context.getDrawable(R.drawable.wordpress_category_style_four));
                    break;
            }
        }

        void bind(final CategoryDesigns item, final OnWordPressCategoryClickListener listener) {
            itemView.setOnClickListener(v -> listener.onCategoryClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_design, parent, false);
        return new CategoryDesignsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CategoryDesigns designs = categoriesDesign.get(position);
        CategoryDesignsHolder categoryDesignsHolder = (CategoryDesignsHolder) holder;
        categoryDesignsHolder.setData(designs);
        categoryDesignsHolder.bind(categoriesDesign.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return categoriesDesign.size();
    }

}
