package com.me.cl.capstoneproject.ui.detail.mvp;

import android.util.Pair;

import com.me.cl.capstoneproject.bean.CommercialItem;

import io.reactivex.Single;

/**
 * Created by CL on 11/22/17.
 */

public interface CommercialDetailInteractor {
    CommercialItem getCommercialBaseInfo();
    Single getReviews(String startKey);
    void saveLastKey(String key);
    String getLastKey();
    String getCommercialId();
    void saveToCache (String key,Object data);
    Object getFromCache (String key);
    Pair caculateRating(Float addScore, int addCount);
}
