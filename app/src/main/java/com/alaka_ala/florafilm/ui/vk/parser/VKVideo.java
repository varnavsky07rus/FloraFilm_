package com.alaka_ala.florafilm.ui.vk.parser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.alaka_ala.florafilm.ui.vk.LoginVkActivity;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VKVideo {
    public static final float VERSION_API = 5.199F;
    public static final String DEF_HOST_API = "https://api.vk.com/method/";
    public static final String METHOD_VIDEO_GET = "video.get";
    public static final String METHOD_VIDEO_SEARCH = "video.search";
    public static final String METHOD_VIDEO_COMMENTS = "video.getComments";
    public static final String METHOD_VIDEO_GET_ALBUMS = "video.getAlbums";
    public static final String METHOD_VIDEO_GET_ALBUM_BY_ID = "video.getAlbumById";

    public static String DEF_USER_AGENT;
    private final String ACCESS_TOKEN;

    private final String film4K = "-116989148"; // https://vkvideo.ru/@4kkinovk
    private final String vkFilm = "-217672812"; //
    private final String mirmultovv = "-222948973"; // https://vkvideo.ru/@mirmultovv
    private final String interview = "";  // https://vkvideo.ru/interview
    private final String music = ""; // https://vkvideo.ru/music
    private final String tourism = "";  // https://vkvideo.ru/tourisme


    public VKVideo(String accessToken) {
        ACCESS_TOKEN = accessToken;
        DEF_USER_AGENT = LoginVkActivity.USER_AGENT;
    }


    /**
     * Получение всех видео группы по id группы (По умолчанию возвращает 99 фильмов из группы)
     */
    public void getVideosGroup(String owner_id, int offset, GetAllVideosGroupCallback gavgc) {
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();
                boolean ok = bundle.getBoolean("ok", false);
                if (ok) {
                    ArrayList<VideoItem> videos = (ArrayList<VideoItem>) bundle.getSerializable("videos");
                    gavgc.onSuccess(videos);
                } else {
                    gavgc.onError(new Exception("Ошибка получения видео"));
                }
                return false;
            }
        });
        String url = DEF_HOST_API + METHOD_VIDEO_GET + "?owner_id=" + owner_id + "&offset" + offset + "&access_token=" + ACCESS_TOKEN + "&v=" + VERSION_API;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder req = new Request.Builder();
        req.url(url);
        req.addHeader("User-Agent", DEF_USER_AGENT);
        req.addHeader("Accept-Language", "ru,en;q=0.9");
        okHttpClient.newCall(req.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("ok", false);
                bundle.putSerializable("videos", null);
                Message msg = new Message();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String body = response.body().string();
                if (response.isSuccessful()) {
                    if (JsonParser.parseString(body).isJsonObject()) {
                        try {
                            JSONObject jsonBody = new JSONObject(body);
                            jsonBody = jsonBody.getJSONObject("response");
                            JSONArray items = jsonBody.getJSONArray("items");
                            ArrayList<VideoItem> videos = getVideoItems(items);

                            Bundle bundle = new Bundle();
                            bundle.putBoolean("ok", true);
                            bundle.putSerializable("videos", videos);
                            Message msg = new Message();
                            msg.setData(bundle);
                            handler.sendMessage(msg);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            onFailure(call, new IOException("Ошибка создания объекта JSONObject: " + e.getMessage()));
                        }
                    }
                } else {
                    onFailure(call, new IOException("Ошибка выполнения запроса, ответ сервера: " + body));
                }
            }
        });
    }

    public interface GetAllVideosGroupCallback {
        void onSuccess(ArrayList<VideoItem> videos);

        void onError(Exception e);
    }


    /**
     * Идентификаторы в формате {owner_id}_{video_id},
     * перечисленные через запятую. Идентификатор сообщества должен начинаться со знака - (минус)
     * Пример: -166562603_456239104,743784474_456239017.
     * Некоторые видеозаписи закрыты приватностью и не могут
     * быть получены без ключа доступа. В этом случае отправьте идентификатор в формате {owner_id}_{video_id}_{access_key}.
     *
     * @-Пример: 1_129207899_220df2876123d3542f, 6492_135055734_e0a9bcc31144f67fbd
     * @-Примечание. Ключ доступа возвращается вместе с остальными данными видео в методах, которые возвращают закрытые приватностью видео, доступные в этом контексте. Например, это поле есть у видео, возвращаемых методом messages.getHistory.
     */
    public void getVideo(String videos, GetVideoCallback gvc) {
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();
                boolean ok = bundle.getBoolean("ok", false);
                if (ok) {
                    ArrayList<VideoItem> videos = (ArrayList<VideoItem>) bundle.getSerializable("videos");
                    gvc.onSuccess(videos);
                } else {
                    gvc.onError("Ошибка получения видео");
                }
                return false;
            }
        });
        String url = DEF_HOST_API + METHOD_VIDEO_GET + "?videos=" + videos + "&access_token=" + ACCESS_TOKEN + "&v=" + VERSION_API;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder req = new Request.Builder();
        req.url(url);
        req.addHeader("User-Agent", DEF_USER_AGENT);
        req.addHeader("Accept-Language", "ru,en;q=0.9");
        okHttpClient.newCall(req.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("ok", false);
                bundle.putSerializable("videos", null);
                Message msg = new Message();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String body = response.body().string();
                if (response.isSuccessful()) {
                    if (JsonParser.parseString(body).isJsonObject()) {
                        try {
                            JSONObject jsonBody = new JSONObject(body);
                            jsonBody = jsonBody.getJSONObject("response");
                            JSONArray items = jsonBody.getJSONArray("items");
                            ArrayList<VideoItem> videos = getVideoItems(items);

                            Bundle bundle = new Bundle();
                            bundle.putBoolean("ok", true);
                            bundle.putSerializable("videos", videos);
                            Message msg = new Message();
                            msg.setData(bundle);
                            handler.sendMessage(msg);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            onFailure(call, new IOException("Ошибка создания объекта JSONObject: " + e.getMessage()));
                        }
                    }
                } else {
                    onFailure(call, new IOException("Ошибка выполнения запроса, ответ сервера: " + body));
                }
            }
        });
    }

    public interface GetVideoCallback {
        void onSuccess(ArrayList<VideoItem> videos);

        void onError(String e);

        default void finish() {
        }
    }


    /**
     * Получение видео на отдельный плейлист группы по id группы (По умолчанию возвращает 99 фильмов из группы)
     */
    public void getVideosGroup(String owner_id, String album_id, int offset, GetAllVideosGroupCallback gavgc) {
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();
                boolean ok = bundle.getBoolean("ok", false);
                if (ok) {
                    ArrayList<VideoItem> videos = (ArrayList<VideoItem>) bundle.getSerializable("videos");
                    gavgc.onSuccess(videos);
                } else {
                    gavgc.onError(new Exception("Ошибка получения видео"));
                }
                return false;
            }
        });
        String url = DEF_HOST_API + METHOD_VIDEO_GET + "?owner_id=" + owner_id + "&album_id=" + album_id + "&offset=" + offset + "&access_token=" + ACCESS_TOKEN + "&v=" + VERSION_API;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder req = new Request.Builder();
        req.url(url);
        req.addHeader("User-Agent", DEF_USER_AGENT);
        req.addHeader("Accept-Language", "ru,en;q=0.9");
        okHttpClient.newCall(req.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("ok", false);
                bundle.putSerializable("videos", null);
                Message msg = new Message();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String body = response.body().string();
                if (response.isSuccessful()) {
                    if (JsonParser.parseString(body).isJsonObject()) {
                        try {
                            JSONObject jsonBody = new JSONObject(body);
                            jsonBody = jsonBody.getJSONObject("response");
                            JSONArray items = jsonBody.getJSONArray("items");
                            ArrayList<VideoItem> videos = getVideoItems(items);

                            Bundle bundle = new Bundle();
                            bundle.putBoolean("ok", true);
                            bundle.putSerializable("videos", videos);
                            Message msg = new Message();
                            msg.setData(bundle);
                            handler.sendMessage(msg);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            onFailure(call, new IOException("Ошибка создания объекта JSONObject: " + e.getMessage()));
                        }
                    }
                } else {
                    onFailure(call, new IOException("Ошибка выполнения запроса, ответ сервера: " + body));
                }
            }
        });

    }


    private static final Map<Integer, GroupItem> groupes = new HashMap<>();

    // Получение плейлистов группы (если открыты)
    public void getAlbums(int owner_id, int offset, GetAlbumsCallback callback) {

        Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();
                boolean ok = bundle.getBoolean("ok", false);
                if (ok) {
                    GroupItem group = (GroupItem) bundle.getSerializable("group");
                    groupes.put(owner_id, group);
                    callback.onSuccess(group);
                    callback.finish();
                } else {
                    String error = bundle.getString("error", "");
                    callback.onError(error);
                }
                return false;
            }
        });
        if (groupes.containsKey(owner_id)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("ok", true);
            bundle.putSerializable("group", groupes.get(owner_id));
            Message msg = new Message();
            msg.setData(bundle);
            handler.sendMessage(msg);
            return;
        }
        int count = 100;
        int extended = 1;
        String url = DEF_HOST_API + METHOD_VIDEO_GET_ALBUMS + "?owner_id=" + owner_id + "&extended=" + extended + "&count=" + count + "&offset=" + offset + "&access_token=" + ACCESS_TOKEN + "&v=" + VERSION_API;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder req = new Request.Builder();
        req.url(url);
        req.addHeader("User-Agent", DEF_USER_AGENT);
        req.addHeader("Accept-Language", "ru,en;q=0.9");
        okHttpClient.newCall(req.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendHandlerError(e, handler);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                if (response.isSuccessful()) {
                    if (JsonParser.parseString(body).isJsonObject()) {
                        try {
                            JSONObject jsonBody = new JSONObject(body);
                            jsonBody = jsonBody.getJSONObject("response");
                            JSONArray items = jsonBody.getJSONArray("items");

                            // Для добавления в GroupItem
                            ArrayList<PlaylistGroupItem> playlistsGroupItems = new ArrayList<>();
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                PlaylistGroupItem.Builder builder = new PlaylistGroupItem.Builder();
                                builder.setCount(item.has("count") ? item.getInt("count") : 0);
                                builder.setUpdatedTime(item.has("updated_time") ? item.getInt("updated_time") : 0);
                                builder.setId(item.has("id") ? item.getInt("id") : 0);
                                builder.setOwnerId(item.has("owner_id") ? item.getInt("owner_id") : 0);
                                builder.setTitle(item.has("title") ? item.getString("title") : "");
                                // Парсинг изображений
                                ArrayList<VideoItem.Image> images = new ArrayList<>();
                                JSONArray imagesJson = item.has("image") ? item.getJSONArray("image") : new JSONArray();
                                for (int j = 0; j < imagesJson.length(); j++) {
                                    JSONObject image = imagesJson.getJSONObject(j);
                                    VideoItem.Image.Builder bImage = new VideoItem.Image.Builder();
                                    bImage.setUrl(image.has("url") ? image.getString("url") : "");
                                    bImage.setHeight(image.has("height") ? image.getInt("height") : 0);
                                    bImage.setWidth(image.has("width") ? image.getInt("width") : 0);
                                    images.add(bImage.build());
                                }
                                builder.setImages(images);
                                builder.setIsSubscribed(item.has("is_subscribed") && item.getBoolean("is_subscribed"));
                                builder.setCanView(item.has("can_view") ? item.getInt("can_view") : 0);
                                builder.setFirstVideo_id(item.has("first_video_id") ? item.getString("first_video_id") : "");
                                builder.setCanSubscribe(item.has("can_subscribe") ? item.getInt("can_subscribe") : 0);
                                builder.setResponseType(item.has("response_type") ? item.getString("response_type") : "");
                                playlistsGroupItems.add(builder.build());
                            }

                            // Получение всех видео группы
                            OkHttpClient oks = new OkHttpClient();
                            Request.Builder req = new Request.Builder();
                            String urlVideoGet = DEF_HOST_API + METHOD_VIDEO_GET + "?owner_id=" + owner_id + "&extended=" + extended + "&count=" + count + "&offset=" + offset + "&access_token=" + ACCESS_TOKEN + "&v=" + VERSION_API;
                            req.url(urlVideoGet);
                            oks.newCall(req.build()).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    sendHandlerError(e, handler);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String body = response.body().string();
                                    if (response.isSuccessful()) {
                                        if (JsonParser.parseString(body).isJsonObject()) {
                                            try {
                                                JSONObject jsonBody = new JSONObject(body);
                                                jsonBody = jsonBody.has("response") ? jsonBody.getJSONObject("response") : new JSONObject();
                                                JSONArray items = jsonBody.has("items") ? jsonBody.getJSONArray("items") : new JSONArray();
                                                JSONObject group = jsonBody.has("groups") ? jsonBody.getJSONArray("groups").getJSONObject(0) : new JSONObject();

                                                int count = jsonBody.has("count") ? jsonBody.getInt("count") : 0;
                                                int is_closed = group.has("is_closed") ? group.getInt("is_closed") : 0;
                                                String name = group.has("name") ? group.getString("name") : "";
                                                String photo_100 = group.has("photo_100") ? group.getString("photo_100") : "";
                                                String screen_name = group.has("screen_name") ? group.getString("screen_name") : "";

                                                ArrayList<VideoItem> videos = getVideoItems(items);

                                                GroupItem groupItem = new GroupItem(playlistsGroupItems, videos, name, screen_name, count, is_closed, photo_100);

                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean("ok", true);
                                                bundle.putSerializable("group", groupItem);
                                                Message msg = new Message();
                                                msg.setData(bundle);
                                                handler.sendMessage(msg);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                onFailure(call, new IOException("#2 Ошибка создания объекта JSONObject: " + e.getMessage()));
                                            }
                                        } else {
                                            onFailure(call, new IOException("#1 Полученный ответ не содержит данных формата JSONObject"));
                                        }
                                    }
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            onFailure(call, new IOException("Ошибка создания объекта JSONObject: " + e.getMessage()));
                        }
                    }
                } else {
                    onFailure(call, new IOException("Ошибка получения альбомов. Код ответа: " + response.code()));
                }
            }
        });


    }

    private static @NonNull ArrayList<VideoItem> getVideoItems(JSONArray items) throws JSONException {
        ArrayList<VideoItem> videos = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject files = items.getJSONObject(i).has("files") ? items.getJSONObject(i).getJSONObject("files") : new JSONObject();
            JSONObject trailer = null;
            if (items.getJSONObject(i).has("trailer")) {
                trailer = items.getJSONObject(i).getJSONObject("trailer");
            }
            JSONArray images = items.getJSONObject(i).has("image") ? items.getJSONObject(i).getJSONArray("image") : new JSONArray();
            JSONArray first_frames = items.getJSONObject(i).has("first_frame") ? items.getJSONObject(i).getJSONArray("first_frame") : new JSONArray();
            JSONObject likes = items.getJSONObject(i).has("likes") ? items.getJSONObject(i).getJSONObject("likes") : new JSONObject();
            JSONObject reposts = items.getJSONObject(i).has("reposts") ? items.getJSONObject(i).getJSONObject("reposts") : new JSONObject();

            int comments = items.getJSONObject(i).has("comments") ? items.getJSONObject(i).getInt("comments") : 0;
            String description = items.getJSONObject(i).has("description") ? items.getJSONObject(i).getString("description") : "";
            int duration = items.getJSONObject(i).has("duration") ? items.getJSONObject(i).getInt("duration") : 0;
            int id = items.getJSONObject(i).has("id") ? items.getJSONObject(i).getInt("id") : 0;
            int owner_id = items.getJSONObject(i).has("owner_id") ? items.getJSONObject(i).getInt("owner_id") : 0;
            String ov_id = items.getJSONObject(i).has("ov_id") ? items.getJSONObject(i).getString("ov_id") : "";
            String title = items.getJSONObject(i).has("title") ? items.getJSONObject(i).getString("title") : "";
            String player = items.getJSONObject(i).has("player") ? items.getJSONObject(i).getString("player") : "";
            String track_code = items.getJSONObject(i).has("track_code") ? items.getJSONObject(i).getString("track_code") : "";
            String type = items.getJSONObject(i).has("type") ? items.getJSONObject(i).getString("type") : "";
            int views = items.getJSONObject(i).getInt("views");


            VideoItem.Builder builder = new VideoItem.Builder();

            VideoItem.File filesList;
            ArrayList<VideoItem.Image> imageList = new ArrayList<>();
            ArrayList<VideoItem.FirstFrame> firstFrameList = new ArrayList<>();
            VideoItem.Trailer trailerList;
            VideoItem.Likes likesList;
            VideoItem.Reposts repostsList;


            // Тут надо исключить файлы которые отсустсвуют

            VideoItem.File.Builder bFile = new VideoItem.File.Builder();

            bFile.set144(files.has("mp4_144") ? files.getString("mp4_144") : "");

            bFile.set240(files.has("mp4_240") ? files.getString("mp4_240") : "");
            bFile.set360(files.has("mp4_360") ? files.getString("mp4_360") : "");
            bFile.set480(files.has("mp4_480") ? files.getString("mp4_480") : "");
            bFile.set720(files.has("mp4_720") ? files.getString("mp4_720") : "");
            bFile.set1080(files.has("mp4_1080") ? files.getString("mp4_1080") : "");
            bFile.setHls(files.has("hls") ? files.getString("hls") : "");
            bFile.setDashSep(files.has("dash_sep") ? files.getString("dash_sep") : "");
            bFile.setDashStreams(files.has("dash_streams") ? files.getString("dash_streams") : "");
            bFile.setHlsStreams(files.has("hls_streams") ? files.getString("hls_streams") : "");
            bFile.setFailoverHost(files.has("failover_host") ? files.getString("failover_host") : "");
            filesList = bFile.build();
            builder.setFiles(filesList);

            for (int j = 0; j < images.length(); j++) {
                JSONObject image = images.getJSONObject(j);
                VideoItem.Image.Builder bImage = new VideoItem.Image.Builder();
                bImage.setUrl(image.has("url") ? image.getString("url") : "");
                bImage.setHeight(image.has("height") ? image.getInt("height") : 0);
                bImage.setWidth(image.has("width") ? image.getInt("width") : 0);
                imageList.add(bImage.build());
            }
            builder.setImages(imageList);

            for (int j = 0; j < first_frames.length(); j++) {
                JSONObject firstFrame = first_frames.getJSONObject(j);
                VideoItem.FirstFrame.Builder bFirstFrame = new VideoItem.FirstFrame.Builder();
                bFirstFrame.setUrl(firstFrame.has("url") ? firstFrame.getString("url") : "");
                bFirstFrame.setHeight(firstFrame.has("height") ? firstFrame.getInt("height") : 0);
                bFirstFrame.setWidth(firstFrame.has("width") ? firstFrame.getInt("width") : 0);
                firstFrameList.add(bFirstFrame.build());
            }
            builder.setFirstFrames(firstFrameList);

            if (trailer != null) {
                VideoItem.Trailer.Builder bTrailer = new VideoItem.Trailer.Builder();
                bTrailer.set240(trailer.has("mp4_240") ? trailer.getString("mp4_240") : "");
                bTrailer.set360(trailer.has("mp4_360") ? trailer.getString("mp4_360") : "");
                bTrailer.set480(trailer.has("mp4_480") ? trailer.getString("mp4_480") : "");
                bTrailer.set720(trailer.has("mp4_720") ? trailer.getString("mp4_720") : "");
                bTrailer.set1080(trailer.has("mp4_1080") ? trailer.getString("mp4_1080") : "");
                trailerList = bTrailer.build();
                builder.setTrailers(trailerList);
            }


            VideoItem.Likes.Builder bLikes = new VideoItem.Likes.Builder();
            bLikes.setCount(likes.has("count") ? likes.getInt("count") : 0);
            bLikes.setUser_likes(likes.has("user_likes") ? likes.getInt("user_likes") : 0);
            likesList = bLikes.build();
            builder.setLikes(likesList);


            VideoItem.Reposts.Builder bReposts = new VideoItem.Reposts.Builder();
            bReposts.setCount(reposts.has("count") ? reposts.getInt("count") : 0);
            bReposts.setMailCount(reposts.has("mail_count") ? reposts.getInt("mail_count") : 0);
            bReposts.setWallCount(reposts.has("wall_count") ? reposts.getInt("wall_count") : 0);
            bReposts.setUserReposted(reposts.has("user_reposted") ? reposts.getInt("user_reposted") : 0);
            repostsList = bReposts.build();
            builder.setReposts(repostsList);

            builder.setCountComments(comments);
            builder.setDescription(description);
            builder.setDuration(duration);
            builder.setId(id);
            builder.setOwnerId(owner_id);
            //builder.setAuthor(is_author);
            builder.setOvId(ov_id);
            builder.setTitle(title);
            //builder.setFavorite(is_favorite);
            builder.setPlayer(player);
            builder.setTrackCode(track_code);
            builder.setType(type);
            builder.setViews(views);
            videos.add(builder.build());
        }
        return videos;
    }

    private static void sendHandlerError(IOException e, Handler handler) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("ok", false);
        bundle.putString("error", e.getMessage());
        Message msg = new Message();
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    public interface GetAlbumsCallback {
        void onSuccess(GroupItem group);

        void onError(String error);

        default void finish() {
        }
    }

    // Получение конкретного Альбома (Плейлиста)
    public void getAlbum(int owner_id, int album_id, int offset, GetAlbumCallback callback) {
        Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();
                boolean ok = bundle.getBoolean("ok", false);
                if (ok) {
                    PlaylistGroupItem playlistGroupItem = (PlaylistGroupItem) bundle.getSerializable("playlist");
                    callback.onSuccess(playlistGroupItem);
                    callback.finish();
                } else {
                    String error = bundle.getString("error", "");
                    callback.onError(error);
                }
                return false;
            }
        });
        int count = 100;
        int extended = 1;
        String url = DEF_HOST_API + METHOD_VIDEO_GET_ALBUM_BY_ID + "?owner_id=" + owner_id + "&album_id=" + album_id + "&extended=" + extended + "&count=" + count + "&offset=" + offset + "&access_token=" + ACCESS_TOKEN + "&v=" + VERSION_API;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder req = new Request.Builder();
        req.url(url);
        req.addHeader("User-Agent", DEF_USER_AGENT);
        req.addHeader("Accept-Language", "ru,en;q=0.9");
        okHttpClient.newCall(req.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendHandlerError(e, handler);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                if (response.isSuccessful()) {
                    if (JsonParser.parseString(body).isJsonObject()) {
                        try {
                            JSONObject jsonBody = new JSONObject(body);
                            jsonBody = jsonBody.getJSONObject("response");

                            PlaylistGroupItem.Builder builder = new PlaylistGroupItem.Builder();
                            builder.setCount(jsonBody.has("count") ? jsonBody.getInt("count") : 0);
                            builder.setUpdatedTime(jsonBody.has("updated_time") ? jsonBody.getInt("updated_time") : 0);
                            builder.setId(jsonBody.has("id") ? jsonBody.getInt("id") : 0);
                            builder.setOwnerId(jsonBody.has("owner_id") ? jsonBody.getInt("owner_id") : 0);
                            builder.setTitle(jsonBody.has("title") ? jsonBody.getString("title") : "");
                            // Парсинг изображений
                            ArrayList<VideoItem.Image> images = new ArrayList<>();
                            JSONArray imagesJson = jsonBody.has("image") ? jsonBody.getJSONArray("image") : new JSONArray();
                            for (int j = 0; j < imagesJson.length(); j++) {
                                JSONObject image = imagesJson.getJSONObject(j);
                                VideoItem.Image.Builder bImage = new VideoItem.Image.Builder();
                                bImage.setUrl(image.has("url") ? image.getString("url") : "");
                                bImage.setHeight(image.has("height") ? image.getInt("height") : 0);
                                bImage.setWidth(image.has("width") ? image.getInt("width") : 0);
                                images.add(bImage.build());
                            }
                            builder.setImages(images);
                            builder.setIsSubscribed(jsonBody.has("is_subscribed") && jsonBody.getBoolean("is_subscribed"));
                            builder.setCanView(jsonBody.has("can_view") ? jsonBody.getInt("can_view") : 0);
                            builder.setFirstVideo_id(jsonBody.has("first_video_id") ? jsonBody.getString("first_video_id") : "");
                            builder.setCanSubscribe(jsonBody.has("can_subscribe") ? jsonBody.getInt("can_subscribe") : 0);
                            builder.setResponseType(jsonBody.has("response_type") ? jsonBody.getString("response_type") : "");


                            Bundle bundle = new Bundle();
                            bundle.putSerializable("playlist", builder.build());
                            bundle.putBoolean("ok", true);
                            Message msg = new Message();
                            msg.setData(bundle);
                            handler.sendMessage(msg);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            onFailure(call, new IOException("Ошибка создания объекта JSONObject: " + e.getMessage()));
                        }
                    }
                } else {
                    onFailure(call, new IOException("Ошибка получения альбомов. Код ответа: " + response.code()));
                }
            }
        });


    }

    public interface GetAlbumCallback {
        void onSuccess(PlaylistGroupItem playlistGroupItem);

        void onError(String error);

        default void finish() {
        }
    }

    /**
     * Поиск видео по названию
     */
    public void searchVideos(String query, int offset, SearchVideosCallback svc) {
        Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();
                boolean ok = bundle.getBoolean("ok", false);
                if (ok) {
                    ArrayList<VideoItem> videos = (ArrayList<VideoItem>) bundle.getSerializable("videos");
                    svc.onSuccessSearch(videos);
                } else {
                    svc.onErrorSearch(new Exception("Ошибка получения видео"));
                }

                return false;
            }
        });
        String baseUrl = DEF_HOST_API + METHOD_VIDEO_SEARCH + "?q=" + query + "&access_token=" + ACCESS_TOKEN + "&v=" + VERSION_API;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder req = new Request.Builder();
        req.url(baseUrl);
        req.addHeader("User-Agent", DEF_USER_AGENT);
        req.addHeader("Accept-Language", "ru,en;q=0.9");
        okHttpClient.newCall(req.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("ok", false);
                bundle.putSerializable("videos", null);
                Message msg = new Message();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                if (response.isSuccessful()) {
                    if (JsonParser.parseString(body).isJsonObject()) {
                        try {
                            JSONObject jsonBody = new JSONObject(body);
                            jsonBody = jsonBody.has("response") ? jsonBody.getJSONObject("response") : new JSONObject();
                            JSONArray items = jsonBody.has("items") ? jsonBody.getJSONArray("items") : new JSONArray();
                            ArrayList<VideoItem> videos = new ArrayList<>();
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject files = items.getJSONObject(i).has("files") ? items.getJSONObject(i).getJSONObject("files") : new JSONObject();
                                JSONObject trailer = items.getJSONObject(i).has("trailer") ? items.getJSONObject(i).getJSONObject("trailer") : new JSONObject();

                                JSONArray images = items.getJSONObject(i).has("image") ? items.getJSONObject(i).getJSONArray("image") : new JSONArray();
                                JSONArray first_frames = items.getJSONObject(i).has("first_frame") ? items.getJSONObject(i).getJSONArray("first_frame") : new JSONArray();
                                JSONObject likes = items.getJSONObject(i).has("likes") ? items.getJSONObject(i).getJSONObject("likes") : new JSONObject();
                                JSONObject reposts = items.getJSONObject(i).has("reposts") ? items.getJSONObject(i).getJSONObject("reposts") : new JSONObject();

                                int comments = items.getJSONObject(i).has("comments") ? items.getJSONObject(i).getInt("comments") : 0;
                                String description = items.getJSONObject(i).has("description") ? items.getJSONObject(i).getString("description") : "";
                                int duration = items.getJSONObject(i).has("duration") ? items.getJSONObject(i).getInt("duration") : 0;
                                int id = items.getJSONObject(i).has("id") ? items.getJSONObject(i).getInt("id") : 0;
                                int owner_id = items.getJSONObject(i).has("owner_id") ? items.getJSONObject(i).getInt("owner_id") : 0;
                                String ov_id = items.getJSONObject(i).has("ov_id") ? items.getJSONObject(i).getString("ov_id") : "";
                                String title = items.getJSONObject(i).has("title") ? items.getJSONObject(i).getString("title") : "";
                                String player = items.getJSONObject(i).has("player") ? items.getJSONObject(i).getString("player") : "";
                                String track_code = items.getJSONObject(i).has("track_code") ? items.getJSONObject(i).getString("track_code") : "";
                                String type = items.getJSONObject(i).has("type") ? items.getJSONObject(i).getString("type") : "";
                                int views = items.getJSONObject(i).getInt("views");


                                VideoItem.Builder builder = new VideoItem.Builder();

                                VideoItem.File filesList;
                                ArrayList<VideoItem.Image> imageList = new ArrayList<>();
                                ArrayList<VideoItem.FirstFrame> firstFrameList = new ArrayList<>();
                                VideoItem.Trailer trailerList;
                                VideoItem.Likes likesList;
                                VideoItem.Reposts repostsList;


                                // Тут надо исключить файлы которые отсустсвуют

                                VideoItem.File.Builder bFile = new VideoItem.File.Builder();

                                bFile.set144(files.has("mp4_144") ? files.getString("mp4_144") : "");

                                bFile.set240(files.has("mp4_240") ? files.getString("mp4_240") : "");
                                bFile.set360(files.has("mp4_360") ? files.getString("mp4_360") : "");
                                bFile.set480(files.has("mp4_480") ? files.getString("mp4_480") : "");
                                bFile.set720(files.has("mp4_720") ? files.getString("mp4_720") : "");
                                bFile.set1080(files.has("mp4_1080") ? files.getString("mp4_1080") : "");
                                bFile.setHls(files.has("hls") ? files.getString("hls") : "");
                                bFile.setDashSep(files.has("dash_sep") ? files.getString("dash_sep") : "");
                                bFile.setDashStreams(files.has("dash_streams") ? files.getString("dash_streams") : "");
                                bFile.setHlsStreams(files.has("hls_streams") ? files.getString("hls_streams") : "");
                                bFile.setFailoverHost(files.has("failover_host") ? files.getString("failover_host") : "");
                                filesList = bFile.build();
                                builder.setFiles(filesList);

                                for (int j = 0; j < images.length(); j++) {
                                    JSONObject image = images.getJSONObject(j);
                                    VideoItem.Image.Builder bImage = new VideoItem.Image.Builder();
                                    bImage.setUrl(image.has("url") ? image.getString("url") : "");
                                    bImage.setHeight(image.has("height") ? image.getInt("height") : 0);
                                    bImage.setWidth(image.has("width") ? image.getInt("width") : 0);
                                    imageList.add(bImage.build());
                                }
                                builder.setImages(imageList);

                                for (int j = 0; j < first_frames.length(); j++) {
                                    JSONObject firstFrame = first_frames.getJSONObject(j);
                                    VideoItem.FirstFrame.Builder bFirstFrame = new VideoItem.FirstFrame.Builder();
                                    bFirstFrame.setUrl(firstFrame.has("url") ? firstFrame.getString("url") : "");
                                    bFirstFrame.setHeight(firstFrame.has("height") ? firstFrame.getInt("height") : 0);
                                    bFirstFrame.setWidth(firstFrame.has("width") ? firstFrame.getInt("width") : 0);
                                    firstFrameList.add(bFirstFrame.build());
                                }
                                builder.setFirstFrames(firstFrameList);

                                if (trailer != null) {
                                    VideoItem.Trailer.Builder bTrailer = new VideoItem.Trailer.Builder();
                                    bTrailer.set240(trailer.has("mp4_240") ? trailer.getString("mp4_240") : "");
                                    bTrailer.set360(trailer.has("mp4_360") ? trailer.getString("mp4_360") : "");
                                    bTrailer.set480(trailer.has("mp4_480") ? trailer.getString("mp4_480") : "");
                                    bTrailer.set720(trailer.has("mp4_720") ? trailer.getString("mp4_720") : "");
                                    bTrailer.set1080(trailer.has("mp4_1080") ? trailer.getString("mp4_1080") : "");
                                    trailerList = bTrailer.build();
                                    builder.setTrailers(trailerList);
                                }


                                VideoItem.Likes.Builder bLikes = new VideoItem.Likes.Builder();
                                bLikes.setCount(likes.has("count") ? likes.getInt("count") : 0);
                                bLikes.setUser_likes(likes.has("user_likes") ? likes.getInt("user_likes") : 0);
                                likesList = bLikes.build();
                                builder.setLikes(likesList);


                                VideoItem.Reposts.Builder bReposts = new VideoItem.Reposts.Builder();
                                bReposts.setCount(reposts.has("count") ? reposts.getInt("count") : 0);
                                bReposts.setMailCount(reposts.has("mail_count") ? reposts.getInt("mail_count") : 0);
                                bReposts.setWallCount(reposts.has("wall_count") ? reposts.getInt("wall_count") : 0);
                                bReposts.setUserReposted(reposts.has("user_reposted") ? reposts.getInt("user_reposted") : 0);
                                repostsList = bReposts.build();
                                builder.setReposts(repostsList);

                                builder.setCountComments(comments);
                                builder.setDescription(description);
                                builder.setDuration(duration);
                                builder.setId(id);
                                builder.setOwnerId(owner_id);
                                //builder.setAuthor(is_author);
                                builder.setOvId(ov_id);
                                builder.setTitle(title);
                                //builder.setFavorite(is_favorite);
                                builder.setPlayer(player);
                                builder.setTrackCode(track_code);
                                builder.setType(type);
                                builder.setViews(views);
                                videos.add(builder.build());
                            }

                            Bundle bundle = new Bundle();
                            bundle.putBoolean("ok", true);
                            bundle.putSerializable("videos", videos);
                            Message msg = new Message();
                            msg.setData(bundle);
                            handler.sendMessage(msg);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            onFailure(call, new IOException("Ошибка создания объекта JSONObject: " + e.getMessage()));
                        }
                    }
                } else {
                    onFailure(call, new IOException("Ошибка выполнения запроса, ответ сервера: " + body));
                }
            }
        });
    }

    public interface SearchVideosCallback {
        void onSuccessSearch(ArrayList<VideoItem> videos);

        void onErrorSearch(Exception e);
    }


    /**
     * Получение комментариев к видео
     */
    public void getCommentsVideo(String owner_id, String video_id, int offset, int count, GetCommentsVideoCallback gcv) {
        Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();
                boolean ok = bundle.getBoolean("ok", false);
                if (ok) {
                    ArrayList<CommentVideo> comments = (ArrayList<CommentVideo>) bundle.getSerializable("comments");
                    gcv.onSuccess(comments);
                } else {
                    gcv.onError(new Exception(bundle.getString("error")));
                }
                return false;
            }
        });
        String sort = "desc"; // от новых к старым (asc: от старых к новым)
        String extended = "1";
        String baseUrl = DEF_HOST_API + METHOD_VIDEO_COMMENTS + "?owner_id=" + owner_id + "&video_id=" + video_id + "&offset=" + offset + "&count=" + count + "&sort=" + sort + "&access_token=" + ACCESS_TOKEN + "&extended=" + extended + "&fields=photo_100" + "&v=" + VERSION_API;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder req = new Request.Builder();
        req.url(baseUrl);
        req.addHeader("User-Agent", DEF_USER_AGENT);
        req.addHeader("Accept-Language", "ru,en;q=0.9");
        okHttpClient.newCall(req.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("ok", false);
                bundle.putString("error", "Ошибка выполнения запроса, Подробнее:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                if (response.isSuccessful()) {
                    if (JsonParser.parseString(body).isJsonObject()) {
                        try {
                            JSONObject jsonBody = new JSONObject(body);
                            jsonBody = jsonBody.has("response") ? jsonBody.getJSONObject("response") : new JSONObject();
                            JSONArray items = jsonBody.has("items") ? jsonBody.getJSONArray("items") : new JSONArray();
                            JSONArray profiles = jsonBody.has("profiles") ? jsonBody.getJSONArray("profiles") : new JSONArray();
                            JSONArray groups = jsonBody.has("groups") ? jsonBody.getJSONArray("groups") : new JSONArray();

                            ArrayList<CommentVideo> comments = new ArrayList<>();
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                try {
                                    String userName = "NO NAME";
                                    int id = item.has("id") ? item.getInt("id") : 0;
                                    int from_id = item.has("from_id") ? item.getInt("from_id") : 0;
                                    int date = item.has("date") ? item.getInt("date") : 0;
                                    String text = item.has("text") ? item.getString("text") : "";
                                    String photo_100 = "";
                                    if (from_id > 0) {
                                        // Пользователь
                                        int countProfiles = profiles.length();
                                        for (int j = 0; j < countProfiles; j++) {
                                            if (from_id == profiles.getJSONObject(j).getInt("id")) {
                                                photo_100 = profiles.getJSONObject(j).has("photo_100") ? profiles.getJSONObject(j).getString("photo_100") : "";
                                                userName = (profiles.getJSONObject(j).has("first_name") ? profiles.getJSONObject(j).getString("first_name") : "") + " " + (profiles.getJSONObject(j).has("last_name") ? profiles.getJSONObject(j).getString("last_name") : "");
                                            }
                                        }
                                    } else {
                                        // Группа
                                        int countGroups = groups.length();
                                        for (int j = 0; j < countGroups; j++) {
                                            if (Math.abs(from_id) == groups.getJSONObject(j).getInt("id")) {
                                                photo_100 = groups.getJSONObject(j).has("photo_100") ? groups.getJSONObject(j).getString("photo_100") : "";
                                                userName = groups.getJSONObject(j).has("name") ? groups.getJSONObject(j).getString("name") : "";
                                            }
                                        }

                                    }


                                    comments.add(new CommentVideo(id, from_id, date, photo_100, text, userName));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    onFailure(call, new IOException("1. Ошибка создания объекта JSONObject: " + e.getMessage()));
                                }
                            }
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("ok", true);
                            bundle.putSerializable("comments", comments);
                            Message msg = new Message();
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onFailure(call, new IOException("2. Ошибка создания объекта JSONObject: " + e.getMessage()));
                        }
                    }
                } else {
                    onFailure(call, new IOException(body));
                }
            }
        });


    }

    public interface GetCommentsVideoCallback {
        void onSuccess(ArrayList<CommentVideo> comments);

        void onError(Exception e);
    }

    public static class CommentVideo implements Serializable {

        public CommentVideo(int id, int from_id, int date, String photo_100, String text, String userName) {
            this.photo_100 = photo_100;
            this.userName = userName;
            this.id = id;
            this.from_id = from_id;
            this.date = date;
            this.text = text;
        }

        private String userName = "NO NAME";
        private int id = 0;
        private int from_id = 0;
        private int date = 0;
        private String text = "";
        private String photo_100 = "";


        public String getPhoto_100() {
            return photo_100;
        }

        public String getText() {
            return text;
        }

        public int getDate() {
            return date;
        }

        public int getFrom_id() {
            return from_id;
        }

        public int getId() {
            return id;
        }

        public String getUserName() {
            return userName;
        }
    }


    public static class VideoItem implements Serializable {
        public static class File implements Serializable {
            public String getMp4_144() {
                return mp4_144;
            }

            public String getMp4_240() {
                return mp4_240;
            }

            public String getMp4_360() {
                return mp4_360;
            }

            public String getMp4_480() {
                return mp4_480;
            }

            public String getMp4_720() {
                return mp4_720;
            }

            public String getMp4_1080() {
                return mp4_1080;
            }

            public String getHls() {
                return hls;
            }

            public String getDash_sep() {
                return dash_sep;
            }

            public String getDash_streams() {
                return dash_streams;
            }

            public String getHls_streams() {
                return hls_streams;
            }

            public String getFailover_host() {
                return failover_host;
            }

            private final String mp4_144;
            private final String mp4_240;
            private final String mp4_360;
            private final String mp4_480;
            private final String mp4_720;
            private final String mp4_1080;
            private final String hls;
            private final String dash_sep;
            private final String dash_streams;
            private final String hls_streams;
            private final String failover_host;

            public File(Builder builder) {
                this.mp4_144 = builder.mp4_144;
                this.mp4_240 = builder.mp4_240;
                this.mp4_360 = builder.mp4_360;
                this.mp4_480 = builder.mp4_480;
                this.mp4_720 = builder.mp4_720;
                this.mp4_1080 = builder.mp4_1080;
                this.hls = builder.hls;
                this.dash_sep = builder.dash_sep;
                this.dash_streams = builder.dash_streams;
                this.hls_streams = builder.hls_streams;
                this.failover_host = builder.failover_host;
            }

            public static class Builder {
                private String mp4_144;
                private String mp4_240;
                private String mp4_360;
                private String mp4_480;
                private String mp4_720;
                private String mp4_1080;
                private String hls;
                private String dash_sep;
                private String dash_streams;
                private String hls_streams;
                private String failover_host;

                public Builder set144(String mp4_144) {
                    this.mp4_144 = mp4_144;
                    return this;
                }

                public Builder set240(String mp4_240) {
                    this.mp4_240 = mp4_240;
                    return this;
                }

                public Builder set360(String mp4_360) {
                    this.mp4_360 = mp4_360;
                    return this;
                }

                public Builder set480(String mp4_480) {
                    this.mp4_480 = mp4_480;
                    return this;
                }

                public Builder set720(String mp4_720) {
                    this.mp4_720 = mp4_720;
                    return this;
                }

                public Builder set1080(String mp4_1080) {
                    this.mp4_1080 = mp4_1080;
                    return this;
                }

                public Builder setHls(String hls) {
                    this.hls = hls;
                    return this;
                }

                public Builder setDashSep(String dash_sep) {
                    this.dash_sep = dash_sep;
                    return this;
                }

                public Builder setDashStreams(String dash_streams) {
                    this.dash_streams = dash_streams;
                    return this;
                }

                public Builder setHlsStreams(String hls_streams) {
                    this.hls_streams = hls_streams;
                    return this;
                }

                public Builder setFailoverHost(String failover_host) {
                    this.failover_host = failover_host;
                    return this;
                }

                public File build() {
                    return new File(this);
                }
            }

        }

        public static class Trailer implements Serializable {
            private final String mp4_240;
            private final String mp4_360;
            private final String mp4_480;
            private final String mp4_720;
            private final String mp4_1080;

            public String getMp4_1080() {
                return mp4_1080;
            }

            public String getMp4_720() {
                return mp4_720;
            }

            public String getMp4_480() {
                return mp4_480;
            }

            public String getMp4_360() {
                return mp4_360;
            }

            public String getMp4_240() {
                return mp4_240;
            }


            public Trailer(Builder builder) {
                this.mp4_240 = builder.mp4_240;
                this.mp4_360 = builder.mp4_360;
                this.mp4_480 = builder.mp4_480;
                this.mp4_720 = builder.mp4_720;
                this.mp4_1080 = builder.mp4_1080;
            }

            public static class Builder {
                private String mp4_240;
                private String mp4_360;
                private String mp4_480;
                private String mp4_720;
                private String mp4_1080;

                public Builder set240(String mp4_240) {
                    this.mp4_240 = mp4_240;
                    return this;
                }

                public Builder set360(String mp4_360) {
                    this.mp4_360 = mp4_360;
                    return this;
                }

                public Builder set480(String mp4_480) {
                    this.mp4_480 = mp4_480;
                    return this;
                }

                public Builder set720(String mp4_720) {
                    this.mp4_720 = mp4_720;
                    return this;
                }

                public Builder set1080(String mp4_1080) {
                    this.mp4_1080 = mp4_1080;
                    return this;
                }

                public Trailer build() {
                    return new Trailer(this);
                }

            }
        }

        public static class Image implements Serializable {
            private final String url;

            private final int width;

            private final int height;

            public String getUrl() {
                return url;
            }

            public int getWidth() {
                return width;
            }

            public int getHeight() {
                return height;
            }

            public Image(Builder builder) {
                this.url = builder.url;
                this.width = builder.width;
                this.height = builder.height;
            }

            public static class Builder {
                private String url;
                private int width;
                private int height;

                public Builder setUrl(String url) {
                    this.url = url;
                    return this;
                }

                public Builder setWidth(int width) {
                    this.width = width;
                    return this;
                }

                public Builder setHeight(int height) {
                    this.height = height;
                    return this;
                }

                public Image build() {
                    return new Image(this);

                }
            }
        }

        public static class FirstFrame implements Serializable {
            private final String url;
            private final int width;
            private final int height;

            public String getUrl() {
                return url;
            }

            public int getWidth() {
                return width;
            }

            public int getHeight() {
                return height;
            }

            public FirstFrame(Builder builder) {
                this.url = builder.url;
                this.width = builder.width;
                this.height = builder.height;
            }

            public static class Builder {
                private String url;
                private int width;
                private int height;

                public Builder setUrl(String url) {
                    this.url = url;
                    return this;
                }

                public Builder setWidth(int width) {
                    this.width = width;
                    return this;
                }

                public Builder setHeight(int height) {
                    this.height = height;
                    return this;
                }

                public FirstFrame build() {
                    return new FirstFrame(this);
                }
            }
        }

        public static class Likes implements Serializable {
            private final int count;

            private final int user_likes;

            public int isUser_likes() {
                return user_likes;
            }

            public int getCount() {
                return count;
            }

            public Likes(Builder builder) {
                this.count = builder.count;
                this.user_likes = builder.user_likes;
            }

            public static class Builder {
                private int count;
                private int user_likes;

                public Builder setCount(int count) {
                    this.count = count;
                    return this;
                }

                public Builder setUser_likes(int user_likes) {
                    this.user_likes = user_likes;
                    return this;
                }

                public Likes build() {
                    return new Likes(this);
                }
            }
        }

        public static class Reposts implements Serializable {
            private final int count;
            private final int wall_count;
            private final int mail_count;
            private final int user_reposted;

            public int getUser_reposted() {
                return user_reposted;
            }

            public int getCount() {
                return count;
            }

            public int getWall_count() {
                return wall_count;
            }

            public int getMail_count() {
                return mail_count;
            }

            public Reposts(Builder builder) {
                this.count = builder.count;
                this.wall_count = builder.wall_count;
                this.mail_count = builder.mail_count;
                this.user_reposted = builder.user_reposted;
            }

            public static class Builder {
                private int count;
                private int wall_count;
                private int mail_count;
                private int user_reposted;

                public Builder setCount(int count) {
                    this.count = count;
                    return this;
                }

                public Builder setWallCount(int wall_count) {
                    this.wall_count = wall_count;
                    return this;
                }

                public Builder setMailCount(int mail_count) {
                    this.mail_count = mail_count;
                    return this;
                }

                public Builder setUserReposted(int user_reposted) {
                    this.user_reposted = user_reposted;
                    return this;
                }

                public Reposts build() {
                    return new Reposts(this);
                }
            }

        }

        public File getFiles() {
            return files;
        }

        public Trailer getTrailers() {
            return trailers;
        }

        public ArrayList<Image> getImages() {
            return images;
        }

        public ArrayList<FirstFrame> getFirst_frames() {
            return first_frames;
        }

        public Likes getLikes() {
            return likes;
        }

        public Reposts getReposts() {
            return reposts;
        }

        public long getAdding_date() {
            return adding_date;
        }

        public int getComments() {
            return comments;
        }

        public String getDescription() {
            return description;
        }

        public int getDuration() {
            return duration;
        }

        public int getId() {
            return id;
        }

        public int getOwner_id() {
            return owner_id;
        }

        public boolean isIs_author() {
            return is_author;
        }

        public String getOv_id() {
            return ov_id;
        }

        public String getTitle() {
            return title;
        }

        public boolean isIs_favorite() {
            return is_favorite;
        }

        public String getPlayer() {
            return player;
        }

        public String getTrack_code() {
            return track_code;
        }

        public String getType() {
            return type;
        }

        public int getViews() {
            return views;
        }

        private final File files;
        private Trailer trailers = null;
        private ArrayList<Image> images = null;
        private ArrayList<FirstFrame> first_frames = null;
        private Likes likes = null;
        private Reposts reposts = null;
        private long adding_date = 0;
        private int comments = 0;
        private String description = "";
        private int duration = 0;
        private int id = 0;
        private int owner_id = 0;
        private boolean is_author = false;
        private String ov_id = "";
        private String title = "";
        private boolean is_favorite = false;
        private String player = "";
        private String track_code = "";
        private String type = "";
        private int views = 0;

        public VideoItem(Builder builder) {
            this.files = builder.files;
            this.trailers = builder.trailers;
            this.images = builder.images;
            this.first_frames = builder.first_frames;
            this.likes = builder.likes;
            this.reposts = builder.reposts;
            this.adding_date = builder.adding_date;
            this.comments = builder.comments;
            this.description = builder.description;
            this.duration = builder.duration;
            this.id = builder.id;
            this.owner_id = builder.owner_id;
            this.is_author = builder.is_author;
            this.ov_id = builder.ov_id;
            this.title = builder.title;
            this.is_favorite = builder.is_favorite;
            this.player = builder.player;
            this.track_code = builder.track_code;
            this.type = builder.type;
            this.views = builder.views;
        }

        public static class Builder implements Serializable {
            private File files;
            private Trailer trailers = null;
            private ArrayList<Image> images = null;
            private ArrayList<FirstFrame> first_frames = null;
            private Likes likes = null;
            private Reposts reposts = null;
            private long adding_date = 0;
            private int comments = 0;
            private String description = "";
            private int duration = 0;
            private int id = 0;
            private int owner_id = 0;
            private boolean is_author = false;
            private String ov_id = "";
            private String title = "";
            private boolean is_favorite = false;
            private String player = "";
            private String track_code = "";
            private String type = "";
            private int views = 0;

            public Builder setFiles(File files) {
                this.files = files;
                return this;
            }

            public Builder setTrailers(Trailer trailers) {
                this.trailers = trailers;
                return this;
            }

            public Builder setImages(ArrayList<Image> images) {
                this.images = images;
                return this;
            }

            public Builder setFirstFrames(ArrayList<FirstFrame> first_frames) {
                this.first_frames = first_frames;
                return this;
            }

            public Builder setLikes(Likes likes) {
                this.likes = likes;
                return this;
            }

            public Builder setReposts(Reposts reposts) {
                this.reposts = reposts;
                return this;
            }

            public Builder setAddingDate(long adding_date) {
                this.adding_date = adding_date;
                return this;
            }

            public Builder setCountComments(int countComments) {
                this.comments = countComments;
                return this;
            }

            public Builder setDescription(String description) {
                this.description = description;
                return this;
            }

            public Builder setDuration(int duration) {
                this.duration = duration;
                return this;
            }

            public Builder setId(int id) {
                this.id = id;
                return this;
            }

            public Builder setOwnerId(int owner_id) {
                this.owner_id = owner_id;
                return this;
            }

            public Builder setIsAuthor(boolean is_author) {
                this.is_author = is_author;
                return this;
            }

            public Builder setOvId(String ov_id) {
                this.ov_id = ov_id;
                return this;
            }

            public Builder setTitle(String title) {
                this.title = title;
                return this;
            }

            public Builder setIsFavorite(boolean is_favorite) {
                this.is_favorite = is_favorite;
                return this;
            }

            public Builder setPlayer(String player) {
                this.player = player;
                return this;
            }

            public Builder setTrackCode(String track_code) {
                this.track_code = track_code;
                return this;
            }

            public Builder setType(String type) {
                this.type = type;
                return this;
            }

            public Builder setViews(int views) {
                this.views = views;
                return this;
            }

            public VideoItem build() {
                return new VideoItem(this);
            }

        }


    }

    public static class PlaylistGroupItem implements Serializable {

        public int getCount() {
            return count;
        }

        public long getUpdatedTime() {
            return updated_time;
        }

        public int getId() {
            return id;
        }

        public int getOwnerId() {
            return owner_id;
        }

        public String getTitle() {
            return title;
        }

        public ArrayList<VideoItem.Image> getImages() {
            return images;
        }

        public boolean isIsSubscribed() {
            return is_subscribed;
        }

        public int getCanView() {
            return can_view;
        }

        public String getFirstVideoId() {
            return first_video_id;
        }

        public int getCanSubscribe() {
            return can_subscribe;
        }

        public String getResponseType() {
            return response_type;
        }


        private int count = 0; // Кол-во видео внутри плейлиста
        private long updated_time = 0; // Последнее обновление плелиста
        private int id = 0; // ID плейлиста
        private final int owner_id; // Владелец плейлиста
        private final String title; // Название плейлиста
        private ArrayList<VideoItem.Image> images = new ArrayList<>();   // Изображения группы
        private boolean is_subscribed = false; // Подписан ли на плейлист
        private int can_view = 0;        // Можно ли просмотреть
        private String first_video_id = "";  // ownerID_videoID ID первого видео
        private int can_subscribe = 0;   // можно ли подписаться на обновления плейлиста
        private String response_type = "";   // Тип ответа. min / full

        public PlaylistGroupItem(Builder builder) {
            this.count = builder.count;
            this.updated_time = builder.updated_time;
            this.id = builder.id;
            this.owner_id = builder.owner_id;
            this.title = builder.title;
            this.images = builder.images;
            this.is_subscribed = builder.is_subscribed;
            this.can_view = builder.can_view;
            this.first_video_id = builder.first_video_id;
            this.can_subscribe = builder.can_subscribe;
            this.response_type = builder.responseType;
        }

        public static class Builder implements Serializable {
            private int count = 0; // Кол-во видео внутри плейлиста
            private long updated_time = 0; // Последнее обновление плелиста
            private int id = 0; // ID плейлиста
            private int owner_id; // Владелец плейлиста
            private String title; // Название плейлиста
            private ArrayList<VideoItem.Image> images = new ArrayList<>();   // Изображения группы
            private boolean is_subscribed = false; // Подписан ли на плейлист
            private int can_view = 0;        // Можно ли просмотреть
            private String first_video_id = "";  // ownerID_videoID ID первого видео
            private int can_subscribe = 0;   // можно ли подписаться на обновления плейлиста
            private String responseType = "";   // Тип ответа. min / full

            public void setResponseType(String response_type) {
                this.responseType = response_type;
            }

            public void setCanSubscribe(int can_subscribe) {
                this.can_subscribe = can_subscribe;
            }

            public void setFirstVideo_id(String first_video_id) {
                this.first_video_id = first_video_id;
            }

            public void setCanView(int can_view) {
                this.can_view = can_view;
            }

            public void setIsSubscribed(boolean is_subscribed) {
                this.is_subscribed = is_subscribed;
            }

            public void setImages(ArrayList<VideoItem.Image> images) {
                this.images = images;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public void setOwnerId(int owner_id) {
                this.owner_id = owner_id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public void setUpdatedTime(long updated_time) {
                this.updated_time = updated_time;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public PlaylistGroupItem build() {
                return new PlaylistGroupItem(this);
            }
        }

    }

    public static class GroupItem implements Serializable {
        private final ArrayList<PlaylistGroupItem> playLists;
        private final ArrayList<VideoItem> videoItems;
        private final String name;
        private final String screen_name;
        private final String photo_100;
        private final int countVideo;
        private final int is_closed;

        public String getPhoto_100() {
            return photo_100;
        }

        public int getIs_closed() {
            return is_closed;
        }

        public String getScreenName() {
            return screen_name;
        }

        public int getCountVideo() {
            return countVideo;
        }

        public String getTitleGroup() {
            return name;
        }

        public ArrayList<VideoItem> getVideoItems() {
            return videoItems;
        }

        public ArrayList<PlaylistGroupItem> getPlayLists() {
            return playLists;
        }

        public GroupItem(ArrayList<PlaylistGroupItem> playLists,
                         ArrayList<VideoItem> videoItems,
                         String name,
                         String screen_name,
                         int countVideo,
                         int is_closed, String photo_100) {
            this.photo_100 = photo_100;
            this.is_closed = is_closed;
            this.screen_name = screen_name;
            this.playLists = playLists;
            this.videoItems = videoItems;
            this.name = name;
            this.countVideo = countVideo;
        }

    }


}
