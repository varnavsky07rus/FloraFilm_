package com.alaka_ala.florafilm.ui.viewed;

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
import com.alaka_ala.florafilm.databinding.FragmentViewedBinding;
import com.alaka_ala.florafilm.sys.kp_api.Collection;
import com.alaka_ala.florafilm.sys.kp_api.ListFilmItem;
import com.alaka_ala.florafilm.sys.utils.MyRecyclerViewItemTouchListener;
import com.alaka_ala.florafilm.sys.utils.UtilsFavoriteAndViewFilm;
import com.alaka_ala.florafilm.sys.utils.UniversalRecyclerAdapter;

import java.util.ArrayList;
import java.util.Map;


public class ViewedFragment extends Fragment {
    private FragmentViewedBinding binding;
    private RecyclerView rvViewed;
    private UniversalRecyclerAdapter adapter;
    private UtilsFavoriteAndViewFilm utilsFavoriteAndViewFilm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentViewedBinding.inflate(inflater, container, false);

        utilsFavoriteAndViewFilm = new UtilsFavoriteAndViewFilm(getContext());

        rvViewed = binding.rvViewed;

        Map<String, ListFilmItem> viewedFilmsMap = utilsFavoriteAndViewFilm.getViewedFilmsMap();

        Collection collection = new Collection(Collection.TITLE_FAVORITE, String.valueOf(viewedFilmsMap.size()), "1", new ArrayList<>(viewedFilmsMap.values()));

        boolean isNullData = collection.getItems().isEmpty();
        if (isNullData) {
            adapter = new UniversalRecyclerAdapter(getLayoutInflater(), utilsFavoriteAndViewFilm, collection, UniversalRecyclerAdapter.TYPE_NULL_DATA);
        } else {
            adapter = new UniversalRecyclerAdapter(getLayoutInflater(), utilsFavoriteAndViewFilm, collection, UniversalRecyclerAdapter.TYPE_FULL_WIDTH);
        }

        if (isNullData) {
            rvViewed.setLayoutManager(new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false));
        } else {
            rvViewed.setLayoutManager(new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false));
        }

        rvViewed.addOnItemTouchListener(new MyRecyclerViewItemTouchListener(getContext(), rvViewed, new MyRecyclerViewItemTouchListener.OnItemClickListener() {
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

        rvViewed.setAdapter(adapter);

        return binding.getRoot();
    }
}