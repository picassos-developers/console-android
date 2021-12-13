package com.picassos.mint.console.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.mint.console.android.R;
import com.picassos.mint.console.android.constants.AuthAPI;
import com.picassos.mint.console.android.interfaces.OnVideoClickListener;
import com.picassos.mint.console.android.models.VideoDesigns;

import java.util.List;

public class VideoStylesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<VideoDesigns> videosList;
    private final OnVideoClickListener listener;
    private final Context context;

    public VideoStylesAdapter(Context context, List<VideoDesigns> videosList, OnVideoClickListener listener) {
        this.videosList = videosList;
        this.listener = listener;
        this.context = context;
    }

    class VideoDesignsHolder extends RecyclerView.ViewHolder {

        ImageView design;

        VideoDesignsHolder(@NonNull View itemView) {
            super(itemView);
            design = itemView.findViewById(R.id.design);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void setData(VideoDesigns data) {
            switch (data.getAuth()) {
                case AuthAPI.YOUTUBE_VIDEO_STYLE_ONE:
                case AuthAPI.VIMEO_VIDEO_STYLE_ONE:
                    design.setImageDrawable(context.getDrawable(R.drawable.video_style_one));
                    break;
                case AuthAPI.YOUTUBE_VIDEO_STYLE_TWO:
                case AuthAPI.VIMEO_VIDEO_STYLE_TWO:
                    design.setImageDrawable(context.getDrawable(R.drawable.video_style_two));
                    break;
                case AuthAPI.YOUTUBE_VIDEO_STYLE_THREE:
                case AuthAPI.VIMEO_VIDEO_STYLE_THREE:
                    design.setImageDrawable(context.getDrawable(R.drawable.video_style_three));
                    break;
                case AuthAPI.YOUTUBE_VIDEO_STYLE_FOUR:
                case AuthAPI.VIMEO_VIDEO_STYLE_FOUR:
                    design.setImageDrawable(context.getDrawable(R.drawable.video_style_four));
                    break;
                case AuthAPI.YOUTUBE_VIDEO_STYLE_FIVE:
                case AuthAPI.VIMEO_VIDEO_STYLE_FIVE:
                    design.setImageDrawable(context.getDrawable(R.drawable.video_style_five));
                    break;
                case AuthAPI.YOUTUBE_VIDEO_STYLE_SIX:
                case AuthAPI.VIMEO_VIDEO_STYLE_SIX:
                    design.setImageDrawable(context.getDrawable(R.drawable.video_style_six));
                    break;
                case AuthAPI.YOUTUBE_VIDEO_STYLE_SEVEN:
                case AuthAPI.VIMEO_VIDEO_STYLE_SEVEN:
                    design.setImageDrawable(context.getDrawable(R.drawable.video_style_seven));
                    break;
                case AuthAPI.YOUTUBE_VIDEO_STYLE_EIGHT:
                case AuthAPI.VIMEO_VIDEO_STYLE_EIGHT:
                    design.setImageDrawable(context.getDrawable(R.drawable.video_style_eight));
                    break;
                case AuthAPI.YOUTUBE_VIDEO_STYLE_NINE:
                case AuthAPI.VIMEO_VIDEO_STYLE_NINE:
                    design.setImageDrawable(context.getDrawable(R.drawable.video_style_nine));
                    break;
                case AuthAPI.YOUTUBE_VIDEO_STYLE_TEN:
                case AuthAPI.VIMEO_VIDEO_STYLE_TEN:
                    design.setImageDrawable(context.getDrawable(R.drawable.video_style_ten));
                    break;
            }
        }

        void bind(final VideoDesigns item, final OnVideoClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_design, parent, false);
        return new VideoDesignsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VideoDesigns designs = videosList.get(position);
        VideoDesignsHolder videoDesignsHolder = (VideoDesignsHolder) holder;
        videoDesignsHolder.setData(designs);
        videoDesignsHolder.bind(videosList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

}
