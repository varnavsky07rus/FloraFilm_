package com.alaka_ala.florafilm.sys.utils;

import android.annotation.SuppressLint;
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
        // Пустые данные
        if (holder instanceof ViewHolderNullData) {
            ViewHolderNullData holderNullData = (ViewHolderNullData) holder;
            if (collection != null) {
                holderNullData.textViewResultText.setText("К сожалению по запросу:\n" + collection.getTitleCollection() + "\nНичего не найдено");
            }
            return;
        }

        holder.itemView.setId(collection.getItems().get(position).getKinopoiskId());
        holder.itemView.setContentDescription(collection.getTitleCollection());

        boolean isFavorite = utilsFavoriteAndViewFilm.isFilmInFavorite(String.valueOf(collection.getItems().get(position).getKinopoiskId()));
        boolean isViewed = utilsFavoriteAndViewFilm.isFilmInViewed(String.valueOf(collection.getItems().get(position).getKinopoiskId()));

        // Большой постер
        if (holder instanceof ViewHolderBigPoster) {
            ViewHolderBigPoster holderBigPoster = (ViewHolderBigPoster) holder;
            Picasso.get().load(collection.getItems().get(position).getPosterUrlPreview()).into(holderBigPoster.imageViewPosterBig);
            if (isFavorite) {
                holderBigPoster.imageViewSaveToFavoriteBig.setVisibility(View.VISIBLE);
            } else {
                holderBigPoster.imageViewSaveToFavoriteBig.setVisibility(View.GONE);
            }
            if (isViewed) {
                holderBigPoster.imageViewIsViewedBig.setVisibility(View.VISIBLE);

            } else {
                holderBigPoster.imageViewIsViewedBig.setVisibility(View.GONE);
            }

            holderBigPoster.imageViewSaveToFavoriteBig.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    utilsFavoriteAndViewFilm.removeFromFavorite(String.valueOf(collection.getItems().get(holder.getPosition()).getKinopoiskId()), holder.getPosition());
                    holderBigPoster.imageViewSaveToFavoriteBig.setVisibility(View.GONE);
                }
            });

            holderBigPoster.imageViewIsViewedBig.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    utilsFavoriteAndViewFilm.removeFromViewed(String.valueOf(collection.getItems().get(holder.getPosition()).getKinopoiskId()), holder.getPosition());
                    holderBigPoster.imageViewIsViewedBig.setVisibility(View.GONE);
                }
            });
        }

        // Маленький постер
        else if (holder instanceof ViewHolderSmallPoster) {
            ViewHolderSmallPoster holderSmallPoster = (ViewHolderSmallPoster) holder;
            Picasso.get().load(collection.getItems().get(position).getPosterUrlPreview()).into(holderSmallPoster.imageViewPosterSmall);
            if (isFavorite) {
                holderSmallPoster.imageViewSaveToFavoriteSmall.setVisibility(View.VISIBLE);
            } else {
                holderSmallPoster.imageViewSaveToFavoriteSmall.setVisibility(View.GONE);
            }
            if (isViewed) {
                holderSmallPoster.imageViewIsViewedSmall.setVisibility(View.VISIBLE);
            } else {
                holderSmallPoster.imageViewIsViewedSmall.setVisibility(View.GONE);

            }

            holderSmallPoster.imageViewSaveToFavoriteSmall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    utilsFavoriteAndViewFilm.removeFromFavorite(String.valueOf(collection.getItems().get(holder.getPosition()).getKinopoiskId()), holder.getPosition());
                    holderSmallPoster.imageViewSaveToFavoriteSmall.setVisibility(View.GONE);
                }
            });

            holderSmallPoster.imageViewIsViewedSmall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    utilsFavoriteAndViewFilm.removeFromViewed(String.valueOf(collection.getItems().get(holder.getPosition()).getKinopoiskId()), holder.getPosition());
                    holderSmallPoster.imageViewIsViewedSmall.setVisibility(View.GONE);
                }
            });
        }

        // Полный размер
        else if (holder instanceof ViewHolderFullWidth) {
            ViewHolderFullWidth holderFullWidth = (ViewHolderFullWidth) holder;
            if (isFavorite) {
                holderFullWidth.imageViewisForeverItem3.setVisibility(View.VISIBLE);
            } else {
                holderFullWidth.imageViewisForeverItem3.setVisibility(View.GONE);
            }
            if (isViewed) {
                holderFullWidth.imageViewIsViewedItem3.setVisibility(View.VISIBLE);
            } else {
                holderFullWidth.imageViewIsViewedItem3.setVisibility(View.GONE);
            }
            holderFullWidth.imageViewisForeverItem3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    utilsFavoriteAndViewFilm.removeFromFavorite(String.valueOf(collection.getItems().get(holder.getPosition()).getKinopoiskId()), holder.getPosition());
                    holderFullWidth.imageViewisForeverItem3.setVisibility(View.GONE);
                }
            });
            holderFullWidth.imageViewIsViewedItem3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    utilsFavoriteAndViewFilm.removeFromViewed(String.valueOf(collection.getItems().get(holder.getPosition()).getKinopoiskId()), holder.getPosition());
                    holderFullWidth.imageViewIsViewedItem3.setVisibility(View.GONE);
                }
            });
            Picasso.get().load(collection.getItems().get(position).getPosterUrlPreview()).into(holderFullWidth.imageViewPosterFilmItem3);
            holderFullWidth.textViewTitleFilmItem3.setText(collection.getItems().get(position).getNameRu());

        }
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
