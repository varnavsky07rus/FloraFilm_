package com.alaka_ala.florafilm.ui.search.viewPager.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.FragmentViewedSearchBinding;
import com.alaka_ala.florafilm.sys.kp_api.Collection;
import com.alaka_ala.florafilm.sys.utils.MyRecyclerViewItemTouchListener;
import com.alaka_ala.florafilm.sys.utils.UtilsFavoriteAndViewFilm;
import com.alaka_ala.florafilm.sys.utils.UniversalRecyclerAdapter;
import com.alaka_ala.florafilm.ui.search.SearchFragment;
import com.google.android.material.snackbar.Snackbar;


public class ViewedSearchFragment extends Fragment implements SearchFragment.SearchListenerResult {
    private FragmentViewedSearchBinding binding;
    private RecyclerView rvViewedSearch;
    private UtilsFavoriteAndViewFilm utils;
    private UniversalRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentViewedSearchBinding.inflate(inflater, container, false);
        rvViewedSearch = binding.rvViewedSearch;
        rvViewedSearch.setLayoutManager(new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL, false));
        utils = new UtilsFavoriteAndViewFilm(getContext());
        SearchFragment.setSearchListenerResult(this);
        rvViewedSearch.addOnItemTouchListener(new MyRecyclerViewItemTouchListener(getContext(), rvViewedSearch, new MyRecyclerViewItemTouchListener.OnItemClickListener() {
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

        utils.setMethodListener(new UtilsFavoriteAndViewFilm.MethodListener() {
            @Override
            public void onAddToViewed(int positionRecyclerItem) {
                UtilsFavoriteAndViewFilm.MethodListener.super.onAddToViewed(positionRecyclerItem);
                Snackbar.make(binding.getRoot(), "Фильм добавлен в просмотренные", Snackbar.LENGTH_SHORT).show();
                SearchFragment.onNotifyDataSearchChanged(true, 1, collection.getItems().get(positionRecyclerItem));
            }

            @Override
            public void onAddToFavorite(int positionRecyclerItem) {
                UtilsFavoriteAndViewFilm.MethodListener.super.onAddToFavorite(positionRecyclerItem);
                Snackbar.make(binding.getRoot(), "Фильм добавлен в избранное", Snackbar.LENGTH_SHORT).show();
                SearchFragment.onNotifyDataSearchChanged(true, 2, collection.getItems().get(positionRecyclerItem));
            }

            @Override
            public void onRemoveFromFavorite(int positionRecyclerItem) {
                UtilsFavoriteAndViewFilm.MethodListener.super.onRemoveFromFavorite(positionRecyclerItem);
                Snackbar.make(binding.getRoot(), "Фильм удален из избранного", Snackbar.LENGTH_SHORT).show();
                SearchFragment.onNotifyDataSearchChanged(false, 2, collection.getItems().get(positionRecyclerItem));
            }

            @Override
            public void onRemoveFromViewed(int positionRecyclerItem) {
                UtilsFavoriteAndViewFilm.MethodListener.super.onRemoveFromViewed(positionRecyclerItem);
                Snackbar.make(binding.getRoot(), "Фильм удален из просмотренных", Snackbar.LENGTH_SHORT).show();
                SearchFragment.onNotifyDataSearchChanged(false, 1, collection.getItems().get(positionRecyclerItem));
            }
        });






        return binding.getRoot();
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
                    return false;
                }
            });
        } else if (holder instanceof UniversalRecyclerAdapter.ViewHolderFullWidth) {
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
                    return false;
                }
            });
        }


        popupMenu.show();
    }


    private Collection collection;
    @Override
    public void result(String query, Collection collection) {
        this.collection = collection;
        if (this.collection.getItems().isEmpty()) {
            rvViewedSearch.setLayoutManager(new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false));
            rvViewedSearch.setAdapter(adapter = new UniversalRecyclerAdapter(getLayoutInflater(), utils, collection, UniversalRecyclerAdapter.TYPE_NULL_DATA));
            return;
        }
        rvViewedSearch.setAdapter(adapter = new UniversalRecyclerAdapter(getLayoutInflater(), utils, collection, UniversalRecyclerAdapter.TYPE_FULL_WIDTH));
        rvViewedSearch.setLayoutManager(new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL, false));
    }


    @Override
    public void onResume() {
        super.onResume();
        SearchFragment.setSearchListenerResult(this);
        if (collection == null) return;
        if (adapter == null) {
            if (collection.getItems().isEmpty()) {
                rvViewedSearch.setLayoutManager(new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false));
                rvViewedSearch.setAdapter(adapter = new UniversalRecyclerAdapter(getLayoutInflater(), utils, collection, UniversalRecyclerAdapter.TYPE_NULL_DATA));
                return;
            }
            rvViewedSearch.setLayoutManager(new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL, false));
            rvViewedSearch.setAdapter(adapter = new UniversalRecyclerAdapter(getLayoutInflater(), utils, collection, UniversalRecyclerAdapter.TYPE_FULL_WIDTH));
        } else  {
            adapter.notifyDataSetChanged();
        }
    }
}