package com.alaka_ala.florafilm.ui.vk.ui.groups;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.ui.vk.parser.VKVideo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterPlaylistsGroup extends RecyclerView.Adapter<AdapterPlaylistsGroup.MyViewHolder> {
    private final VKVideo.GroupItem groupItem;
    private boolean isNullPlaylist = false;

    public AdapterPlaylistsGroup(VKVideo.GroupItem groupItem) {
        this.groupItem = groupItem;
        isNullPlaylist = groupItem.getPlayLists().isEmpty();
    }

    public VKVideo.GroupItem getPlaylistsGroupItems() {
        return groupItem;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.rv_item_group_playlist, null);
        return new MyViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (isNullPlaylist) {
            holder.textViewCountVideoPlaylist.setText("" + groupItem.getVideoItems().get(position).getViews());
            holder.textViewTitlePlaylist.setText(groupItem.getVideoItems().get(position).getTitle());
            String urlPlaylistImg = "";
            for (VKVideo.VideoItem.Image image : groupItem.getVideoItems().get(position).getImages()) {
                urlPlaylistImg = image.getUrl();
                if (image.getHeight() == 1280) {
                    urlPlaylistImg = image.getUrl();
                    break;
                }
            }
            Picasso.get().load(urlPlaylistImg).into(holder.imageViewPosterVkPlaylist);
            holder.imageViewPlaylistVideo.setImageDrawable(holder.itemView.getContext().getResources().getDrawable(R.drawable.round_remove_red_eye_24));
        } else {
            holder.textViewCountVideoPlaylist.setText("" + groupItem.getPlayLists().get(position).getCount());
            String urlPlaylistImg = "";
            for (VKVideo.VideoItem.Image image : groupItem.getPlayLists().get(position).getImages()) {
                urlPlaylistImg = image.getUrl();
                if (image.getHeight() == 1280) {
                    urlPlaylistImg = image.getUrl();
                    break;
                }
            }
            if (!urlPlaylistImg.isEmpty()) {
                Picasso.get().load(groupItem.getPlayLists().get(position).getImages().get(4).getUrl()).into(holder.imageViewPosterVkPlaylist);
            }
            holder.textViewTitlePlaylist.setText(groupItem.getPlayLists().get(position).getTitle());
        }

    }

    @Override
    public int getItemCount() {
        if (isNullPlaylist) {
            return groupItem.getVideoItems().size();
        }
        return groupItem.getPlayLists().size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageViewPosterVkPlaylist;
        private final TextView textViewCountVideoPlaylist;
        private final TextView textViewTitlePlaylist;
        private ImageView imageViewPlaylistVideo;


        public MyViewHolder(View itemView) {
            super(itemView);
            imageViewPosterVkPlaylist = itemView.findViewById(R.id.imageViewPosterVkPlaylist);
            textViewCountVideoPlaylist = itemView.findViewById(R.id.textViewCountVideoPlaylist);
            textViewTitlePlaylist = itemView.findViewById(R.id.textViewTitlePlaylist);
            imageViewPlaylistVideo = itemView.findViewById(R.id.imageViewPlaylistVideo);
        }
    }
}
