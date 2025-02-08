package com.alaka_ala.florafilm.ui.vk.ui.groups.playlist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.FragmentPlaylistGroupBinding;
import com.alaka_ala.florafilm.sys.utils.MyRecyclerViewItemTouchListener;
import com.alaka_ala.florafilm.sys.utils.MyRecyclerViewScrollListener;
import com.alaka_ala.florafilm.ui.player.exo.ExoActivity;
import com.alaka_ala.florafilm.ui.player.exo.models.EPData;
import com.alaka_ala.florafilm.ui.vk.AccountManager;
import com.alaka_ala.florafilm.ui.vk.parser.VKVideo;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PlaylistGroupFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentPlaylistGroupBinding binding;
    private VKVideo.PlaylistGroupItem playlistGroupItem;
    private VKVideo.VideoItem groupVideoItem;
    private VKVideo vkVideo;
    private static ArrayList<VKVideo.VideoItem> videos = new ArrayList<>();
    private AdapterVideos adapterVideos;
    private Button buttonPlayAll;
    private ConstraintLayout nullDatalayoutRoot;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlaylistGroupBinding.inflate(inflater, container, false);
        buttonPlayAll = binding.buttonPlayAll;
        nullDatalayoutRoot = binding.nullDatalayout.getRoot();
        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(this);


        assert getArguments() != null;
        if (getArguments().containsKey("playlist")) {
            playlistGroupItem = (VKVideo.PlaylistGroupItem) getArguments().getSerializable("playlist");
        }

        buttonPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videos.isEmpty()) return;
                Intent intent = new Intent(getContext(), ExoActivity.class);
                EPData.Serial.Builder epdata = new EPData.Serial.Builder();
                // Создаем список с сезонами
                ArrayList<EPData.Serial.Season> seasonsList = new ArrayList<>();
                // Создаем 1 сезон и добавляем в него данные
                EPData.Serial.Season.Builder seasonBuilder = new EPData.Serial.Season.Builder();
                seasonBuilder.setTitle("Плейлист: " + playlistGroupItem.getTitle());
                // Создаем список серий и создаем их в цикле
                ArrayList<EPData.Serial.Episode> episodesList = new ArrayList<>();
                for (int i = 0; i < videos.size(); i++) {
                    EPData.Serial.Episode.Builder episodeBuilder = new EPData.Serial.Episode.Builder();
                    // Создаем список переводов но перевод всегда один
                    ArrayList<EPData.Serial.Translations> translationsList = new ArrayList<>();
                    EPData.Serial.Translations.Builder translationBuilder = new EPData.Serial.Translations.Builder();
                    List<Map.Entry<String, String>> videoData = new ArrayList<>();
                    videoData.add(new AbstractMap.SimpleEntry<>("HLS", videos.get(i).getFiles().getHls()));
                    videoData.add(new AbstractMap.SimpleEntry<>("DASH", videos.get(i).getFiles().getDash_sep()));
                    videoData.add(new AbstractMap.SimpleEntry<>("MP4 144p", videos.get(i).getFiles().getMp4_144()));
                    videoData.add(new AbstractMap.SimpleEntry<>("MP4 240p", videos.get(i).getFiles().getMp4_240()));
                    videoData.add(new AbstractMap.SimpleEntry<>("MP4 360p", videos.get(i).getFiles().getMp4_360()));
                    videoData.add(new AbstractMap.SimpleEntry<>("MP4 480p", videos.get(i).getFiles().getMp4_480()));
                    videoData.add(new AbstractMap.SimpleEntry<>("MP4 720p", videos.get(i).getFiles().getMp4_720()));
                    videoData.add(new AbstractMap.SimpleEntry<>("MP4 1080p", videos.get(i).getFiles().getMp4_1080()));
                    translationBuilder.setVideoData(videoData);
                    translationBuilder.setTitle("Перевод неизвестен");
                    translationsList.add(translationBuilder.build());
                    episodeBuilder.setTranslations(translationsList);
                    episodeBuilder.setTitle(videos.get(i).getTitle());
                    episodesList.add(episodeBuilder.build());
                }
                seasonBuilder.setEpisodes(episodesList);
                seasonsList.add(seasonBuilder.build());
                epdata.setSeasons(seasonsList);
                intent.putExtra("serial", epdata.build());
                intent.putExtra("titleQuality", "HLS");
                intent.putExtra("indexQuality", 0);
                intent.putExtra("indexSeason", 0);
                intent.putExtra("indexEpisode", 0);
                intent.putExtra("indexTranslation", 0);
                startActivity(intent);
            }
        });


        if (playlistGroupItem == null) return binding.getRoot();
        groupPlaylistVideoCreate();


        return binding.getRoot();
    }


    private RecyclerView rvItemPlaylist;
    private void groupPlaylistVideoCreate() {
        vkVideo = new VKVideo(AccountManager.getAccessToken(getContext()));

        ImageView app_bar_image_film_poster = binding.appBarImageFilmPoster;
        Picasso.get().load(playlistGroupItem.getImages().get(playlistGroupItem.getImages().size() - 1).getUrl()).into(app_bar_image_film_poster);

        rvItemPlaylist = binding.rvItemPlaylist;

        rvItemPlaylist.setLayoutManager(new LinearLayoutManager(getContext()));
        rvItemPlaylist.setAdapter(adapterVideos = new AdapterVideos());
        rvItemPlaylist.addOnItemTouchListener(new MyRecyclerViewItemTouchListener(getContext(), rvItemPlaylist, new MyRecyclerViewItemTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                EPData.Film.Translations.Builder translationBuilder = new EPData.Film.Translations.Builder();
                List<Map.Entry<String, String>> videoData = new ArrayList<>();
                videoData.add(new AbstractMap.SimpleEntry<>("HLS", videos.get(position).getFiles().getHls()));
                translationBuilder.setVideoData(videoData);
                translationBuilder.setTitle("Перевод неизвестен");
                EPData.Film.Builder epdata = new EPData.Film.Builder();
                ArrayList<EPData.Film.Translations> translations = new ArrayList<>();
                translations.add(translationBuilder.build());
                epdata.setTranslations(translations);
                epdata.setPoster(videos.get(position).getImages().get(videos.get(position).getImages().size() - 1).getUrl());
                epdata.setId(String.valueOf(videos.get(position).getId()));

                Intent intent = new Intent(getContext(), ExoActivity.class);
                intent.putExtra("film", epdata.build());
                intent.putExtra("titleQuality", "HLS");
                intent.putExtra("indexQuality", 0);
                intent.putExtra("indexSeason", 0);
                intent.putExtra("indexEpisode", 0);
                intent.putExtra("indexTranslation", 0);

                startActivity(intent);


            }

            @Override
            public void onLongItemClick(RecyclerView.ViewHolder holder, View view, int position) {

            }
        }));

        rvItemPlaylist.addOnScrollListener(new MyRecyclerViewScrollListener(MyRecyclerViewScrollListener.VERTICAL) {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd() {
                vkVideo.getVideosGroup(String.valueOf(playlistGroupItem.getOwnerId()), String.valueOf(playlistGroupItem.getId()), videos.size(), new VKVideo.GetAllVideosGroupCallback() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(ArrayList<VKVideo.VideoItem> vds) {
                        videos.addAll(vds);
                        if (videos.isEmpty()) {
                            nullDatalayoutRoot.setVisibility(View.VISIBLE);
                            buttonPlayAll.setVisibility(View.GONE);
                        } else {
                            rvItemPlaylist.setVisibility(View.VISIBLE);
                            nullDatalayoutRoot.setVisibility(View.GONE);
                            adapterVideos.notifyItemInserted(videos.size());
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        });


        vkVideo.getVideosGroup(String.valueOf(playlistGroupItem.getOwnerId()), String.valueOf(playlistGroupItem.getId()), videos.size(), new VKVideo.GetAllVideosGroupCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<VKVideo.VideoItem> vds) {
                videos.addAll(vds);
                if (videos.isEmpty()) {
                    nullDatalayoutRoot.setVisibility(View.VISIBLE);
                    buttonPlayAll.setVisibility(View.GONE);
                } else {
                    rvItemPlaylist.setVisibility(View.VISIBLE);
                    nullDatalayoutRoot.setVisibility(View.GONE);
                    adapterVideos.notifyItemInserted(videos.size());
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private boolean isUpdate = false;
    @Override
    public void onRefresh() {
        Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (!videos.isEmpty() && adapterVideos != null && msg.what == 1) {
                    adapterVideos.notifyDataSetChanged();
                }
                swipeRefreshLayout.setRefreshing(false);
                isUpdate = false;
                return false;
            }
        });
        isUpdate = true;
        vkVideo.getVideosGroup(String.valueOf(playlistGroupItem.getOwnerId()), String.valueOf(playlistGroupItem.getId()), videos.size(), new VKVideo.GetAllVideosGroupCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(ArrayList<VKVideo.VideoItem> vds) {
                videos.addAll(videos.size(), vds);
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onError(Exception e) {
                handler.sendEmptyMessage(0);
            }
        });
    }


    private static class AdapterVideos extends RecyclerView.Adapter<AdapterVideos.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_5, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            VKVideo.VideoItem videoItem = videos.get(position);
            Picasso.get().load(videoItem.getImages().get(videoItem.getImages().size() / 2).getUrl()).into(holder.imageViewVkImgPoster);
            holder.textViewTitleVkVideo.setText(videoItem.getTitle());
            holder.textViewTotalViewsVkVideo.setText(videoItem.getViews() + " просмотров");
            holder.cardViewMoreOptionsVkVideo.setId(position);
            holder.cardViewMoreOptionsVkVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return videos.size();
        }

        private static class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView imageViewVkImgPoster;
            private final TextView textViewTitleVkVideo;
            private final TextView textViewTotalViewsVkVideo;
            private final CardView cardViewMoreOptionsVkVideo;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageViewVkImgPoster = itemView.findViewById(R.id.imageViewVkImgPoster);
                textViewTitleVkVideo = itemView.findViewById(R.id.textViewTitleVkVideo);
                textViewTotalViewsVkVideo = itemView.findViewById(R.id.textViewTotalViewsVkVideo);
                cardViewMoreOptionsVkVideo = itemView.findViewById(R.id.cardViewMoreOptionsVkVideo);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videos.clear();
    }
}