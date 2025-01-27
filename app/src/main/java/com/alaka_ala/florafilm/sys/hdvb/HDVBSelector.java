package com.alaka_ala.florafilm.sys.hdvb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.sys.hdvb.models.HDVBFilm;
import com.alaka_ala.florafilm.sys.hdvb.models.HDVBSerial;
import com.alaka_ala.florafilm.sys.vibix.VibixSelector;
import com.alaka_ala.florafilm.ui.player.exo.ExoActivity;
import com.google.android.material.snackbar.Snackbar;

public class HDVBSelector {
    public interface SelectorListener {
        void onClick(int indexTranslation, int indexSeasons, int indexEpisode, int indexQuality);
    }

    private VibixSelector.SelectorListener selectorListener;

    public void setSelectorListener(VibixSelector.SelectorListener selectorListener) {
        this.selectorListener = selectorListener;
    }

    public HDVBSelector(LinearLayout root, String CURRENT_TYPE_CONTENT) {
        this.root = root;
        this.CURRENT_TYPE_CONTENT = CURRENT_TYPE_CONTENT;
    }

    private final LinearLayout root;
    private HDVBFilm film = null;
    private HDVBSerial serial = null;
    private final String CURRENT_TYPE_CONTENT;

    public HDVBSelector(LinearLayout root, HDVBFilm film) {
        CURRENT_TYPE_CONTENT = "FILM";
        this.root = root;
        this.film = film;
    }

    public HDVBSelector(LinearLayout root, HDVBSerial serial) {
        CURRENT_TYPE_CONTENT = "SERIAL";
        this.root = root;
        this.serial = serial;
    }

    public HDVBFilm getFilm() {
        return film;
    }

    public HDVBSerial getSerial() {
        return serial;
    }

    public String getCurrentTypeContent() {
        return CURRENT_TYPE_CONTENT;
    }

    public LinearLayout getRoot() {
        return root;
    }

    private Activity activity;
    public void buildSelector(Activity activity) {
        this.activity = activity;
        if (CURRENT_TYPE_CONTENT.equals("FILM")) {
            createFilmSelector();
        } else {
            createSerialSelector();
        }
    }

    // Создание фильма
    @SuppressLint("MissingInflatedId")
    private void createFilmSelector() {
        if (film == null) return;
        for (int i = 0; i < film.getHdvbDataFilm().getTranslations().size(); i++) {
            String titleTranslation = film.getHdvbDataFilm().getTranslations().get(i).getTitle() + " | [HDVB]";
            // Корневой элемент View
            View viewTranslation = LayoutInflater.from(root.getContext()).inflate(R.layout.selector_film_item_1, root, false);
            // Задаем название перевода
            TextView textViewTitleFolder = viewTranslation.findViewById(R.id.textViewTitleFolder);
            // Данный LinearLayout нужен для отработки кликов
            LinearLayout linearLayoutTitleClick = viewTranslation.findViewById(R.id.linearLayoutTitleClick);
            linearLayoutTitleClick.setId(i);
            // В данный LinearLayout помещаем все элементы (файлы)
            LinearLayout linearLayoutFf = viewTranslation.findViewById(R.id.linearLayoutFf);

            textViewTitleFolder.setText(titleTranslation);
            linearLayoutTitleClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vTranslation) {
                    if (linearLayoutFf.getChildCount() > 0) {
                        linearLayoutFf.removeAllViews();
                        return;
                    }
                    int sizeVideos = film.getHdvbDataFilm().getTranslations().get(vTranslation.getId()).getVideoData().size();
                    for (int i = 0; i < sizeVideos; i++) {
                        String titleQuality = film.getHdvbDataFilm().getTranslations().get(vTranslation.getId()).getVideoData().get(i).getKey();
                        String urlVideo = film.getHdvbDataFilm().getTranslations().get(vTranslation.getId()).getVideoData().get(i).getValue();

                        // Корневой элемент View
                        View viewVideo = LayoutInflater.from(root.getContext()).inflate(R.layout.selector_film_item_2, root, false);
                        // Задаем название качества
                        TextView textViewTitleFiles = viewVideo.findViewById(R.id.textViewTitleFiles);
                        textViewTitleFiles.setText(titleQuality);
                        // Данный LinearLayout нужен для отработки кликов
                        LinearLayout linearLayoutTitleClick2 = viewVideo.findViewById(R.id.linearLayoutTitleClick2);
                        linearLayoutTitleClick2.setId(i);
                        linearLayoutTitleClick2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View vQuality) {
                                /*Intent intent = new Intent(activity, PlayerWebActivity.class);
                                intent.putExtra("url", urlVideo);
                                intent.putExtra("poster", film.getPoster());
                                activity.startActivity(intent);*/

                                Intent intent = new Intent(activity, ExoActivity.class);
                                intent.putExtra("film", film);
                                intent.putExtra("indexTranslation", vTranslation.getId());
                                intent.putExtra("titleQuality", titleQuality);
                                intent.putExtra("indexQuality", vQuality.getId());
                                intent.putExtra("urlVideo", urlVideo);
                                activity.startActivity(intent);


                            }
                        });

                        linearLayoutFf.addView(viewVideo);


                    }
                }
            });


            root.addView(viewTranslation);
        }
    }

    // Создание сериала
    private void createSerialSelector() {
        if (serial == null) return;
        for (int i = 0; i < serial.getHdvbDataSerial().getSeasons().size(); i++) {
            // Тут парсятся сезоны
            String titleSeason = serial.getHdvbDataSerial().getSeasons().get(i).getTitle();
            View viewSeason = LayoutInflater.from(root.getContext()).inflate(R.layout.selector_film_item_1, root, false);
            LinearLayout linearLayoutRootEpisode = viewSeason.findViewById(R.id.linearLayoutFf);
            TextView textViewTitleFolder = viewSeason.findViewById(R.id.textViewTitleFolder);
            textViewTitleFolder.setText(titleSeason);
            LinearLayout linearLayoutTitleClick = viewSeason.findViewById(R.id.linearLayoutTitleClick);
            linearLayoutTitleClick.setId(i);
            linearLayoutTitleClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vSeason) {
                    if (linearLayoutRootEpisode.getChildCount() > 0) {
                        linearLayoutRootEpisode.removeAllViews();
                        return;
                    }

                    for (int j = 0; j < serial.getHdvbDataSerial().getSeasons().get(vSeason.getId()).getEpisodes().size(); j++) {
                        // Тут парсятся эпизоды
                        String titleEpisode = serial.getHdvbDataSerial().getSeasons().get(vSeason.getId()).getEpisodes().get(j).getTitle();
                        View viewEpisode = LayoutInflater.from(root.getContext()).inflate(R.layout.selector_film_item_1, root, false);
                        LinearLayout linearLayoutTranslations = viewEpisode.findViewById(R.id.linearLayoutFf);
                        TextView textViewTitleFolder = viewEpisode.findViewById(R.id.textViewTitleFolder);
                        textViewTitleFolder.setText(titleEpisode);
                        LinearLayout linearLayoutTitleClick = viewEpisode.findViewById(R.id.linearLayoutTitleClick);
                        linearLayoutTitleClick.setId(j);

                        linearLayoutTitleClick.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View vEpisode) {
                                if (linearLayoutTranslations.getChildCount() > 0) {
                                    linearLayoutTranslations.removeAllViews();
                                    return;
                                }
                                for (int k = 0; k < serial.getHdvbDataSerial().getSeasons().get(vSeason.getId()).getEpisodes().get(vEpisode.getId()).getTranslations().size(); k++) {
                                    // Тут парсятся переводы
                                    String titleTranslation = serial.getHdvbDataSerial().getSeasons().get(vSeason.getId()).getEpisodes().get(vEpisode.getId()).getTranslations().get(k).getTitle();
                                    View viewTranslation = LayoutInflater.from(root.getContext()).inflate(R.layout.selector_film_item_1, root, false);
                                    LinearLayout linearLayoutVideos = viewTranslation.findViewById(R.id.linearLayoutFf);
                                    TextView textViewTitleFolder = viewTranslation.findViewById(R.id.textViewTitleFolder);
                                    textViewTitleFolder.setText(titleTranslation);
                                    LinearLayout linearLayoutTitleClick = viewTranslation.findViewById(R.id.linearLayoutTitleClick);
                                    linearLayoutTitleClick.setId(k);

                                    linearLayoutTitleClick.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View vTranslation) {
                                            if (linearLayoutVideos.getChildCount() > 0) {
                                                linearLayoutVideos.removeAllViews();
                                                return;
                                            }
                                            for (int l = 0; l < serial.getHdvbDataSerial().getSeasons().get(vSeason.getId()).getEpisodes().get(vEpisode.getId()).getTranslations().get(vTranslation.getId()).getVideoData().size(); l++) {
                                                // Тут парсятся видео файлы
                                                String titleQuality = serial.getHdvbDataSerial().getSeasons().get(vSeason.getId()).getEpisodes().get(vEpisode.getId()).getTranslations().get(vTranslation.getId()).getVideoData().get(l).getKey();
                                                String urlVideo = serial.getHdvbDataSerial().getSeasons().get(vSeason.getId()).getEpisodes().get(vEpisode.getId()).getTranslations().get(vTranslation.getId()).getVideoData().get(l).getValue();

                                                // Корневой элемент View
                                                View viewVideo = LayoutInflater.from(root.getContext()).inflate(R.layout.selector_film_item_2, root, false);
                                                // Задаем название качества
                                                TextView textViewTitleFiles = viewVideo.findViewById(R.id.textViewTitleFiles);
                                                textViewTitleFiles.setText(titleQuality);
                                                // Данный LinearLayout нужен для отработки кликов
                                                LinearLayout linearLayoutTitleClick2 = viewVideo.findViewById(R.id.linearLayoutTitleClick2);
                                                linearLayoutTitleClick2.setId(l);

                                                linearLayoutTitleClick2.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View vQuality) {
                                                        Intent intent = new Intent(activity, ExoActivity.class);
                                                        intent.putExtra("serial", serial);
                                                        intent.putExtra("titleQuality", titleQuality);
                                                        intent.putExtra("indexQuality", vQuality.getId());
                                                        intent.putExtra("indexSeason", vSeason.getId());
                                                        intent.putExtra("indexEpisode", vEpisode.getId());
                                                        intent.putExtra("indexTranslation", vTranslation.getId());
                                                        intent.putExtra("urlVideo", urlVideo);
                                                        activity.startActivity(intent);


                                                        Snackbar.make(root, "tapToIndex:" + vQuality.getId() + " | url:" + urlVideo, Snackbar.LENGTH_SHORT).show();
                                                    }
                                                });

                                                linearLayoutVideos.addView(viewVideo);
                                            }
                                        }
                                    });

                                    // Переводы добавляются в linearLayoutTranslations
                                    linearLayoutTranslations.addView(viewTranslation);
                                }
                            }
                        });

                        // Епизоды добавляются в linearLayoutRootEpisode
                        linearLayoutRootEpisode.addView(viewEpisode);
                    }
                }
            });

            // Сезоны добавляются в Root (linearLayoutRoot)
            root.addView(viewSeason);
        }
    }
}
