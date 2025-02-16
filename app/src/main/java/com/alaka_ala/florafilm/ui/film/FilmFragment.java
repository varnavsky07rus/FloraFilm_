package com.alaka_ala.florafilm.ui.film;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.FragmentFilmBinding;
import com.alaka_ala.florafilm.sys.hdvb.HDVB;
import com.alaka_ala.florafilm.sys.hdvb.HDVBSelector;
import com.alaka_ala.florafilm.sys.hdvb.models.HDVBFilm;
import com.alaka_ala.florafilm.sys.hdvb.models.HDVBSerial;
import com.alaka_ala.florafilm.sys.kinovibe.KinoVibe;
import com.alaka_ala.florafilm.sys.kp_api.ItemFilmInfo;
import com.alaka_ala.florafilm.sys.kp_api.KinopoiskAPI;
import com.alaka_ala.florafilm.sys.kp_api.ListFilmItem;
import com.alaka_ala.florafilm.sys.kp_api.ListStaffItem;
import com.alaka_ala.florafilm.sys.utils.MyRecyclerViewItemTouchListener;
import com.alaka_ala.florafilm.sys.utils.PersonsRecyclerAdapter;
import com.alaka_ala.florafilm.sys.utils.UtilsFavoriteAndViewFilm;
import com.alaka_ala.florafilm.sys.vibix.Vibix;
import com.alaka_ala.florafilm.sys.vibix.VibixSelector;
import com.alaka_ala.florafilm.ui.film.actors.ActorFragment;
import com.alaka_ala.florafilm.ui.film.vk_pager.AdapterViewPager2VkFiles;
import com.alaka_ala.florafilm.ui.player.exo.ExoActivity;
import com.alaka_ala.florafilm.ui.player.exo.models.EPData;
import com.alaka_ala.florafilm.ui.vk.AccountManager;
import com.alaka_ala.florafilm.ui.vk.parser.VKVideo;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FilmFragment extends Fragment implements Vibix.ConnectionVibix {
    private FragmentFilmBinding binding;
    private Bundle bundle;
    private KinopoiskAPI kinopoiskAPI;
    // Данный класс не содержит в себе детальную информацию,
    // а лишь краткую, по этому с него нам необходимо лишь взять ID фильма
    private ListFilmItem film;
    private int kinopoiskId;
    private ItemFilmInfo filmInfo;
    private String TYPE_CONTENT;

    private ImageView appBarImage;
    private ImageView imageViewLogoFilm;
    private TextView textViewNameFilm;
    private TextView textViewDescriptionFilm;
    private TextView textViewRatingKp;
    private TextView textViewVoteCountKp;
    private TextView textViewCountries;

    private CardView progressBar;
    private Vibix vibix;
    private KinoVibe kinoVibe;
    private VKVideo vkVideo;
    private LinearLayout linearLayoutContent;
    private LinearLayout linearLayoutRatings;
    private UtilsFavoriteAndViewFilm utilsFavoriteAndViewFilm;


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFilmBinding.inflate(inflater, container, false);
        // Устанавливаем флаги для отображения поверх строки состояния
        /*getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/
        linearLayoutContent = binding.linearLayoutContent;
        utilsFavoriteAndViewFilm = new UtilsFavoriteAndViewFilm(getContext());

        bundle = getArguments();
        if (bundle == null) return binding.getRoot();
        film = (ListFilmItem) bundle.getSerializable("film");
        if (film == null) return binding.getRoot();
        switch (film.getType()) {
            case "FILM":
                TYPE_CONTENT = "(фильм)";
                break;
            case "TV_SERIES":
            case "TV_SHOW":
            case "MINI_SERIES":
            case "ANIME":
                TYPE_CONTENT = "(сериал)";
                break;
            case "null":
                TYPE_CONTENT = "";
                break;
        }
        // Получаем ActionBar из Activity
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(!film.getNameRu().isEmpty() ? film.getNameRu() + TYPE_CONTENT : film.getNameEn() + TYPE_CONTENT);
        }


        appBarImage = binding.appBarImage;
        imageViewLogoFilm = binding.imageViewLogoFilm;
        textViewNameFilm = binding.textViewNameFilm;
        textViewDescriptionFilm = binding.textViewDescriptionFilm;
        progressBar = binding.cardViewProgressBar;
        textViewRatingKp = binding.textViewRatingKp;
        textViewVoteCountKp = binding.textViewVoteCountKp;
        linearLayoutRatings = binding.linearLayoutRatings;
        textViewCountries = binding.textViewCountries;


        getKinopoiskData();

        getVibixData();

        getVKData(inflater);

        getHdvbData();

        getKinoVibeData();

        return binding.getRoot();
    }

    private void getKinoVibeData() {
        kinoVibe = new KinoVibe();
        kinoVibe.parse(film.getKinopoiskId(), new KinoVibe.ConnectionKinoVibe() {
            @Override
            public void startParse() {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void finishParse(String file) {
                if (getContext() == null) return;
                if (file.isEmpty()) return;
                LayoutInflater inflater = getLayoutInflater();
                // Добавляем название балансера на страницу фильма
                View layout_film_files = inflater.inflate(R.layout.layout_film_files, null, false);
                LinearLayout linearLayoutTitleFiles = layout_film_files.findViewById(R.id.linearLayoutTitleFiles);
                ImageView imageViewArrowFiles = layout_film_files.findViewById(R.id.imageViewArrowFiles);
                TextView textViewTitleBalancer = layout_film_files.findViewById(R.id.textViewTitleBalancer);
                String title = "[KinoVibe] [VPN]";
                textViewTitleBalancer.setText(title);
                LinearLayout root = layout_film_files.findViewById(R.id.linearLayoutRootGroups);
                linearLayoutTitleFiles.setOnClickListener(new View.OnClickListener() {
                    boolean isAnimate = false;
                    @Override
                    public void onClick(View v) {
                        if (isAnimate) return;
                        if (root.getVisibility() == View.VISIBLE) {
                            // Скрываем все элементы
                            imageViewArrowFiles.animate().setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    isAnimate = true;
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    isAnimate = false;
                                    if (root.getChildCount() > 0) {
                                        root.removeAllViews();
                                    }
                                    root.setVisibility(View.GONE);
                                }
                            }).rotation(0).start();
                        } else {
                            // Показываем все элементы
                            imageViewArrowFiles.animate().setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    isAnimate = true;
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    isAnimate = false;
                                    // Добавляем слой с названием озвучки
                                    View folderLayout = getLayoutInflater().inflate(R.layout.selector_film_item_1, null, false);
                                    TextView textViewTitleFolder = folderLayout.findViewById(R.id.textViewTitleFolder);
                                    textViewTitleFolder.setText("Неизвестный перевод");
                                    LinearLayout linearLayoutTitleClick = folderLayout.findViewById(R.id.linearLayoutTitleClick);
                                    LinearLayout linearLayoutFf = folderLayout.findViewById(R.id.linearLayoutFf);
                                    linearLayoutTitleClick.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Добавляем слой с названием файла
                                            if (linearLayoutFf.getChildCount() > 0) {
                                                linearLayoutFf.setVisibility(View.GONE);
                                                linearLayoutFf.removeAllViews();
                                                return;
                                            }
                                            linearLayoutFf.setVisibility(View.VISIBLE);
                                            View fileLayout = getLayoutInflater().inflate(R.layout.selector_film_item_2, null, false);
                                            TextView textViewTitleFiles = fileLayout.findViewById(R.id.textViewTitleFiles);
                                            textViewTitleFiles.setText("SD");
                                            LinearLayout linearLayoutTitleClick2 = fileLayout.findViewById(R.id.linearLayoutTitleClick2);
                                            linearLayoutFf.addView(fileLayout);

                                            linearLayoutTitleClick2.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    EPData.Film.Builder builderFilm = new EPData.Film.Builder();
                                                    builderFilm.setId(String.valueOf(film.getKinopoiskId()));
                                                    builderFilm.setPoster(film.getPosterUrl());

                                                    EPData.Film.Translations.Builder builderTranslations = new EPData.Film.Translations.Builder();
                                                    builderTranslations.setTitle("Неизвестный перевод");
                                                    List<Map.Entry<String, String>> videoData = new ArrayList<>();
                                                    videoData.add(new AbstractMap.SimpleEntry<>("(SD) MP4", file));
                                                    builderTranslations.setVideoData(videoData);
                                                    ArrayList<EPData.Film.Translations> translations = new ArrayList<>();
                                                    translations.add(builderTranslations.build());
                                                    builderFilm.setTranslations(translations);

                                                    Intent intent = new Intent(getActivity(), ExoActivity.class);
                                                    intent.putExtra("film", builderFilm.build());
                                                    intent.putExtra("indexTranslation", 0);
                                                    intent.putExtra("titleQuality", 0);
                                                    intent.putExtra("indexQuality", 0);
                                                    intent.putExtra("urlVideo", file);
                                                    getActivity().startActivity(intent);

                                                }
                                            });
                                        }
                                    });
                                    root.addView(folderLayout);
                                    root.setVisibility(View.VISIBLE);
                                }
                            }).rotation(90).start();
                        }
                    }
                });
                linearLayoutContent.addView(layout_film_files);



            }

            @Override
            public void errorParse(String err, int code) {
                if (getContext() == null) return;
                /*if (code != 404) {
                    Snackbar.make(getView(), "KinoVibe: " + err, 5000).show();
                }*/
            }
        });
    }

    private void getHdvbData() {
        HDVB hdvb = new HDVB(getResources().getString(R.string.api_key_hdvb));
        hdvb.parse(film.getKinopoiskId(), new HDVB.ResultParseCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void serial(HDVBSerial serial) {
                if (serial == null) return;
                LayoutInflater inflater = getLayoutInflater();
                View layout_film_files = inflater.inflate(R.layout.layout_film_files, null, false);
                LinearLayout linearLayoutTitleFiles = layout_film_files.findViewById(R.id.linearLayoutTitleFiles);
                ImageView imageViewArrowFiles = layout_film_files.findViewById(R.id.imageViewArrowFiles);
                TextView textViewTitleBalancer = layout_film_files.findViewById(R.id.textViewTitleBalancer);
                StringBuilder blocks = new StringBuilder();
                for (int i = 0; i < serial.getBlockList().size(); i++) {
                    blocks.append(serial.getBlockList().get(i).getCountry());
                    if (i != serial.getBlockList().size() - 1) blocks.append(", ");
                }
                String title = serial.getBlockList().isEmpty() ? "[HDVB]" : "\uD83D\uDD12[HDVB] [" + blocks + "]";
                textViewTitleBalancer.setText(title);
                LinearLayout root = layout_film_files.findViewById(R.id.linearLayoutRootGroups);
                linearLayoutTitleFiles.setOnClickListener(new View.OnClickListener() {
                    boolean isAnimate = false;
                    @Override
                    public void onClick(View v) {
                        if (serial.getHdvbDataSerial().getSeasons().isEmpty()) {
                            Snackbar.make(binding.getRoot(), "Файлы отсутствуют", Snackbar.LENGTH_SHORT).show();
                        } else {
                            if (isAnimate) return;
                            if (root.getVisibility() == View.VISIBLE) {
                                imageViewArrowFiles.animate().setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        isAnimate = true;
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        isAnimate = false;
                                        root.setVisibility(View.GONE);
                                    }
                                }).rotation(0).start();
                            } else {
                                imageViewArrowFiles.animate().setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        isAnimate = true;
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        isAnimate = false;
                                        root.setVisibility(View.VISIBLE);
                                    }
                                }).rotation(90).start();
                            }
                        }
                    }
                });
                linearLayoutContent.addView(layout_film_files);

                HDVBSelector hdvbSelector = new HDVBSelector(root, serial);
                hdvbSelector.buildSelector(getActivity());
            }

            @Override
            public void film(HDVBFilm film) {
                if (film == null) return;
                LayoutInflater inflater = getLayoutInflater();
                View layout_film_files = inflater.inflate(R.layout.layout_film_files, null, false);
                LinearLayout linearLayoutTitleFiles = layout_film_files.findViewById(R.id.linearLayoutTitleFiles);
                ImageView imageViewArrowFiles = layout_film_files.findViewById(R.id.imageViewArrowFiles);
                TextView textViewTitleBalancer = layout_film_files.findViewById(R.id.textViewTitleBalancer);
                StringBuilder blocks = new StringBuilder();
                for (int i = 0; i < film.getBlockList().size(); i++) {
                    blocks.append(film.getBlockList().get(i).getCountry()).append(", ");
                    if (i != film.getBlockList().size() - 1) blocks.append(", ");
                }
                String title = film.getBlockList().isEmpty() ? "[HDVB]" : "\uD83D\uDD12[HDVB] [" + blocks + "]";
                textViewTitleBalancer.setText(title);
                LinearLayout root = layout_film_files.findViewById(R.id.linearLayoutRootGroups);
                linearLayoutTitleFiles.setOnClickListener(new View.OnClickListener() {
                    boolean isAnimate = false;

                    @Override
                    public void onClick(View v) {
                        if (film.getHdvbDataFilm().getTranslations().isEmpty()) {
                            Snackbar.make(binding.getRoot(), "Файлы отсутствуют", Snackbar.LENGTH_SHORT).show();
                        } else {
                            if (isAnimate) return;
                            if (root.getVisibility() == View.VISIBLE) {
                                imageViewArrowFiles.animate().setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        isAnimate = true;
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        isAnimate = false;
                                        root.setVisibility(View.GONE);
                                    }
                                }).rotation(0).start();
                            } else {
                                imageViewArrowFiles.animate().setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        isAnimate = true;
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        isAnimate = false;
                                        root.setVisibility(View.VISIBLE);
                                    }
                                }).rotation(90).start();
                            }
                        }
                    }
                });
                linearLayoutContent.addView(layout_film_files);

                HDVBSelector hdvbSelector = new HDVBSelector(root, film);
                hdvbSelector.buildSelector(getActivity());
            }

            @Override
            public void error(String err) {

            }
        });
    }

    private void getVKData(LayoutInflater inflater) {
        if (AccountManager.getAccount(getContext()) == null) return;
        vkVideo = new VKVideo(AccountManager.getAccessToken(getContext()));

        String nameFilm = !film.getNameRu().isEmpty() ? film.getNameRu() : film.getNameEn();
        nameFilm += film.getType().equals("TV_SERIES") ? " (Cериал " + film.getYear() + " " + film.getNameEn() + ")" : " (Фильм " + film.getYear() + " " + (film.getNameOriginal() == null ? film.getNameOriginal() : "") + ")";

        String finalNameFilm = nameFilm;
        vkVideo.searchVideos(nameFilm, 0, new VKVideo.SearchVideosCallback() {
            private VKVideo.SearchVideosCallback searchVideosCallback;

            @Override
            public void onSuccessSearch(ArrayList<VKVideo.VideoItem> videos) {
                if (getContext() == null) return;
                if (videos.isEmpty()) return;
                searchVideosCallback = this;
                View layoutFilmsFilesVk = inflater.inflate(R.layout.layout_films_files_vk, null, false);
                LinearLayout linearLayoutTitleFilesVk = layoutFilmsFilesVk.findViewById(R.id.linearLayoutTitleFilesVk);
                linearLayoutTitleFilesVk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (layoutFilmsFilesVk.findViewById(R.id.linearLayoutRootVk).getVisibility() == View.GONE) {
                            layoutFilmsFilesVk.findViewById(R.id.imageViewArrowFilesVk).animate().rotation(90).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    layoutFilmsFilesVk.findViewById(R.id.linearLayoutRootVk).setVisibility(View.VISIBLE);
                                    ViewPager2 viewP2 = layoutFilmsFilesVk.findViewById(R.id.viewP2);
                                    AdapterViewPager2VkFiles adapterViewPager2VkFiles = new AdapterViewPager2VkFiles(getChildFragmentManager(), getLifecycle(), videos);
                                    viewP2.setAdapter(adapterViewPager2VkFiles);
                                }
                            }).start();
                        } else {
                            layoutFilmsFilesVk.findViewById(R.id.imageViewArrowFilesVk).animate().rotation(0).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    layoutFilmsFilesVk.findViewById(R.id.linearLayoutRootVk).setVisibility(View.GONE);
                                }
                            }).start();
                        }


                    }
                });
                linearLayoutContent.addView(layoutFilmsFilesVk);


            }

            @Override
            public void onErrorSearch(Exception e) {
                if (getContext() == null) return;
                searchVideosCallback = this;
                Snackbar.make(binding.getRoot(), "Ошибка поиска в ВК. Повторите попытку...", 5000).setAction("Повтрить", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vkVideo.searchVideos(finalNameFilm, 0, searchVideosCallback);
                    }
                }).show();
            }
        });
    }

    private void getVibixData() {
        vibix = new Vibix(getResources().getString(R.string.api_key_vibix));
        vibix.parse(kinopoiskId, this);
    }

    private void getKinopoiskData() {
        kinopoiskId = film.getKinopoiskId();
        kinopoiskAPI = new KinopoiskAPI(getResources().getString(R.string.api_key_kinopoisk));

        if (filmInfo == null) {
            kinopoiskAPI.getInforamationItem(kinopoiskId, new KinopoiskAPI.RequestCallbackInformationItem() {
                KinopoiskAPI.RequestCallbackInformationItem requestCallbackInformationItem;

                @Override
                public void onSuccessInfoItem(ItemFilmInfo itemFilmInfo) {
                    requestCallbackInformationItem = this;
                    filmInfo = itemFilmInfo;
                    setFullData(filmInfo);
                }

                @Override
                public void onFailureInfoItem(IOException e) {
                    if (getContext() != null) {
                        requestCallbackInformationItem = this;
                        Snackbar.make(binding.getRoot(), "Ошибка загрузки информации о фильме. Повторите попытку...", 5000).setAction("Повторить", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                kinopoiskAPI.getInforamationItem(kinopoiskId, requestCallbackInformationItem);
                            }
                        }).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            setFullData(filmInfo);
        }

        if (listStaffItem == null) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    kinopoiskAPI.getListStaff(kinopoiskId, new KinopoiskAPI.RequestCallbackStaffList() {
                        KinopoiskAPI.RequestCallbackStaffList requestCallbackStaffList;

                        @Override
                        public void onSuccessStaffList(ArrayList<ListStaffItem> listStaffItems) {
                            requestCallbackStaffList = this;
                            listStaffItem = listStaffItems;
                        }

                        @Override
                        public void onFailureStaffList(IOException e) {
                            if (getContext() == null) return;
                            requestCallbackStaffList = this;
                            Snackbar.make(binding.getRoot(), "Ошибка загрузки списка актёров. Повторите попытку...", 5000).setAction("Повторить", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    kinopoiskAPI.getListStaff(kinopoiskId, requestCallbackStaffList);
                                }
                            }).show();
                        }

                        @Override
                        public void finishStafList() {
                            createActors();
                        }


                    });
                }
            }, 500);

        } else {
            createActors();
        }
    }

    private void createActors() {
        RecyclerView rvActors = binding.layoutActors.rvActors;
        FrameLayout frameLayoutRvActors = binding.layoutActors.frameLayoutRvActors;
        LinearLayout linearLayoutActorTitle = binding.layoutActors.linearLayoutActorTitle;
        ImageView imageViewArrowActors = binding.layoutActors.imageViewArrowActors;
        linearLayoutActorTitle.setOnClickListener(new View.OnClickListener() {
            private boolean isAnimate = false;

            @Override
            public void onClick(View v) {
                if (listStaffItem != null) {
                    if (!isAnimate) {
                        if (frameLayoutRvActors.getVisibility() == View.GONE) {
                            imageViewArrowActors.animate().setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    isAnimate = true;
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    isAnimate = false;
                                    frameLayoutRvActors.setVisibility(View.VISIBLE);
                                }
                            }).rotation(90).start();
                        } else {
                            imageViewArrowActors.animate().setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    isAnimate = true;
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    isAnimate = false;
                                    frameLayoutRvActors.setVisibility(View.GONE);
                                }
                            }).rotation(0).start();
                        }
                    }
                } else {
                    Snackbar.make(binding.getRoot(), "Актёры отсутствуют", 5000).show();
                }

            }
        });
        if (getContext() == null) return;
        rvActors.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.HORIZONTAL, false));
        rvActors.setAdapter(new PersonsRecyclerAdapter(PersonsRecyclerAdapter.TYPE_HOLDER_MAIN_PERSON, listStaffItem, getLayoutInflater()));
        rvActors.addOnItemTouchListener(new MyRecyclerViewItemTouchListener(getContext(), rvActors, new MyRecyclerViewItemTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                ActorFragment.newInstance(listStaffItem.get(position).getStaffId()).show(getChildFragmentManager(), "ActorFragment");
            }

            @Override
            public void onLongItemClick(RecyclerView.ViewHolder holder, View view, int position) {

            }
        }));
    }

    @SuppressLint("SetTextI18n")
    private void setFullData(ItemFilmInfo itemFilmInfo) {
        progressBar.setVisibility(View.GONE);
        kinopoiskId = itemFilmInfo.getKinopoiskId();
        String description = itemFilmInfo.getDescription();
        String myShortDescription = "";
        String name = itemFilmInfo.getNameRu();
        if (name.equals("null")) {
            name = itemFilmInfo.getNameEn();
            if (name.equals("null")) {
                name = itemFilmInfo.getNameOriginal();
            }
        }
        String year = itemFilmInfo.getYear();
        String posterCover = itemFilmInfo.getCoverUrl();
        if (posterCover.equals("null")) {
            posterCover = itemFilmInfo.getPosterUrl();
        }
        String logoUrl = itemFilmInfo.getLogoUrl();
        String slogan = itemFilmInfo.getSlogan();


        // IMAGE POSTER
        if (!logoUrl.equals("null")) {
            Picasso.get().load(logoUrl).into(imageViewLogoFilm);
        }
        if (!posterCover.equals("null")) {
            Picasso.get().load(posterCover).into(appBarImage);
        }


        // NAME
        if (year.equals("null")) {
            textViewNameFilm.setText(name);
        } else {
            textViewNameFilm.setText(name + " (" + year + ")");
        }

        // Ratings
        linearLayoutRatings.setVisibility(View.VISIBLE);
        textViewRatingKp.setText(itemFilmInfo.getRatingKinopoisk() + " КП");
        textViewVoteCountKp.setText(itemFilmInfo.getRatingKinopoiskVoteCount() + " оценок");
        StringBuilder countries = new StringBuilder();
        for (int i = 0; i < itemFilmInfo.getCountries().size(); i++) {
            countries.append(itemFilmInfo.getCountries().get(i).getCountry());
            if (i != itemFilmInfo.getCountries().size() - 1) countries.append(", ");
        }
        textViewCountries.setText(countries);


        // DESCRIPTION
        if (description.equals("null")) {
            description = "Описание отсутствует";
        } else {
            if (!slogan.equals("null")) {
                description = description + "\n\n\n" + slogan;
            }
        }
        if (description.length() > 200) {
            myShortDescription = description.substring(0, 200) + "...\t\tПродолжить";
        } else {
            myShortDescription = description;
        }
        textViewDescriptionFilm.setText(myShortDescription);
        String finalMyShortDescription = myShortDescription;
        String finalDescription = description;
        textViewDescriptionFilm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (textViewDescriptionFilm.getText().equals(finalMyShortDescription)) {
                    textViewDescriptionFilm.setText(finalDescription);
                } else {
                    textViewDescriptionFilm.setText(finalMyShortDescription);
                }
            }
        });


    }

    private ArrayList<ListStaffItem> listStaffItem;

    @Override
    public void startParseVibix() {

    }

    private static EPData.Film vibixFilm;
    private static EPData.Serial vibixSerial;


    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    public void finishParseFilmVibix(EPData.Film vibixFilm) {
        if (getContext() == null) return;
        FilmFragment.vibixFilm = vibixFilm;
        if (FilmFragment.vibixFilm == null) return;
        LayoutInflater inflater = getLayoutInflater();
        View layout_film_files = inflater.inflate(R.layout.layout_film_files, null, false);
        LinearLayout linearLayoutTitleFiles = layout_film_files.findViewById(R.id.linearLayoutTitleFiles);
        ImageView imageViewArrowFiles = layout_film_files.findViewById(R.id.imageViewArrowFiles);
        TextView textViewTitleBalancer = layout_film_files.findViewById(R.id.textViewTitleBalancer);
        textViewTitleBalancer.setText("[VIBIX]");
        LinearLayout root = layout_film_files.findViewById(R.id.linearLayoutRootGroups);

        linearLayoutTitleFiles.setOnClickListener(new View.OnClickListener() {
            boolean isAnimate = false;

            @Override
            public void onClick(View v) {
                if (FilmFragment.vibixFilm.getTranslations().isEmpty()) {
                    Snackbar.make(binding.getRoot(), "Файлы отсутствуют", Snackbar.LENGTH_SHORT).show();
                } else {
                    if (isAnimate) return;
                    if (root.getVisibility() == View.VISIBLE) {
                        imageViewArrowFiles.animate().setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                isAnimate = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                isAnimate = false;
                                root.setVisibility(View.GONE);
                            }
                        }).rotation(0).start();
                    } else {
                        imageViewArrowFiles.animate().setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                isAnimate = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                isAnimate = false;
                                root.setVisibility(View.VISIBLE);
                            }
                        }).rotation(90).start();
                    }
                }
            }
        });

        linearLayoutContent.addView(layout_film_files);

        VibixSelector vibixSelector = new VibixSelector(root, vibixFilm);
        vibixSelector.setSelectorListener(new VibixSelector.SelectorListener() {
            @Override
            public void onClick(int indexTranslation, int indexSeasons, int indexEpisode, int indexQuality) {

            }
        });
        vibixSelector.buildSelector(getActivity());
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public void finishParseSerialVibix(EPData.Serial vibixSerial) {
        if (getContext() == null) return;
        FilmFragment.vibixSerial = vibixSerial;
        if (FilmFragment.vibixSerial == null) return;
        LayoutInflater inflater = getLayoutInflater();
        View layout_film_files = inflater.inflate(R.layout.layout_film_files, null, false);
        LinearLayout linearLayoutTitleFiles = layout_film_files.findViewById(R.id.linearLayoutTitleFiles);
        ImageView imageViewArrowFiles = layout_film_files.findViewById(R.id.imageViewArrowFiles);
        TextView textViewTitleBalancer = layout_film_files.findViewById(R.id.textViewTitleBalancer);
        textViewTitleBalancer.setText("[VIBIX]");
        LinearLayout root = layout_film_files.findViewById(R.id.linearLayoutRootGroups);

        linearLayoutTitleFiles.setOnClickListener(new View.OnClickListener() {
            boolean isAnimate = false;

            @Override
            public void onClick(View v) {
                if (vibixSerial.getSeasons().isEmpty()) {
                    Snackbar.make(binding.getRoot(), "Файлы отсутствуют", Snackbar.LENGTH_SHORT).show();
                } else {
                    if (isAnimate) return;
                    if (root.getVisibility() == View.VISIBLE) {
                        imageViewArrowFiles.animate().setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                isAnimate = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                isAnimate = false;
                                root.setVisibility(View.GONE);
                            }
                        }).rotation(0).start();
                    } else {
                        imageViewArrowFiles.animate().setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                isAnimate = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                isAnimate = false;
                                root.setVisibility(View.VISIBLE);
                            }
                        }).rotation(90).start();
                    }
                }
            }
        });

        linearLayoutContent.addView(layout_film_files);

        VibixSelector vibixSelector = new VibixSelector(root, vibixSerial);
        vibixSelector.buildSelector(getActivity());

    }

    @SuppressLint({"MissingInflatedId", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void errorParseVibix(IOException e) {
        if (!e.getMessage().equals("Not Found")) {
            Snackbar.make(binding.getRoot(), "ERR:[VIBIX] | " + e.getMessage(), 5000).setAction("Повторить", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibix.parse(kinopoiskId, FilmFragment.this);
                }
            }).show();
        } else {
            if (getContext() != null) return;
            LayoutInflater inflater = getLayoutInflater();
            View layout_film_files = inflater.inflate(R.layout.layout_film_files, null, false);
            LinearLayout linearLayoutTitleFiles = layout_film_files.findViewById(R.id.linearLayoutTitleFiles);
            ImageView imageViewArrowFiles = layout_film_files.findViewById(R.id.imageViewArrowFiles);
            TextView textViewTitleBalancer = layout_film_files.findViewById(R.id.textViewTitleBalancer);
            textViewTitleBalancer.setText("[VIBIX]");
            LinearLayout root = layout_film_files.findViewById(R.id.linearLayoutRootGroups);
            final int[] countTapTitle = {0};
            linearLayoutTitleFiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ++countTapTitle[0];
                    if (countTapTitle[0] == 10) {
                        Snackbar.make(binding.getRoot(), "Нормально крутится?!\uD83D\uDE43", 3000).show();
                        return;
                    }
                    imageViewArrowFiles.animate().rotation(360).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            Snackbar.make(binding.getRoot(), "Файлы отсутствуют", Snackbar.LENGTH_SHORT).show();
                            imageViewArrowFiles.setRotation(0);
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                        }
                    }).setDuration(1200).start();

                }
            });
            linearLayoutContent.addView(layout_film_files);
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.popup_menu_film, menu);
        menu.removeItem(menu.getItem(0).getItemId());
    }


}