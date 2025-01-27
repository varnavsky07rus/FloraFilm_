package com.alaka_ala.florafilm.ui.player.exo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.ActivityExoBinding;
import com.alaka_ala.florafilm.sys.vibix.Vibix;
import com.alaka_ala.florafilm.ui.player.exo.models.EPData;
import com.alaka_ala.florafilm.ui.vk.LoginVkActivity;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class ExoActivity extends AppCompatActivity {
    private ActivityExoBinding binding;

    private ExoPlayer player;

    private EPData.Film film;
    private EPData.Serial serial;
    private String qualityTitle = "AUTO";
    private int indexQuality = -1;
    private int indexSeason = -1;
    private int indexEpisode = -1;
    private int indexTranslation = -1;
    private String TYPE_CONTENT = "NO_TYPE";
    private String BALANCER = "Не определен";

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExoBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getExtras();



        if (TYPE_CONTENT.equals("FILM")) {
            String currentUrl = film.getTranslations().get(indexTranslation).getVideoData().get(indexQuality).getValue();
            initializePlayer(currentUrl);
        } else if (TYPE_CONTENT.equals("SERIAL")) {
            ArrayList<String> currentUrls = getEpisodes();
            initializePlayer(currentUrls);
        }

    }

    private @NonNull ArrayList<String> getEpisodes() {
        ArrayList<String> currentUrls = new ArrayList<>();

        for (int i = 0; i < serial.getSeasons().get(indexSeason).getEpisodes().size(); i++) {
            // парсим все серии выбранного сезона
            String currentUrl = serial.getSeasons().get(indexSeason).getEpisodes().get(i).getTranslations().get(indexTranslation).getVideoData().get(indexQuality).getValue();
            currentUrls.add(currentUrl);
        }
        return currentUrls;
    }

    private void  preparePlayer() {

        // Создаем экземпляр DefaultHttpDataSource.Factory
        DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory();
        // Создаем RequestProperties и устанавливаем User-Agent
        DefaultHttpDataSource.RequestProperties requestProperties = new DefaultHttpDataSource.RequestProperties();
        requestProperties.set("User-Agent", LoginVkActivity.USER_AGENT_KATE); // Замените на ваш User-Agent
        // Устанавливаем RequestProperties в httpDataSourceFactory
        httpDataSourceFactory.setDefaultRequestProperties(requestProperties.getSnapshot());
        player = new ExoPlayer.Builder(this).setMediaSourceFactory(new DefaultMediaSourceFactory(httpDataSourceFactory)).build();
        binding.playerView.setPlayer(player);
    }

    private void initializePlayer(String videoUrl) {
        if (videoUrl.isEmpty()) {
            Snackbar.make(binding.getRoot(), "Некорректный URL", Snackbar.LENGTH_SHORT).show();
        }
        if (player == null) {
            preparePlayer();
        }


        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        player.setMediaItem(mediaItem);

        player.prepare();
        player.play();
    }

    private void initializePlayer(ArrayList<String> videoUrls) {
        if (videoUrls.isEmpty()) {
            Snackbar.make(binding.getRoot(), "Ошибка данных", Snackbar.LENGTH_SHORT).show();
        }
        if (player == null) {
            preparePlayer();
        }
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        for (int i = 0; i < videoUrls.size(); i++) {
            MediaItem mediaItem = MediaItem.fromUri(videoUrls.get(i));
            mediaItems.add(mediaItem);
        }
        player.setMediaItems(mediaItems, indexEpisode, 0);
        player.prepare();
        player.play();
    }

    private void getExtras() {
        film = (EPData.Film) getIntent().getSerializableExtra("film");
        serial = (EPData.Serial) getIntent().getSerializableExtra("serial");
        qualityTitle = getIntent().getStringExtra("titleQuality");
        indexQuality = getIntent().getIntExtra("indexQuality", 0);
        indexSeason = getIntent().getIntExtra("indexSeason", 0);
        indexEpisode = getIntent().getIntExtra("indexEpisode", 0);
        indexTranslation = getIntent().getIntExtra("indexTranslation", 0);
        defineTypeContent();
    }

    private void defineTypeContent() {
        if (film != null) {
            TYPE_CONTENT = "FILM";
        } else if (serial != null) {
            TYPE_CONTENT = "SERIAL";
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}