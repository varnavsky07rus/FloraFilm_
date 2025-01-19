package com.alaka_ala.florafilm.sys.kinovibe;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KinoVibe {
    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 YaBrowser/24.12.0.0 Safari/537.36";
    public interface ConnectionKinoVibe {
        void startParse();
        void finishParse(String file);
        void errorParse(String err);
    }

    private ConnectionKinoVibe connectionKinoVibe;

    public void parse(int kinopoiskId, ConnectionKinoVibe ckb) {
        connectionKinoVibe = ckb;
        ckb.startParse();
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Bundle bundle = msg.getData();
                String error = bundle.getString("error");
                int code = bundle.getInt("code");
                boolean ok = bundle.getBoolean("ok");
                String data = bundle.getString("data");
                if (code == 200) {
                    ckb.finishParse(data);
                } else {
                    ckb.errorParse(error);
                }
                return false;
            }
        });
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String baseUrl = "https://kinovibe.co/embed/kinopoisk/" + kinopoiskId;
                Document doc = null;
                try {
                    doc = Jsoup.connect(baseUrl).method(Connection.Method.GET).userAgent(USER_AGENT).get();
                } catch (IOException e) {
                    Bundle bundle = new Bundle();
                    bundle.putString("error", e.getMessage());
                    bundle.putInt("code", 404);
                    bundle.putBoolean("ok", false);
                    bundle.putString("data", "");
                    Message msg = new Message();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }

                Pattern pattern = Pattern.compile("new Playerjs\\(\\s*(\\{.+?\\})\\s*\\)");
                String jsonPlayerData = doc.toString();
                Matcher matcher = pattern.matcher(jsonPlayerData);
                if (matcher.find()) {
                    String jsonObjectString = matcher.group(1);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonObjectString);
                        String file = jsonObject.getString("file").replace(",", "");
                        if (!file.endsWith("txt")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("error", "Сериалы с KinoVibe временно не поддерживаются!");
                            bundle.putInt("code", 200);
                            bundle.putBoolean("ok", true);
                            bundle.putString("data", file);
                            Message msg = new Message();
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString("error", "Сериалы с KinoVibe временно не поддерживаются!");
                            bundle.putInt("code", 206);
                            bundle.putBoolean("ok", true);
                            bundle.putString("data", "");
                            Message msg = new Message();
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("error", "Json not found from HTML");
                    bundle.putInt("code", 204);
                    bundle.putBoolean("ok", false);
                    bundle.putString("data", "");
                    Message msg = new Message();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }

            }
        });
        thread.start();








    }




}
