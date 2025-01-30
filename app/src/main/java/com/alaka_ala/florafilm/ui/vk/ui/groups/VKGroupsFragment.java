package com.alaka_ala.florafilm.ui.vk.ui.groups;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.FragmentVKGroupsBinding;
import com.alaka_ala.florafilm.ui.vk.AccountManager;
import com.alaka_ala.florafilm.ui.vk.LoginVkActivity;
import com.alaka_ala.florafilm.ui.vk.parser.VKVideo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class VKGroupsFragment extends Fragment {
    private FragmentVKGroupsBinding binding;
    private VKVideo vkVideo;
    private LinearLayout linearLayoutRootGroups;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVKGroupsBinding.inflate(inflater, container, false);
        vkVideo = new VKVideo(AccountManager.getAccessToken(getContext()));
        linearLayoutRootGroups = binding.linearLayoutRootGroups;

        if (AccountManager.getAccessToken(getContext()) == null) {
            new MaterialAlertDialogBuilder(getContext()).setTitle("Добавление аккаунта").setMessage("Необходимо добавить аккаунт VK для работы с ВК Видео").setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getContext(), LoginVkActivity.class));
                    getActivity().finish();
                }
            });
        }


        // FloraFilm
        vkVideo.getAlbums("-229006348", 0, new VKVideo.GetAlbumsCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(VKVideo.GroupItem group) {
                addGroup(group);
            }

            @Override
            public void onError(String e) {
                Snackbar.make(binding.getRoot(), e, Snackbar.LENGTH_LONG).show();
            }
        });

        // KINO BRO
        vkVideo.getAlbums("-220018529", 0, new VKVideo.GetAlbumsCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(VKVideo.GroupItem group) {
                addGroup(group);
            }

            @Override
            public void onError(String e) {
                Snackbar.make(binding.getRoot(), e, Snackbar.LENGTH_LONG).show();
            }
        });


        // КИНО ОКНО HD
        vkVideo.getAlbums("-210183487", 0, new VKVideo.GetAlbumsCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(VKVideo.GroupItem group) {
                addGroup(group);
            }

            @Override
            public void onError(String e) {
                Snackbar.make(binding.getRoot(), e, Snackbar.LENGTH_LONG).show();
            }
        });

        // ShadowTavern (Видео с подборками что посмотреть)
        vkVideo.getAlbums("-182568089", 0, new VKVideo.GetAlbumsCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(VKVideo.GroupItem group) {
                addGroup(group);
            }

            @Override
            public void onError(String e) {
                Snackbar.make(binding.getRoot(), e, Snackbar.LENGTH_LONG).show();
            }
        });

        // Матроскин фильм
        vkVideo.getAlbums("-162918645", 0, new VKVideo.GetAlbumsCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(VKVideo.GroupItem group) {
                addGroup(group);
            }

            @Override
            public void onError(String e) {
                Snackbar.make(binding.getRoot(), e, Snackbar.LENGTH_LONG).show();
            }
        });
        return binding.getRoot();
    }
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    private void addGroup(VKVideo.GroupItem group) {
        View view = getLayoutInflater().inflate(R.layout.rv_item_vk_group, null, false);
        LinearLayout linearLayoutTitleGroup = view.findViewById(R.id.linearLayoutTitleGroup);
        linearLayoutTitleGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
         TextView textViewTitlePlaylistOrFilm = view.findViewById(R.id.textViewTitlePlaylistOrFilm);
        String playlistOrFilm = group.getPlayLists().isEmpty() ? "Видео" : "Плейлисты";
        textViewTitlePlaylistOrFilm.setText(playlistOrFilm);
        TextView textViewTitleGroup = view.findViewById(R.id.textViewTitleGroup);
        textViewTitleGroup.setText(group.getTitleGroup());
        TextView textViewTotalViedosPlaylists = view.findViewById(R.id.textViewTotalViedosPlaylists);
        textViewTotalViedosPlaylists.setText("Видео " + group.getCountVideo() + " / Плейлистов " + group.getPlayLists().size());
        ImageView imageViewPhotoGroup100 = view.findViewById(R.id.imageViewPhotoGroup100);
        Picasso.get().load(group.getPhoto_100()).into(imageViewPhotoGroup100);
        RecyclerView rvPlaylistsGroup = view.findViewById(R.id.rvPlaylistsGroup);
        rvPlaylistsGroup.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        AdapterPlaylistsGroup adapterPlaylistsGroup = new AdapterPlaylistsGroup(group);
        rvPlaylistsGroup.setAdapter(adapterPlaylistsGroup);
        String screenName = group.getScreenName();
        // Делаем что бы моя группа была первая в списке
        if (screenName.equals("florafilm_app")) {
            linearLayoutRootGroups.addView(view, 0);
        } else {
            // Все остальные внизу
            linearLayoutRootGroups.addView(view);
        }
    }


}