package com.me.cl.capstoneproject.ui.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.database.DatabaseReference;
import com.me.cl.capstoneproject.base.Constant;
import com.me.cl.capstoneproject.ui.list.mvp.CommercialListInteractor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import static com.me.cl.capstoneproject.base.Constant.ListPage.BUNDLE_KEY_CATE;
import static com.me.cl.capstoneproject.base.Constant.ListPage.CACHE_KEY_SORT_TYPE;
import static com.me.cl.capstoneproject.base.Constant.ListPage.SORT_LIST_FROM_CACHE;

/**
 * Created by CL on 11/29/17.
 */

public class
CommercialListInteractorImpl implements CommercialListInteractor {

    private Context context;
    private DatabaseReference databaseReference;
    private Retrofit retrofit;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private HashMap cache=new HashMap();
    private Bundle bundle=new Bundle();

    @Inject
    public CommercialListInteractorImpl(@Named("CommercialList") Context context, DatabaseReference databaseReference, Retrofit retrofit, FusedLocationProviderClient fusedLocationProviderClient) {
        this.context = context;
        this.databaseReference = databaseReference;
        this.retrofit = retrofit;
        this.fusedLocationProviderClient = fusedLocationProviderClient;
    }

    @Override
    @SuppressLint("MissingPermission")
    public Single fetchSortList(int sortType, String kind,final String district) {
        Map<String, String> options = new HashMap<>();
        if (sortType==SORT_LIST_FROM_CACHE&&getFromCache(CACHE_KEY_SORT_TYPE)!=null) {
            sortType= (int) getFromCache(CACHE_KEY_SORT_TYPE);
        }
        switch (sortType) {
            case Constant.ListPage.SORT_LIST_BY_COSTUME:
                options.put("type", "costume");
                options.put("order", "asce");
                if ("New York".equals(district)) {
                    return retrofit.create(FirebaseDatabaseService.class).fetchCommercialList(kind, null,options);
                } else {
                    return retrofit.create(FirebaseDatabaseService.class).fetchCommercialList(kind, district,options);
                }
            case Constant.ListPage.SORT_LIST_BY_RATE:
                options.put("type", "rating");
                options.put("order", "desc");
                if ("New York".equals(district)) {
                    return retrofit.create(FirebaseDatabaseService.class).fetchCommercialList(kind, null,options);
                } else {
                    return retrofit.create(FirebaseDatabaseService.class).fetchCommercialList(kind, district,options);
                }

            case Constant.ListPage.SORT_LIST_BY_DISTANCE:
                return Single.create(e -> fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        e.onSuccess(location);
                    } else {
                        e.onError(new IllegalArgumentException("null location"));
                    }
                })).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).flatMap(o -> {
                    Location location = (Location) o;
                    options.put("type", "distance");
                    options.put("order", "asce");
                    options.put("latitude", String.valueOf(location.getLatitude()));
                    options.put("longitude", String.valueOf(location.getLongitude()));
                    if ("New York".equals(district)) {
                        return retrofit.create(FirebaseDatabaseService.class).fetchCommercialList(kind, null,options);
                    } else {
                        return retrofit.create(FirebaseDatabaseService.class).fetchCommercialList(kind, district,options);
                    }
                });
            default:
                options.put("order", "asce");
                if ("New York".equals(district)) {
                    return retrofit.create(FirebaseDatabaseService.class).fetchCommercialList(kind, null,options);
                } else {
                    return retrofit.create(FirebaseDatabaseService.class).fetchCommercialList(kind, district,options);
                }
        }

    }

    @Override
    public String getCurrentType(Intent intent){
            return intent.getStringExtra(BUNDLE_KEY_CATE);
    }

    @Override
    public void writeToCache(String key,Object value){
        cache.put(key,value);
    }

    @Override
    public Object getFromCache(String key){
        if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            return null;
        }
    }

    @Override
    public Bundle getSaveInstanceState() {
        return bundle;
    }

    @Override
    public void saveInstanceState(Bundle outState) {
        if (outState != null) {
            bundle = outState;
        } else {
            bundle.clear();
        }
    }
}
