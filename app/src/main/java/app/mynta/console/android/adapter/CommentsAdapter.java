package app.mynta.console.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.mynta.console.android.R;

import app.mynta.console.android.interfaces.OnCommentLongClickListener;
import app.mynta.console.android.models.Comments;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Comments> commentsList;
    private final OnCommentLongClickListener listener;

    public CommentsAdapter(Context context, List<Comments> commentsList, OnCommentLongClickListener listener) {
        this.context = context;
        this.commentsList = commentsList;
        this.listener = listener;
    }

    class CommentsHolder extends RecyclerView.ViewHolder {

        TextView author, date, description, votes, icon;

        CommentsHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.comment_author);
            date = itemView.findViewById(R.id.comment_date);
            description = itemView.findViewById(R.id.comment_description);
            votes = itemView.findViewById(R.id.comment_votes);
            icon = itemView.findViewById(R.id.comment_author_icon);
        }

        @SuppressLint("SetTextI18n")
        public void setData(Comments data) {
            author.setText(data.getUsername().substring(0, 1).toUpperCase() + data.getUsername().substring(1));
            date.setText(data.getDate());
            description.setText(Html.fromHtml(data.getDescription()));
            votes.setText(data.getVotes() + " " + context.getString(R.string.upvotes));
            icon.setText(data.getUsername().substring(0, 1).toUpperCase());
        }

        void bind(final Comments item, final OnCommentLongClickListener listener) {
            itemView.setOnLongClickListener(v -> {
                listener.onItemClick(item);
                return true;
            });
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_listitem, parent, false);
        return new CommentsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comments comments = commentsList.get(position);
        CommentsHolder commentsHolder = (CommentsHolder) holder;
        commentsHolder.setData(comments);
        commentsHolder.bind(commentsList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

}
