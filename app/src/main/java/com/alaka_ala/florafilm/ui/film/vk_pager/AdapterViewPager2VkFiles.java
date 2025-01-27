package com.alaka_ala.florafilm.ui.film.vk_pager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.alaka_ala.florafilm.ui.vk.parser.VKVideo;

import java.util.ArrayList;

public class AdapterViewPager2VkFiles extends FragmentStateAdapter {
    private ArrayList<VKVideo.VideoItem> videos;
    public AdapterViewPager2VkFiles(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ArrayList<VKVideo.VideoItem> videos) {
        super(fragmentManager, lifecycle);
        this.videos = videos;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("video", videos.get(position));
        return VKViewPagerFragment.newInstance(bundle);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }
}
