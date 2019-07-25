package com.me.cl.capstoneproject.ui.list.mvp;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.me.cl.capstoneproject.base.BasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;

import javax.annotation.Nullable;

/**
 * Created by CL on 11/29/17.
 */

public interface CommercialListPresenter<V> extends BasePresenter<V> {
    void sortList(int sortType, String kind, String district, Context context,Boolean swipe,Boolean animate);
    String getCurrentTypeFromIntent(Intent intent);
    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults,Activity activity);
    void handleActivityResult(Integer requestCode , Integer resultCode, @Nullable Intent data,Activity activity);
    void init(Intent intent,Bundle savedInstanceState,LifecycleProvider<Lifecycle.Event> provider);

    void onSaveInstanceState(Bundle outState);
}
