package com.alaka_ala.florafilm.sys.update_app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.alaka_ala.florafilm.sys.AsyncThreadBuilder;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class UpdateApp {
    private static final String baseURL = "https://github.com/varnavsky07rus/FloraFilm_";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 YaBrowser/24.1";


    public static void findNewVersion(Context context, FindUpdateCallback fuc) {
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();
                String error = bundle.getString("error", "");
                String moreError = bundle.getString("moreError", "");
                if (!error.isEmpty()) {
                    fuc.onError(error, moreError);
                    return false;
                }
                String newVersionCode = bundle.getString("newVersionCode", "");
                String description = bundle.getString("description", "");
                String urlAPK = bundle.getString("urlAPK", "");
                String urlOutput_metadata_json = bundle.getString("urlOutput_metadata_json", "");
                if (!newVersionCode.isEmpty()) {
                    fuc.onUpdateAvailable(newVersionCode, description, urlAPK, urlOutput_metadata_json);
                } else {
                    fuc.onNoUpdateAvailable();
                }
                return true;
            }
        });


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document page = Jsoup.connect(baseURL).header("User-Agent", USER_AGENT).get();
                    Elements elements = page.getElementsByClass("css-truncate css-truncate-target text-bold mr-2");
                    String version = elements.get(0).text();
                    if (version.startsWith("v")) {
                        String appVersion = getAppVersion(context);
                        if (!appVersion.isEmpty()) {
                            if (!appVersion.equals(version)) {
                                // Доступно обновление
                                String url = "https://github.com/varnavsky07rus/FloraFilm_/releases/tag/" + version;
                                page = Jsoup.connect(url).header("User-Agent", USER_AGENT).get();
                                elements = page.getElementsByClass("markdown-body my-3");

                                String description = elements.get(0).text();
                                String urlAPK = "https://github.com/varnavsky07rus/FloraFilm_/releases/download/" + version + "/app-release.apk";
                                String urlOutput_metadata_json = "https://github.com/varnavsky07rus/FloraFilm_/releases/download/" + version + "/output_metadata.json";

                                Bundle bundle = new Bundle();
                                bundle.putString("error", "");
                                bundle.putString("moreError", "");
                                bundle.putString("newVersionCode", version);
                                bundle.putString("description", description);
                                bundle.putString("urlAPK", urlAPK);
                                bundle.putString("urlOutput_metadata_json", urlOutput_metadata_json);
                                Message msg = new Message();
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            } else {
                                // Нет обновления
                                Bundle bundle = new Bundle();
                                bundle.putString("error", "");
                                bundle.putString("moreError", "");
                                bundle.putString("newVersionCode", "");
                                bundle.putString("description", "");
                                bundle.putString("urlAPK", "");
                                bundle.putString("urlOutput_metadata_json", "");
                                Message msg = new Message();
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        }

                    }


                } catch (IOException e) {
                    // Нет обновления
                    Bundle bundle = new Bundle();
                    bundle.putString("error", "Ошибка поиска обновлений");
                    bundle.putString("moreError", e.getMessage());
                    bundle.putString("newVersionCode", "");
                    bundle.putString("description", "");
                    bundle.putString("urlAPK", "");
                    bundle.putString("urlOutput_metadata_json", "");
                    Message msg = new Message();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }
        });
        thread.start();


    }

    public interface FindUpdateCallback {
        void onUpdateAvailable(String newVersionCode, String description, String urlAPK, String urlMetadataJson);

        void onError(String error, String moreError);

        default void onNoUpdateAvailable() {
        }

        ;
    }

    public static String getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void downloadUpdate(Context context, String urlAPK, UpdateCallbackDownload ucd) {
        // Создаем path для файла
        File file = new File(context.getExternalCacheDir().getPath(), "app-release.apk");

        // Загрузка файла
        AsyncThreadBuilder asyncThreadBuilder = new AsyncThreadBuilder() {
            @Override
            public Runnable start(Handler finishHandler) {
                return new Runnable() {
                    @Override
                    public void run() {
                        // Создаем асинхронную задачу на получение и обновление размера файла
                        if (file.exists()) {
                            file.delete();
                        }
                        try {
                            FileUtils.copyURLToFile(new URL(urlAPK), file);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isDownloaded", true);
                            Message msg = new Message();
                            msg.setData(bundle);
                            finishHandler.sendMessage(msg);

                        } catch (IOException e) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isDownloaded", false);
                            Message msg = new Message();
                            msg.setData(bundle);
                            finishHandler.sendMessage(msg);
                            e.printStackTrace();
                        }
                    }
                };
            }

            @Override
            public void finishHandler(Bundle bundle) {
                if (!bundle.getBoolean("isDownloaded")) {
                    ucd.onError("Ошибка загрузки обновления", "");
                    return;
                }
                ucd.onUpdateDownloaded(file);
            }
        };
        asyncThreadBuilder.onStart();

        // Получение и обновление размера файла по мере загрузки
        AsyncThreadBuilder asyncThreadBuilder2 = new AsyncThreadBuilder() {
            @Override
            public Runnable start(Handler finishHandler) {
                return new Runnable() {
                    @Override
                    public void run() {
                        // Получаем размер файла
                        long totalFileSize = getFileSizeFromUrl(urlAPK);
                        while (true) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            long downloadedFileSize = file.length();
                            int progress = (int) ((downloadedFileSize * 100) / totalFileSize);
                            String formatedDownloadedSize = String.format("%.2f", downloadedFileSize / 1024.0 / 1024.0);
                            String formatedCountSize = String.format("%.2f", totalFileSize / 1024.0 / 1024.0);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isDownloaded", false);
                            bundle.putInt("progress", progress);
                            bundle.putString("downloadedSize", formatedDownloadedSize);
                            bundle.putString("countSize", formatedCountSize);
                            Message msg = new Message();
                            msg.setData(bundle);
                            if (progress == 100) {
                                bundle.putBoolean("isDownloaded", true);
                                finishHandler.sendMessage(msg);
                                break;
                            }
                            finishHandler.sendMessage(msg);
                        }


                    }
                };
            }

            @Override
            public void finishHandler(Bundle bundle) {
                ucd.onProgressDownload(bundle.getInt("progress"), bundle.getString("downloadedSize"), bundle.getString("countSize"));
            }
        };
        asyncThreadBuilder2.onStart();


    }

    private static long getFileSizeFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //connection.setRequestMethod("HEAD"); // Запрашиваем только заголовки
            connection.connect();
            long contentLength = connection.getContentLengthLong();
            return contentLength;
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Возвращаем -1 в случае ошибки
        }
    }

    public interface UpdateCallbackDownload {
        void onUpdateDownloaded(File file);

        void onError(String error, String moreError);

        void onProgressDownload(int progress, String downloadedSize, String countSize);
    }


}
