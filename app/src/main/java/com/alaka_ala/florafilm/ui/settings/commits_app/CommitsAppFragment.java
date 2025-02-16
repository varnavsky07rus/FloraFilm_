package com.alaka_ala.florafilm.ui.settings.commits_app;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.CommitsAppItemBinding;
import com.alaka_ala.florafilm.databinding.FragmentCommitsAppBinding;
import com.alaka_ala.florafilm.sys.TimelineView;

import java.util.ArrayList;
import java.util.Arrays;



public class CommitsAppFragment extends Fragment {
    private FragmentCommitsAppBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCommitsAppBinding.inflate(inflater, container, false);

        RecyclerView updatesRecyclerView  = binding.updatesRecyclerView;
        updatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        String[] updates = getResources().getStringArray(R.array.version_description);
        ArrayList<String> updatesList = new ArrayList<>(Arrays.asList(updates));

        TimelineView timelineView = new TimelineView(getContext());
        updatesRecyclerView.setAdapter(new TimelineView.UpdateAdapter(updatesList, getContext()));


        // Получаем ActionBar из Activity
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Обновления");
        }

        return binding.getRoot();
    }
}