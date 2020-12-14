package com.paulsoft.pelican.ranking.commons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;
import com.paulsoft.service.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.SneakyThrows;

public class ImageCache {

    private static final int MAX_SIZE = 10 * 1024 * 1024; //10MB
    private static final String IMAGE_KEY_PREFIX = "img_";
    public static final int STREAM_TRANSFER_CHUNK_SIZE = 8192;
    public static final String TAG = "ImageCache";
    private static DiskLruCache CACHE_MANAGER;

    @SneakyThrows
    public static void init(Context context) {
        if (CACHE_MANAGER == null || CACHE_MANAGER.isClosed()) {
            File cacheDir = context.getCacheDir();
            CACHE_MANAGER = DiskLruCache.open(cacheDir, 1, 1, MAX_SIZE);

            Log.d(TAG, "Cache initialized. Dir: " + cacheDir + " Total size: " + CACHE_MANAGER.size());
        }
    }

    @SneakyThrows
    public static void close() {
        if(BuildConfig.DEBUG) {
            CACHE_MANAGER.delete();
            Log.d(TAG,"Cache deleted...");
        } else {
            CACHE_MANAGER.close();
            Log.d(TAG,"Cache closed...");
        }

        CACHE_MANAGER = null;
    }

    @SneakyThrows
    public static void flush() {
        CACHE_MANAGER.flush();
    }

    @SneakyThrows
    public static boolean exists(Long id) {
        return null != CACHE_MANAGER.get(IMAGE_KEY_PREFIX + id);
    }

    @SneakyThrows
    public static Bitmap get(Long id) {
        DiskLruCache.Snapshot snapshot = CACHE_MANAGER.get(IMAGE_KEY_PREFIX + id);
        Bitmap image = null;

        if (snapshot != null) {
            InputStream in = snapshot.getInputStream(0);
            image = BitmapFactory.decodeStream(in);
        }

        return image;
    }

    private static void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[STREAM_TRANSFER_CHUNK_SIZE];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
    }

    @SneakyThrows
    public static void put(Long id, InputStream imageStream) {
        DiskLruCache.Editor editor = CACHE_MANAGER.edit(IMAGE_KEY_PREFIX + id);
        OutputStream outputStream = editor.newOutputStream(0);

        try {
            copy(imageStream, outputStream);
            editor.commit();
        } catch (IOException ex) {
            editor.abort();
        }

    }

}
