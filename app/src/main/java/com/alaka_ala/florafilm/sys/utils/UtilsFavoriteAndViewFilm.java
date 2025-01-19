package com.alaka_ala.florafilm.sys.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.alaka_ala.florafilm.sys.kp_api.ListFilmItem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс предназначен для сохранения и получения избранных фильмов из SharedPreferences.
 * а так же отмечает фильм в настройках как просмотренный.
 */
public class UtilsFavoriteAndViewFilm {

    public static final String KEY_PREFS_NAME_FAVORITE = "KEY_FAVORITE_FILMS";
    public static final String FILE_PREFS_NAME_FAVORITE = "FILE_FAVORITE_FILMS";
    public static final String KEY_PREFS_NAME_VIEWED = "KEY_VIEWED_FILMS";
    public static final String FILE_PREFS_NAME_VIEWED = "FILE_VIEWED_FILMS";

    public Context getContext() {
        return context;
    }

    private final Context context;
    private Map<String, ListFilmItem> favoriteFilms = new HashMap<>();  // Список избранных фильмов
    private Map<String, ListFilmItem> viewedFilms = new HashMap<>();         // Список просмотренных фильмов

    public UtilsFavoriteAndViewFilm(Context context) {
        this.context = context;
        if (context == null) throw new NullPointerException("Context is null");
        favoriteFilms = getFavoriteFilms();
        viewedFilms = getViewedFilms();
    }
    /**
     * Добавляет фильм в закладках
     */
    public void addToFavorite(String kinopoisk_id, ListFilmItem listFilmItem, int positionRecyclerItem) {
        favoriteFilms.put(kinopoisk_id, listFilmItem);
        serializeAndSaveToPreferences(favoriteFilms, FILE_PREFS_NAME_FAVORITE, KEY_PREFS_NAME_FAVORITE);
        if (methodListener != null) {
            methodListener.onAddToFavorite(positionRecyclerItem);
        }
    }
    /**
     * Удаляет фильм из закладок
     */
    public void removeFromFavorite(String kinopoisk_id, int positionRecyclerItem) {
        favoriteFilms.remove(kinopoisk_id);
        serializeAndSaveToPreferences(favoriteFilms, FILE_PREFS_NAME_FAVORITE, KEY_PREFS_NAME_FAVORITE);
        if (methodListener != null) {
            methodListener.onRemoveFromFavorite(positionRecyclerItem);
        }
    }
    /** Очищает список избранных фильмов */
    public void clearFavoriteFilms(int positionRecyclerItem) {
        favoriteFilms.clear();
        serializeAndSaveToPreferences(favoriteFilms, FILE_PREFS_NAME_FAVORITE, KEY_PREFS_NAME_FAVORITE);
        if (methodListener != null) {
            methodListener.onRemoveFromFavorite(positionRecyclerItem);
        }
    }
    /**Получение списка избранных фильмов*/
    public Map<String, ListFilmItem> getFavoriteFilmsMap() {
        return favoriteFilms;
    }
    /**Проверка фильма в избранных*/
    public boolean isFilmInFavorite(String kinopoisk_id) {
        return favoriteFilms.containsKey(kinopoisk_id);
    }



    /**Добавляет фильм в просмотренные*/
    public void addToViewed(String kinopoisk_id, ListFilmItem listFilmItem, int positionRecyclerItem) {
        viewedFilms.put(kinopoisk_id, listFilmItem);
        serializeAndSaveToPreferences(viewedFilms, FILE_PREFS_NAME_VIEWED, KEY_PREFS_NAME_VIEWED);
        if (methodListener != null) {
            methodListener.onAddToViewed(positionRecyclerItem);
        }
    }
    /** Удаление фильма из просморенных */
    public void removeFromViewed(String kinopoisk_id, int positionRecyclerItem) {
        viewedFilms.remove(kinopoisk_id);
        serializeAndSaveToPreferences(viewedFilms, FILE_PREFS_NAME_VIEWED, KEY_PREFS_NAME_VIEWED);
        if (methodListener != null) {
            methodListener.onRemoveFromViewed(positionRecyclerItem);
        }
    }
    /**Очистка списка просмотренных фильмов*/
    public void clearViewedFilms(int positionRecyclerItem) {
        viewedFilms.clear();
        serializeAndSaveToPreferences(viewedFilms, FILE_PREFS_NAME_VIEWED, KEY_PREFS_NAME_VIEWED);
        if (methodListener != null) {
            methodListener.onRemoveFromViewed(positionRecyclerItem);
        }
    }
    /**Получение списка просмотренных фильмов*/
    public Map<String, ListFilmItem> getViewedFilmsMap() {
        return viewedFilms;
    }
    /**Проверка фильма в просмотренных*/
    public boolean isFilmInViewed(String kinopoisk_id) {
        return viewedFilms.containsKey(kinopoisk_id);
    }



    // Получение SharedPreferences
    private SharedPreferences getSharedPreferences(String fileKey) {
        return context.getSharedPreferences(fileKey, Context.MODE_PRIVATE);
    }
    // Сохранение объекта в SharedPreferences
    private void serializeAndSaveToPreferences(Object object, String fileKey, String objectKey) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();

            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);

            SharedPreferences sharedPreferences = getSharedPreferences(fileKey);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(objectKey, encodedString);
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Получение объекта из SharedPreferences
    private Object deserializeAndLoadFromPreferences(String fileKey, String objectKey) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(fileKey);
            String encodedString = sharedPreferences.getString(objectKey, null);
            if (encodedString != null) {
                byte[] byteArray = Base64.decode(encodedString, Base64.DEFAULT);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                return objectInputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
    // Получение списка просмотренных фильмов
    private Map<String, ListFilmItem> getViewedFilms() {
        Object object = deserializeAndLoadFromPreferences(FILE_PREFS_NAME_VIEWED, KEY_PREFS_NAME_VIEWED);
        if (object instanceof Map) {
            return (Map<String, ListFilmItem>) object;
        }
        return new HashMap<>();
    }
    // Получение списка избранных фильмов
    private Map<String, ListFilmItem> getFavoriteFilms() {
        Object object = deserializeAndLoadFromPreferences(FILE_PREFS_NAME_FAVORITE, KEY_PREFS_NAME_FAVORITE);
        if (object instanceof Map) {
            return (Map<String, ListFilmItem>) object;
        }
        return new HashMap<>();
    }

    private MethodListener methodListener;

    public void setMethodListener(MethodListener ml) {
        if (ml != null) {
            methodListener = ml;
        }
    }

    public interface MethodListener {
        default void onAddToViewed(int positionRecyclerItem){}
        default void onAddToFavorite(int positionRecyclerItem){}
        default void onRemoveFromFavorite(int positionRecyclerItem){}
        default void onRemoveFromViewed(int positionRecyclerItem){}
    }

    

}
