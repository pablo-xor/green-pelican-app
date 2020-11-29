package com.paulsoft.pelican.ranking.repository;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import java.util.Optional;


public class PreferencesRepository {

    public static final String PREFERENCES_NAME = "com.paulsoft.pelican.settings";
    private SharedPreferences sharedPreferences;

    public PreferencesRepository(Context contextWrapper) {
        sharedPreferences = contextWrapper.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public <T> void save(Preference preference, Class<T> type, T value) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(Integer.class.equals(type)) {
            editor.putInt(preference.getKey(), (Integer) value);
        } else if (Boolean.class.equals(type)) {
            editor.putBoolean(preference.getKey(), (Boolean) value);
        } else if (Float.class.equals(type)) {
            editor.putFloat(preference.getKey(), (Float) value);
        } else if (Long.class.equals(type)) {
            editor.putLong(preference.getKey(), (Long) value);
        } else if (String.class.equals(type)) {
            editor.putString(preference.getKey(), (String) value);
        } else throw new UnsupportedOperationException("Unsupported type");

        editor.apply();

    }

    public <T> Optional<T> load(Preference preference, Class<T> type) {
        return Optional.ofNullable(load(preference.getKey(), type, null));
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
