package com.alaka_ala.florafilm.ui.home.viewPager.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alaka_ala.florafilm.databinding.FragmentNewsMediaItemBinding;
import com.alaka_ala.florafilm.sys.kp_api.NewsMedia;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsMediaItemFragment extends Fragment {
    private NewsMedia newsMedia;
    private int index = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentNewsMediaItemBinding binding = FragmentNewsMediaItemBinding.inflate(inflater, container, false);
        ImageView imageViewPosterNews = binding.imageViewPosterNews;
        TextView textViewDescriptionNews = binding.textViewDescriptionNews;
        TextView textViewDateNews = binding.textViewDateNews;
        TextView textViewTitleNews = binding.textViewTitleNews;

        if (getArguments() != null) {
            index = getArguments().getInt("index");
            newsMedia = (NewsMedia) getArguments().getSerializable("newsMedia");
            Picasso.get().load(newsMedia.getImageUrl()).into(imageViewPosterNews);
            textViewTitleNews.setText(newsMedia.getTitle());
            textViewDescriptionNews.setText(newsMedia.getDescription());
            textViewDateNews.setText(newsMedia.getPublishedAt());
        }


        return binding.getRoot();
    }


    public static NewsMediaItemFragment newInstance(Bundle b) {
        NewsMediaItemFragment fragment = new NewsMediaItemFragment();
        fragment.setArguments(b);
        return fragment;
    }
}