package com.me.cl.capstoneproject.ui.detail.di;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.me.cl.capstoneproject.adapter.recyclerview.ReviewListAdapter;
import com.me.cl.capstoneproject.adapter.viewpager.AlbumBannerAdapter;
import com.me.cl.capstoneproject.di.ActivityScope;
import com.me.cl.capstoneproject.ui.detail.CommercialDetailInteractorImpl;
import com.me.cl.capstoneproject.ui.detail.CommercialDetailPresenterImpl;
import com.me.cl.capstoneproject.ui.detail.mvp.CommercialDetailInteractor;
import com.me.cl.capstoneproject.ui.detail.mvp.CommercialDetailPresenter;
import com.me.cl.capstoneproject.ui.detail.mvp.CommercialDetailView;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;

/**
 * Created by CL on 11/22/17.
 */
@Module
@ActivityScope
public class CommercialDetailModule {

    private AppCompatActivity activity;

    public CommercialDetailModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Provides
    Context provideContext(){return activity.getBaseContext();}

    @Provides
    Activity provideActivity(){return activity;}

    @Provides
    CommercialDetailInteractor provideCommercialDetailInteractor(CommercialDetailInteractorImpl commercialDetailInteractor){return commercialDetailInteractor;}

    @Provides
    CommercialDetailPresenter<CommercialDetailView> provideCommercialDetailPresenter(CommercialDetailPresenterImpl commercialDetailPresenter){return commercialDetailPresenter;}

    @Provides
    ReviewListAdapter provideReviewListAdapter(Context context){return  new ReviewListAdapter(context,new ArrayList<>());}

    @Provides
    AlbumBannerAdapter provideAlbumBannerAdapter(Activity activity){return new AlbumBannerAdapter(((FragmentActivity)activity).getSupportFragmentManager(),new ArrayList<>());}
}
