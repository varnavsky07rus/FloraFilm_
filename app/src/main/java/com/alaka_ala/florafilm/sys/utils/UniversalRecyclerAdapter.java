package com.alaka_ala.florafilm.sys.utils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import androidx.recyclerview.widget.RecyclerView;

import com.alaka_ala.florafilm.R;
import com.alaka_ala.florafilm.sys.kp_api.Collection;
import com.alaka_ala.florafilm.sys.kp_api.ListFilmItem;
import com.squareup.picasso.Picasso;

public class UniversalRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TYPE_HOLDER_BIG_POSTER = "TYPE_HOLDER_BIG_POSTER";
    public static final String TYPE_HOLDER_SMALL_POSTER = "TYPE_HOLDER_SMALL_POSTER";
    public static final String TYPE_FULL_WIDTH = "TYPE_FULL_WIDTH";
    public static final String TYPE_NULL_DATA = "TYPE_NULL_DATA";


    @StringDef({TYPE_HOLDER_BIG_POSTER, TYPE_HOLDER_SMALL_POSTER, TYPE_FULL_WIDTH, TYPE_NULL_DATA})
    public @interface TypeHolder {}
    private final UtilsFavoriteAndViewFilm utilsFavoriteAndViewFilm;
    public void setCollection(Collection collection) {
        this.collection = collection;
    }
    private Collection collection;
    public Collection getCollection() {
        return collection;
    }
    private String typeHolder = TYPE_HOLDER_BIG_POSTER;
    private final LayoutInflater layoutInflater;
    public UniversalRecyclerAdapter(LayoutInflater layoutInflater, UtilsFavoriteAndViewFilm utills, Collection collection, @TypeHolder String typeHolder) {
        this.layoutInflater = layoutInflater;
        utilsFavoriteAndViewFilm = utills;
        this.collection = collection;
        if (typeHolder != null) {
            this.typeHolder = typeHolder;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (typeHolder) {
            case TYPE_HOLDER_BIG_POSTER: {
                View view = layoutInflater.inflate(R.layout.rv_item_1, null, false);
                return new ViewHolderBigPoster(view);
            }
            case TYPE_HOLDER_SMALL_POSTER: {
                View view = layoutInflater.inflate(R.layout.rv_item_2, null, false);
                return new ViewHolderSmallPoster(view);
            }
            case TYPE_FULL_WIDTH: {
                View view = layoutInflater.inflate(R.layout.rv_item_3, null, false);
                return new ViewHolderFullWidth(view);
            }
            case TYPE_NULL_DATA: {
                View view = layoutInflater.inflate(R.layout.rv_item_null_data, null, false);
                return new ViewHolderNullData(view);
            }
            default:
                return null;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Handler handlerUniversalAdapter = new Handler( new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                int position = msg.getData().getInt("position");
                boolean isFavorite = msg.getData().getBoolean("favorite");
                boolean isViewed = msg.getData().getBoolean("viewed");

                ListFilmItem item = collection.getItems().get(position);
                holder.itemView.setId(item.getKinopoiskId());
                holder.itemView.setContentDescription(collection.getTitleCollection());

                // Большой постер
                if (holder instanceof ViewHolderBigPoster) {
                    ViewHolderBigPoster holderBigPoster = (ViewHolderBigPoster) holder;

                    if (isFavorite) {
                        holderBigPoster.imageViewSaveToFavoriteBig.setVisibility(View.VISIBLE);
                        holderBigPoster.imageViewSaveToFavoriteBig.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utilsFavoriteAndViewFilm.removeFromFavorite(String.valueOf(collection.getItems().get(holder.getPosition()).getKinopoiskId()), holder.getPosition());
                                holderBigPoster.imageViewSaveToFavoriteBig.setVisibility(View.GONE);
                            }
                        });
                    }

                    if (isViewed) {
                        holderBigPoster.imageViewIsViewedBig.setVisibility(View.VISIBLE);
                        holderBigPoster.imageViewIsViewedBig.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utilsFavoriteAndViewFilm.removeFromViewed(String.valueOf(collection.getItems().get(holder.getPosition()).getKinopoiskId()), holder.getPosition());
                                holderBigPoster.imageViewIsViewedBig.setVisibility(View.GONE);
                            }
                        });
                    }

                    Picasso.get().load(collection.getItems().get(position).getPosterUrlPreview()).placeholder(R.drawable.sad_rounded_square_emoticon).into(holderBigPoster.imageViewPosterBig);
                }

                // Маленький постер
                else if (holder instanceof ViewHolderSmallPoster) {
                    ViewHolderSmallPoster holderSmallPoster = (ViewHolderSmallPoster) holder;

                    if (isFavorite) {
                        holderSmallPoster.imageViewSaveToFavoriteSmall.setVisibility(View.VISIBLE);
                        holderSmallPoster.imageViewSaveToFavoriteSmall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utilsFavoriteAndViewFilm.removeFromFavorite(String.valueOf(collection.getItems().get(holder.getPosition()).getKinopoiskId()), holder.getPosition());
                                holderSmallPoster.imageViewSaveToFavoriteSmall.setVisibility(View.GONE);
                            }
                        });
                    }

                    if (isViewed) {
                        holderSmallPoster.imageViewIsViewedSmall.setVisibility(View.VISIBLE);
                        holderSmallPoster.imageViewIsViewedSmall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utilsFavoriteAndViewFilm.removeFromViewed(String.valueOf(collection.getItems().get(holder.getPosition()).getKinopoiskId()), holder.getPosition());
                                holderSmallPoster.imageViewIsViewedSmall.setVisibility(View.GONE);
                            }
                        });
                    }

                    Picasso.get().load(collection.getItems().get(position).getPosterUrlPreview()).placeholder(R.drawable.sad_rounded_square_emoticon).into(holderSmallPoster.imageViewPosterSmall);
                }

                // Полный размер
                else if (holder instanceof ViewHolderFullWidth) {
                    ViewHolderFullWidth holderFullWidth = (ViewHolderFullWidth) holder;

                    if (isFavorite) {
                        holderFullWidth.imageViewisForeverItem3.setVisibility(View.VISIBLE);
                        holderFullWidth.imageViewisForeverItem3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utilsFavoriteAndViewFilm.removeFromFavorite(String.valueOf(collection.getItems().get(holder.getPosition()).getKinopoiskId()), holder.getPosition());
                                holderFullWidth.imageViewisForeverItem3.setVisibility(View.GONE);
                            }
                        });
                    }

                    if (isViewed) {
                        holderFullWidth.imageViewIsViewedItem3.setVisibility(View.VISIBLE);
                        holderFullWidth.imageViewIsViewedItem3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                utilsFavoriteAndViewFilm.removeFromViewed(String.valueOf(collection.getItems().get(holder.getPosition()).getKinopoiskId()), holder.getPosition());
                                holderFullWidth.imageViewIsViewedItem3.setVisibility(View.GONE);
                            }
                        });
                    }

                    Picasso.get().load(collection.getItems().get(position).getPosterUrlPreview()).placeholder(R.drawable.sad_rounded_square_emoticon).into(holderFullWidth.imageViewPosterFilmItem3);
                    holderFullWidth.textViewTitleFilmItem3.setText(collection.getItems().get(position).getNameRu());

                }

                return false;
            }
        });

        // Пустые данные
        if (holder instanceof ViewHolderNullData) {
            ViewHolderNullData holderNullData = (ViewHolderNullData) holder;
            if (collection != null) {
                holderNullData.textViewResultText.setText("К сожалению по запросу:\n" + collection.getTitleCollection() + "\nНичего не найдено");
            }
            return;
        }

        Thread threadAsyncCreateHolder = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isFavorite = utilsFavoriteAndViewFilm.isFilmInFavorite(String.valueOf(collection.getItems().get(holder.getPosition()).getKinopoiskId()));
                boolean isViewed = utilsFavoriteAndViewFilm.isFilmInViewed(String.valueOf(collection.getItems().get(holder.getPosition()).getKinopoiskId()));
                ListFilmItem item = collection.getItems().get(holder.getPosition());
                Bundle bundle = new Bundle();
                bundle.putInt("position", holder.getPosition());
                bundle.putBoolean("favorite", isFavorite);
                bundle.putBoolean("viewed", isViewed);
                bundle.putSerializable("item", item);

                Message message = new Message();
                message.setData(bundle);
                handlerUniversalAdapter.sendMessage(message);
            }
        });
        threadAsyncCreateHolder.start();


    }

    @Override
    public int getItemCount() {
        if (typeHolder.equals(TYPE_NULL_DATA)) {
            return 1;
        }
        return collection.getItems().size();
    }

    public static class ViewHolderBigPoster extends RecyclerView.ViewHolder {
        public ImageView getImageViewPosterBig() {
            return imageViewPosterBig;
        }

        public ImageView getImageViewSaveToFavoriteBig() {
            return imageViewSaveToFavoriteBig;
        }

        public ImageView getImageViewIsViewedBig() {
            return imageViewIsViewedBig;
        }

        private final ImageView imageViewPosterBig;
        private final ImageView imageViewSaveToFavoriteBig;
        private final ImageView imageViewIsViewedBig;

        public ViewHolderBigPoster(@NonNull View itemView) {
            super(itemView);
            imageViewPosterBig = itemView.findViewById(R.id.imageViewPosterBig);
            imageViewSaveToFavoriteBig = itemView.findViewById(R.id.imageViewSaveToFavoriteBig);
            imageViewIsViewedBig = itemView.findViewById(R.id.imageViewIsViewedBig);
        }

    }

    public static class ViewHolderSmallPoster extends RecyclerView.ViewHolder {
        private final ImageView imageViewPosterSmall;
        private final ImageView imageViewSaveToFavoriteSmall;

        public ImageView getImageViewIsViewedSmall() {
            return imageViewIsViewedSmall;
        }

        public ImageView getImageViewSaveToFavoriteSmall() {
            return imageViewSaveToFavoriteSmall;
        }

        public ImageView getImageViewPosterSmall() {
            return imageViewPosterSmall;
        }

        private final ImageView imageViewIsViewedSmall;

        public ViewHolderSmallPoster(@NonNull View itemView) {
            super(itemView);
            imageViewPosterSmall = itemView.findViewById(R.id.imageViewPosterSmall);
            imageViewSaveToFavoriteSmall = itemView.findViewById(R.id.imageViewSaveToFavoriteSmall);
            imageViewIsViewedSmall = itemView.findViewById(R.id.imageViewIsViewedSmall);
        }
    }

    public static class ViewHolderFullWidth extends RecyclerView.ViewHolder {
        private final ImageView imageViewPosterFilmItem3;   // Постер
        private final TextView textViewTitleFilmItem3;      // Название фильма
        private final ImageView imageViewIsViewedItem3;     // Просмотренный фильм
        private final ImageView imageViewisForeverItem3;    // Добавленный в избранное

        public ViewHolderFullWidth(@NonNull View itemView) {
            super(itemView);
            imageViewPosterFilmItem3 = itemView.findViewById(R.id.imageViewPosterFilmItem3);
            textViewTitleFilmItem3 = itemView.findViewById(R.id.textViewTitleFilmItem3);
            imageViewisForeverItem3 = itemView.findViewById(R.id.imageViewisFavoriteItem3);
            imageViewIsViewedItem3 = itemView.findViewById(R.id.imageViewIsViewedItem3);
        }

        public ImageView getImageViewIsViewedItem3() {
            return imageViewIsViewedItem3;
        }

        public ImageView getImageViewisForeverItem3() {
            return imageViewisForeverItem3;
        }

        public TextView getTextViewTitleFilmItem3() {
            return textViewTitleFilmItem3;
        }

        public ImageView getImageViewPosterFilmItem3() {
            return imageViewPosterFilmItem3;
        }


    }

    public static class ViewHolderNullData extends RecyclerView.ViewHolder {
        private final TextView textViewResultText;
        public ViewHolderNullData(@NonNull View itemView) {
            super(itemView);
            textViewResultText = itemView.findViewById(R.id.textViewResultText);
        }
    }





}
