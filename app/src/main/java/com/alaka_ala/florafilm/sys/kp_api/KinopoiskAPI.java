package com.alaka_ala.florafilm.sys.kp_api;

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
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_COMICS_THEME;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_FAMILY;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_NEWS_MEDIA;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_POPULAR_ALL;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_POPULAR_MOVIES;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_SEARCH;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_SIMILAR;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_TOP_250_MOVIES;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_TOP_250_TV_SHOWS;
import static com.alaka_ala.florafilm.sys.kp_api.Collection.TITLE_VAMPIRE_THEME;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Все методы выполняются асинхронно, но результат возвращает в основном потоке
 */
public class KinopoiskAPI {


    private Map<String, String> headers;
    private final String api_key;


    public KinopoiskAPI(String api_key) {
        this.api_key = api_key;
    }

    public interface RequestCallbackListVieos {
        void onSuccessVideo(ArrayList<FilmTrailer> filmTrailers);

        void onFailureVideo(IOException e);

        /**
         * Метод выполнится после завершения всех подключений...
         * Т.е. если вызвав к примеру несколько запросов getList() то каждый из них вызывает finish();
         * однако данный метод ограничивает вызов каждый раз этого метода ограничив его до одного.
         * На каждый вызов есть отдельный метод, onSuccess().
         * Данный метод будет вызываться столько раз, сколько будет вызвано новых запросов.
         * Так же будет вызываться onFailure() на каждый новый запрос
         */
        void finishVideo();
    }

    public interface RequesCallbackNewsMedia {
        void onSuccessNews(ArrayList<NewsMedia> newsMediaList);

        void onFailureNews(IOException e);

        /**
         * Метод выполнится после завершения всех подключений...
         * Т.е. если вызвав к примеру несколько запросов getList() то каждый из них вызывает finish();
         * однако данный метод ограничивает вызов каждый раз этого метода ограничив его до одного.
         * На каждый вызов есть отдельный метод, onSuccess().
         * Данный метод будет вызываться столько раз, сколько будет вызвано новых запросов.
         * Так же будет вызываться onFailure() на каждый новый запрос
         */
        void finishNews();
    }

    public interface RequestCallbackCollection {
        void onSuccess(Collection collection);

        void onFailure(IOException e);

        /**
         * Метод выполнится после завершения всех подключений...
         * Т.е. если вызвав к примеру несколько запросов getList() то каждый из них вызывает finish();
         * однако данный метод ограничивает вызов каждый раз этого метода ограничив его до одного.
         * На каждый вызов есть отдельный метод, onSuccess().
         * Данный метод будет вызываться столько раз, сколько будет вызвано новых запросов.
         * Так же будет вызываться onFailure() на каждый новый запрос
         */
        void finish();
    }

    public interface RequestCallbackInformationItem {
        void onSuccessInfoItem(ItemFilmInfo itemFilmInfo);

        void onFailureInfoItem(IOException e);

        /**
         * Метод выполнится после завершения всех подключений...
         * Т.е. если вызвав к примеру несколько запросов getList() то каждый из них вызывает finish();
         * однако данный метод ограничивает вызов каждый раз этого метода ограничив его до одного.
         * На каждый вызов есть отдельный метод, onSuccess().
         * Данный метод будет вызываться столько раз, сколько будет вызвано новых запросов.
         * Так же будет вызываться onFailure() на каждый новый запрос
         */
        void finishInfoItem();
    }

    public interface RequestCallbackStaffList {
        void onSuccessStaffList(ArrayList<ListStaffItem> listStaffItem);

        void onFailureStaffList(IOException e);

        /**
         * Метод выполнится после завершения всех подключений...
         * Т.е. если вызвав к примеру несколько запросов getList() то каждый из них вызывает finish();
         * однако данный метод ограничивает вызов каждый раз этого метода ограничив его до одного.
         * На каждый вызов есть отдельный метод, onSuccess().
         * Данный метод будет вызываться столько раз, сколько будет вызвано новых запросов.
         * Так же будет вызываться onFailure() на каждый новый запрос
         */
        void finishStafList();
    }

    public interface RequestCallbackInformationStaff {
        void onSuccessInfoStaff(StaffInfo staffInfo);

        void onFailureInfoStaff(IOException e);

        /**
         * Метод выполнится после завершения всех подключений...
         * Т.е. если вызвав к примеру несколько запросов getList() то каждый из них вызывает finish();
         * однако данный метод ограничивает вызов каждый раз этого метода ограничив его до одного.
         * На каждый вызов есть отдельный метод, onSuccess().
         * Данный метод будет вызываться столько раз, сколько будет вызвано новых запросов.
         * Так же будет вызываться onFailure() на каждый новый запрос
         */
        void finishInfoStaff();
    }

    private interface ConntectCallback {
        void onSuccess(String responseJson);

        void onFailure(IOException e);

        void finish();
    }

    // Общее кол-во подключений
    private int previousCountConnections = 0;
    // Предыдущее кол-во подключений
    private int requestId = 0;
    private int countConnections = 0;

    /**
     * Метод для выполнения запроса к серверу.
     * Выполняется асинхронно, но результат возвращает в основном потоке
     */
    private void connect(String url, ConntectCallback callback) {
        countConnections++;
        previousCountConnections = countConnections;
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                countConnections--;
                int codeResponse = msg.getData().getInt("codeResponse", 0);
                String error = msg.getData().getString("error", "");
                int requestIdl = msg.getData().getInt("requestId", 0);
                String response = msg.getData().getString("response", "");
                boolean ok = msg.getData().getBoolean("ok", false);

                if (ok && codeResponse == 200) {
                    callback.onSuccess(response);
                } else {
                    callback.onFailure(new IOException("Код ответа: " + codeResponse + " Ошибка: " + response));
                }

                requestId = 0;
                callback.finish();

                return false;
            }
        });

        // Добавление заголовков в запрос
        headers = new HashMap<>();
        headers.put("X-API-KEY", api_key);
        //headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36");
        headers.put("Content-Type", "application/json");
        // Создание запроса
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        for (String headerKey : headers.keySet()) {
            requestBuilder.addHeader(headerKey, headers.get(headerKey));
        }

        Request request = requestBuilder.build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bundle bundle = new Bundle();
                bundle.putString("error", e.getMessage());
                bundle.putInt("codeResponse", 0);
                bundle.putInt("requestId", ++requestId);
                Message msg = new Message();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Bundle bundle = new Bundle();
                ResponseBody responseBody = response.body();
                bundle.putBoolean("ok", response.isSuccessful());
                bundle.putInt("codeResponse", response.code());
                bundle.putString("error", response.message());
                if (responseBody != null) {
                    bundle.putString("response", responseBody.string());
                }
                bundle.putInt("requestId", ++requestId);
                Message msg = new Message();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        });

    }


    /**
     * Получение всех популярных фильмов и сериалов
     */
    public void getListTopPopularAll(int page, RequestCallbackCollection rcc) {
        String base_url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/collections?type=TOP_POPULAR_ALL&page=" + page;
        connect(base_url, new ConntectCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (!response.isEmpty()) {
                        if (JsonParser.parseString(response).isJsonObject()) {
                            Collection collection = createCollectionClass(TITLE_POPULAR_ALL, response);
                            rcc.onSuccess(collection);
                        }
                    } else {
                        onFailure(new IOException("Пустой ответ!"));
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                rcc.onFailure(e);
            }

            @Override
            public void finish() {
                rcc.finish();
            }
        });
    }

    /**
     * Получение всех популярных фильмов (только фильмов)
     */
    public void getListTopPopularMovies(int page, RequestCallbackCollection rcc) {
        String base_url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/collections?type=TOP_POPULAR_MOVIES&page=" + page;
        connect(base_url, new ConntectCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (!response.isEmpty()) {
                        if (JsonParser.parseString(response).isJsonObject()) {
                            Collection collection = createCollectionClass(TITLE_POPULAR_MOVIES, response);
                            rcc.onSuccess(collection);
                        }
                    } else {
                        onFailure(new IOException("Пустой ответ!"));
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                rcc.onFailure(e);
            }

            @Override
            public void finish() {
                rcc.finish();
            }
        });
    }

    /**
     * Получение топ 250 сериалов
     */
    public void getListTop250TVShows(int page, RequestCallbackCollection rcc) {
        String base_url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/collections?type=TOP_250_TV_SHOWS&page=" + page;
        connect(base_url, new ConntectCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (!response.isEmpty()) {
                        if (JsonParser.parseString(response).isJsonObject()) {
                            Collection collection = createCollectionClass(TITLE_TOP_250_TV_SHOWS, response);
                            rcc.onSuccess(collection);
                        }
                    } else {
                        onFailure(new IOException("Пустой ответ!"));
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                rcc.onFailure(e);
            }

            @Override
            public void finish() {
                rcc.finish();
            }
        });
    }

    /**
     * Получение топ 250 фильмов
     */
    public void getListTop250Movies(int page, RequestCallbackCollection rcc) {
        String base_url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/collections?type=TOP_250_MOVIES&page=" + page;
        connect(base_url, new ConntectCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (!response.isEmpty()) {
                        if (JsonParser.parseString(response).isJsonObject()) {
                            Collection collection = createCollectionClass(TITLE_TOP_250_MOVIES, response);
                            rcc.onSuccess(collection);
                        }
                    } else {
                        onFailure(new IOException("Пустой ответ!"));
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                rcc.onFailure(e);
            }

            @Override
            public void finish() {
                rcc.finish();
            }
        });
    }

    /**
     * Получение списка фильмов/сериалов на тему: Вампиры
     */
    @Deprecated
    public void getListVampireTheme(int page, RequestCallbackCollection rcc) {
        String base_url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/collections?type=VAMPIRE_THEME&page=" + page;
        connect(base_url, new ConntectCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (!response.isEmpty()) {
                        if (JsonParser.parseString(response).isJsonObject()) {
                            Collection collection = createCollectionClass(TITLE_VAMPIRE_THEME, response);
                            rcc.onSuccess(collection);
                        }
                    } else {
                        onFailure(new IOException("Пустой ответ!"));
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                rcc.onFailure(e);
            }

            @Override
            public void finish() {
                rcc.finish();
            }
        });
    }

    /**
     * Получение списка фильмов/сериалов на тему: Комиксы
     */
    public void getListComicsTheme(int page, RequestCallbackCollection rcc) {
        String base_url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/collections?type=COMICS_THEME&page=" + page;
        connect(base_url, new ConntectCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (!response.isEmpty()) {
                        if (JsonParser.parseString(response).isJsonObject()) {
                            Collection collection = createCollectionClass(TITLE_COMICS_THEME, response);
                            rcc.onSuccess(collection);
                        }
                    } else {
                        onFailure(new IOException("Пустой ответ!"));
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                rcc.onFailure(e);
            }

            @Override
            public void finish() {
                rcc.finish();
            }
        });
    }

    /**
     * Получение списка фильмов/сериалов на тему: Семейные
     */
    @Deprecated
    public void getListFamily(int page, RequestCallbackCollection rcc) {
        String base_url = "https://kinopoiskapiunofficial.tech/api/v2.2/films?genres=19&order=RATING&type=FILM&ratingFrom=0&ratingTo=10&yearFrom=1000&yearTo=3000&page=" + page;
        connect(base_url, new ConntectCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (!response.isEmpty()) {
                        if (JsonParser.parseString(response).isJsonObject()) {
                            Collection collection = createCollectionClass(TITLE_FAMILY, response);
                            rcc.onSuccess(collection);
                        }
                    } else {
                        onFailure(new IOException("Пустой ответ!"));
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                rcc.onFailure(e);
            }

            @Override
            public void finish() {
                rcc.finish();
            }
        });
    }

    /**
     * Получение списка похожих фильмов по kinopoisk_id
     */
    public void getListSimilarFilms(int kinopoisk_id, int page, RequestCallbackCollection rcc) {
        String base_url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/" + kinopoisk_id + "/similars" /*+ page;*/;
        connect(base_url, new ConntectCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (!response.isEmpty()) {
                        if (JsonParser.parseString(response).isJsonObject()) {
                            Collection collection = createCollectionClass(TITLE_SIMILAR, response);
                            rcc.onSuccess(collection);
                        }
                    } else {
                        onFailure(new IOException("Пустой ответ!"));
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                rcc.onFailure(e);
            }

            @Override
            public void finish() {
                rcc.finish();
            }
        });
    }


    /**
     * Получение списка фильмов/сериалов по названию
     */
    public void getListSearch(String query, int page, RequestCallbackCollection rcc) {
        String query_encode = URLEncoder.encode(query);
        String base_url = "https://kinopoiskapiunofficial.tech/api/v2.1/films/search-by-keyword?keyword=" + query_encode + "&page=" + page;
        connect(base_url, new ConntectCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (!response.isEmpty()) {
                        if (JsonParser.parseString(response).isJsonObject()) {
                            Collection collection = createCollectionClass(TITLE_SEARCH, response);
                            rcc.onSuccess(collection);
                        }
                    } else {
                        onFailure(new IOException("Пустой ответ!"));
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                rcc.onFailure(e);
            }

            @Override
            public void finish() {
                rcc.finish();
            }
        });
    }

    /**
     * Получение списка фильмов/сериалов по жанру
     */
    public void getListGenre(String genreTitle, int page, RequestCallbackCollection rcc) {
        int genre = 0;
        switch (genreTitle) {
            case TITLE_CATEGORY_GENRE_19:
                // Семейные
                genre = 19;
                break;
            case TITLE_CATEGORY_GENRE_18:
                // Мультфильмы
                genre = 18;
                break;
            case TITLE_CATEGORY_GENRE_17:
                // Ужасы
                genre = 17;
                break;
            case TITLE_CATEGORY_GENRE_14:
                // Военные
                genre = 14;
                break;
            case TITLE_CATEGORY_GENRE_13:
                // Комедии
                genre = 13;
                break;
            case TITLE_CATEGORY_GENRE_12:
                // Фэнтэзи
                genre = 12;
                break;
            case TITLE_CATEGORY_GENRE_11:
                // Боеввик
                genre = 11;
                break;
            case TITLE_CATEGORY_GENRE_10:
                // Вестерн
                genre = 10;
                break;
            case TITLE_CATEGORY_GENRE_7:
                // Приключения
                genre = 7;
                break;
            case TITLE_CATEGORY_GENRE_24:
                // Аниме
                genre = 24;
                break;
        }
        String base_url = "https://kinopoiskapiunofficial.tech/api/v2.2/films?genres=" + genre + "&order=RATING&type=ALL&ratingFrom=0&ratingTo=10&yearFrom=1000&yearTo=3000&page=" + page;
        connect(base_url, new ConntectCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (!response.isEmpty()) {
                        if (JsonParser.parseString(response).isJsonObject()) {
                            Collection collection = createCollectionClass(genreTitle, response);
                            rcc.onSuccess(collection);
                        }
                    } else {
                        onFailure(new IOException("Пустой ответ!"));
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                rcc.onFailure(e);
            }

            @Override
            public void finish() {
                rcc.finish();
            }
        });
    }


    /**
     * Получение информации о фильме по его kinopoisk_id
     */
    public void getInforamationItem(int kinopoisk_id, RequestCallbackInformationItem rcii) {
        String base_url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/" + kinopoisk_id;
        connect(base_url, new ConntectCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (!response.isEmpty()) {
                        if (JsonParser.parseString(response).isJsonObject()) {
                            ItemFilmInfo itemFilmInfo = createItemInfoClass(response);
                            rcii.onSuccessInfoItem(itemFilmInfo);
                        }
                    } else {
                        onFailure(new IOException("Пустой ответ!"));
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                rcii.onFailureInfoItem(e);
            }

            @Override
            public void finish() {
                rcii.finishInfoItem();
            }
        });
    }


    /**
     * Получение списка актёров/актрис по kinopoisk_id
     */
    public void getListStaff(int kinopoisk_id, RequestCallbackStaffList rca) {
        String base_url = "https://kinopoiskapiunofficial.tech/api/v1/staff?filmId=" + kinopoisk_id;
        connect(base_url, new ConntectCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (!response.isEmpty()) {
                        if (JsonParser.parseString(response).isJsonArray()) {
                            ArrayList<ListStaffItem> listStaffItem = createListStaffClass(response);
                            rca.onSuccessStaffList(listStaffItem);
                        }
                    } else {
                        onFailure(new IOException("Пустой ответ!"));
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                rca.onFailureStaffList(e);
            }

            @Override
            public void finish() {
                rca.finishStafList();
            }
        });
    }

    public void getListVieos(int kinopoisk_id, RequestCallbackListVieos rclv) {
        String base_url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/" + kinopoisk_id + "/videos";
        connect(base_url, new ConntectCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (!response.isEmpty()) {
                        if (JsonParser.parseString(response).isJsonObject()) {
                            ArrayList<FilmTrailer> filmTrailers = createListVieosClass(response);
                            rclv.onSuccessVideo(filmTrailers);
                        }
                    } else {
                        onFailure(new IOException("Пустой ответ!"));
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                rclv.onFailureVideo(e);
            }

            @Override
            public void finish() {
                rclv.finishVideo();
            }
        });
    }

    private ArrayList<FilmTrailer> createListVieosClass(String response) throws JSONException {
        ArrayList<FilmTrailer> filmTrailers = new ArrayList<>();
        if (response.isEmpty()) return filmTrailers;
        JSONObject jsonObjectList = new JSONObject(response);
        if (jsonObjectList.has("items")) {
            JSONArray items = jsonObjectList.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String url = "";
                String name = "";
                String site = "";
                if (item.has("url")) {
                    url = item.getString("url");
                }
                if (item.has("name")) {
                    name = item.getString("name");
                }
                if (item.has("site")) {
                    site = item.getString("site");
                }
                filmTrailers.add(new FilmTrailer.Builder().setUrl(url).setName(name).setSite(site).build());
            }
        }

        return filmTrailers;
    }

    /**
     * Получение подробной информации об актёре/актрисе по staffId
     */
    public void getInformationStaff(int staffId, RequestCallbackInformationStaff rcis) {
        String base_url = "https://kinopoiskapiunofficial.tech/api/v1/staff/" + staffId;
        connect(base_url, new ConntectCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (!response.isEmpty()) {
                        if (JsonParser.parseString(response).isJsonObject()) {
                            StaffInfo staffInfo = createStaffInfoClass(response);
                            rcis.onSuccessInfoStaff(staffInfo);
                        }
                    } else {
                        onFailure(new IOException("Пустой ответ!"));
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                rcis.onFailureInfoStaff(e);
            }

            @Override
            public void finish() {
                rcis.finishInfoStaff();
            }
        });
    }

    public void getListNewsMedia(int page, RequesCallbackNewsMedia rcnm) {
        String base_url = "https://kinopoiskapiunofficial.tech/api/v1/media_posts?page=" + page;
        connect(base_url, new ConntectCallback() {
            @Override
            public void onSuccess(String responseJson) {
                try {
                    ArrayList<NewsMedia> newsMediaList = createNewsDataClass(responseJson);
                    rcnm.onSuccessNews(newsMediaList);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                rcnm.onFailureNews(e);
            }

            @Override
            public void finish() {
                rcnm.finishNews();
            }
        });
    }

    private ArrayList<NewsMedia> createNewsDataClass(String responseJson) throws JSONException {
        ArrayList<NewsMedia> newsMediaList = new ArrayList<>();
        int kinopoiskId = 0;
        String imageUrl = "";
        String title = "";
        String description = "";
        String urlPost = "";
        String publishedAt = "";
        if (responseJson.isEmpty()) return new ArrayList<>();
        JSONObject jsonObject = new JSONObject(responseJson);
        JSONArray items = jsonObject.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            if (item.has("kinopoiskId")) {
                kinopoiskId = item.getInt("kinopoiskId");
            }
            if (item.has("imageUrl")) {
                imageUrl = item.getString("imageUrl");
            }
            if (item.has("title")) {
                title = item.getString("title");
            }
            if (item.has("description")) {
                description = item.getString("description");
            }
            if (item.has("url")) {
                urlPost = item.getString("url");
            }
            if (item.has("publishedAt")) {
                publishedAt = item.getString("publishedAt");
            }
            newsMediaList.add(new NewsMedia(TITLE_NEWS_MEDIA, i, kinopoiskId, imageUrl, title, description, urlPost, publishedAt));
        }

        return newsMediaList;
    }

    private StaffInfo createStaffInfoClass(String response) throws JSONException {
        JSONObject jsonStaff = new JSONObject(response);
        int personId = 0;
        String webUrl = "";
        String nameRu = "";
        String nameEn = "";
        String sex = "";
        String posterUrl = "";
        int growth = 0;
        String birthday = "";
        String death = "";
        int age = 0;
        String birthplace = "";
        String deathplace = "";
        ArrayList<StaffSpouseItem> spouses = new ArrayList<>();
        int hasAwards = 0;
        String profession = "";
        ArrayList<String> facts = new ArrayList<>();
        ArrayList<StaffFilmsItem> films = new ArrayList<>();

        if (jsonStaff.has("personId")) {
            personId = jsonStaff.getInt("personId");
        }

        if (jsonStaff.has("webUrl")) {
            webUrl = jsonStaff.getString("webUrl");
        }

        if (jsonStaff.has("nameRu")) {
            nameRu = jsonStaff.getString("nameRu");
        }

        if (jsonStaff.has("nameEn")) {
            nameEn = jsonStaff.getString("nameEn");
        }

        if (jsonStaff.has("sex")) {
            sex = jsonStaff.getString("sex");
        }

        if (jsonStaff.has("posterUrl")) {
            posterUrl = jsonStaff.getString("posterUrl");
        }

        if (jsonStaff.has("growth")) {
            growth = jsonStaff.getInt("growth");
        }

        if (jsonStaff.has("birthday")) {
            birthday = jsonStaff.getString("birthday");
        }

        if (jsonStaff.has("death")) {
            death = jsonStaff.getString("death");
        }

        if (jsonStaff.has("age")) {
            age = jsonStaff.getInt("age");
        }

        if (jsonStaff.has("birthplace")) {
            birthplace = jsonStaff.getString("birthplace");
        }

        if (jsonStaff.has("deathplace")) {
            deathplace = jsonStaff.getString("deathplace");
        }

        if (jsonStaff.has("spouses")) {
            JSONArray spousesArray = jsonStaff.getJSONArray("spouses");
            for (int i = 0; i < spousesArray.length(); i++) {
                JSONObject spouse = spousesArray.getJSONObject(i);
                int personIdSpouse = 0;
                String nameSpouse = "";
                boolean divorcedSpouse = false;
                String divorcedReasonSpouse = "";
                String sexSpouse = "";
                int children = 0;
                String webUrlSpouse = "";
                String relationSpouse = "";

                if (spouse.has("personId")) {
                    personIdSpouse = spouse.getInt("personId");
                }
                if (spouse.has("name")) {
                    nameSpouse = spouse.getString("name");
                }
                if (spouse.has("divorced")) {
                    divorcedSpouse = spouse.getBoolean("divorced");
                }
                if (spouse.has("reason")) {
                    divorcedReasonSpouse = spouse.getString("reason");
                }
                if (spouse.has("sex")) {
                    sexSpouse = spouse.getString("sex");
                }
                if (spouse.has("children")) {
                    children = spouse.getInt("children");
                }
                if (spouse.has("webUrl")) {
                    webUrlSpouse = spouse.getString("webUrl");
                }
                if (spouse.has("relation")) {
                    relationSpouse = spouse.getString("relation");
                }
                StaffSpouseItem staffSpouseItem = new StaffSpouseItem(String.valueOf(personIdSpouse), webUrlSpouse, nameSpouse, sexSpouse);
                spouses.add(staffSpouseItem);
            }
        }

        if (jsonStaff.has("hasAwards")) {
            hasAwards = jsonStaff.getInt("hasAwards");
        }

        if (jsonStaff.has("profession")) {
            profession = jsonStaff.getString("profession");
        }

        if (jsonStaff.has("facts")) {
            JSONArray factsArray = jsonStaff.getJSONArray("facts");
            for (int i = 0; i < factsArray.length(); i++) {
                facts.add(factsArray.getString(i));
            }
        }

        if (jsonStaff.has("films")) {
            JSONArray filmsArray = jsonStaff.getJSONArray("films");
            for (int i = 0; i < filmsArray.length(); i++) {
                JSONObject film = filmsArray.getJSONObject(i);
                int filmIdI = 0;
                String nameRuI = "";
                String nameEnI = "";
                int ratingI = 0;
                boolean generalI = false;
                String descriptionI = "";
                String professionKeyI = "";

                if (film.has("filmId")) {
                    if (film.get("filmId") instanceof Integer) {
                        filmIdI = film.getInt("filmId");
                    }
                }
                if (film.has("nameRu")) {
                    nameRuI = film.getString("nameRu");
                }
                if (film.has("nameEn")) {
                    nameEnI = film.getString("nameEn");
                }
                if (film.has("rating")) {
                    if (film.get("rating") instanceof Integer) {
                        filmIdI = film.getInt("rating");
                    }
                }
                if (film.has("general")) {
                    if (film.get("general") instanceof Boolean) {
                        generalI = film.getBoolean("general");
                    }
                }
                if (film.has("description")) {
                    descriptionI = film.getString("description");
                }
                if (film.has("professionKey")) {
                    professionKeyI = film.getString("professionKey");
                }

                StaffFilmsItem staffFilmsItem = new StaffFilmsItem(filmIdI, nameRuI, nameEnI, ratingI, generalI, descriptionI, professionKeyI);
                films.add(staffFilmsItem);
            }
        }


        return new StaffInfo(personId, webUrl, nameRu, nameEn, sex, posterUrl, growth, birthday, death, age, birthplace, deathplace, spouses, hasAwards, profession, facts, films);
    }

    private ArrayList<ListStaffItem> createListStaffClass(String response) throws JSONException {
        ArrayList<ListStaffItem> list = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(response);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int staffId = 0;
            String nameRu = "";
            String nameEn = "";
            String description = "";
            String posterUrl = "";
            String professionText = "";
            String professionKey = "";
            if (jsonObject.has("staffId")) {
                staffId = jsonObject.getInt("staffId");
            }
            if (jsonObject.has("nameRu")) {
                nameRu = jsonObject.getString("nameRu");
            }
            if (jsonObject.has("nameEn")) {
                nameEn = jsonObject.getString("nameEn");
            }
            if (jsonObject.has("description")) {
                description = jsonObject.getString("description");
            }
            if (jsonObject.has("posterUrl")) {
                posterUrl = jsonObject.getString("posterUrl");
            }
            if (jsonObject.has("professionText")) {
                professionText = jsonObject.getString("professionText");
            }
            if (jsonObject.has("professionKey")) {
                professionKey = jsonObject.getString("professionKey");
            }
            list.add(new ListStaffItem(staffId, nameRu, nameEn, description, posterUrl, professionText, professionKey));
        }
        return list;
    }

    private Collection createCollectionClass(String titleCollection, String json) throws JSONException {
        // Создание JSON Объекта на основе ответа от сервера
        JSONObject jsonCollection = new JSONObject(json);
        // преобразование JSON Объекта в новый объект класса Collection
        String total = "0";
        String totalPages = "0";
        ArrayList<ListFilmItem> items = new ArrayList<>();


        if (jsonCollection.has("total")) {
            total = jsonCollection.getString("total");
        }
        if (jsonCollection.has("searchFilmsCountResult")) {
            total = jsonCollection.getString("searchFilmsCountResult");
        }
        // Если total равен 0 то значит фильмов нет и нет смысла дальше парсить
        if (total.equals("0")) {
            return new Collection(titleCollection, total, totalPages, items);
        }
        if (jsonCollection.has("totalPages")) {
            totalPages = jsonCollection.getString("totalPages");
        }
        if (jsonCollection.has("pagesCount")) {
            totalPages = jsonCollection.getString("pagesCount");
        }

        if (jsonCollection.has("items")) {
            JSONArray itemsArray = jsonCollection.getJSONArray("items");
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject item = itemsArray.getJSONObject(i);

                // парсинг основной информации
                int kinopoiskId = 0;
                int imdbId = 0;
                double ratingKinopoisk = 0;
                int year = 0;
                String nameRu = "null",
                        nameEn = "null",
                        nameOriginal = "null",
                        ratingImdb = "null",
                        type = "null",
                        posterUrl = "null",
                        posterUrlPreview = "null",
                        coverUrl = "null",
                        logoUrl = "null",
                        description = "null",
                        ratingAgeLimits = "null";

                if (item.has("kinopoiskId")) {
                    if (item.get("kinopoiskId") instanceof Integer) {
                        kinopoiskId = item.getInt("kinopoiskId");
                    }
                }
                if (item.has("filmId")) {
                    if (item.get("filmId") instanceof Integer) {
                        kinopoiskId = item.getInt("filmId");
                    }
                }
                if (item.has("imdbId")) {
                    if (item.get("imdbId") instanceof Integer) {
                        imdbId = item.getInt("imdbId");
                    }
                }
                if (item.has("nameRu")) {
                    nameRu = item.getString("nameRu");
                    if (nameRu.equals("null") && item.has("nameEn")) {
                        nameRu = item.getString("nameEn");
                    }
                    if (nameRu.equals("null") && item.has("nameOriginal")) {
                        nameRu = item.getString("nameOriginal");
                    }
                }
                if (item.has("nameEn")) {
                    nameEn = item.getString("nameEn");
                    if (nameEn.equals("null") && item.has("nameRu")) {
                        nameEn = item.getString("nameRu");
                    }
                    if (nameEn.equals("null") && item.has("nameOriginal")) {
                        nameEn = item.getString("nameOriginal");
                    }
                }
                if (item.has("nameOriginal")) {
                    nameOriginal = item.getString("nameOriginal");
                }
                if (item.has("ratingKinopoisk")) {
                    if (item.get("ratingKinopoisk") instanceof Double) {
                        ratingKinopoisk = item.getDouble("ratingKinopoisk");
                    }
                }
                if (item.has("ratingImdb")) {
                    ratingImdb = item.getString("ratingImdb");
                }
                if (item.has("year")) {
                    if (item.get("year") instanceof Integer) {
                        year = item.getInt("year");
                    }
                }
                if (item.has("type")) {
                    type = item.getString("type");
                }
                if (item.has("posterUrl")) {
                    posterUrl = item.getString("posterUrl");
                }
                if (item.has("posterUrlPreview")) {
                    posterUrlPreview = item.getString("posterUrlPreview");
                }
                if (item.has("coverUrl")) {
                    coverUrl = item.getString("coverUrl");
                }
                if (item.has("logoUrl")) {
                    logoUrl = item.getString("logoUrl");
                }
                if (item.has("description")) {
                    description = item.getString("description");
                }
                if (item.has("ratingAgeLimits")) {
                    ratingAgeLimits = item.getString("ratingAgeLimits");
                }

                // парсинг стран
                ArrayList<Country> countries = new ArrayList<>();
                JSONArray jsonArrayCountry = new JSONArray();
                if (item.has("countries")) {
                    jsonArrayCountry = item.getJSONArray("countries");
                }
                for (int j = 0; j < jsonArrayCountry.length(); j++) {
                    String country = jsonArrayCountry.getJSONObject(j).getString("country");
                    countries.add(new Country(country));
                }

                // парсинг Жанров
                ArrayList<Genre> genres = new ArrayList<>();
                JSONArray jsonArrayGenre = new JSONArray();
                if (item.has("genres")) {
                    jsonArrayGenre = item.getJSONArray("genres");
                }
                for (int j = 0; j < jsonArrayGenre.length(); j++) {
                    String genre = jsonArrayGenre.getJSONObject(j).getString("genre");
                    genres.add(new Genre(genre));
                }

                items.add(new ListFilmItem(kinopoiskId, imdbId, nameRu, nameEn, nameOriginal, countries, genres, ratingKinopoisk, ratingImdb, year, type, posterUrl, posterUrlPreview, coverUrl, logoUrl, description, ratingAgeLimits));

            }
            return new Collection(titleCollection, total, totalPages, items);
        } else if (jsonCollection.has("films")) {
            JSONArray itemsArray = jsonCollection.getJSONArray("films");
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject item = itemsArray.getJSONObject(i);

                // парсинг основной информации
                int kinopoiskId = 0;
                int imdbId = 0;
                double ratingKinopoisk = 0;
                int year = 0;
                String nameRu = "",
                        nameEn = "",
                        nameOriginal = "",
                        ratingImdb = "",
                        type = "",
                        posterUrl = "",
                        posterUrlPreview = "",
                        coverUrl = "",
                        logoUrl = "",
                        description = "",
                        ratingAgeLimits = "";

                if (item.has("kinopoiskId")) {
                    if (item.get("kinopoiskId") instanceof Integer) {
                        kinopoiskId = item.getInt("kinopoiskId");
                    }
                }
                if (item.has("filmId")) {
                    if (item.get("filmId") instanceof Integer) {
                        kinopoiskId = item.getInt("filmId");
                    }
                }
                if (item.has("imdbId")) {
                    if (item.get("imdbId") instanceof Integer) {
                        imdbId = item.getInt("imdbId");
                    }
                }
                if (item.has("nameRu")) {
                    nameRu = item.getString("nameRu");
                }
                if (item.has("nameEn")) {
                    nameEn = item.getString("nameEn");
                }
                if (item.has("nameOriginal")) {
                    nameOriginal = item.getString("nameOriginal");
                }
                if (item.has("ratingKinopoisk")) {
                    if (item.get("ratingKinopoisk") instanceof Double) {
                        ratingKinopoisk = item.getDouble("ratingKinopoisk");
                    }
                }
                if (item.has("ratingImdb")) {
                    ratingImdb = item.getString("ratingImdb");
                }
                if (item.has("year")) {
                    if (item.get("year") instanceof Double) {
                        year = item.getInt("year");
                    }
                }
                if (item.has("type")) {
                    type = item.getString("type");
                }
                if (item.has("posterUrl")) {
                    posterUrl = item.getString("posterUrl");
                }
                if (item.has("posterUrlPreview")) {
                    posterUrlPreview = item.getString("posterUrlPreview");
                }
                if (item.has("coverUrl")) {
                    coverUrl = item.getString("coverUrl");
                }
                if (item.has("logoUrl")) {
                    logoUrl = item.getString("logoUrl");
                }
                if (item.has("description")) {
                    description = item.getString("description");
                }
                if (item.has("ratingAgeLimits")) {
                    ratingAgeLimits = item.getString("ratingAgeLimits");
                }

                // парсинг стран
                ArrayList<Country> countries = new ArrayList<>();
                JSONArray jsonArrayCountry = new JSONArray();
                if (item.has("countries")) {
                    jsonArrayCountry = item.getJSONArray("countries");
                }
                for (int j = 0; j < jsonArrayCountry.length(); j++) {
                    String country = jsonArrayCountry.getJSONObject(j).getString("country");
                    countries.add(new Country(country));
                }

                // парсинг Жанров
                ArrayList<Genre> genres = new ArrayList<>();
                JSONArray jsonArrayGenre = new JSONArray();
                if (item.has("genres")) {
                    jsonArrayGenre = item.getJSONArray("genres");
                }
                for (int j = 0; j < jsonArrayGenre.length(); j++) {
                    String genre = jsonArrayGenre.getJSONObject(j).getString("genre");
                    genres.add(new Genre(genre));
                }

                items.add(new ListFilmItem(kinopoiskId, imdbId, nameRu, nameEn, nameOriginal, countries, genres, ratingKinopoisk, ratingImdb, year, type, posterUrl, posterUrlPreview, coverUrl, logoUrl, description, ratingAgeLimits));

            }
            return new Collection(titleCollection, total, totalPages, items);
        }
        return new Collection(titleCollection, "-2", "-2", new ArrayList<>());
    }

    private ItemFilmInfo createItemInfoClass(String json) throws JSONException {
        // Создание JSON Объекта на основе ответа от сервера
        JSONObject jsonItem = new JSONObject(json);
        // преобразование JSON Объекта в новый объект класса Collection
        int kinopoiskId = 0;
        String kinopoiskHDId = "0";
        String imdbId = "0";
        String nameRu = "0";
        String nameEn = "0";
        String nameOriginal = "0";
        String posterUrl = "0";
        String posterUrlPreview = "0";
        String coverUrl = "0";
        String logoUrl = "0";
        int reviewsCount = 0;
        int ratingGoodReview = 0;
        int ratingGoodReviewVoteCount = 0;
        double ratingKinopoisk = 0D;
        int ratingKinopoiskVoteCount = 0;
        int ratingImdb = 0;
        int ratingImdbVoteCount = 0;
        int ratingFilmCritics = 0;
        int ratingFilmCriticsVoteCount = 0;
        int ratingAwait = 0;
        int ratingAwaitCount = 0;
        int ratingRfCritics = 0;
        int ratingRfCriticsVoteCount = 0;
        String webUrl = "0";
        String year = "0";
        int filmLength = 0;
        String slogan = "0";
        String description = "0";
        String shortDescription = "0";
        String editorAnnotation = "0";
        boolean isTicketsAvailable = false;
        String productionStatus = "0";
        String type = "0";
        String ratingMpaa = "0";
        String ratingAgeLimits = "0";
        ArrayList<Country> countries = new ArrayList<>();
        ArrayList<Genre> genres = new ArrayList<>();
        String startYear = "0";
        String endYear = "0";
        boolean serial = false;
        boolean shortFilm = false;
        boolean completed = false;
        boolean hasImax = false;
        boolean has3D = false;
        boolean lastSync = false;


        if (jsonItem.has("kinopoiskId")) {
            if (jsonItem.get("kinopoiskId") instanceof Integer) {
                kinopoiskId = jsonItem.getInt("kinopoiskId");
            }
        }
        if (jsonItem.has("kinopoiskHDId")) {
            kinopoiskHDId = jsonItem.getString("kinopoiskHDId");
        }
        if (jsonItem.has("imdbId")) {
            imdbId = jsonItem.getString("imdbId");
        }
        if (jsonItem.has("nameRu")) {
            nameRu = jsonItem.getString("nameRu");
        }
        if (jsonItem.has("nameEn")) {
            nameEn = jsonItem.getString("nameEn");
        }
        if (jsonItem.has("nameOriginal")) {
            nameOriginal = jsonItem.getString("nameOriginal");
        }
        if (jsonItem.has("posterUrl")) {
            posterUrl = jsonItem.getString("posterUrl");
        }
        if (jsonItem.has("posterUrlPreview")) {
            posterUrlPreview = jsonItem.getString("posterUrlPreview");
        }
        if (jsonItem.has("coverUrl")) {
            coverUrl = jsonItem.getString("coverUrl");
        }
        if (jsonItem.has("logoUrl")) {
            logoUrl = jsonItem.getString("logoUrl");
        }
        if (jsonItem.has("reviewsCount")) {
            if (jsonItem.get("reviewsCount") instanceof Integer) {
                reviewsCount = jsonItem.getInt("reviewsCount");
            }
        }
        if (jsonItem.has("ratingGoodReview")) {
            if (jsonItem.get("ratingGoodReview") instanceof Integer) {
                ratingGoodReview = jsonItem.getInt("ratingGoodReview");
            }
        }
        if (jsonItem.has("ratingGoodReviewVoteCount")) {
            if (jsonItem.get("ratingGoodReviewVoteCount") instanceof Integer) {
                ratingGoodReviewVoteCount = jsonItem.getInt("ratingGoodReviewVoteCount");
            }
        }
        if (jsonItem.has("ratingKinopoisk")) {
            if (jsonItem.get("ratingKinopoisk") instanceof Double) {
                ratingKinopoisk = jsonItem.getDouble("ratingKinopoisk");
            }
        }
        if (jsonItem.has("ratingKinopoiskVoteCount")) {
            if (jsonItem.get("ratingKinopoiskVoteCount") instanceof Integer) {
                ratingKinopoiskVoteCount = jsonItem.getInt("ratingKinopoiskVoteCount");
            }
        }
        if (jsonItem.has("ratingImdb")) {
            if (jsonItem.get("ratingImdb") instanceof Integer) {
                ratingImdb = jsonItem.getInt("ratingImdb");
            }
        }
        if (jsonItem.has("ratingImdbVoteCount")) {
            if (jsonItem.get("ratingImdbVoteCount") instanceof Integer) {
                ratingImdbVoteCount = jsonItem.getInt("ratingImdbVoteCount");
            }
        }
        if (jsonItem.has("ratingFilmCritics")) {
            if (jsonItem.get("ratingFilmCritics") instanceof Integer) {
                ratingFilmCritics = jsonItem.getInt("ratingFilmCritics");
            }
        }
        if (jsonItem.has("ratingFilmCriticsVoteCount")) {
            if (jsonItem.get("ratingFilmCriticsVoteCount") instanceof Integer) {
                ratingFilmCriticsVoteCount = jsonItem.getInt("ratingFilmCriticsVoteCount");
            }
        }
        if (jsonItem.has("ratingAwait")) {
            if (jsonItem.get("ratingAwait") instanceof Integer) {
                ratingAwait = jsonItem.getInt("ratingAwait");
            }
        }
        if (jsonItem.has("ratingAwaitCount")) {
            if (jsonItem.get("ratingAwaitCount") instanceof Integer) {
                ratingAwaitCount = jsonItem.getInt("ratingAwaitCount");
            }
        }
        if (jsonItem.has("ratingRfCritics")) {
            if (jsonItem.get("ratingRfCritics") instanceof Integer) {
                ratingRfCritics = jsonItem.getInt("ratingRfCritics");
            }
        }
        if (jsonItem.has("ratingRfCriticsVoteCount")) {
            if (jsonItem.get("ratingRfCriticsVoteCount") instanceof Integer) {
                ratingRfCriticsVoteCount = jsonItem.getInt("ratingRfCriticsVoteCount");
            }
        }
        if (jsonItem.has("webUrl")) {
            webUrl = jsonItem.getString("webUrl");
        }
        if (jsonItem.has("year")) {
            year = jsonItem.getString("year");
        }
        if (jsonItem.has("filmLength")) {
            if (jsonItem.get("filmLength") instanceof Integer) {
                filmLength = jsonItem.getInt("filmLength");
            }
        }
        if (jsonItem.has("slogan")) {
            slogan = jsonItem.getString("slogan");
        }
        if (jsonItem.has("description")) {
            description = jsonItem.getString("description");
        }
        if (jsonItem.has("shortDescription")) {
            shortDescription = jsonItem.getString("shortDescription");
        }
        if (jsonItem.has("editorAnnotation")) {
            editorAnnotation = jsonItem.getString("editorAnnotation");
        }
        if (jsonItem.has("isTicketsAvailable")) {
            if (jsonItem.get("isTicketsAvailable") instanceof Boolean) {
                isTicketsAvailable = jsonItem.getBoolean("isTicketsAvailable");
            }
        }
        if (jsonItem.has("productionStatus")) {
            productionStatus = jsonItem.getString("productionStatus");
        }
        if (jsonItem.has("type")) {
            type = jsonItem.getString("type");
        }
        if (jsonItem.has("ratingMpaa")) {
            ratingMpaa = jsonItem.getString("ratingMpaa");
        }
        if (jsonItem.has("ratingAgeLimits")) {
            ratingAgeLimits = jsonItem.getString("ratingAgeLimits");
        }

        if (jsonItem.has("countries")) {
            if (jsonItem.get("countries") instanceof JSONArray) {
                JSONArray jsonArrayCountry = jsonItem.getJSONArray("countries");
                for (int i = 0; i < jsonArrayCountry.length(); i++) {
                    String country = jsonArrayCountry.getJSONObject(i).getString("country");
                    countries.add(new Country(country));
                }
            }
        }

        if (jsonItem.has("genres")) {
            if (jsonItem.get("genres") instanceof JSONArray) {
                JSONArray jsonArrayGenre = jsonItem.getJSONArray("genres");
                for (int i = 0; i < jsonArrayGenre.length(); i++) {
                    String genre = jsonArrayGenre.getJSONObject(i).getString("genre");
                    genres.add(new Genre(genre));
                }
            }
        }

        if (jsonItem.has("startYear")) {
            startYear = jsonItem.getString("startYear");
        }
        if (jsonItem.has("endYear")) {
            endYear = jsonItem.getString("endYear");
        }
        if (jsonItem.has("serial")) {
            if (jsonItem.get("serial") instanceof Boolean) {
                serial = jsonItem.getBoolean("serial");
            }
        }
        if (jsonItem.has("shortFilm")) {
            if (jsonItem.get("shortFilm") instanceof Boolean) {
                shortFilm = jsonItem.getBoolean("shortFilm");
            }
        }
        if (jsonItem.has("completed")) {
            if (jsonItem.get("completed") instanceof Boolean) {
                completed = jsonItem.getBoolean("completed");
            }
        }
        if (jsonItem.has("hasImax")) {
            if (jsonItem.get("hasImax") instanceof Boolean) {
                hasImax = jsonItem.getBoolean("hasImax");
            }
        }
        if (jsonItem.has("has3D")) {
            if (jsonItem.get("has3D") instanceof Boolean) {
                has3D = jsonItem.getBoolean("has3D");
            }
        }
        if (jsonItem.has("lastSync")) {
            if (jsonItem.get("lastSync") instanceof Boolean) {
                lastSync = jsonItem.getBoolean("lastSync");
            }
        }

        return new ItemFilmInfo(
                kinopoiskId,
                kinopoiskHDId,
                imdbId,
                nameRu,
                nameEn,
                nameOriginal,
                posterUrl,
                posterUrlPreview,
                coverUrl,
                logoUrl,
                reviewsCount,
                ratingGoodReview,
                ratingGoodReviewVoteCount,
                ratingKinopoisk,
                ratingKinopoiskVoteCount,
                ratingImdb,
                ratingImdbVoteCount,
                ratingFilmCritics,
                ratingFilmCriticsVoteCount,
                ratingAwait,
                ratingAwaitCount,
                ratingRfCritics,
                ratingRfCriticsVoteCount,
                webUrl,
                year,
                filmLength,
                slogan,
                description,
                shortDescription,
                editorAnnotation,
                isTicketsAvailable,
                productionStatus,
                type,
                ratingMpaa,
                ratingAgeLimits,
                countries,
                genres,
                startYear,
                endYear,
                serial,
                shortFilm,
                completed,
                hasImax,
                has3D,
                lastSync
        );
    }

}
