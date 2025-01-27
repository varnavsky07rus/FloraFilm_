package com.alaka_ala.florafilm.sys.update_app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.alaka_ala.florafilm.sys.AsyncThreadBuilder;
import com.google.gson.JsonParser;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateApp {
    public static final String baseUrlAPK = "https://github.com/varnavsky07rus/FloraFilm_/raw/refs/heads/master/app/release/app-release.apk";
    public static final String output_metadata_json = "https://raw.githubusercontent.com/varnavsky07rus/FloraFilm_/refs/heads/master/app/release/output-metadata.json";


    public static void findUpdate(Context context, FindUpdateCallback finderCb) {
        final String ERROR = "ERROR";
        final String SUCCESS = "SUCCESS";

        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.getData().getString("status").equals(SUCCESS)) {
                    finderCb.onUpdateDetect(msg.getData().getString("urlApk"), msg.getData().getString("versionName"), msg.getData().getInt("versionCode"), getCurrentAppVersionCode(context));
                } else if (msg.getData().getString("status").equals(ERROR)) {
                    finderCb.findError(msg.getData().getString("error"));
                }
                return false;
            }
        });
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(output_metadata_json);
        okHttpClient.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Bundle bundle = new Bundle();
                bundle.putString("status", ERROR);
                bundle.putString("error", e.getMessage());
                Message msg = new Message();
                msg.setData(bundle);
                handler.sendMessage(msg);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                if (response.isSuccessful()) {
                    if (JsonParser.parseString(json).isJsonObject()) {
                        try {
                            JSONObject jsonBody = new JSONObject(json);
                            JSONArray elements = jsonBody.getJSONArray("elements");
                            JSONObject element = elements.getJSONObject(0);
                            int versionCode = element.getInt("versionCode");        // 1
                            String versionName = element.getString("versionName");  // 1.0.0
                            String variantName = jsonBody.getString("variantName"); // release/debug

                            if (getCurrentAppVersionCode(context) < versionCode) {
                                Bundle bundle = new Bundle();
                                bundle.putString("status", SUCCESS);
                                bundle.putString("urlApk", baseUrlAPK);
                                bundle.putInt("versionCode", versionCode);
                                bundle.putString("variantName", variantName);
                                bundle.putString("versionName", versionName);
                                Message msg = new Message();
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putString("status", ERROR);
                                bundle.putString("error", "Нет новых обновлений");
                                Message msg = new Message();
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }


                        } catch (JSONException e) {
                            onFailure(call, new IOException("Ошибка создания/парсинга JSONObject | [metadata_json]"));
                        }
                    }
                } else {
                    onFailure(call, new IOException("Ошибка поиска обновлений | [metadata_json]"));
                }
            }
        });
    }
    public interface FindUpdateCallback {
        void onUpdateDetect(String urlApk, String newVersionName, int newVersionCode, int currentVersionCode);
        void findError(String error);
    }

    public static String getCurrentAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getCurrentAppVersionCode(Context context) {
        int versionCode = 0; // Инициализируем versionCode значением по умолчанию
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode; // Получаем versionCode
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
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
