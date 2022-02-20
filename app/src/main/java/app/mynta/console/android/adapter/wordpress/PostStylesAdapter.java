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
import app.mynta.console.android.interfaces.OnWordPressPostClickListener;
import app.mynta.console.android.models.wordpress.PostDesigns;

import java.util.List;

public class PostStylesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<PostDesigns> postsList;
    private final OnWordPressPostClickListener listener;
    private final Context context;

    public PostStylesAdapter(Context context, List<PostDesigns> postDesigns, OnWordPressPostClickListener listener) {
        this.postsList = postDesigns;
        this.listener = listener;
        this.context = context;
    }

    class PostDesignsHolder extends RecyclerView.ViewHolder {

        ImageView design;

        PostDesignsHolder(@NonNull View itemView) {
            super(itemView);
            design = itemView.findViewById(R.id.design);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void setData(PostDesigns data) {
            switch (data.getAuth()) {
                case AuthAPI.WORD_PRESS_POST_STYLE_ONE:
                    design.setImageDrawable(context.getDrawable(R.drawable.wordpress_post_style_one));
                    break;
                case AuthAPI.WORD_PRESS_POST_STYLE_TWO:
                    design.setImageDrawable(context.getDrawable(R.drawable.wordpress_post_style_two));
                    break;
                case AuthAPI.WORD_PRESS_POST_STYLE_THREE:
                    design.setImageDrawable(context.getDrawable(R.drawable.wordpress_post_style_three));
                    break;
                case AuthAPI.WORD_PRESS_POST_STYLE_FOUR:
                    design.setImageDrawable(context.getDrawable(R.drawable.wordpress_post_style_four));
                    break;
                case AuthAPI.WORD_PRESS_POST_STYLE_FIVE:
                    design.setImageDrawable(context.getDrawable(R.drawable.wordpress_post_style_five));
                    break;
                case AuthAPI.WORD_PRESS_POST_STYLE_SIX:
                    design.setImageDrawable(context.getDrawable(R.drawable.wordpress_post_style_six));
                    break;
                case AuthAPI.WORD_PRESS_POST_STYLE_SEVEN:
                    design.setImageDrawable(context.getDrawable(R.drawable.wordpress_post_style_seven));
                    break;
                case AuthAPI.WORD_PRESS_POST_STYLE_EIGHT:
                    design.setImageDrawable(context.getDrawable(R.drawable.wordpress_post_style_eight));
                    break;
                case AuthAPI.WORD_PRESS_POST_STYLE_NINE:
                    design.setImageDrawable(context.getDrawable(R.drawable.wordpress_post_style_nine));
                    break;
                case AuthAPI.WORD_PRESS_POST_STYLE_TEN:
                    design.setImageDrawable(context.getDrawable(R.drawable.wordpress_post_style_ten));
                    break;
            }
        }

        void bind(final PostDesigns item, final OnWordPressPostClickListener listener) {
            itemView.setOnClickListener(v -> listener.onPostClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_design, parent, false);
        return new PostDesignsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostDesigns designs = postsList.get(position);
        PostDesignsHolder postDesignsHolder = (PostDesignsHolder) holder;
        postDesignsHolder.setData(designs);
        postDesignsHolder.bind(postsList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

}
