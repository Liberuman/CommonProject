package com.sxu.commonproject.view;

import android.content.Context;
import android.content.SharedPreferences;

import com.sxu.commonproject.app.CommonApplication;

/**
 * Created by juhg on 16/3/8.
 */
public class PreferenceUtil {

    public static String PREFERENCE_NAME = "CommonData";

    public static boolean putString(String key, String value) {
        SharedPreferences preferences = CommonApplication.getInstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getString(String key) {
        SharedPreferences preferences = CommonApplication.getInstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    public static String getString(String key, String defaultValue) {
        SharedPreferences preferences = CommonApplication.getInstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(key, defaultValue);
    }

    public static boolean putInt(String key, int value) {
        SharedPreferences preferences = CommonApplication.getInstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static int getInt(String key) {
        SharedPreferences preferences = CommonApplication.getInstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }

    public static int getInt(String key, int defaultValue) {
        SharedPreferences preferences = CommonApplication.getInstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(key, defaultValue);
    }

    public static boolean putLong(String key, long value) {
        SharedPreferences preferences = CommonApplication.getInstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    public static long getLong(String key) {
        SharedPreferences preferences = CommonApplication.getInstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getLong(key, 0);
    }

    public static long getLong(String key, long defaultValue) {
        SharedPreferences preferences = CommonApplication.getInstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getLong(key, defaultValue);
    }

    public static boolean putFloat(String key, float value) {
        SharedPreferences preferences = CommonApplication.getInstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    public static float getFloat(String key) {
        SharedPreferences preferences = CommonApplication.getInstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getFloat(key, 0.0f);
    }

    public static float getFloat(String key, float defaultValue) {
        SharedPreferences preferences = CommonApplication.getInstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getFloat(key, defaultValue);
    }

    public static boolean putBoolean(String key, boolean value) {
        SharedPreferences preferences = CommonApplication.getInstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean getBoolean(String key) {
        SharedPreferences preferences = CommonApplication.getInstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences preferences = CommonApplication.getInstance().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, defaultValue);
    }
}
