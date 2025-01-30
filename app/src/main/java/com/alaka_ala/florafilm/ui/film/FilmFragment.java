package com.alaka_ala.florafilm.ui.film;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.FragmentFilmBinding;
import com.alaka_ala.florafilm.sys.kinovibe.KinoVibe;
import com.alaka_ala.florafilm.sys.kp_api.ItemFilmInfo;
import com.alaka_ala.florafilm.sys.kp_api.KinopoiskAPI;
import com.alaka_ala.florafilm.sys.kp_api.ListFilmItem;
import com.alaka_ala.florafilm.sys.kp_api.ListStaffItem;
import com.alaka_ala.florafilm.sys.utils.PersonsRecyclerAdapter;
import com.alaka_ala.florafilm.sys.vibix.Vibix;
import com.alaka_ala.florafilm.sys.vibix.VibixSelector;
import com.alaka_ala.florafilm.ui.film.vk_pager.AdapterViewPager2VkFiles;
import com.alaka_ala.florafilm.ui.player.exo.models.EPData;
import com.alaka_ala.florafilm.ui.vk.AccountManager;
import com.alaka_ala.florafilm.ui.vk.parser.VKVideo;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;


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
    private CardView progressBar;
    private Vibix vibix;
    private KinoVibe kinoVibe;
    private VKVideo vkVideo;
    private LinearLayout linearLayoutContent;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFilmBinding.inflate(inflater, container, false);
        // Устанавливаем флаги для отображения поверх строки состояния
        /*getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/
        linearLayoutContent = binding.linearLayoutContent;


        bundle = getArguments();
        if (bundle == null) return binding.getRoot();
        film = (ListFilmItem) bundle.getSerializable("film");
        if (film == null) return binding.getRoot();

        appBarImage = binding.appBarImage;
        imageViewLogoFilm = binding.imageViewLogoFilm;
        textViewNameFilm = binding.textViewNameFilm;
        textViewDescriptionFilm = binding.textViewDescriptionFilm;
        progressBar = binding.cardViewProgressBar;

        getKinopoiskData();

        getVibixData();

        getVKData(inflater);





        return binding.getRoot();
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
        rvActors.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.HORIZONTAL, false));
        if (getContext() == null) return;
        rvActors.setAdapter(new PersonsRecyclerAdapter(PersonsRecyclerAdapter.TYPE_HOLDER_MAIN_PERSON, listStaffItem, getLayoutInflater()));
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
        TYPE_CONTENT = itemFilmInfo.getType();

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


}