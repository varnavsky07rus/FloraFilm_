package com.alaka_ala.florafilm.ui.vk;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AccountManager extends LoginVkActivity {

    private static final String PREF_NAME = "account_prefs";
    private static final String KEY_ACCOUNT = "account_key";

    // Метод для сохранения объекта Account в SharedPreferences
    public static boolean saveAccount(Context context, Account account) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(account);
            oos.close();
            String serializedAccount = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            editor.putString(KEY_ACCOUNT, serializedAccount);
            editor.apply();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Метод для получения объекта Account из SharedPreferences
    public static Account getAccount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String serializedAccount = prefs.getString(KEY_ACCOUNT, null);
        if (serializedAccount == null) {
            return null;
        }
        try {
            byte[] bytes = Base64.decode(serializedAccount, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Account account = (Account) ois.readObject();
            ois.close();
            return account;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Метод для удаления объекта Account из SharedPreferences
    public static boolean deleteAccount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_ACCOUNT);
        editor.apply();
        return true;
    }

    public static String getAccessToken(Context context) {
        Account account = getAccount(context);
        return account.getAccessToken();
    }
}