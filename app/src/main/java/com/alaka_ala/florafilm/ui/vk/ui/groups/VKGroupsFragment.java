package com.alaka_ala.florafilm.ui.vk.ui.groups;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.FragmentVKGroupsBinding;
import com.alaka_ala.florafilm.sys.AsyncThreadBuilder;
import com.alaka_ala.florafilm.sys.utils.MyRecyclerViewItemTouchListener;
import com.alaka_ala.florafilm.ui.player.exo.ExoActivity;
import com.alaka_ala.florafilm.ui.player.exo.models.EPData;
import com.alaka_ala.florafilm.ui.vk.AccountManager;
import com.alaka_ala.florafilm.ui.vk.LoginVkActivity;
import com.alaka_ala.florafilm.ui.vk.parser.VKVideo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class VKGroupsFragment extends Fragment {
    private FragmentVKGroupsBinding binding;
    private VKVideo vkVideo;
    private LinearLayout linearLayoutRootGroups;
    private NestedScrollView scrollViewsGroups;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVKGroupsBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);

        if (AccountManager.getAccessToken(getContext()) == null) {
            new MaterialAlertDialogBuilder(getContext()).setTitle("Добавление аккаунта").setMessage("Необходимо добавить аккаунт VK для работы с ВК Видео").setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getContext(), LoginVkActivity.class));
                }
            }).show();
            return binding.getRoot();
        }

        vkVideo = new VKVideo(AccountManager.getAccessToken(getContext()));
        linearLayoutRootGroups = binding.linearLayoutRootGroups;
        scrollViewsGroups = binding.scrollViewsGroups;
        AsyncThreadBuilder asyncThreadBuilder = new AsyncThreadBuilder() {
            @Override
            public Runnable start(Handler finishHandler) {
                getGroups();
                return null;
            }

            @Override
            public void finishHandler(Bundle bundle) {

            }
        };
        asyncThreadBuilder.onStart();
        return binding.getRoot();
    }

    private void getGroups() {
        // FloraFilm
        vkVideo.getAlbums(-229006348, 0, new VKVideo.GetAlbumsCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(VKVideo.GroupItem group) {
                addGroup(group);
            }

            @Override
            public void onError(String e) {
                if (getContext() == null) return;
                Toast.makeText(getContext(), e, Toast.LENGTH_SHORT).show();
            }
        });

        // Кино в ВК Видео
        vkVideo.getAlbums(-217672812, 0, new VKVideo.GetAlbumsCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(VKVideo.GroupItem group) {
                addGroup(group);
            }

            @Override
            public void onError(String e) {
                if (getContext() == null) return;
                Snackbar.make(binding.getRoot(), e, Snackbar.LENGTH_LONG).show();
            }
        });

        // Парадиз
        vkVideo.getAlbums(-208081064, 0, new VKVideo.GetAlbumsCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(VKVideo.GroupItem group) {
                addGroup(group);
            }

            @Override
            public void onError(String e) {
                if (getContext() == null) return;
                Snackbar.make(binding.getRoot(), e, Snackbar.LENGTH_LONG).show();
            }
        });

        // KINO BRO
        vkVideo.getAlbums(-220018529, 0, new VKVideo.GetAlbumsCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(VKVideo.GroupItem group) {
                addGroup(group, "КИНО БРО");
            }

            @Override
            public void onError(String e) {
                if (getContext() == null) return;
                Snackbar.make(binding.getRoot(), e, Snackbar.LENGTH_LONG).show();
            }
        });


        // КИНО ОКНО HD
        vkVideo.getAlbums(-210183487, 0, new VKVideo.GetAlbumsCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(VKVideo.GroupItem group) {
                addGroup(group);
            }

            @Override
            public void onError(String e) {
                if (getContext() == null) return;
                Snackbar.make(binding.getRoot(), e, Snackbar.LENGTH_LONG).show();
            }
        });

        // ShadowTavern (Видео с подборками что посмотреть)
        vkVideo.getAlbums(-182568089, 0, new VKVideo.GetAlbumsCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(VKVideo.GroupItem group) {
                addGroup(group);
            }

            @Override
            public void onError(String e) {
                if (getContext() == null) return;
                Snackbar.make(binding.getRoot(), e, Snackbar.LENGTH_LONG).show();
            }
        });

        /*// Матроскин фильм
        vkVideo.getAlbums(-162918645, 0, new VKVideo.GetAlbumsCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(VKVideo.GroupItem group) {
                addGroup(group);
            }

            @Override
            public void onError(String e) {
                if (getContext() == null) return;
                Snackbar.make(binding.getRoot(), e, Snackbar.LENGTH_LONG).show();
            }
        });*/

        vkVideo.getAlbums(-23712274, 0, new VKVideo.GetAlbumsCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(VKVideo.GroupItem group) {
                addGroup(group);
            }

            @Override
            public void onError(String e) {
                if (getContext() == null) return;
                Snackbar.make(binding.getRoot(), e, Snackbar.LENGTH_LONG).show();
            }
        });


        int countMyGroups = GroupsMyManager.getGroups(getContext()).size();
        if (countMyGroups > 0) {
            for (int i = 0; i < countMyGroups; i++) {
                String id = GroupsMyManager.getGroups(getContext()).get(i);
                try {
                    vkVideo.getAlbums(Integer.parseInt(id), 0, new VKVideo.GetAlbumsCallback() {
                        @Override
                        public void onSuccess(VKVideo.GroupItem group) {
                            addGroup(group);
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
                } catch (NumberFormatException e) {
                    GroupsMyManager.removeGroup(getContext(), id);
                }

            }
        }


    }

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    private void addGroup(VKVideo.GroupItem group) {
        if (getContext() == null || group == null) return;
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
        String s = group.getPhoto_100();
        if (!s.isEmpty()) {
            Picasso.get().load(s).into(imageViewPhotoGroup100);
        }
        RecyclerView rvPlaylistsGroup = view.findViewById(R.id.rvPlaylistsGroup);
        rvPlaylistsGroup.setId(group.getTitleGroup().getBytes().length);
        rvPlaylistsGroup.setId(group.getTitleGroup().getBytes().length);
        rvPlaylistsGroup.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        AdapterPlaylistsGroup adapterPlaylistsGroup = new AdapterPlaylistsGroup(group);
        rvPlaylistsGroup.setAdapter(adapterPlaylistsGroup);
        rvPlaylistsGroup.addOnItemTouchListener(new MyRecyclerViewItemTouchListener(getContext(), rvPlaylistsGroup, new MyRecyclerViewItemTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                if (!group.getPlayLists().isEmpty()) {
                    Bundle bundle = new Bundle();
                    String key = !group.getPlayLists().isEmpty() ? "playlist" : "video";
                    bundle.putSerializable(key, !group.getPlayLists().isEmpty() ? group.getPlayLists().get(position) : group.getVideoItems().get(position));
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_VKGroupsFragment_to_playlistGroupFragment, bundle);
                } else {
                    EPData.Film.Builder filmBuilder = new EPData.Film.Builder();
                    filmBuilder.setId(String.valueOf(position));
                    filmBuilder.setPoster(group.getVideoItems().get(position).getImages().get(group.getVideoItems().get(position).getImages().size() - 1).getUrl());
                    EPData.Film.Translations.Builder translationBuilder = new EPData.Film.Translations.Builder();
                    translationBuilder.setTitle(group.getVideoItems().get(position).getTitle());
                    List<Map.Entry<String, String>> videoData = new ArrayList<>();
                    videoData.add(new AbstractMap.SimpleEntry<>("HLS", group.getVideoItems().get(position).getFiles().getHls()));
                    videoData.add(new AbstractMap.SimpleEntry<>("DASH", group.getVideoItems().get(position).getFiles().getDash_sep()));
                    videoData.add(new AbstractMap.SimpleEntry<>("MP4 144p", group.getVideoItems().get(position).getFiles().getMp4_144()));
                    videoData.add(new AbstractMap.SimpleEntry<>("MP4 240p", group.getVideoItems().get(position).getFiles().getMp4_240()));
                    videoData.add(new AbstractMap.SimpleEntry<>("MP4 360p", group.getVideoItems().get(position).getFiles().getMp4_360()));
                    videoData.add(new AbstractMap.SimpleEntry<>("MP4 480p", group.getVideoItems().get(position).getFiles().getMp4_480()));
                    videoData.add(new AbstractMap.SimpleEntry<>("MP4 720p", group.getVideoItems().get(position).getFiles().getMp4_720()));
                    videoData.add(new AbstractMap.SimpleEntry<>("MP4 1080p", group.getVideoItems().get(position).getFiles().getMp4_1080()));
                    translationBuilder.setVideoData(videoData);
                    EPData.Film.Translations translations = translationBuilder.build();
                    ArrayList<EPData.Film.Translations> translationsList = new ArrayList<>();
                    translationsList.add(translations);
                    filmBuilder.setTranslations(translationsList);
                    Intent intent = new Intent(getContext(), ExoActivity.class);
                    intent.putExtra("film", filmBuilder.build());
                    intent.putExtra("titleQuality", "HLS");
                    intent.putExtra("indexQuality", 4);
                    intent.putExtra("indexSeason", 0);
                    intent.putExtra("indexEpisode", 0);
                    intent.putExtra("indexTranslation", 0);
                    startActivity(intent);
                }

            }

            @Override
            public void onLongItemClick(RecyclerView.ViewHolder holder, View view, int position) {

            }
        }));
        String screenName = group.getScreenName();
        // Делаем что бы моя группа была первая в списке
        if (screenName.equals("florafilm_app")) {
            linearLayoutRootGroups.addView(view, 0);
        } else {
            // Все остальные внизу
            linearLayoutRootGroups.addView(view);
        }
    }

    @SuppressLint("SetTextI18n")
    private void addGroup(VKVideo.GroupItem group, String titleGroup) {
        if (getContext() == null || group == null) return;
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
        textViewTitleGroup.setText(!group.getTitleGroup().isEmpty() ? titleGroup : "");
        TextView textViewTotalViedosPlaylists = view.findViewById(R.id.textViewTotalViedosPlaylists);
        textViewTotalViedosPlaylists.setText("Видео " + group.getCountVideo() + " / Плейлистов " + group.getPlayLists().size());
        ImageView imageViewPhotoGroup100 = view.findViewById(R.id.imageViewPhotoGroup100);
        String s = group.getPhoto_100();
        if (!s.isEmpty()) {
            Picasso.get().load(s).into(imageViewPhotoGroup100);
        }
        RecyclerView rvPlaylistsGroup = view.findViewById(R.id.rvPlaylistsGroup);
        rvPlaylistsGroup.setId(group.getTitleGroup().getBytes().length);
        rvPlaylistsGroup.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        AdapterPlaylistsGroup adapterPlaylistsGroup = new AdapterPlaylistsGroup(group);
        rvPlaylistsGroup.setAdapter(adapterPlaylistsGroup);
        rvPlaylistsGroup.addOnItemTouchListener(new MyRecyclerViewItemTouchListener(getContext(), rvPlaylistsGroup, new MyRecyclerViewItemTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("playlist", group.getPlayLists().get(position));
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_VKGroupsFragment_to_playlistGroupFragment, bundle);
            }

            @Override
            public void onLongItemClick(RecyclerView.ViewHolder holder, View view, int position) {

            }
        }));
        String screenName = group.getScreenName();
        // Делаем что бы моя группа была первая в списке
        if (screenName.equals("florafilm_app")) {
            linearLayoutRootGroups.addView(view, 0);
        } else {
            // Все остальные внизу
            linearLayoutRootGroups.addView(view);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //menu.add("Поиск").setIcon(R.drawable.rounded_video_search_24).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        menu.add("Добавить свою группу").setIcon(R.drawable.rounded_add_2_24).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        CharSequence title = item.getTitle();
        if (title == null) return super.onOptionsItemSelected(item);
        if (item.getTitle().equals("Поиск")) {
            //Navigation.findNavController(binding.getRoot()).navigate(R.id.action_VKGroupsFragment_to_searchFragment);
        } else if (item.getTitle().equals("Добавить свою группу")) {
            EditText editText = new EditText(getContext());
            editText.setHint("https://vk.com/video-23712274_456240936");
            new MaterialAlertDialogBuilder(getContext()).setView(editText).setTitle("Что бы добавить группу вставьте ссылку на любое видео из этой группы").setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (getContext() == null) return;
                    String url = editText.getText().toString();
                    GroupsMyManager groupsMyManager = new GroupsMyManager();
                    if (groupsMyManager.addGroup(getContext(), url)) {
                        Toast.makeText(getContext(), "Группа добавлена", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Ссылка данного типа не поддерживается =(", Toast.LENGTH_SHORT).show();
                    }
                }
            }).show();
        }

        return super.onOptionsItemSelected(item);
    }


    public static class GroupsMyManager {

        public static boolean addGroup(Context context, String url) {
            if (url == null) return false;
            if (url.isEmpty()) return false;
            ArrayList<String> listIds = getGroups(context);
            String regex = "-(\\d+)_";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(url);

            if (matcher.find()) {
                String extractedValue = "-" + matcher.group(1); // Добавляем минус обратно
                System.out.println(extractedValue); // Выведет: -12345678
                if (extractedValue.startsWith("-") && !extractedValue.endsWith("_")) {
                    listIds.add(extractedValue);
                    SharedPreferences preferences = context.getSharedPreferences("GMManager", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("groups", String.join(",", listIds));
                    editor.apply();
                    return true;
                }
            } else {
                System.out.println("Совпадение не найдено");
                return false;
            }
            return false;
        }

        public static void removeGroup(Context context, String id) {
            ArrayList<String> listIds = getGroups(context);
            listIds.remove(id);
            SharedPreferences preferences = context.getSharedPreferences("GMManager", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("groups", String.join(",", listIds));
            editor.apply();
        }

        public static ArrayList<String> getGroups(Context context) {
            ArrayList<String> listIds = new ArrayList<>();
            SharedPreferences preferences = context.getSharedPreferences("GMManager", Context.MODE_PRIVATE);
            String arrayListStr = preferences.getString("groups", "");
            if (arrayListStr.isEmpty()) return listIds;
            String[] split = arrayListStr.split(",");
            listIds.addAll(Arrays.asList(split));
            return listIds;
        }

    }


}