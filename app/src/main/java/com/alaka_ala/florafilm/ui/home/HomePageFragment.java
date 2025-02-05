package com.alaka_ala.florafilm.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.FragmentHomePageBinding;
import com.alaka_ala.florafilm.sys.AsyncThreadBuilder;
import com.alaka_ala.florafilm.sys.kp_api.Collection;
import com.alaka_ala.florafilm.sys.kp_api.KinopoiskAPI;
import com.alaka_ala.florafilm.sys.kp_api.NewsMedia;
import com.alaka_ala.florafilm.sys.utils.MyRecyclerViewItemTouchListener;
import com.alaka_ala.florafilm.sys.utils.SettingsApp;
import com.alaka_ala.florafilm.sys.utils.UtilsFavoriteAndViewFilm;
import com.alaka_ala.florafilm.ui.home.viewPager.AdapterNewsMediaViewPager2;
import com.alaka_ala.florafilm.sys.utils.UniversalRecyclerAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class HomePageFragment extends Fragment implements KinopoiskAPI.RequesCallbackNewsMedia, SwipeRefreshLayout.OnRefreshListener {
    private FragmentHomePageBinding binding;


    private KinopoiskAPI kinopoiskAPI;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UtilsFavoriteAndViewFilm utils;
    private SettingsApp settingsApp;


    private RecyclerView rvPopularAll;
    private RecyclerView rvTopTvShow250;
    private RecyclerView rvTopMovie250;
    private RecyclerView rvComics;
    private RecyclerView rvPopularOnlyMovie;
    private ViewPager2 viewPager2;


    private AdapterNewsMediaViewPager2 adapterNewsMediaViewPager2;
    private static UniversalRecyclerAdapter adapterPopularAll;
    private static UniversalRecyclerAdapter adapterTvShow250;
    private static UniversalRecyclerAdapter adapterMovie250;
    private static UniversalRecyclerAdapter adapterComics;
    private static UniversalRecyclerAdapter adapterPopularOnlyMovies;

    //private MyRecyclerViewItemTouchListener onItemClickListenerPopularAll;
    private MyRecyclerViewItemTouchListener onItemClickListenerTvShow250;
    //private MyRecyclerViewItemTouchListener onItemClickListenerMovie250;
    //private MyRecyclerViewItemTouchListener onItemClickListenerComics;
    private MyRecyclerViewItemTouchListener onItemClickListenerPopularOnlyMovies;



    private static final Map<String, Collection> collections = new HashMap<>();
    private static ArrayList<NewsMedia> newsMediaList = new ArrayList<>();
    private NestedScrollView nstdScrollView;


    private static int page = 1;
    private static final long timeoutRefreshSec = 120000; // 2 мин интервал обновления
    private static long lastRefresTs = 0;                // TimeStamp Последнего обновления
    private int positionNestedScrollView = 0;
    private int positionItemViewPager = 0;
    private boolean isAnimationInterface;


    private LinearLayout linearLayoutTopTvShow;   // Топ 250 сериалов
    private LinearLayout linearLayoutNewOFilm;   // Популярные фильмы
    private LinearLayout linearLayoutTopMovie;   // Топ 250 фильмов
    private LinearLayout linearLayoutNewFilm; // Топ 250 новинок фильмов
    private LinearLayout linearLayoutTopMovieComics;   // По комиксам

    private Chip genre19; // Семейные
    private Chip genre18; // Мультфильмы
    private Chip genre17; // Ужасы
    private Chip genre14; // Военные
    private Chip genre13; // Комедии
    private Chip genre12; // Фэнтези
    private Chip genre11; // Боевик
    private Chip genre10; // Вестерн
    private Chip genre7; // Приключения
    private Chip genre24; // Аниме

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            positionNestedScrollView = savedInstanceState.getInt("posNestedScroll", 0);
            positionItemViewPager = savedInstanceState.getInt("posViewPager", 0);
            lastRefresTs = savedInstanceState.getLong("lastRefresTs", 0);
        }
        binding = FragmentHomePageBinding.inflate(inflater, container, false);
        utils = new UtilsFavoriteAndViewFilm(getContext());
        settingsApp = new SettingsApp(getContext());
        kinopoiskAPI = new KinopoiskAPI(getResources().getString(R.string.api_key_kinopoisk));
        utils.setMethodListener(new UtilsFavoriteAndViewFilm.MethodListener() {
            @Override
            public void onAddToViewed(int positionRecyclerItem) {
                UtilsFavoriteAndViewFilm.MethodListener.super.onAddToViewed(positionRecyclerItem);
                Snackbar.make(binding.getRoot(), "Фильм добавлен в просмотренные", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToFavorite(int positionRecyclerItem) {
                UtilsFavoriteAndViewFilm.MethodListener.super.onAddToFavorite(positionRecyclerItem);
                Snackbar.make(binding.getRoot(), "Фильм добавлен в избранное", Snackbar.LENGTH_SHORT).show();

            }

            @Override
            public void onRemoveFromFavorite(int positionRecyclerItem) {
                UtilsFavoriteAndViewFilm.MethodListener.super.onRemoveFromFavorite(positionRecyclerItem);
                Snackbar.make(binding.getRoot(), "Фильм удален из избранных", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onRemoveFromViewed(int positionRecyclerItem) {
                UtilsFavoriteAndViewFilm.MethodListener.super.onRemoveFromViewed(positionRecyclerItem);
                Snackbar.make(binding.getRoot(), "Фильм удален из просмотренных", Snackbar.LENGTH_SHORT).show();
            }
        });
        isAnimationInterface = settingsApp.getParam(SettingsApp.SettingsKeys.INTERFACE_ANIMATION, SettingsApp.SettingsDefsVal.DEF_INTERFACE_ANIMATION);

        rvPopularAll = binding.layoutNewFilm.rvHomeNewFilmPopularAll;
        rvTopTvShow250 = binding.layoutRvTopTvShow.rvTopTvShow;
        viewPager2 = binding.layoutVpNewsMedia.viewPager2;
        rvTopMovie250 = binding.layoutRvTopMovie.rvTopMovie;
        rvComics = binding.layoutRvMovieComics.rvTopMovieComics;
        rvPopularOnlyMovie = binding.layoutRvPopularMovie.rvHomeNewFilmPopularOFilm;


        linearLayoutNewFilm = binding.layoutNewFilm.linearLayoutNewFilm;
        linearLayoutTopTvShow = binding.layoutRvTopTvShow.linearLayoutTopTvShow;
        linearLayoutTopMovie = binding.layoutRvTopMovie.linearLayoutTopMovie;
        linearLayoutTopMovieComics = binding.layoutRvMovieComics.linearLayoutTopMovieComics;
        linearLayoutNewOFilm = binding.layoutRvPopularMovie.linearLayoutNewOFilm;

        AsyncThreadBuilder a = new AsyncThreadBuilder() {
            @Override
            public Runnable start(Handler finishHandler) {
                genre19 = binding.layoutRvCcategoryChips.genre19;
                genre18 = binding.layoutRvCcategoryChips.genre18;
                genre17 = binding.layoutRvCcategoryChips.genre17;
                genre14 = binding.layoutRvCcategoryChips.genre14;
                genre13 = binding.layoutRvCcategoryChips.genre13;
                genre12 = binding.layoutRvCcategoryChips.genre12;
                genre11 = binding.layoutRvCcategoryChips.genre11;
                genre10 = binding.layoutRvCcategoryChips.genre10;
                genre7 = binding.layoutRvCcategoryChips.genre7;
                genre24 = binding.layoutRvCcategoryChips.genre24;
                return null;
            }

            @Override
            public void finishHandler(Bundle bundle) {

            }
        };
        a.onStart();


        nstdScrollView = binding.nstdScrollView;

        swipeRefreshLayout = binding.getRoot();



        onClickChipListener();
        onClickTextViewListenersMore();

        swipeRefreshLayout.setOnRefreshListener(this);

        rvPopularAll.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvTopTvShow250.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvTopMovie250.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvComics.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.HORIZONTAL, false));
        rvPopularOnlyMovie.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false));

        nstdScrollView.setScrollY(positionNestedScrollView);


        AsyncThreadBuilder threadBuilder;
        if (collections.isEmpty()) {
            threadBuilder = new AsyncThreadBuilder() {
                @Override
                public Runnable start(Handler finishHandler) {
                    loadDataAndCreateAdapters();
                    return null;
                }

                @Override
                public void finishHandler(Bundle bundle) {

                }
            };

        } else {
            threadBuilder = new AsyncThreadBuilder() {
                @Override
                public Runnable start(Handler finishHandler) {
                    setAdapters();
                    return null;
                }

                @Override
                public void finishHandler(Bundle bundle) {

                }
            };
        }
        threadBuilder.onStart();


        createRecyclerTouchListeners();

        return binding.getRoot();
    }

    private void createRecyclerTouchListeners() {

        /*rvPopularAll.removeOnItemTouchListener(onItemClickListenerPopularAll);
        rvPopularAll.addOnItemTouchListener(onItemClickListenerPopularAll = new MyRecyclerViewItemTouchListener(getContext(), rvPopularAll, new MyRecyclerViewItemTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("film", HomePageFragment.collections.get(Collection.TITLE_POPULAR_ALL).getItems().get(position));
                Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_filmFragment, bundle);
            }

            @Override
            public void onLongItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                showPopUpMenu(holder, view, position);
            }
        }));*/

        rvTopTvShow250.removeOnItemTouchListener(onItemClickListenerTvShow250);
        rvTopTvShow250.addOnItemTouchListener(onItemClickListenerTvShow250 = new MyRecyclerViewItemTouchListener(getContext(), rvTopTvShow250, new MyRecyclerViewItemTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("film", HomePageFragment.collections.get(Collection.TITLE_TOP_250_TV_SHOWS).getItems().get(position));
                Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_filmFragment, bundle);
            }

            @Override
            public void onLongItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                showPopUpMenu(holder, view, position);
            }
        }));

        /*rvTopMovie250.removeOnItemTouchListener(onItemClickListenerMovie250);
        rvTopMovie250.addOnItemTouchListener(onItemClickListenerMovie250 = new MyRecyclerViewItemTouchListener(getContext(), rvTopMovie250, new MyRecyclerViewItemTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("film", HomePageFragment.collections.get(Collection.TITLE_TOP_250_MOVIES).getItems().get(position));
                Navigation.findNavController(view).navigate(R.id.filmFragment, bundle);
            }

            @Override
            public void onLongItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                showPopUpMenu(holder, view, position);
            }
        }));*/

        rvPopularOnlyMovie.removeOnItemTouchListener(onItemClickListenerPopularOnlyMovies);
        rvPopularOnlyMovie.addOnItemTouchListener(onItemClickListenerPopularOnlyMovies = new MyRecyclerViewItemTouchListener(getContext(), rvPopularOnlyMovie, new MyRecyclerViewItemTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("film", HomePageFragment.collections.get(Collection.TITLE_POPULAR_MOVIES).getItems().get(position));
                Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_filmFragment, bundle);
            }

            @Override
            public void onLongItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                showPopUpMenu(holder, view, position);
            }
        }));

        /*rvComics.removeOnItemTouchListener(onItemClickListenerComics);
        rvComics.addOnItemTouchListener(onItemClickListenerComics = new MyRecyclerViewItemTouchListener(getContext(), rvComics, new MyRecyclerViewItemTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("film", HomePageFragment.collections.get(Collection.TITLE_COMICS_THEME).getItems().get(position));
                Navigation.findNavController(view).navigate(R.id.filmFragment, bundle);
            }

            @Override
            public void onLongItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                showPopUpMenu(holder, view, position);
            }
        }));*/

    }

    private void setAdapters() {

        /*rvPopularAll.setAdapter(adapterPopularAll);
        rvPopularAll.addOnItemTouchListener(onItemClickListenerPopularAll);*/


        rvTopTvShow250.setAdapter(adapterTvShow250);
        rvTopTvShow250.addOnItemTouchListener(onItemClickListenerTvShow250);


        /*rvTopMovie250.setAdapter(adapterMovie250);
        rvTopMovie250.addOnItemTouchListener(onItemClickListenerMovie250);*/


        rvPopularOnlyMovie.setAdapter(adapterPopularOnlyMovies);
        rvPopularOnlyMovie.addOnItemTouchListener(onItemClickListenerPopularOnlyMovies);


        /*rvComics.setAdapter(adapterComics);
        rvComics.addOnItemTouchListener(onItemClickListenerComics);*/


    }

    private void loadDataAndCreateAdapters() {

        /*if (!collections.containsKey(Collection.TITLE_POPULAR_ALL)) {
            kinopoiskAPI.getListTopPopularAll(page, new KinopoiskAPI.RequestCallbackCollection() {
                @Override
                public void onSuccess(Collection collection) {
                    collections.put(collection.getTitleCollection(), collection);
                    rvPopularAll.setAdapter(adapterPopularAll = new UniversalRecyclerAdapter(getLayoutInflater(), utils, collection, UniversalRecyclerAdapter.TYPE_HOLDER_BIG_POSTER));
                }

                @Override
                public void onFailure(IOException e) {

                }

                @Override
                public void finish() {

                }
            });
        } else {
            rvPopularAll.setAdapter(adapterPopularAll);
            rvPopularAll.addOnItemTouchListener(onItemClickListenerPopularAll);
        }*/

        if (settingsApp.getParam(SettingsApp.SettingsKeys.BLOCK_NEWS_MEDIA, SettingsApp.SettingsDefsVal.VISIBLE_BLOCK_NEWS_MEDIA)) {
            binding.layoutVpNewsMedia.getRoot().setVisibility(View.VISIBLE);
            if (!collections.containsKey(Collection.TITLE_NEWS_MEDIA)) {
                kinopoiskAPI.getListNewsMedia(page, this);
            }
        }

        if (!collections.containsKey(Collection.TITLE_TOP_250_TV_SHOWS)) {
            kinopoiskAPI.getListTop250TVShows(page, new KinopoiskAPI.RequestCallbackCollection() {
                @Override
                public void onSuccess(Collection collection) {
                    collections.put(collection.getTitleCollection(), collection);
                    rvTopTvShow250.setAdapter(adapterTvShow250 = new UniversalRecyclerAdapter(getLayoutInflater(), utils, collection, UniversalRecyclerAdapter.TYPE_HOLDER_SMALL_POSTER));
                }

                @Override
                public void onFailure(IOException e) {

                }

                @Override
                public void finish() {

                }
            });
        } else {
            rvTopTvShow250.setAdapter(adapterTvShow250);
            rvTopTvShow250.addOnItemTouchListener(onItemClickListenerTvShow250);

        }

        /*if (!collections.containsKey(Collection.TITLE_TOP_250_MOVIES)) {
            kinopoiskAPI.getListTop250Movies(page, new KinopoiskAPI.RequestCallbackCollection() {
                @Override
                public void onSuccess(Collection collection) {
                    collections.put(collection.getTitleCollection(), collection);
                    rvTopMovie250.setAdapter(adapterMovie250 = new UniversalRecyclerAdapter(getLayoutInflater(), utils, collection, UniversalRecyclerAdapter.TYPE_HOLDER_SMALL_POSTER));
                }

                @Override
                public void onFailure(IOException e) {

                }

                @Override
                public void finish() {

                }
            });
        } else {
            rvTopMovie250.setAdapter(adapterMovie250);
            rvTopMovie250.addOnItemTouchListener(onItemClickListenerMovie250);

        }*/

        /*if (!collections.containsKey(Collection.TITLE_COMICS_THEME)) {
            kinopoiskAPI.getListComicsTheme(page, new KinopoiskAPI.RequestCallbackCollection() {
                @Override
                public void onSuccess(Collection collection) {
                    collections.put(collection.getTitleCollection(), collection);
                    rvComics.setAdapter(adapterComics = new UniversalRecyclerAdapter(getLayoutInflater(), utils, collection, UniversalRecyclerAdapter.TYPE_HOLDER_SMALL_POSTER));
                }

                @Override
                public void onFailure(IOException e) {

                }

                @Override
                public void finish() {

                }
            });
        } else {
            rvComics.setAdapter(adapterComics);
            rvComics.addOnItemTouchListener(onItemClickListenerComics);
        }*/

        if (!collections.containsKey(Collection.TITLE_POPULAR_MOVIES)) {
            kinopoiskAPI.getListTopPopularMovies(page, new KinopoiskAPI.RequestCallbackCollection() {
                @Override
                public void onSuccess(Collection collection) {
                    collections.put(collection.getTitleCollection(), collection);
                    rvPopularOnlyMovie.setAdapter(adapterPopularOnlyMovies = new UniversalRecyclerAdapter(getLayoutInflater(), utils, collection, UniversalRecyclerAdapter.TYPE_FULL_WIDTH));
                }

                @Override
                public void onFailure(IOException e) {

                }

                @Override
                public void finish() {

                }
            });

        } else {
            rvPopularOnlyMovie.setAdapter(adapterPopularOnlyMovies);
            rvPopularOnlyMovie.addOnItemTouchListener(onItemClickListenerPopularOnlyMovies);

        }


    }

    private void onClickChipListener() {
        genre19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", Collection.TITLE_FAMILY);
                Navigation.findNavController(v).navigate(R.id.categoryListFilmFragment, bundle);
            }
        });

        genre18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", Collection.TITLE_CATEGORY_GENRE_18);
                Navigation.findNavController(v).navigate(R.id.categoryListFilmFragment, bundle);
            }
        });

        genre17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", Collection.TITLE_CATEGORY_GENRE_17);
                Navigation.findNavController(v).navigate(R.id.categoryListFilmFragment, bundle);
            }
        });

        genre14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", Collection.TITLE_CATEGORY_GENRE_14);
                Navigation.findNavController(v).navigate(R.id.categoryListFilmFragment, bundle);
            }
        });

        genre13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", Collection.TITLE_CATEGORY_GENRE_13);
                Navigation.findNavController(v).navigate(R.id.categoryListFilmFragment, bundle);
            }
        });

        genre12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", Collection.TITLE_CATEGORY_GENRE_12);
                Navigation.findNavController(v).navigate(R.id.categoryListFilmFragment, bundle);
            }
        });

        genre11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", Collection.TITLE_CATEGORY_GENRE_11);
                Navigation.findNavController(v).navigate(R.id.categoryListFilmFragment, bundle);
            }
        });

        genre10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", Collection.TITLE_CATEGORY_GENRE_10);
                Navigation.findNavController(v).navigate(R.id.categoryListFilmFragment, bundle);
            }
        });

        genre7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", Collection.TITLE_CATEGORY_GENRE_7);
                Navigation.findNavController(v).navigate(R.id.categoryListFilmFragment, bundle);
            }
        });

        genre24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", Collection.TITLE_CATEGORY_GENRE_24);
                Navigation.findNavController(v).navigate(R.id.categoryListFilmFragment, bundle);
            }
        });
    }

    private void onClickTextViewListenersMore() {
        // Новинки Фильмы и сериалы
        linearLayoutNewFilm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", Collection.TITLE_POPULAR_ALL);
                Navigation.findNavController(v).navigate(R.id.categoryListFilmFragment, bundle);
            }
        });

        // Топ 250 сериалов
        linearLayoutTopTvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", Collection.TITLE_TOP_250_TV_SHOWS);
                Navigation.findNavController(v).navigate(R.id.categoryListFilmFragment, bundle);
            }
        });

        // Топ 250 фильмов
        linearLayoutTopMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", Collection.TITLE_TOP_250_MOVIES);
                Navigation.findNavController(v).navigate(R.id.categoryListFilmFragment, bundle);
            }
        });

        // По комиксам
        linearLayoutTopMovieComics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", Collection.TITLE_COMICS_THEME);
                Navigation.findNavController(v).navigate(R.id.categoryListFilmFragment, bundle);
            }
        });

        // Популярные фильмы
        linearLayoutNewOFilm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("category", Collection.TITLE_POPULAR_MOVIES);
                Navigation.findNavController(v).navigate(R.id.categoryListFilmFragment, bundle);
            }
        });


    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSuccessNews(ArrayList<NewsMedia> newsMediaList) {
        if (!newsMediaList.isEmpty()) {
            HomePageFragment.newsMediaList = newsMediaList;
        }
        if (HomePageFragment.newsMediaList.isEmpty()) return;
        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        if (isAnimationInterface) {
            /*AdapterNewsMediaViewPager2.ZoomOutPageTransformer zoom = new AdapterNewsMediaViewPager2.ZoomOutPageTransformer();
            viewPager2.setPageTransformer(zoom);*/

            AdapterNewsMediaViewPager2.ZoomOutPageTransformer customPageTransformer = new AdapterNewsMediaViewPager2.ZoomOutPageTransformer();
            viewPager2.setPageTransformer(customPageTransformer);

        }

        adapterNewsMediaViewPager2 = new AdapterNewsMediaViewPager2(getChildFragmentManager(), getLifecycle(), newsMediaList);
        viewPager2.setAdapter(adapterNewsMediaViewPager2);

    }

    @Override
    public void onFailureNews(IOException e) {
        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void finishNews() {
        if (adapterNewsMediaViewPager2 == null) return;
        adapterNewsMediaViewPager2.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        onSuccessNews(newsMediaList);
        nstdScrollView.setScrollY(positionNestedScrollView);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        return;
        /*long currentTime = System.currentTimeMillis();
        if ((currentTime - lastRefresTs) > timeoutRefreshSec) {
            page = 0;
            lastRefresTs = currentTime;

            // Обновление данных
            loadDataAndCreateAdapters();

            Snackbar.make(binding.getRoot(), "Данные обновлены", Snackbar.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        } else {
            Snackbar.make(binding.getRoot(), "Обновление не требуется. Осталось " + (timeoutRefreshSec - (currentTime - lastRefresTs)) / 1000 + " сек.", Snackbar.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }*/
    }

    private void showPopUpMenu(RecyclerView.ViewHolder holder, View view, int position) {
        boolean isFavorite = utils.isFilmInFavorite(String.valueOf(view.getId()));
        boolean isViewed = utils.isFilmInViewed(String.valueOf(view.getId()));

        PopupMenu popupMenu = new PopupMenu(getContext(), holder.itemView);
        popupMenu.inflate(R.menu.popup_menu_film);
        popupMenu.setForceShowIcon(true);
        if (isFavorite) {
            popupMenu.getMenu().findItem(R.id.add_to_favorite).setVisible(false);
        }
        else {
            popupMenu.getMenu().findItem(R.id.remove_is_forever).setVisible(false);
        }
        if (isViewed) {
            popupMenu.getMenu().findItem(R.id.add_to_view).setVisible(false);
        }
        else {
            popupMenu.getMenu().findItem(R.id.remove_is_view).setVisible(false);
        }

        if (holder instanceof UniversalRecyclerAdapter.ViewHolderBigPoster) {
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.add_to_favorite) {
                        // Добавить в избранное
                        utils.addToFavorite(String.valueOf(view.getId()), collections.get(view.getContentDescription().toString()).getItems().get(position), position);
                        ((UniversalRecyclerAdapter.ViewHolderBigPoster) holder).getImageViewSaveToFavoriteBig().setVisibility(View.VISIBLE);
                        return true;
                    } else if (item.getItemId() == R.id.remove_is_forever) {
                        // Удалить из избранного
                        utils.removeFromFavorite(String.valueOf(view.getId()), position);
                        ((UniversalRecyclerAdapter.ViewHolderBigPoster) holder).getImageViewSaveToFavoriteBig().setVisibility(View.GONE);
                        return true;
                    } else if (item.getItemId() == R.id.add_to_view) {
                        // Добавить в просмотренные
                        utils.addToViewed(String.valueOf(view.getId()), collections.get(view.getContentDescription().toString()).getItems().get(position), position);
                        ((UniversalRecyclerAdapter.ViewHolderBigPoster) holder).getImageViewIsViewedBig().setVisibility(View.VISIBLE);
                        return true;
                    } else if (item.getItemId() == R.id.remove_is_view) {
                        // Удалить из просмотренных
                        utils.removeFromViewed(String.valueOf(view.getId()), position);
                        ((UniversalRecyclerAdapter.ViewHolderBigPoster) holder).getImageViewIsViewedBig().setVisibility(View.GONE);
                        return true;
                    } else if (item.getItemId() == R.id.search_similar) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("kinopoiskId", collections.get(view.getContentDescription().toString()).getItems().get(position).getKinopoiskId());
                        bundle.putString("category", Collection.TITLE_SIMILAR);

                        NavOptions.Builder builder = new NavOptions.Builder();
                        builder.setLaunchSingleTop(false);
                        builder.setPopUpTo(R.id.homePageFragment, false);
                        Navigation.findNavController(view).navigate(R.id.categoryListFilmFragment, bundle, builder.build());
                        return true;
                    } else if (item.getItemId() == R.id.open_film) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("film", collections.get(view.getContentDescription().toString()).getItems().get(position));
                        Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_filmFragment, bundle);
                    }
                    return false;
                }
            });
        }

        else if (holder instanceof UniversalRecyclerAdapter.ViewHolderSmallPoster) {
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.add_to_favorite) {
                        // Добавить в избранное
                        utils.addToFavorite(String.valueOf(view.getId()), collections.get(view.getContentDescription().toString()).getItems().get(position), position);
                        ((UniversalRecyclerAdapter.ViewHolderSmallPoster) holder).getImageViewSaveToFavoriteSmall().setVisibility(View.VISIBLE);
                        return true;
                    } else if (item.getItemId() == R.id.remove_is_forever) {
                        // Удалить из избранного
                        utils.removeFromFavorite(String.valueOf(view.getId()), position);
                        ((UniversalRecyclerAdapter.ViewHolderSmallPoster) holder).getImageViewSaveToFavoriteSmall().setVisibility(View.GONE);
                        return true;
                    } else if (item.getItemId() == R.id.add_to_view) {
                        // Добавить в просмотренные
                        utils.addToViewed(String.valueOf(view.getId()), collections.get(view.getContentDescription().toString()).getItems().get(position), position);
                        ((UniversalRecyclerAdapter.ViewHolderSmallPoster) holder).getImageViewIsViewedSmall().setVisibility(View.VISIBLE);
                        return true;
                    } else if (item.getItemId() == R.id.remove_is_view) {
                        // Удалить из просмотренных
                        utils.removeFromViewed(String.valueOf(view.getId()), position);
                        ((UniversalRecyclerAdapter.ViewHolderSmallPoster) holder).getImageViewIsViewedSmall().setVisibility(View.GONE);
                        return true;
                    } else if (item.getItemId() == R.id.search_similar) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("kinopoiskId", collections.get(view.getContentDescription().toString()).getItems().get(position).getKinopoiskId());
                        bundle.putString("category", Collection.TITLE_SIMILAR);
                        NavOptions.Builder builder = new NavOptions.Builder();
                        builder.setLaunchSingleTop(false);
                        builder.setPopUpTo(R.id.homePageFragment, false);
                        Navigation.findNavController(view).navigate(R.id.categoryListFilmFragment, bundle, builder.build());
                        return true;
                    } else if (item.getItemId() == R.id.open_film) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("film", collections.get(view.getContentDescription().toString()).getItems().get(position));
                        Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_filmFragment, bundle);
                    }
                    return false;
                }
            });
        }

        else if (holder instanceof UniversalRecyclerAdapter.ViewHolderFullWidth) {
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.add_to_favorite) {
                        // Добавить в избранное
                        utils.addToFavorite(String.valueOf(view.getId()), collections.get(view.getContentDescription().toString()).getItems().get(position), position);
                        ((UniversalRecyclerAdapter.ViewHolderFullWidth) holder).getImageViewisForeverItem3().setVisibility(View.VISIBLE);
                        return true;
                    } else if (item.getItemId() == R.id.remove_is_forever) {
                        // Удалить из избранного
                        utils.removeFromFavorite(String.valueOf(view.getId()), position);
                        ((UniversalRecyclerAdapter.ViewHolderFullWidth) holder).getImageViewisForeverItem3().setVisibility(View.GONE);
                        return true;
                    } else if (item.getItemId() == R.id.add_to_view) {
                        // Добавить в просмотренные
                        utils.addToViewed(String.valueOf(view.getId()), collections.get(view.getContentDescription().toString()).getItems().get(position), position);
                        ((UniversalRecyclerAdapter.ViewHolderFullWidth) holder).getImageViewIsViewedItem3().setVisibility(View.VISIBLE);
                        return true;
                    } else if (item.getItemId() == R.id.remove_is_view) {
                        // Удалить из просмотренных
                        utils.removeFromViewed(String.valueOf(view.getId()), position);
                        ((UniversalRecyclerAdapter.ViewHolderFullWidth) holder).getImageViewIsViewedItem3().setVisibility(View.GONE);
                        return true;
                    } else if (item.getItemId() == R.id.search_similar) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("kinopoiskId", collections.get(view.getContentDescription().toString()).getItems().get(position).getKinopoiskId());
                        bundle.putString("category", Collection.TITLE_SIMILAR);
                        NavOptions.Builder builder = new NavOptions.Builder();
                        builder.setLaunchSingleTop(false);
                        builder.setPopUpTo(R.id.homePageFragment, false);
                        Navigation.findNavController(view).navigate(R.id.categoryListFilmFragment, bundle, builder.build());
                        return true;
                    } else if (item.getItemId() == R.id.open_film) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("film", collections.get(view.getContentDescription().toString()).getItems().get(position));
                        Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_filmFragment, bundle);
                    }
                    return false;
                }
            });
        }

        popupMenu.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        int posNestedScroll = nstdScrollView.getScrollY();
        Bundle bundleSaveState = new Bundle();
        bundleSaveState.putInt("posNestedScroll", posNestedScroll);
        bundleSaveState.putInt("posViewPager", viewPager2.getCurrentItem());
        bundleSaveState.putLong("lastRefresTs", lastRefresTs);
        onSaveInstanceState(bundleSaveState);

    }

}