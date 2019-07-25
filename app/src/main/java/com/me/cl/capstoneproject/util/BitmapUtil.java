package com.me.cl.capstoneproject.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.LruCache;
import android.view.View;

import java.util.UUID;


public class BitmapUtil {
    private static LruCache<String, Bitmap> mMemoryCache;
    static {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8; // Use 1/8th of the available memory for this memory cache.
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024; // The cache size will be measured in kilobytes rather than number of items.
            }
        };
    }

    public static void storeBitmapInIntent(Bitmap bitmap, Intent intent) {
        String key = "bitmap_" + UUID.randomUUID();
        storeBitmapInMemCache(key, bitmap);
        intent.putExtra("bitmap_num",intent.getIntExtra("bitmap_num",0)+1);
        intent.putExtra("bitmap_id_"+intent.getIntExtra("bitmap_num",0), key);

//        ByteArrayOutputStream bs = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bs);
//        intent.putExtra("background", bs.toByteArray());
    }

    public static void storeBitmapInMemCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public static Bitmap fetchBitmapFromIntent(Intent intent) {
        if (intent.getIntExtra("bitmap_num",0)<=0) {
            return null;
        }
        String key = intent.getStringExtra("bitmap_id_"+intent.getIntExtra("bitmap_num",0));
        intent.putExtra("bitmap_num",intent.getIntExtra("bitmap_num",0)-1);
//        byte[] byteArray = intent.getByteArrayExtra("background");
//        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return getBitmapFromMemCache(key);
    }

    public static Bitmap createBitmap(View v) {
        Bitmap bitmap;
        if (v.getWidth() <= 0 && v.getHeight() <= 0) {
            v.measure(0, 0);
            bitmap = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }
}
