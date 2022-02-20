package app.mynta.console.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.mynta.console.android.R;

import app.mynta.console.android.interfaces.OnArticleClickListener;
import app.mynta.console.android.models.Articles;

import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Articles> articlesList;
    private final OnArticleClickListener listener;

    public ArticlesAdapter(List<Articles> articlesList, OnArticleClickListener listener) {
        this.articlesList = articlesList;
        this.listener = listener;
    }

    static class ArticlesHolder extends RecyclerView.ViewHolder {

        TextView title;

        ArticlesHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.article_title);
        }

        public void setData(Articles data) {
            title.setText(data.getTitle());
        }

        void bind(final Articles item, final OnArticleClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_listitem, parent, false);
        return new ArticlesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Articles articles = articlesList.get(position);
        ArticlesHolder articlesHolder = (ArticlesHolder) holder;
        articlesHolder.setData(articles);
        articlesHolder.bind(articlesList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return articlesList.size();
    }

}
