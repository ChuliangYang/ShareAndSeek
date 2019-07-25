package com.me.cl.capstoneproject.ui.detail;

import android.app.Activity;
import android.util.Pair;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.me.cl.capstoneproject.bean.CommercialItem;
import com.me.cl.capstoneproject.bean.ReviewBean;
import com.me.cl.capstoneproject.bean.ReviewBeanWrapper;
import com.me.cl.capstoneproject.ui.detail.mvp.CommercialDetailInteractor;
import com.me.cl.capstoneproject.util.BaseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import io.reactivex.Single;

import static com.me.cl.capstoneproject.base.Constant.DetailPage.BUNDLE_KEY_COMMERCIAL_ITEM;
import static com.me.cl.capstoneproject.base.Constant.MAP_LAST_KEY;
import static com.me.cl.capstoneproject.base.Constant.MainPage.DATA_KEY_COMMERCIAL_ITEM;

/**
 * Created by CL on 11/22/17.
 */

public class CommercialDetailInteractorImpl implements CommercialDetailInteractor {

    private Activity activity;
    private DatabaseReference databaseReference;
    private HashMap savedDate=new HashMap();



    @Inject
    public CommercialDetailInteractorImpl(Activity activity,DatabaseReference databaseReference) {
        this.activity = activity;
        this.databaseReference = databaseReference;
    }


    @Override
    public @Nullable CommercialItem getCommercialBaseInfo() {
        if (activity.getIntent().getParcelableExtra(DATA_KEY_COMMERCIAL_ITEM)!=null) {
            CommercialItem commercialItem=activity.getIntent().getParcelableExtra(DATA_KEY_COMMERCIAL_ITEM);
            savedDate.put(DATA_KEY_COMMERCIAL_ITEM,commercialItem);
            return commercialItem;
        }
        return null;
    }

    @Override
    public Single getReviews(String startKey) {
        return Single.create(e -> databaseReference.child("commercial").child("reviews").child((((CommercialItem) savedDate.get(DATA_KEY_COMMERCIAL_ITEM)).getCommercialId()))
//                .orderByKey().startAt(startKey).limitToFirst(Constant.MainPage.LIST_PAGE_SIZE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<ReviewBeanWrapper> reviewBeans = new ArrayList<>();
                        for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                            ReviewBean review = dataSnap.getValue(ReviewBean.class);
                            ReviewBeanWrapper reviewBeanWrapper=new ReviewBeanWrapper();
                            reviewBeanWrapper.setReviewId(dataSnapshot.getKey());
                            reviewBeanWrapper.setParentId((String) savedDate.get(BUNDLE_KEY_COMMERCIAL_ITEM));
                            reviewBeanWrapper.setReviewBean(review);
                            reviewBeans.add(reviewBeanWrapper);
                        }
                        if (reviewBeans.size()>0) {
                            saveLastKey(reviewBeans.get(reviewBeans.size()-1).getReviewId());
                        }
                        e.onSuccess(reviewBeans);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        e.onError(new Error("DatabaseError"));
                    }
                }));
    }

    @Override
    public void saveLastKey(String key) {
        savedDate.put(MAP_LAST_KEY,key);
    }

    @Override
    public String getLastKey() {
        return BaseUtils.checkStringEmpty(savedDate.get(MAP_LAST_KEY));
    }

    @Override
    public String getCommercialId(){
        if (savedDate.get(DATA_KEY_COMMERCIAL_ITEM)!=null) {
            return ((CommercialItem)(savedDate.get(DATA_KEY_COMMERCIAL_ITEM))).getCommercialId();
        }
        return "";
    }

    @Override
    public void saveToCache (String key,Object data){
        savedDate.put(key,data);
    }

    @Override
    public Object getFromCache (String key){
        return savedDate.get(key);
    }

    @Override
    public Pair caculateRating(Float addScore,int addCount){
        if (savedDate.get(DATA_KEY_COMMERCIAL_ITEM) instanceof  CommercialItem) {
            CommercialItem commercialItem= (CommercialItem)savedDate.get(DATA_KEY_COMMERCIAL_ITEM) ;
            if (commercialItem!=null) {
                commercialItem.setTotalRating(commercialItem.getTotalRating()+addScore);
                commercialItem.setReview_count(commercialItem.getReview_count()+addCount);
                Float newScore= (commercialItem.getTotalRating())/(commercialItem.getReview_count());
                commercialItem.setRating(newScore);
                return  new Pair(commercialItem.getRating(),commercialItem.getReview_count());
            }
        }
        return null;
    }
}
