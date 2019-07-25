package com.me.cl.capstoneproject.ui.list.mvp;

import android.content.Intent;
import android.os.Bundle;

import javax.annotation.Nullable;

import io.reactivex.Single;

/**
 * Created by CL on 11/29/17.
 */

public interface CommercialListInteractor {
     Single fetchSortList(int sortType,String kind,String district);
     String getCurrentType(Intent intent);
     void writeToCache(String key,Object value);
     @Nullable Object getFromCache(String key);

    Bundle getSaveInstanceState();

    void saveInstanceState(Bundle outState);
}
