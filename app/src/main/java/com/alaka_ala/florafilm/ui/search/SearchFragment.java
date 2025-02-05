package com.alaka_ala.florafilm.ui.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.FragmentSearchBinding;
import com.alaka_ala.florafilm.sys.kp_api.Collection;
import com.alaka_ala.florafilm.sys.kp_api.KinopoiskAPI;
import com.alaka_ala.florafilm.sys.kp_api.ListFilmItem;
import com.alaka_ala.florafilm.sys.utils.SettingsApp;
import com.alaka_ala.florafilm.sys.utils.UtilsFavoriteAndViewFilm;
import com.alaka_ala.florafilm.ui.search.viewPager.AdapterSearchViewPager;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private TabLayout tabs;
    private ViewPager2 viewPgerSearchTab;
    private AdapterSearchViewPager adapter;

    private KinopoiskAPI kinopoiskAPI;
    private Map<String, ListFilmItem> favoriteList = new HashMap<>();
    private Map<String, ListFilmItem> viewedList = new HashMap<>();
    private UtilsFavoriteAndViewFilm utils;

    private SettingsApp settingsApp;

    private static String currentQueryText = "";

    private boolean isAnimateViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        kinopoiskAPI = new KinopoiskAPI(getResources().getString(R.string.api_key_kinopoisk));
        utils = new UtilsFavoriteAndViewFilm(getContext());
        settingsApp = new SettingsApp(getContext());
        isAnimateViewPager = settingsApp.getParam(SettingsApp.SettingsKeys.INTERFACE_ANIMATION, SettingsApp.SettingsDefsVal.DEF_INTERFACE_ANIMATION);
        viewedList = utils.getViewedFilmsMap();
        favoriteList = utils.getFavoriteFilmsMap();

        tabs = binding.tabs;
        viewPgerSearchTab = binding.viewPgerSearchTab;
        if (isAnimateViewPager) {
            viewPgerSearchTab.setPageTransformer(new AdapterSearchViewPager.WheelPageTransformer());
        }


        notifyDataSearchChanged = new NotifyDataSearchChanged() {
            @Override
            public void notifyDataChanged(boolean isAdd, int whatList, ListFilmItem item) {
                if (isAdd) {
                    if (whatList == 1) {
                        viewedList.put(String.valueOf(item.getKinopoiskId()), item);
                    } else if (whatList == 2) {
                        favoriteList.put(String.valueOf(item.getKinopoiskId()), item);
                    }
                } else {
                    if (whatList == 1) {
                        viewedList.remove(String.valueOf(item.getKinopoiskId()));
                    } else if (whatList == 2) {
                        favoriteList.remove(String.valueOf(item.getKinopoiskId()));
                    }
                }
            }
        };


        viewPgerSearchTab.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                tabs.setScrollPosition(position, positionOffset, false);
            }

            private int positionPage = 0;

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                positionPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state != ViewPager2.SCROLL_STATE_IDLE) return;
                Handler handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {

                        return false;
                    }
                });

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (positionPage == 0) {
                            //searchGlobal(currentQueryText);
                        } else if (positionPage == 1) {
                            searchFavoriteQuery(currentQueryText);
                        } else if (positionPage == 2) {
                            searchViewedQuery(currentQueryText);
                        }
                    }
                }, 20);

            }
        });

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPgerSearchTab.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        adapter = new AdapterSearchViewPager(getChildFragmentManager(), getLifecycle());

        viewPgerSearchTab.setAdapter(adapter);


        getActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );

        return binding.getRoot();
    }

    private Collection collectionResultGlobal;

    private void searchGlobal(String query) {
        kinopoiskAPI.getListSearch(query, 1, new KinopoiskAPI.RequestCallbackCollection() {
            @Override
            public void onSuccess(Collection collection) {
                collectionResultGlobal = collection;
            }

            @Override
            public void onFailure(IOException e) {

            }

            @Override
            public void finish() {
                publishResult(currentQueryText, collectionResultGlobal);
            }
        });
    }

    private void searchFavoriteQuery(String query) {
        ArrayList<ListFilmItem> list = new ArrayList<>(favoriteList.values());
        ArrayList<ListFilmItem> listResultSearch = new ArrayList<>();
        for (ListFilmItem item : list) {
            if (searchRegex(query, item.getNameRu() + " " + item.getNameEn())) {
                listResultSearch.add(item);
            }
        }
        Collection collection = new Collection(Collection.TITLE_SEARCH_FAVORITE, String.valueOf(listResultSearch.size()), "1", listResultSearch);
        publishResult(query, collection);
    }

    private void searchViewedQuery(String query) {
        ArrayList<ListFilmItem> list = new ArrayList<>(viewedList.values());
        ArrayList<ListFilmItem> listResultSearch = new ArrayList<>();
        for (ListFilmItem item : list) {
            if (searchRegex(query, item.getNameRu() + " " + item.getNameEn())) {
                listResultSearch.add(item);
            }
        }
        Collection collection = new Collection(Collection.TITLE_SEARCH_VIEWED, String.valueOf(listResultSearch.size()), "1", listResultSearch);
        publishResult(query, collection);
    }

    private boolean searchRegex(String query, String title) {
        // 1. Проверка на полное совпадение (нечувствительно к регистру)
        if (title.toLowerCase().contains(query.toLowerCase())) {
            return true;
        }

        // 2. Проверка на процент совпадения букв
        int matchingChars = 0;
        for (int i = 0; i < query.length() && i < title.length(); i++) {
            if (Character.toLowerCase(query.charAt(i)) == Character.toLowerCase(title.charAt(i))) {
                matchingChars++;
            }
        }

        double matchPercentage = (double) matchingChars / query.length() * 100;

        return matchPercentage >= 70;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        SearchView searchView = new SearchView(getContext());
        searchView.setQueryHint("Поиск..."); // Устанавливаем подсказку
        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQueryText = query;
                if (viewPgerSearchTab.getCurrentItem() == 0) {
                    searchGlobal(query);
                    return false;
                } else if (viewPgerSearchTab.getCurrentItem() == 1) {
                    searchFavoriteQuery(query);
                    return false;
                } else if (viewPgerSearchTab.getCurrentItem() == 2) {
                    searchViewedQuery(query);
                    return false;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQueryText = newText;
                if (viewPgerSearchTab.getCurrentItem() == 1) {
                    searchFavoriteQuery(newText);
                    return false;
                } else if (viewPgerSearchTab.getCurrentItem() == 2) {
                    searchViewedQuery(newText);
                    return false;
                }
                return false;
            }
        });

        menu.add("Поиск")
                .setIcon(R.drawable.rounded_video_search_24)
                .setActionView(searchView)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
    }

    /**
     * Интерфейс реализации которого будет вызывать {@link SearchFragment} методом
     * {@link SearchFragment#setSearchListenerResult(SearchFragment.SearchListenerResult searchListenerResult)}
     */
    public interface SearchListenerResult {
        void result(String query, Collection collection);
    }

    /**
     * Интерфейс реализации которого будет вызывать {@link SearchFragment} методом
     * {@link SearchFragment#setSearchListenerResult(SearchFragment.SearchListenerResult searchListenerResult)}
     */
    private static SearchListenerResult searchListenerResult;

    /**
     * Устанавливает {@link SearchListenerResult} для {@link SearchFragment},
     * данный метод можно использовать как локально во фрагменте, так и добавить в fragment
     * через implementation но после указать на контекст т.е. -> this |
     * в {@link SearchFragment#setSearchListenerResult(SearchFragment.SearchListenerResult searchListenerResult)}
     */
    public static void setSearchListenerResult(SearchListenerResult searchListenerResult) {
        if (searchListenerResult == null) return;
        SearchFragment.searchListenerResult = searchListenerResult;
    }

    /**
     * Данный метод предназначен исключительно для {@link SearchFragment},
     * что бы передать результат поиска в один из фрагментов ViewPager2
     */
    private void publishResult(String query, Collection collection) {
        if (searchListenerResult == null) return;
        searchListenerResult.result(query, collection);
    }


    private interface NotifyDataSearchChanged {
        void notifyDataChanged(boolean isAdd, int whatList, ListFilmItem item);
    }

    private static NotifyDataSearchChanged notifyDataSearchChanged;

    /**
     * Когда в одном из фрагментов происходит одно из действий: добавление или удаление в просмотренные или избранные
     * то данный метод обновит данные внутри {@link SearchFragment} для дальнейшей работы с ними (или без них)
     * Данный метод вызывает внутри из фрагмента ViewPager2
     */
    public static void onNotifyDataSearchChanged(boolean isAdd, int whatList, ListFilmItem item) {
        if (notifyDataSearchChanged == null) return;
        notifyDataSearchChanged.notifyDataChanged(isAdd, whatList, item);
    }

}