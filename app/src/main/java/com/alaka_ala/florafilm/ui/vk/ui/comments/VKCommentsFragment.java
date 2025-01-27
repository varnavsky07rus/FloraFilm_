package com.alaka_ala.florafilm.ui.vk.ui.comments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.FragmentVkCommentsBinding;
import com.alaka_ala.florafilm.ui.vk.parser.VKVideo;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class VKCommentsFragment extends Fragment {
    private FragmentVkCommentsBinding binding;
    private ArrayList<VKVideo.CommentVideo> comments;
    private RecyclerView rvComments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVkCommentsBinding.inflate(inflater, container, false);
        comments = (ArrayList<VKVideo.CommentVideo>) getArguments().getSerializable("comments");
        if (comments == null) return binding.getRoot();
        rvComments = binding.rvComments;
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvComments.setAdapter(new VKCommentsAdapter());



        return binding.getRoot();
    }

    private class VKCommentsAdapter extends RecyclerView.Adapter<VKCommentsAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.rv_item_comment, null, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textViewComment.setText(comments.get(position).getText());
            holder.textViewUserName.setText(comments.get(position).getUserName());
            holder.textViewDateComment.setText(formatDateFromUnixTimestamp(comments.get(position).getDate()));
            Picasso.get().load(comments.get(position).getPhoto_100()).into(holder.imageViewAccountImage);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView textViewUserName;
            private final TextView textViewComment;
            private final TextView textViewDateComment;
            private final ImageView imageViewAccountImage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewComment = itemView.findViewById(R.id.textViewComment);
                textViewUserName = itemView.findViewById(R.id.textViewUserName);
                textViewDateComment = itemView.findViewById(R.id.textViewDateComment);
                imageViewAccountImage = itemView.findViewById(R.id.imageViewAccountImage);
            }
        }
    }


    public static String formatDateFromUnixTimestamp(int timestamp) {
        Date date = new Date(timestamp * 1000L); // Unix timestamp is in seconds, convert to milliseconds
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }


}