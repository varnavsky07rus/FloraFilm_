package com.alaka_ala.florafilm.ui.film;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.FragmentFilmBinding;
import com.alaka_ala.florafilm.sys.hdvb.HDVB;
import com.alaka_ala.florafilm.sys.kinovibe.KinoVibe;
import com.alaka_ala.florafilm.sys.kp_api.FilmTrailer;
import com.alaka_ala.florafilm.sys.kp_api.ItemFilmInfo;
import com.alaka_ala.florafilm.sys.kp_api.KinopoiskAPI;
import com.alaka_ala.florafilm.sys.kp_api.ListFilmItem;
import com.alaka_ala.florafilm.sys.kp_api.ListStaffItem;
import com.alaka_ala.florafilm.sys.utils.PersonsRecyclerAdapter;
import com.alaka_ala.florafilm.sys.vibix.Vibix;
import com.alaka_ala.florafilm.sys.vibix.VibixSelector;
import com.alaka_ala.florafilm.ui.player.exo.ExoActivity;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class FilmFragment extends Fragment implements KinopoiskAPI.RequestCallbackInformationItem, KinopoiskAPI.RequestCallbackStaffList, KinopoiskAPI.RequestCallbackListVieos, Vibix.ConnectionVibix {
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
        kinopoiskId = film.getKinopoiskId();
        kinopoiskAPI = new KinopoiskAPI(getResources().getString(R.string.api_key_kinopoisk));
        kinopoiskAPI.getInforamationItem(kinopoiskId, this);
        kinopoiskAPI.getListStaff(kinopoiskId, this);
        kinopoiskAPI.getListVieos(kinopoiskId, this);


        vibix = new Vibix(getResources().getString(R.string.api_key_vibix));
        vibix.parse(kinopoiskId, this);


        /*kinoVibe = new KinoVibe();
        kinoVibe.parse(kinopoiskId, new KinoVibe.ConnectionKinoVibe() {
            @Override
            public void startParse() {

            }

            @Override
            public void finishParse(String file) {
                LayoutInflater inflater = getLayoutInflater();
                View layout_film_files = inflater.inflate(R.layout.layout_film_files, null, false);
                LinearLayout linearLayoutTitleFiles = layout_film_files.findViewById(R.id.linearLayoutTitleFiles);
                ImageView imageViewArrowFiles = layout_film_files.findViewById(R.id.imageViewArrowFiles);
                TextView textViewTitleBalancer = layout_film_files.findViewById(R.id.textViewTitleBalancer);
                textViewTitleBalancer.setText("[KinoVibe]");
                LinearLayout root = layout_film_files.findViewById(R.id.linearLayoutRoot);

                linearLayoutTitleFiles.setOnClickListener(new View.OnClickListener() {
                    boolean isAnimate = false;

                    @Override
                    public void onClick(View v) {
                        if (!isAnimate) {
                            if (root.getChildCount() == 0) {
                                imageViewArrowFiles.animate().rotation(90).setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        isAnimate = false;
                                        View viewFile = inflater.inflate(R.layout.selector_film_item_2, null, false);
                                        TextView textViewTitleFiles = viewFile.findViewById(R.id.textViewTitleFiles);
                                        textViewTitleFiles.setText("Просмотр");
                                        root.addView(viewFile);
                                        root.setVisibility(View.VISIBLE);
                                    }
                                }).start();
                            } else {
                                imageViewArrowFiles.animate().rotation(0).setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        isAnimate = false;
                                        root.setVisibility(View.GONE);
                                        root.removeAllViews();
                                    }
                                }).start();
                            }
                        }
                    }
                });

                linearLayoutContent.addView(layout_film_files);
            }

            @Override
            public void errorParse(String err) {
                Snackbar.make(getView(), err, Snackbar.LENGTH_LONG).show();
            }
        });*/


        appBarImage = binding.appBarImage;
        imageViewLogoFilm = binding.imageViewLogoFilm;
        textViewNameFilm = binding.textViewNameFilm;
        textViewDescriptionFilm = binding.textViewDescriptionFilm;
        progressBar = binding.cardViewProgressBar;


        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    private void setFullData(ItemFilmInfo itemFilmInfo) {
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

    @Override
    public void onSuccessInfoItem(ItemFilmInfo itemFilmInfo) {
        filmInfo = itemFilmInfo;
        setFullData(filmInfo);
    }

    @Override
    public void onFailureInfoItem(IOException e) {
        if (getContext() != null) {
            Snackbar.make(binding.getRoot(), "Ошибка загрузки информации о фильме. Повторите попытку...", Snackbar.LENGTH_SHORT).setAction("Повторить", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    kinopoiskAPI.getInforamationItem(kinopoiskId, FilmFragment.this);
                }
            }).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void finishInfoItem() {
        progressBar.setVisibility(View.GONE);
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onSuccessVideo(ArrayList<FilmTrailer> filmTrailers) {

    }

    @Override
    public void onFailureVideo(IOException e) {

    }

    @Override
    public void finishVideo() {

    }


    private static ArrayList<ListStaffItem> listStaffItem;

    @Override
    public void onSuccessStaffList(ArrayList<ListStaffItem> listStaffItem) {
        FilmFragment.listStaffItem = listStaffItem;
    }

    @Override
    public void onFailureStaffList(IOException e) {
        Snackbar.make(binding.getRoot(), "Ошибка загрузки списка актёров. Повторите попытку...", Snackbar.LENGTH_SHORT).setAction("Повторить", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kinopoiskAPI.getListStaff(kinopoiskId, FilmFragment.this);
            }
        }).show();
    }

    @Override
    public void finishStafList() {
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
                    Snackbar.make(binding.getRoot(), "Актёры отсутствуют", Snackbar.LENGTH_SHORT).show();
                }

            }
        });
        rvActors.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.HORIZONTAL, false));
        if (getContext() == null) return;
        rvActors.setAdapter(new PersonsRecyclerAdapter(PersonsRecyclerAdapter.TYPE_HOLDER_MAIN_PERSON, listStaffItem, getLayoutInflater()));
    }


    @Override
    public void startParse() {

    }

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    public void finishParseFilm(Vibix.VibixFilm vibixFilm) {
        if (vibixFilm == null) return;
        if (getContext() == null) return;

        LayoutInflater inflater = getLayoutInflater();
        View layout_film_files = inflater.inflate(R.layout.layout_film_files, null, false);
        LinearLayout linearLayoutTitleFiles = layout_film_files.findViewById(R.id.linearLayoutTitleFiles);
        ImageView imageViewArrowFiles = layout_film_files.findViewById(R.id.imageViewArrowFiles);
        TextView textViewTitleBalancer = layout_film_files.findViewById(R.id.textViewTitleBalancer);
        textViewTitleBalancer.setText("[VIBIX]");
        LinearLayout root = layout_film_files.findViewById(R.id.linearLayoutRoot);

        linearLayoutTitleFiles.setOnClickListener(new View.OnClickListener() {
            boolean isAnimate = false;

            @Override
            public void onClick(View v) {
                if (vibixFilm.getTranslations().isEmpty()) {
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
    public void finishParseSerial(Vibix.VibixSerial vibixSerial) {
        if (getContext() == null) return;
        LayoutInflater inflater = getLayoutInflater();
        View layout_film_files = inflater.inflate(R.layout.layout_film_files, null, false);
        LinearLayout linearLayoutTitleFiles = layout_film_files.findViewById(R.id.linearLayoutTitleFiles);
        ImageView imageViewArrowFiles = layout_film_files.findViewById(R.id.imageViewArrowFiles);
        TextView textViewTitleBalancer = layout_film_files.findViewById(R.id.textViewTitleBalancer);
        textViewTitleBalancer.setText("[VIBIX]");
        LinearLayout root = layout_film_files.findViewById(R.id.linearLayoutRoot);

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
    public void errorParse(IOException e) {
        if (!e.getMessage().equals("Not Found")) {
            Snackbar.make(binding.getRoot(), "ERR:[VIBIX] | " + e.getMessage(), Snackbar.LENGTH_SHORT).setAction("Повторить", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibix.parse(kinopoiskId, FilmFragment.this);
                }
            }).show();
        } else {
            if (getContext() == null) return;
            LayoutInflater inflater = getLayoutInflater();
            View layout_film_files = inflater.inflate(R.layout.layout_film_files, null, false);
            LinearLayout linearLayoutTitleFiles = layout_film_files.findViewById(R.id.linearLayoutTitleFiles);
            ImageView imageViewArrowFiles = layout_film_files.findViewById(R.id.imageViewArrowFiles);
            TextView textViewTitleBalancer = layout_film_files.findViewById(R.id.textViewTitleBalancer);
            textViewTitleBalancer.setText("[VIBIX]");
            LinearLayout root = layout_film_files.findViewById(R.id.linearLayoutRoot);
            final int[] countTapTitle = {0};
            linearLayoutTitleFiles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ++countTapTitle[0];
                    if (countTapTitle[0] == 10) {
                        Snackbar.make(binding.getRoot(), "Нормально крутится?!\uD83D\uDE43", Snackbar.LENGTH_SHORT).show();
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