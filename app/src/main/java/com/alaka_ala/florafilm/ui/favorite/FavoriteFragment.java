package com.alaka_ala.florafilm.ui.favorite;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.FragmentFavoriteBinding;
import com.alaka_ala.florafilm.sys.kp_api.Collection;
import com.alaka_ala.florafilm.sys.kp_api.ListFilmItem;
import com.alaka_ala.florafilm.sys.utils.MyRecyclerViewItemTouchListener;
import com.alaka_ala.florafilm.sys.utils.UtilsFavoriteAndViewFilm;
import com.alaka_ala.florafilm.sys.utils.UniversalRecyclerAdapter;
import com.alaka_ala.florafilm.ui.home.HomePageFragment;

import java.util.ArrayList;
import java.util.Map;

public class FavoriteFragment extends Fragment {
    private FragmentFavoriteBinding binding;
    private UtilsFavoriteAndViewFilm utilsFavoriteAndViewFilm;
    private RecyclerView rvFavorite;
    private UniversalRecyclerAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);

        utilsFavoriteAndViewFilm = new UtilsFavoriteAndViewFilm(getContext());

        rvFavorite = binding.rvFavorite;

        Map<String, ListFilmItem> favoriteFilmsMap = utilsFavoriteAndViewFilm.getFavoriteFilmsMap();

        Collection collection = new Collection(Collection.TITLE_FAVORITE, String.valueOf(favoriteFilmsMap.size()), "1", new ArrayList<>(favoriteFilmsMap.values()));


        if (collection.getItems().isEmpty()) {
            adapter = new UniversalRecyclerAdapter(rvFavorite, getLayoutInflater(), utilsFavoriteAndViewFilm, collection, UniversalRecyclerAdapter.TYPE_NULL_DATA);
        } else {
            adapter = new UniversalRecyclerAdapter(rvFavorite, getLayoutInflater(), utilsFavoriteAndViewFilm, collection, UniversalRecyclerAdapter.TYPE_FULL_WIDTH);
        }

        if (collection.getItems().isEmpty()) {
            rvFavorite.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false));
        } else {
            rvFavorite.setLayoutManager(new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false));
        }

        rvFavorite.addOnItemTouchListener(new MyRecyclerViewItemTouchListener(getContext(), rvFavorite, new MyRecyclerViewItemTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                ListFilmItem item = collection.getItems().get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("film", item);
                Navigation.findNavController(view).navigate(R.id.filmFragment, bundle);
            }

            @Override
            public void onLongItemClick(RecyclerView.ViewHolder holder, View view, int position) {

            }
        }));

        rvFavorite.setAdapter(adapter);



        return binding.getRoot();
    }




}