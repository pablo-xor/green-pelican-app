package com.paulsoft.service;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import java.util.Optional;


public class PreferencesRepository {

    public static final String PREFERENCES_NAME = "com.paulsoft.pelican.settings";
    private SharedPreferences sharedPreferences;

    public PreferencesRepository(ContextWrapper contextWrapper) {
        sharedPreferences = contextWrapper.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public <T> void save(String key, Class<T> type, T value) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(Integer.class.equals(type)) {
            editor.putInt(key, (Integer) value);
        } else if (Boolean.class.equals(type)) {
            editor.putBoolean(key, (Boolean) value);
        } else if (Float.class.equals(type)) {
            editor.putFloat(key, (Float) value);
        } else if (Long.class.equals(type)) {
            editor.putLong(key, (Long) value);
        } else if (String.class.equals(type)) {
            editor.putString(key, (String) value);
        } else throw new UnsupportedOperationException("Unsupported type");

        editor.apply();

    }

    public <T> Optional<T> load(String key, Class<T> type) {
        return Optional.ofNullable(load(key, type, null));
    }

    public <T> T load(String key, Class<T> type, T defaultValue) {

        if(!sharedPreferences.contains(key)) {
           return defaultValue;
        }

        if(Integer.class.equals(type)) {
            return (T) Integer.valueOf(sharedPreferences.getInt(key, -1));
        } else if (Boolean.class.equals(type)) {
            return (T) Boolean.valueOf(sharedPreferences.getBoolean(key, false));
        } else if (Float.class.equals(type)) {
            return (T) Float.valueOf(sharedPreferences.getFloat(key, -1));
        } else if (Long.class.equals(type)) {
            return (T) Long.valueOf(sharedPreferences.getLong(key, -1L));
        } else if (String.class.equals(type)) {
            return (T) sharedPreferences.getString(key, "");
        } else throw new UnsupportedOperationException("Unsupported type");

    }
}
