package com.me.cl.capstoneproject.ui.list;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.me.cl.capstoneproject.ui.list.mvp.CommercialListInteractor;
import com.me.cl.capstoneproject.ui.list.mvp.CommercialListPresenter;
import com.me.cl.capstoneproject.ui.list.mvp.CommercialListView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.me.cl.capstoneproject.base.Constant.Dialog.FETCHING;
import static com.me.cl.capstoneproject.base.Constant.Dialog.FETCHING_DISTANCE_FAILED;
import static com.me.cl.capstoneproject.base.Constant.Dialog.FETCHING_FAILED;
import static com.me.cl.capstoneproject.base.Constant.Dialog.OK;
import static com.me.cl.capstoneproject.base.Constant.Dialog.PLEASE_WAIT;
import static com.me.cl.capstoneproject.base.Constant.Dialog.SORRY;
import static com.me.cl.capstoneproject.base.Constant.ListPage.CACHE_KEY_PEDING_DISTANCE_SORT;
import static com.me.cl.capstoneproject.base.Constant.ListPage.CACHE_KEY_SORT_TYPE;
import static com.me.cl.capstoneproject.base.Constant.ListPage.REQUEST_CODE_DETAIL;
import static com.me.cl.capstoneproject.base.Constant.ListPage.SORT_LIST_BY_DISTANCE;
import static com.me.cl.capstoneproject.base.Constant.ListPage.SORT_LIST_FROM_CACHE;
import static com.me.cl.capstoneproject.base.Constant.MainPage.CATE_RESTAURANTS;
import static com.me.cl.capstoneproject.base.Constant.UploadPage.REQUEST_CODE_ACCESS_FINE_LOCATION;

/**
 * Created by CL on 11/29/17.
 */

public class CommercialListPresenterImpl implements CommercialListPresenter<CommercialListView> {
    CommercialListInteractor interactor;
    CommercialListView view;
    HashMap<String,Disposable> disposables=new HashMap();
    LifecycleProvider<Lifecycle.Event> provider;
    final String COMMERCIAL_LIST="COMMERCIAL_LIST";
    final String RV_STATE="RV_STATE";
    final String SP_TITLE_STATE="SP_TITLE_STATE";
    final String SP_SORT_STATE="SP_SORT_STATE";
    final String RX_KEY="sortList";
    final String CACHE_KEY_CURRENT_TYPE="CurrentType";
    static final String CACHE_KEY_TITLE_POSITION="SP_TITLE_POSITION";

    @Inject
    public CommercialListPresenterImpl(CommercialListInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void manage(CommercialListView view) {
        this.view=view;
    }

    @Override
    public void destory() {
        disposables.clear();
        disposables=null;
    }

    @Override
    public void init() {

    }

    @Override
    public void init(Intent intent,Bundle savedInstanceState,LifecycleProvider<Lifecycle.Event> provider) {
        interactor.writeToCache(CACHE_KEY_CURRENT_TYPE,interactor.getCurrentType(intent));
        this.provider=provider;
//        if (savedInstanceState!=null) {
//            interactor.saveInstanceState(savedInstanceState);
//            if (interactor.getSaveInstanceState().getParcelableArrayList(COMMERCIAL_LIST) != null) {
//                view.showList(interactor.getSaveInstanceState().getParcelableArrayList(COMMERCIAL_LIST),false,true,new Parcelable[]{interactor.getSaveInstanceState().getParcelable(RV_STATE),interactor.getSaveInstanceState().getParcelable(SP_TITLE_STATE),interactor.getSaveInstanceState().getParcelable(SP_SORT_STATE)});
//                interactor.writeToCache(CACHE_KEY_SORT_TYPE,interactor.getSaveInstanceState().getInt(CACHE_KEY_SORT_TYPE));
//                if (interactor.getSaveInstanceState().getInt(CACHE_KEY_TITLE_POSITION)>0) {
//                    view.setTitleSpinnerInitEnable(false);
//                }
//                interactor.saveInstanceState(null);
//            }
//        }
    }

    @Override
    public void sortList(int sortType, String cate, String district, Context context,Boolean swipe,Boolean animate){
//        if (interactor.getSaveInstanceState().getParcelableArrayList(COMMERCIAL_LIST) != null) {
//            interactor.saveInstanceState(null);
//        } else {
            if (sortType==SORT_LIST_BY_DISTANCE) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CODE_ACCESS_FINE_LOCATION);
                        interactor.writeToCache(CACHE_KEY_PEDING_DISTANCE_SORT,new String[]{cate,district});
                    }
                    return;
                }
            }
            if(!(interactor.getFromCache(CACHE_KEY_SORT_TYPE)!=null&&(int)(interactor.getFromCache(CACHE_KEY_SORT_TYPE))==sortType)){
                    disposables.put(RX_KEY,interactor.fetchSortList(sortType,cate,district).subscribeOn(Schedulers.io())
                            .doOnSubscribe(disposable ->
                                    view.controlIndeterminateProgress(true,PLEASE_WAIT,FETCHING,dialog -> {
                                        if (disposables.containsKey(RX_KEY)) {
                                            disposables.get(RX_KEY).dispose();
                                        }

                                    },null,swipe)
                            )
                            .observeOn(AndroidSchedulers.mainThread())
                            .toObservable()
                            .compose(provider.bindToLifecycle())
                            .subscribe(o -> {
                                view.controlIndeterminateProgress(false,null,null,dialog -> {
                                    if (disposables.containsKey(RX_KEY)) {
                                        disposables.get(RX_KEY).dispose();
                                    }
                                },null,swipe);
                                view.showList((List) o,animate,false,null);
                                if (sortType!=SORT_LIST_FROM_CACHE) {
                                    interactor.writeToCache(CACHE_KEY_SORT_TYPE,sortType);
                                }
                            },throwable -> {
                                Timber.e(throwable.toString());
                                view.controlIndeterminateProgress(false,null,null,dialog -> {
                                    if (disposables.containsKey(RX_KEY)) {
                                        disposables.get(RX_KEY).dispose();
                                    }
                                },null,swipe);
                                if (interactor.getFromCache(CACHE_KEY_SORT_TYPE) instanceof Integer) {
                                    view.restoreLastSortType((Integer) interactor.getFromCache(CACHE_KEY_SORT_TYPE));
                                }
                                view.showSnackBar(FETCHING_FAILED);
                            }));
            }
    }

    @Override
    public String getCurrentTypeFromIntent(Intent intent){
        if (interactor.getCurrentType(intent) != null) {
            return interactor.getCurrentType(intent);
        } else {
            return CATE_RESTAURANTS;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults,Activity activity){
        switch (requestCode) {
            case REQUEST_CODE_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (interactor.getFromCache(CACHE_KEY_PEDING_DISTANCE_SORT)!=null) {
                        String[] param=(String[])(interactor.getFromCache(CACHE_KEY_PEDING_DISTANCE_SORT));
                        sortList(SORT_LIST_BY_DISTANCE,param[0],param[1],activity,false,true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if (interactor.getFromCache(CACHE_KEY_SORT_TYPE)!=null) {
                        view.restoreLastSortType((Integer) interactor.getFromCache(CACHE_KEY_SORT_TYPE));
                    }
                    view.showAlertDialog(SORRY,FETCHING_DISTANCE_FAILED,OK,null,null,activity);
                }
                interactor.writeToCache(CACHE_KEY_PEDING_DISTANCE_SORT,null);
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void handleActivityResult(Integer requestCode, Integer resultCode, @Nullable Intent data,Activity activity) {
        if (requestCode==REQUEST_CODE_DETAIL&&resultCode==Activity.RESULT_OK) {
            sortList(SORT_LIST_FROM_CACHE, (String) interactor.getFromCache(CACHE_KEY_CURRENT_TYPE), view.getSpinnerTitle(), activity,false,false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
//        outState.putParcelableArrayList(COMMERCIAL_LIST,view.getListModel());
//        outState.putParcelable(RV_STATE,view.getRvState());
//        outState.putParcelable(SP_TITLE_STATE,view.getSpinnerTitleState());
//        outState.putParcelable(SP_SORT_STATE,view.getSpinnerSortState());
//        outState.putInt(CACHE_KEY_SORT_TYPE, (Integer) interactor.getFromCache(CACHE_KEY_SORT_TYPE));
//        outState.putInt(CACHE_KEY_TITLE_POSITION, view.getSpinnerTitlePosition());
    }
}
