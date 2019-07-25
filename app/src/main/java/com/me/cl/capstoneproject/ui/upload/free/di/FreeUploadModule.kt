package com.me.cl.capstoneproject.ui.upload.free.di

import android.content.Context
import com.me.cl.capstoneproject.di.ActivityScope
import com.me.cl.capstoneproject.di.FreeUploadQL
import com.me.cl.capstoneproject.ui.upload.free.FreeUploadInteractorImpl
import com.me.cl.capstoneproject.ui.upload.free.FreeUploadPresenterImpl
import com.me.cl.capstoneproject.ui.upload.free.mvp.FreeUploadInteractor
import com.me.cl.capstoneproject.ui.upload.free.mvp.FreeUploadPresenter
import com.me.cl.capstoneproject.ui.upload.free.mvp.FreeUploadView
import dagger.Module
import dagger.Provides

/**
 * Created by CL on 11/29/17.
 */
@Module
@ActivityScope
class FreeUploadModule(private val activity: android.app.Activity) {

    @Provides
    @ActivityScope
    @FreeUploadQL
     fun provideContext(): Context {
        return activity
    }

    @Provides
     fun provideFreeUploadInteractor(freeUploadInteractor: FreeUploadInteractorImpl): FreeUploadInteractor {
        return freeUploadInteractor
    }

    @Provides
     fun provideFreeUploadPresenter(freeUploadPresenter: FreeUploadPresenterImpl): FreeUploadPresenter<FreeUploadView> {
        return freeUploadPresenter
    }

//    @Provides
//     fun provideCommercialUploadPresenter(commercialUploadPresenterImpl: CommercialUploadPresenterImpl): CommercialUploadPresenter<CommercialUploadView> {
//        return commercialUploadPresenterImpl
//    }
}
