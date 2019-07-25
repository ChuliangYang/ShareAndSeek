package com.me.cl.capstoneproject.ui.list;

import com.me.cl.capstoneproject.bean.CommercialItem;
import com.me.cl.capstoneproject.bean.FreeItem;
import com.me.cl.capstoneproject.bean.HelpItem;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by CL on 11/27/17.
 */

public interface FirebaseDatabaseService {
    @GET("retriveSortedCommercialList/sort")
    Single<List<CommercialItem>> fetchCommercialList(@Query("category") String category,@Query("district") String district, @QueryMap Map<String, String> options);

    @GET("retriveFreeList/sort")
    Single<List<FreeItem>> fetchFreeList(@Query("district") String district);

    @GET("retriveHelpList/sort")
    Single<List<HelpItem>> fetchHelpList(@Query("district") String district);
}
