package com.alaka_ala.florafilm.sys.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsApp {

    public SettingsApp(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public Context getContext() {
        return context;
    }

    private final Context context;

    private final SharedPreferences preferences;

    public void saveParam(String key, String val) {
        preferences.edit().putString(key, val).apply();
    }

    public String getParam(String key) {
        return preferences.getString(key, "");
    }

    public void saveParam(String key, int val) {
        preferences.edit().putInt(key, val).apply();
    }

    public int getParam(String key, int defVal) {
        return preferences.getInt(key, defVal);
    }

    public void saveParam(String key, boolean val) {
        preferences.edit().putBoolean(key, val).apply();
    }

    public boolean getParam(String key, boolean defVal) {
        return preferences.getBoolean(key, defVal);
    }

    public void saveParam(String key, float val) {
        preferences.edit().putFloat(key, val).apply();
    }

    public float getParam(String key, float defVal) {
        return preferences.getFloat(key, defVal);
    }

    public void saveParam(String key, long val) {
        preferences.edit().putLong(key, val).apply();
    }

    public long getParam(String key, long defVal) {
        return preferences.getLong(key, defVal);
    }

    public void clearSettings() {
        preferences.edit().clear().apply();
    }

    public void removeParam(String key) {
        preferences.edit().remove(key).apply();
    }

    public boolean hasParam(String key) {
        return preferences.contains(key);
    }

    public void saveParam(String key, ArrayList<String> list) {
        preferences.edit().putString(key, list.toString()).apply();
    }

    public ArrayList<String> getParam(String key, ArrayList<String> defVal) {
        String str = preferences.getString(key, "");
        Object[] list = Arrays.stream(str.split(",")).toArray();
        ArrayList<String> listArray = new ArrayList<>();
        for (Object o : list) {
            listArray.add(o.toString());
        }
        return listArray;
    }

    public static class SettingsKeys {
        /**
         * Плавающая кнопка, которая открывает боковое меню, а при длительном нажатии открывает popUpMenu
         *
         * @_ Используется в MainActivity
         * @ID floatActionButtonMenu
         * @DefsVal {@link SettingsDefsVal#}
         */
        public static String FLOAT_ACTION_BUTTON_MENU = "0x00001";

        public static String FULL_SCREEN_APP_MODE = "0x00002";

        public static String INTERFACE_ANIMATION = "0x00003";

        /**Ключ параметра помощника поиска, на странице "Глобавльный Поиск"
         * при включенном помощнике отображается слой в котором говорится
         * чтобы выполнить поиск необходимо нажать на кнопку на клавиатуре*/
        public static String HIDE_HELPER_SEARCH = "0x00004";

    }

    public static class SettingsDefsVal {
        /**Стандартное значение настройки параметра {@link SettingsKeys#FLOAT_ACTION_BUTTON_MENU}*/
        public static boolean VISIBLE_FLOAT_ACTION_BUTTON_MENU = true;

        /**Стандартное значение настройки параметра {@link SettingsKeys#FULL_SCREEN_APP_MODE}*/
        public static boolean FULL_SCREEN_APP_MODE = false;

        /**Стандартное значение настройки параметра {@link SettingsKeys#INTERFACE_ANIMATION}*/
        public static boolean DEF_INTERFACE_ANIMATION = true;

        /**Стандартное значение настройки параметра {@link SettingsKeys#HIDE_HELPER_SEARCH}
         * Более подробная информация описана в документации ключа.
         * По умолчанию скрытие выключено, если False - значит слой видно, если True - скрыто*/
        public static boolean HIDE_HELPER_SEARCH = false;

    }


}
