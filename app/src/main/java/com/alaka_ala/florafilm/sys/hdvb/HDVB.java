package com.alaka_ala.florafilm.sys.hdvb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HDVB {
    public static final String TYPE_CONTENT_FILM = "movie";
    public static final String TYPE_CONTENT_SERIAL = "serial";
    private final Map<String, String> headers = new HashMap<>();
    private final String API_KEY;

    private static String IFRAME = "";
    private static String FILE;
    private static String HREF;                                                                // всегда актуальный заголовок headers - origin
    private static String CUID;
    private static final String REFERER = "[url=" + IFRAME + "][/url]";




    public HDVB(String apiKey) {
        API_KEY = apiKey;
        final String TOKEN_HDVB = "b9ae5f8c4832244060916af4aa9d1939";
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
        String ORIGIN = "[url]vb17123filippaaniketos.pw[/url]";
        String X_CSRF_TOKEN = "AALlPbfua1Kxj1K3Ohk$rlmBM-zm3e9ENIgU-sLEuU6K5OWgpdwEG8DyEC7FLwvV";

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


    public void parse(int kinopoiskId) {
        connectHDVBApi(kinopoiskId);
    }

    // Сначала получаем инфу из офицального API
    private void connectHDVBApi(int kinopoiskId) {
        String urlString = "https://apivb.info/api/videos.json?id_kp=" + kinopoiskId + "&token=" + API_KEY;
        /*OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder();
        *//*for (String headerKey : headers.keySet()){
            requestBuilder.addHeader(headerKey, Objects.requireNonNull(headers.get(headerKey)));
        }*//*
        requestBuilder.url(urlString);
        Request request = requestBuilder.build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    System.out.println(body);
                }
            }
        });*/
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.connect();
                    String response;
                    BufferedReader bf;
                    if (httpURLConnection.getResponseCode() == 200) {
                        bf = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    } else {
                        bf = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
                    }
                    response = bf.readLine();
                    String s = "";

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();



    }


    private void parseFilm(int kinopoiskId) {

    }


    private void parseSerial(int kinopoiskId) {

    }










}
