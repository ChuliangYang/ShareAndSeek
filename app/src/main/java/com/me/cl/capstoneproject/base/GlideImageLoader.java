package com.me.cl.capstoneproject.base;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lzy.imagepicker.loader.ImageLoader;

import java.io.File;

/**
 * Created by CL on 12/5/17.
 */

public class GlideImageLoader implements ImageLoader {
    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        RequestOptions requestOptions=new RequestOptions();
            Glide.with(activity).load(Uri.fromFile(new File(path))).into(imageView);
    }

    @Override
    public void displayImagePreview(Activity activity, String path, ImageView imageView, int width, int height) {
        Uri uri= Uri.parse(path);
        if (uri.getScheme()!=null&&uri.getScheme().contains("http")) {
            Glide.with(activity).load(path).into(imageView);
        } else {
            Glide.with(activity).load(Uri.fromFile(new File(path))).into(imageView);
        }
    }

    @Override
    public void clearMemoryCache() {

    }
}
