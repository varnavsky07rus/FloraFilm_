package com.alaka_ala.florafilm.ui.film.actors;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.databinding.FragmentActorBinding;
import com.alaka_ala.florafilm.sys.kp_api.Collection;
import com.alaka_ala.florafilm.sys.kp_api.KinopoiskAPI;
import com.alaka_ala.florafilm.sys.kp_api.ListFilmItem;
import com.alaka_ala.florafilm.sys.kp_api.StaffFilmsItem;
import com.alaka_ala.florafilm.sys.kp_api.StaffInfo;
import com.alaka_ala.florafilm.sys.kp_api.StaffSpouseItem;
import com.alaka_ala.florafilm.sys.utils.MyRecyclerViewItemTouchListener;
import com.alaka_ala.florafilm.ui.home.HomePageFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActorFragment extends BottomSheetDialogFragment {
    private FragmentActorBinding binding;
    private ImageView imageViewActorPhoto;
    private TextView textViewNameActorRu, textViewNameActorOrig;
    private TextView textViewProffesionActor, textViewActorBirthDay, textViewActorPlaceBirth, textViewCountFilms;
    private FrameLayout frameLayoutSpouses;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActorBinding.inflate(inflater, container, false);



        imageViewActorPhoto = binding.layoutInfoActor.imageViewActorPhoto;
        textViewNameActorRu = binding.layoutInfoActor.textViewNameActorRu;
        textViewNameActorOrig = binding.layoutInfoActor.textViewNameActorOrig;
        textViewProffesionActor = binding.layoutInfoActor.textViewProffesionActor;
        textViewActorBirthDay = binding.layoutInfoActor.textViewActorBirthDay;
        textViewActorPlaceBirth = binding.layoutInfoActor.textViewActorPlaceBirth;
        textViewCountFilms = binding.layoutInfoActor.textViewCountFilms;
        frameLayoutSpouses = binding.layoutInfoActor.frameLayoutSpouses;



        if (getArguments() != null) {
            int staffId = getArguments().getInt("staffId");
            loadActorInforamtion(staffId);
        } else {
            // Здесь показываем слой который говорит о том что даных об актёре нет
            binding.layoutNullData.getRoot().setVisibility(View.VISIBLE);
        }
        return binding.getRoot();
    }

    private void loadActorInforamtion(int staffId) {
        KinopoiskAPI kinopoiskAPI = new KinopoiskAPI(getResources().getString(R.string.api_key_kinopoisk));
        kinopoiskAPI.getInformationStaff(staffId, new KinopoiskAPI.RequestCallbackInformationStaff() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccessInfoStaff(StaffInfo staffInfo) {
                Map<Integer, Boolean> map = new HashMap<>();
                for (int i = 0; i < staffInfo.getFilms().size(); i++) {
                    // Здесь staffFilmItems сортируется отдубликатов
                    if (!map.containsKey(staffInfo.getFilms().get(i).getFilmId())) {
                        map.put(staffInfo.getFilms().get(i).getFilmId(), true);
                    } else {
                        staffInfo.getFilms().remove(i);
                        i--;
                    }
                }

                textViewNameActorRu.setText(staffInfo.getNameRu().equals("null") ? "Неизвестно" : staffInfo.getNameRu() + " (" + staffInfo.getAge() + ")");
                textViewNameActorOrig.setText(staffInfo.getNameEn().equals("null") ? "-" : staffInfo.getNameEn());
                textViewProffesionActor.setText(staffInfo.getProfession().equals("null") ? "Неизвестно" : staffInfo.getProfession());
                textViewActorBirthDay.setText(staffInfo.getBirthday().equals("null") ? "Неизвестно" : staffInfo.getBirthday() + (staffInfo.getDeath().equals("null") ? "" : " • " + staffInfo.getDeath()));
                textViewActorPlaceBirth.setText(staffInfo.getBirthplace().equals("null") ? "Неизвестно" : staffInfo.getBirthplace());
                textViewCountFilms.setText(staffInfo.getFilms().size() + " шт");
                Picasso.get().load(staffInfo.getPosterUrl()).into(imageViewActorPhoto);
                if (!staffInfo.getSpouses().isEmpty()) {
                    frameLayoutSpouses.setVisibility(View.VISIBLE);
                    RecyclerView rvSpouses = binding.layoutInfoActor.rvSpouses;
                    rvSpouses.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    rvSpouses.setAdapter(new ActorSpousesAdapter(staffInfo.getSpouses()));
                    rvSpouses.addOnItemTouchListener(new MyRecyclerViewItemTouchListener(getContext(), rvSpouses, new MyRecyclerViewItemTouchListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                            ActorFragment.newInstance(staffInfo.getSpouses().get(position).getPersonId()).show(getParentFragmentManager(), "SpouseActorFragment");
                        }

                        @Override
                        public void onLongItemClick(RecyclerView.ViewHolder holder, View view, int position) {

                        }
                    }));
                }

                if (!staffInfo.getFilms().isEmpty()) {
                    RecyclerView rvActorFilm = binding.layoutInfoActor.rvActorFilm;
                    rvActorFilm.setLayoutManager(new GridLayoutManager(getContext(), 5, RecyclerView.HORIZONTAL, false));
                    rvActorFilm.setAdapter(new AcrotFilmsAdapter(staffInfo.getFilms()));
                    rvActorFilm.addOnItemTouchListener(new MyRecyclerViewItemTouchListener(getContext(), rvActorFilm, new MyRecyclerViewItemTouchListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(RecyclerView.ViewHolder holder, View view, int position) {
                            Bundle bundle = new Bundle();
                            ListFilmItem film = new ListFilmItem(
                                    staffInfo.getFilms().get(position).getFilmId(),
                                    0,
                                    staffInfo.getFilms().get(position).getNameRu(),
                                    staffInfo.getFilms().get(position).getNameEn(),
                                    "null",
                                    new ArrayList<>(),
                                    new ArrayList<>(),
                                    0.0,
                                    "0",
                                    0,
                                    "null",
                                    staffInfo.getFilms().get(position).getPosterUrl(),
                                    staffInfo.getFilms().get(position).getPosterUrlPreview(),
                                    "null", "null", staffInfo.getFilms().get(position).getDescription(), "null");
                            bundle.putSerializable("film", film);
                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main).navigate(R.id.filmFragment, bundle);
                        }

                        @Override
                        public void onLongItemClick(RecyclerView.ViewHolder holder, View view, int position) {

                        }
                    }));
                }


            }

            @Override
            public void onFailureInfoStaff(IOException e) {
                new MaterialAlertDialogBuilder(getContext()).setMessage(e.getMessage()).setNegativeButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActorFragment.this.dismiss();
                    }
                }).show();
            }

            @Override
            public void finishInfoStaff() {

            }
        });
    }


    public static ActorFragment newInstance(int actorId) {

        Bundle args = new Bundle();
        args.putInt("staffId", actorId);
        ActorFragment fragment = new ActorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private class ActorSpousesAdapter extends RecyclerView.Adapter<ActorSpousesAdapter.ViewHolder> {
        private final ArrayList<StaffSpouseItem> spouseItems;

        private ActorSpousesAdapter(ArrayList<StaffSpouseItem> spouseItems) {
            if (spouseItems == null) {
                this.spouseItems = new ArrayList<>();
                return;
            }
            this.spouseItems = spouseItems;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.rv_item_spouses, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Picasso.get().load(spouseItems.get(position).getPosterUrl()).into(holder.imageViewSpouse);
        }

        @Override
        public int getItemCount() {
            return spouseItems.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView imageViewSpouse;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageViewSpouse = itemView.findViewById(R.id.imageViewSpouse);
            }
        }
    }

    private class AcrotFilmsAdapter extends RecyclerView.Adapter<AcrotFilmsAdapter.ViewHolder> {
        private final ArrayList<StaffFilmsItem> staffFilmItems;
        private AcrotFilmsAdapter(ArrayList<StaffFilmsItem> staffFilmItems) {
            this.staffFilmItems = staffFilmItems;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.rv_item_actor_films, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Handler handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message msg) {
                    Picasso.get().load(msg.getData().getString("urlPoster", "")).into(holder.imageViewActorFilm);
                    String title = msg.getData().getString("title", "");

                    return false;
                }
            });

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String urlPoster = staffFilmItems.get(holder.getAdapterPosition()).getPosterUrl();
                    String title = !staffFilmItems.get(holder.getAdapterPosition()).getNameRu().equals("null") ?
                            staffFilmItems.get(holder.getAdapterPosition()).getNameRu() + " (" + staffFilmItems.get(holder.getAdapterPosition()).getProfessionKey() + ")" :
                            staffFilmItems.get(holder.getAdapterPosition()).getNameEn() + " (" + staffFilmItems.get(holder.getAdapterPosition()).getProfessionKey() + ")";
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("urlPoster", urlPoster);
                    bundle.putString("title", title);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            });
            thread.start();



        }

        @Override
        public int getItemCount() {
            return staffFilmItems.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView imageViewActorFilm;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageViewActorFilm = itemView.findViewById(R.id.imageViewFilmActorPoster);
            }
        }


    }
}