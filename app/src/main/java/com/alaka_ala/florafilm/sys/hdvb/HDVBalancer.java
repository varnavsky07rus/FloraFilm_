package com.alaka_ala.florafilm.sys.hdvb;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.alaka_ala.florafilm.sys.vibix.Vibix.USER_AGENT;

import static java.net.HttpURLConnection.HTTP_OK;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alaka_ala.florafilm.sys.hdvb.models.HDVBFilm;
import com.alaka_ala.florafilm.sys.hdvb.models.HDVBSerial;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HDVBalancer {
    public static final String TYPE_CONTENT_FILM = "movie";
    public static final String TYPE_CONTENT_SERIAL = "serial";
    private final Map<String, String> headers = new HashMap<>();
    private final String API_KEY;
    private static String X_CSRF_TOKEN = "AALlPbfua1Kxj1K3Ohk$rlmBM-zm3e9ENIgU-sLEuU6K5OWgpdwEG8DyEC7FLwvV";
    private static String ORIGIN = "[url]vb17123filippaaniketos.pw[/url]";
    private static String IFRAME = "";
    private static String FILE;
    private static String HREF;                                                                // всегда актуальный заголовок headers - origin
    private static String CUID;
    private static String REFERER = "[url=vb17123filippaaniketos.pw][/url]";
    private static final String HDVB_API_DOMAIN = "https://apivb.com";


    public HDVBalancer(String apiKey) {
        API_KEY = apiKey;
        createMapHeaders();
    }

    public void parse(int kinopoisk_id, ResultParseCallback rpc) {
        connectHDVBApi(kinopoisk_id, rpc);
    }

    // Сначала получаем инфу из офицального API
    private void connectHDVBApi(int kinopoiskId, ResultParseCallback rpc) {
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();
                if (bundle == null) return false;
                int code = 0;
                if (bundle.containsKey("code")) code = bundle.getInt("code");
                String message = bundle.getString("message");
                if (code == 404) {
                    // Фильма нет в базе
                    rpc.noData();
                } else if (code == 503) {
                    // Неизвестнаяя ошибка (сервис не доступен)
                    rpc.error(code, message);
                } else if (code == 423) {
                    // Контент заблокирован в вашей стране
                    rpc.error(code, message);
                } else if (code == 200) {


                }
                return false;
            }
        });
        String urlString = HDVB_API_DOMAIN + "/api/videos.json?id_kp=" + kinopoiskId + "&token=" + API_KEY;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder();
        for (String headerKey : headers.keySet()) {
            requestBuilder.addHeader(headerKey, Objects.requireNonNull(headers.get(headerKey)));
        }
        requestBuilder.url(urlString);
        Request request = requestBuilder.build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bundle bundle = new Bundle();
                bundle.putInt("code", 503); // сервис недоступен
                bundle.putString("message", e.getMessage());
                Message msg = new Message();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    if (JsonParser.parseString(body).isJsonArray()) {
                        try {
                            JSONArray jsonArray = new JSONArray(body);
                            if (jsonArray.length() == 0) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("code", 404); // Контент не найден
                                bundle.putString("message", "Фильм/Сериал отсутствует в базе HDVB");
                                Message msg = new Message();
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                                return;
                            }

                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String type = jsonObject.getString("type");
                            if (type.equals(TYPE_CONTENT_FILM)) {
                                parseFilm(jsonArray);
                            } else if (type.equals(TYPE_CONTENT_SERIAL)) {
                                parseSerial(jsonArray);
                            }


                        } catch (JSONException ignored) {
                            Log.e("HDVB", "Ошибка парсинга JSON #1");
                        }
                    }
                } else {
                    onFailure(call, new IOException("Ошибка соединения с сервером"));
                }
            }

            private void parseSerial(JSONArray jsonArray) {
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        IFRAME = jsonArray.getJSONObject(i).getString("iframe_url");
                        String htmlJSONStr = getHtmlPage(IFRAME);
                        if (JsonParser.parseString(htmlJSONStr).isJsonObject()) {
                            JSONObject htmlJson = null;
                            htmlJson = new JSONObject(htmlJSONStr);
                            String file = htmlJson.getString("file");   // Токен (path) плейлиста для дальнейших запросов
                            String user_country = htmlJson.getString("user_country").toUpperCase(Locale.ROOT);
                            String translator = htmlJson.getString("translator");
                            String userIp = htmlJson.getString("userIp");
                            String movie = htmlJson.getString("movie");
                            String key = htmlJson.getString("key");
                            String href = htmlJson.getString("href");
                            String cuid = htmlJson.getString("cuid");
                            X_CSRF_TOKEN = key;
                            FILE = file;
                            HREF = href;
                            ORIGIN = "[url]" + HREF + "[/url]";
                            CUID = cuid;

                            // Создаем ссылку на получение плейлиста из строки file
                            String filesUrl = IFRAME.replaceAll(".com.+", ".com") + file + "?key=" + key + "&href=" + href + "&cuid=" + cuid;

                            OkHttpClient okHttpClient = new OkHttpClient();
                            Request.Builder requestBuilder = new Request.Builder();
                            createMapHeaders();
                            for (String headerKey : headers.keySet()) {
                                // Перезаписываем заголовки (на всякий)
                                //requestBuilder.removeHeader(headerKey);
                                requestBuilder.addHeader(headerKey, Objects.requireNonNull(headers.get(headerKey)));
                            }
                            requestBuilder.url(filesUrl);
                            okHttpClient.newCall(requestBuilder.build()).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String body = response.body().string();
                                }
                            });

                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putInt("code", 418); // я чайник ( необходимо изменить Regex для выборки JSON объекта из HTML страницы)
                            bundle.putString("message", "Ошибка парсинга данных. Code-418");
                            bundle.putString("body", htmlJSONStr);
                            bundle.putString("url", IFRAME);
                            Message msg = new Message();
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            private void parseFilm(JSONArray jsonArray) {

            }

            // Получает HTML страницу и выполняет поиск JSONObject методом extractJson(). Возвращает JSON
            @NonNull
            private String getHtmlPage(String iframe) {
                Document html = null;
                try {
                    html = Jsoup.connect(iframe).headers(headers).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    Bundle bundle = new Bundle();
                    bundle.putInt("code", 423); // Контент заблокирован по ГЕО
                    bundle.putString("message", "Фильм/Сериал заблокирован в вашей стране");
                    Message msg = new Message();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    return "";
                }
                return extractJson(html.toString());
            }

        });


    }


    // Извлекает JSONObject из HTML
    private String extractJson(String html) {
        // Поиск JSONObject фалйа в HTML с помощью регулярного выражения
        Pattern pattern = Pattern.compile("playerConfigs = +(.+);{1}", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            // Если JSONObject файл найден
            int countGroup = matcher.groupCount();
            // Проверяем что группы не равны 0
            if (countGroup != 0) {
                // Вырезаем JSONObject из HTML с помощью регулярного выражения
                return matcher.group(0).replaceAll("playerConfigs = ", "").replaceAll(";", "");
            }
        } else {
            // Если не нашло JSONObject файл пробуем другой regex
            pattern = Pattern.compile("playerConfigs = +(.+); {1}", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            matcher = pattern.matcher(html);
            if (matcher.find()) {
                // Если JSONObject файл найден
                int countGroup = matcher.groupCount();
                // Проверяем что группы не равны 0
                if (countGroup != 0) {
                    // Вырезаем JSONObject из HTML с помощью регулярного выражения
                    return matcher.group(0).replaceAll("playerConfigs = ", "").replaceAll(";", "");
                }
            } else {
                pattern = Pattern.compile("playerConfigs = +(.+); {3}", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                matcher = pattern.matcher(html);
                if (matcher.find()) {
                    // Если JSONObject файл найден
                    int countGroup = matcher.groupCount();
                    // Проверяем что группы не равны 0
                    if (countGroup != 0) {
                        // Вырезаем JSONObject из HTML с помощью регулярного выражения
                        return matcher.group(0).replaceAll("playerConfigs = ", "").replaceAll(";", "");
                    }
                } else {
                    pattern = Pattern.compile("playerConfigs = +(.+);{3}", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                    matcher = pattern.matcher(html);
                    if (matcher.find()) {
                        // Если JSONObject файл найден
                        int countGroup = matcher.groupCount();
                        // Проверяем что группы не равны 0
                        if (countGroup != 0) {
                            // Вырезаем JSONObject из HTML с помощью регулярного выражения
                            return matcher.group(0).replaceAll("playerConfigs = ", "").replaceAll(";", "");
                        }
                    } else {
                        pattern = Pattern.compile("playerConfigs = +(.+); {2}", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                        matcher = pattern.matcher(html);
                    }
                    if (matcher.find()) {
                        // Если JSONObject файл найден
                        int countGroup = matcher.groupCount();
                        // Проверяем что группы не равны 0
                        if (countGroup != 0) {
                            // Вырезаем JSONObject из HTML с помощью регулярного выражения
                            return matcher.group(0).replaceAll("playerConfigs = ", "").replaceAll(";", "");
                        }
                    } else {
                        pattern = Pattern.compile("playerConfigs = +(.+);{2}", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                        matcher = pattern.matcher(html);
                    }
                }
            }
        }

        return "";
    }

    // Создание заголовков
    private void createMapHeaders() {
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36";
        final String ACCEPT = "*/*";
        final String ACCEPT_ENCODING = "text, deflate, br";
        final String ACCEPT_LANGUAGE = "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7";
        final String CONTENT_TYPE = "application/x-www-form-urlencoded";
        final String SEC_CH_UA = "Not A;Brand\";v=\"99\", \"Chromium\";v=\"99\", \"Google Chrome\";v=\"99\"";
        final String SEC_CH_UA_MOBILE = "?1";
        final String SEC_CH_UA_PLATFORM = "Android";
        final String SEC_FETCH_DEST = "empty";
        final String SEC_FETCH_MODE = "cors";
        final String SEC_FETCH_SITE = "same-origin";
        REFERER = "[url=" + IFRAME + "][/url]";

        headers.put("User-Agent", USER_AGENT);
        headers.put("accept", ACCEPT);
        headers.put("accept-encoding", ACCEPT_ENCODING);
        headers.put("accept-language", ACCEPT_LANGUAGE);
        headers.put("content-type", CONTENT_TYPE);
        headers.put("origin", ORIGIN);
        headers.put("referer", REFERER);
        headers.put("sec-ch-ua", SEC_CH_UA);
        headers.put("sec-ch-ua-mobile", SEC_CH_UA_MOBILE);
        headers.put("sec-ch-ua-platform", SEC_CH_UA_PLATFORM);
        headers.put("sec-fetch-dest", SEC_FETCH_DEST);
        headers.put("sec-fetch-mode", SEC_FETCH_MODE);
        headers.put("sec-fetch-site", SEC_FETCH_SITE);
        headers.put("x-csrf-token", X_CSRF_TOKEN);
    }

    public interface ResultParseCallback {
        void noData();

        void serial(int code, HDVBSerial serial);

        void film(int code, HDVBFilm film);

        void error(int code, String error);

        //void contentBlocked();
    }



}
