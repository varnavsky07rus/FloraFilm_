package com.alaka_ala.florafilm.ui.categories;

import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_CATEGORY_GENRE_10;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_CATEGORY_GENRE_11;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_CATEGORY_GENRE_12;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_CATEGORY_GENRE_13;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_CATEGORY_GENRE_14;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_CATEGORY_GENRE_17;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_CATEGORY_GENRE_18;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_CATEGORY_GENRE_19;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_CATEGORY_GENRE_24;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_CATEGORY_GENRE_7;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_SIMILAR;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.FragmentCategoryListFilmBinding;
import com.alaka_ala.florafilm.sys.kp_api.Collection;
import com.alaka_ala.florafilm.sys.kp_api.KinopoiskAPI;
import com.alaka_ala.florafilm.sys.kp_api.ListFilmItem;
import com.alaka_ala.florafilm.sys.utils.MyRecyclerViewItemTouchListener;
import com.alaka_ala.florafilm.sys.utils.MyRecyclerViewScrollListener;
import com.alaka_ala.florafilm.sys.utils.UtilsFavoriteAndViewFilm;
import com.alaka_ala.florafilm.sys.utils.UniversalRecyclerAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;


public class CategoryListFilmFragment extends Fragment implements KinopoiskAPI.RequestCallbackCollection {
    private FragmentCategoryListFilmBinding binding;
    private RecyclerView rvCategory;
    private UtilsFavoriteAndViewFilm utils;
    private UniversalRecyclerAdapter adapter;
    private KinopoiskAPI kinopoiskAPI;
    private String category;
    private int kinopoiskId;
    private int currentPage = 1;
    private boolean isConnected = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryListFilmBinding.inflate(inflater, container, false);
        rvCategory = binding.rvCategory;
        rvCategory.setLayoutManager(new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false));

        utils = new UtilsFavoriteAndViewFilm(getContext());
        kinopoiskAPI = new KinopoiskAPI(getResources().getString(R.string.api_key_kinopoisk));

        Bundle bundle = getArguments();
        if (bundle == null) return binding.getRoot();

        kinopoiskId = bundle.getInt("kinopoiskId", 0);
        category = bundle.getString("category", "");

        rvCategory.addOnScrollListener(new MyRecyclerViewScrollListener(MyRecyclerViewScrollListener.VERTICAL) {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd() {
                if (!category.equals(TITLE_SIMILAR)) {
                    loadData(++currentPage);
                }
            }
        });


        rvCategory.addOnItemTouchListener(new MyRecyclerViewItemTouchListener(getContext(), rvCategory, new MyRecyclerViewItemTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("film", collection.getItems().get(position));
                Navigation.findNavController(view).navigate(R.id.filmFragment, bundle);
            }

            @Override
            public void onLongItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                showPopUpMenu(holder, view, position);
            }
        }));

        if (this.collection == null) {
            loadData(currentPage);
        } else {
            finish();
        }


        return binding.getRoot();
    }





    private void loadData(int cPage) {
        isConnected = true;
        switch (category) {
            case Collection.TITLE_POPULAR_ALL:
                // Популярные фильмы и сериалы
                kinopoiskAPI.getListTopPopularAll(cPage, this);
                break;
            case Collection.TITLE_POPULAR_MOVIES:
                kinopoiskAPI.getListTopPopularMovies(cPage, this);
                break;
            case Collection.TITLE_TOP_250_TV_SHOWS:
                // Популярные 250 сериалов
                kinopoiskAPI.getListTop250TVShows(cPage, this);
                break;
            case Collection.TITLE_TOP_250_MOVIES:
                // Популярные 250 фильмов
                kinopoiskAPI.getListTop250Movies(cPage, this);
                break;
            case Collection.TITLE_COMICS_THEME:
                // По мотивам комиксов
                kinopoiskAPI.getListComicsTheme(cPage, this);
                break;
            case Collection.TITLE_SIMILAR:
                // Список похожих фильмов
                kinopoiskAPI.getListSimilarFilms(kinopoiskId, cPage, this);
                break;
            case Collection.TITLE_FAMILY:
                // Семейные фильмы
                kinopoiskAPI.getListFamily(cPage, this);
                break;
            case Collection.TITLE_VAMPIRE_THEME:
                // Про вампиров
                kinopoiskAPI.getListVampireTheme(cPage, this);
                break;
            case TITLE_CATEGORY_GENRE_19:
                kinopoiskAPI.getListGenre(TITLE_CATEGORY_GENRE_19, cPage, this);
                break;
            case TITLE_CATEGORY_GENRE_18:
                kinopoiskAPI.getListGenre(TITLE_CATEGORY_GENRE_18, cPage, this);
                break;
            case TITLE_CATEGORY_GENRE_17:
                kinopoiskAPI.getListGenre(TITLE_CATEGORY_GENRE_17, cPage, this);
                break;
            case TITLE_CATEGORY_GENRE_14:
                kinopoiskAPI.getListGenre(TITLE_CATEGORY_GENRE_14, cPage, this);
                break;
            case TITLE_CATEGORY_GENRE_13:
                kinopoiskAPI.getListGenre(TITLE_CATEGORY_GENRE_13, cPage, this);
                break;
            case TITLE_CATEGORY_GENRE_12:
                kinopoiskAPI.getListGenre(TITLE_CATEGORY_GENRE_12, cPage, this);
                break;
            case TITLE_CATEGORY_GENRE_11:
                kinopoiskAPI.getListGenre(TITLE_CATEGORY_GENRE_11, cPage, this);
                break;
            case TITLE_CATEGORY_GENRE_10:
                kinopoiskAPI.getListGenre(TITLE_CATEGORY_GENRE_10, cPage, this);
                break;
            case TITLE_CATEGORY_GENRE_7:
                kinopoiskAPI.getListGenre(TITLE_CATEGORY_GENRE_7, cPage, this);
                break;
            case TITLE_CATEGORY_GENRE_24:
                kinopoiskAPI.getListGenre(TITLE_CATEGORY_GENRE_24, cPage, this);
                break;
        }
    }


    private Collection collection;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSuccess(Collection collection) {
        isConnected = false;
        if (this.collection == null) {
            this.collection = collection;
        } else {
            for (ListFilmItem item : collection.getItems()) {
                this.collection.getItems().add(item);
            }
        }

    }

    @Override
    public void onFailure(IOException e) {
        isConnected = false;
        if (getContext() == null) return;
        Snackbar.make(binding.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void finish() {
        isConnected = false;
        if (adapter == null) {
            if (collection.getItems().isEmpty()) {
                rvCategory.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false));
                adapter = new UniversalRecyclerAdapter(rvCategory, getLayoutInflater(), utils, this.collection, UniversalRecyclerAdapter.TYPE_NULL_DATA);
                rvCategory.setAdapter(adapter);
                return;
            }
            adapter = new UniversalRecyclerAdapter(rvCategory, getLayoutInflater(), utils, this.collection, UniversalRecyclerAdapter.TYPE_FULL_WIDTH);
            rvCategory.setAdapter(adapter);
        } else {
            adapter.setCollection(collection);
            if (rvCategory.getAdapter() == null) {
                rvCategory.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void showPopUpMenu(RecyclerView.ViewHolder holder, View view, int position) {
        boolean isFavorite = utils.isFilmInFavorite(String.valueOf(view.getId()));
        boolean isViewed = utils.isFilmInViewed(String.valueOf(view.getId()));

        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.popup_menu_film);
        popupMenu.setForceShowIcon(true);
        if (isFavorite) {
            popupMenu.getMenu().findItem(R.id.add_to_favorite).setVisible(false);
        } else {
            popupMenu.getMenu().findItem(R.id.remove_is_forever).setVisible(false);
        }
        if (isViewed) {
            popupMenu.getMenu().findItem(R.id.add_to_view).setVisible(false);
        } else {
            popupMenu.getMenu().findItem(R.id.remove_is_view).setVisible(false);
        }

        if (holder instanceof UniversalRecyclerAdapter.ViewHolderBigPoster) {
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.add_to_favorite) {
                        // Добавить в избранное
                        utils.addToFavorite(String.valueOf(view.getId()), collection.getItems().get(position), position);
                        ((UniversalRecyclerAdapter.ViewHolderBigPoster) holder).getImageViewSaveToFavoriteBig().setVisibility(View.VISIBLE);
                        return true;
                    } else if (item.getItemId() == R.id.remove_is_forever) {
                        // Удалить из избранного
                        utils.removeFromFavorite(String.valueOf(view.getId()), position);
                        ((UniversalRecyclerAdapter.ViewHolderBigPoster) holder).getImageViewSaveToFavoriteBig().setVisibility(View.GONE);
                        return true;
                    } else if (item.getItemId() == R.id.add_to_view) {
                        // Добавить в просмотренные
                        utils.addToViewed(String.valueOf(view.getId()), collection.getItems().get(position), position);
                        ((UniversalRecyclerAdapter.ViewHolderBigPoster) holder).getImageViewIsViewedBig().setVisibility(View.VISIBLE);
                        return true;
                    } else if (item.getItemId() == R.id.remove_is_view) {
                        // Удалить из просмотренных
                        utils.removeFromViewed(String.valueOf(view.getId()), position);
                        ((UniversalRecyclerAdapter.ViewHolderBigPoster) holder).getImageViewIsViewedBig().setVisibility(View.GONE);
                        return true;
                    }
                    else if (item.getItemId() == R.id.search_similar) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("kinopoiskId", collection.getItems().get(position).getKinopoiskId());
                        bundle.putString("category", Collection.TITLE_SIMILAR);
                        NavOptions.Builder builder = new NavOptions.Builder();
                        builder.setLaunchSingleTop(false);
                        builder.setPopUpTo(R.id.categoryListFilmFragment, false);
                        Navigation.findNavController(view).navigate(R.id.categoryListFilmFragment, bundle, builder.build());
                        return true;
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
                        utils.addToFavorite(String.valueOf(view.getId()), collection.getItems().get(position), position);
                        ((UniversalRecyclerAdapter.ViewHolderSmallPoster) holder).getImageViewSaveToFavoriteSmall().setVisibility(View.VISIBLE);
                        return true;
                    } else if (item.getItemId() == R.id.remove_is_forever) {
                        // Удалить из избранного
                        utils.removeFromFavorite(String.valueOf(view.getId()), position);
                        ((UniversalRecyclerAdapter.ViewHolderSmallPoster) holder).getImageViewSaveToFavoriteSmall().setVisibility(View.GONE);
                        return true;
                    } else if (item.getItemId() == R.id.add_to_view) {
                        // Добавить в просмотренные
                        utils.addToViewed(String.valueOf(view.getId()), collection.getItems().get(position), position);
                        ((UniversalRecyclerAdapter.ViewHolderSmallPoster) holder).getImageViewIsViewedSmall().setVisibility(View.VISIBLE);
                        return true;
                    } else if (item.getItemId() == R.id.remove_is_view) {
                        // Удалить из просмотренных
                        utils.removeFromViewed(String.valueOf(view.getId()), position);
                        ((UniversalRecyclerAdapter.ViewHolderSmallPoster) holder).getImageViewIsViewedSmall().setVisibility(View.GONE);
                        return true;
                    }
                    else if (item.getItemId() == R.id.search_similar) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("kinopoiskId", collection.getItems().get(position).getKinopoiskId());
                        bundle.putString("category", Collection.TITLE_SIMILAR);
                        NavOptions.Builder builder = new NavOptions.Builder();
                        builder.setLaunchSingleTop(false);
                        builder.setPopUpTo(R.id.categoryListFilmFragment, false);
                        Navigation.findNavController(view).navigate(R.id.categoryListFilmFragment, bundle, builder.build());
                        return true;
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
                        utils.addToFavorite(String.valueOf(view.getId()), collection.getItems().get(position), position);
                        ((UniversalRecyclerAdapter.ViewHolderFullWidth) holder).getImageViewisForeverItem3().setVisibility(View.VISIBLE);
                        return true;
                    } else if (item.getItemId() == R.id.remove_is_forever) {
                        // Удалить из избранного
                        utils.removeFromFavorite(String.valueOf(view.getId()), position);
                        ((UniversalRecyclerAdapter.ViewHolderFullWidth) holder).getImageViewisForeverItem3().setVisibility(View.GONE);
                        return true;
                    } else if (item.getItemId() == R.id.add_to_view) {
                        // Добавить в просмотренные
                        utils.addToViewed(String.valueOf(view.getId()), collection.getItems().get(position), position);
                        ((UniversalRecyclerAdapter.ViewHolderFullWidth) holder).getImageViewIsViewedItem3().setVisibility(View.VISIBLE);
                        return true;
                    } else if (item.getItemId() == R.id.remove_is_view) {
                        // Удалить из просмотренных
                        utils.removeFromViewed(String.valueOf(view.getId()), position);
                        ((UniversalRecyclerAdapter.ViewHolderFullWidth) holder).getImageViewIsViewedItem3().setVisibility(View.GONE);
                        return true;
                    }
                    else if (item.getItemId() == R.id.search_similar) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("kinopoiskId", collection.getItems().get(position).getKinopoiskId());
                        bundle.putString("category", Collection.TITLE_SIMILAR);
                        NavOptions.Builder builder = new NavOptions.Builder();
                        builder.setLaunchSingleTop(false);
                        builder.setPopUpTo(R.id.categoryListFilmFragment, false);
                        Navigation.findNavController(view).navigate(R.id.categoryListFilmFragment, bundle, builder.build());
                        return true;
                    }
                    return false;
                }
            });
        }


        popupMenu.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}