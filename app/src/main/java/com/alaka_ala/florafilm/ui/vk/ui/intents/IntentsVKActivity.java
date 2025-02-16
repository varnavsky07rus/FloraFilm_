package com.alaka_ala.florafilm.ui.vk.ui.intents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.ui.player.exo.ExoActivity;
import com.alaka_ala.florafilm.ui.player.exo.models.EPData;
import com.alaka_ala.florafilm.ui.vk.AccountManager;
import com.alaka_ala.florafilm.ui.vk.parser.VKVideo;
import com.alaka_ala.florafilm.ui.vk.ui.groups.playlist.PlaylistGroupFragment;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntentsVKActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intents_vkactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLIntentsVK), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        String access_token_vk = AccountManager.getAccessToken(this);

        if (access_token_vk != null) {
            VKVideo vkVideo = new VKVideo(access_token_vk);
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    String videoUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
                    if (videoUrl != null) {
                        String regex = "(video)(-?\\d+_\\d+)";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(videoUrl);
                        if (matcher.find()) {
                            String extractedValue = matcher.group(2); // Получаем значение из второй группы захвата
                            vkVideo.getVideo(extractedValue, new VKVideo.GetVideoCallback() {
                                @Override
                                public void onSuccess(ArrayList<VKVideo.VideoItem> videos) {
                                    EPData.Film.Builder epData = new EPData.Film.Builder();
                                    EPData.Film.Translations.Builder translations = new EPData.Film.Translations.Builder();
                                    translations.setTitle("Неизвестен [VK Video]");
                                    List<Map.Entry<String, String>> videoData = new ArrayList<>();
                                    videoData.add(new AbstractMap.SimpleEntry<>("HLS", videos.get(0).getFiles().getHls()));
                                    translations.setVideoData(videoData);
                                    epData.setPoster(videos.get(0).getImages().get(0).getUrl());
                                    ArrayList<EPData.Film.Translations> t = new ArrayList<>();
                                    t.add(translations.build());
                                    epData.setTranslations(t);
                                    Intent intent = new Intent(IntentsVKActivity.this, ExoActivity.class);
                                    intent.putExtra("film", epData.build());
                                    intent.putExtra("titleQuality", "HLS");
                                    intent.putExtra("indexQuality", 0);
                                    intent.putExtra("indexSeason", 0);
                                    intent.putExtra("indexEpisode", 0);
                                    intent.putExtra("indexTranslation", 0);
                                    startActivity(intent);
                                }

                                @Override
                                public void onError(String e) {
                                    Toast.makeText(IntentsVKActivity.this, e, Toast.LENGTH_SHORT).show();
                                }
                            });
                            System.out.println(extractedValue); // Выведет: -206259022_456240394
                        } else {
                            regex = "(playlist/)(-?\\d+_\\d+)";
                            pattern = Pattern.compile(regex);
                            matcher = pattern.matcher(videoUrl);
                            if (matcher.find()) {
                                if (matcher.groupCount() ==  2) {
                                    String[] extractedValue = matcher.group(2).split("_"); // Получаем значение из второй группы захвата
                                    vkVideo.getAlbum(Integer.parseInt(extractedValue[0]), Integer.parseInt(extractedValue[1]), 0, new VKVideo.GetAlbumCallback() {
                                        @Override
                                        public void onSuccess(VKVideo.PlaylistGroupItem playlistGroupItem) {
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("playlist", playlistGroupItem);
                                            PlaylistGroupFragment playlistGroupFragment = new PlaylistGroupFragment();
                                            playlistGroupFragment.setArguments(bundle);
                                            if (getSupportFragmentManager().findFragmentByTag("PLAYLIST_VK") == null) {
                                                getSupportFragmentManager().beginTransaction().add(R.id.mainLIntentsVK, playlistGroupFragment, "PLAYLIST_VK").commit();
                                            } else {
                                                getSupportFragmentManager().beginTransaction().replace(R.id.mainLIntentsVK, playlistGroupFragment, "PLAYLIST_VK").commit();
                                            }
                                        }

                                        @Override
                                        public void onError(String error) {
                                            Toast.makeText(IntentsVKActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(this, "Некорректная ссылка плейлиста", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Данный тип ссылок временно не поддерживается", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }


                }
            }
        } else {
            Toast.makeText(this, "Авторизуйте аккаунт VK в приложении FloraFilm", Toast.LENGTH_SHORT).show();
        }
    }
}