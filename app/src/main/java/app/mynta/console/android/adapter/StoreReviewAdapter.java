package app.mynta.console.android.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.mynta.console.android.R;
import app.mynta.console.android.models.StoreReview;
import app.mynta.console.android.utils.Helper;

import java.util.List;

public class StoreReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<StoreReview> storeReviews;

    public StoreReviewAdapter(List<StoreReview> storeReviews) {
        this.storeReviews = storeReviews;
    }

    static class StoreReviewsHolder extends RecyclerView.ViewHolder {

        TextView author, date, description, rating, icon;

        StoreReviewsHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.review_author);
            date = itemView.findViewById(R.id.review_date);
            description = itemView.findViewById(R.id.review_description);
            icon = itemView.findViewById(R.id.review_author_icon);
            rating = itemView.findViewById(R.id.review_rating);
        }

        @SuppressLint("SetTextI18n")
        public void setData(StoreReview data) {
            author.setText(data.getAuthor().substring(0, 1).toUpperCase() + data.getAuthor().substring(1));
            date.setText(Helper.getFormattedDateString(data.getDate()));
            description.setText(data.getReview());
            icon.setText(data.getAuthor().substring(0, 1).toUpperCase());
            rating.setText(data.getRating() + ".0");
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_listitem, parent, false);
        return new StoreReviewsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StoreReview review = storeReviews.get(position);
        StoreReviewsHolder storeReviewsHolder = (StoreReviewsHolder) holder;
        storeReviewsHolder.setData(review);
    }

    @Override
    public int getItemCount() {
        return storeReviews.size();
    }

}
