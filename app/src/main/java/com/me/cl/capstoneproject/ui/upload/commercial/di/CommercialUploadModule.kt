package com.me.cl.capstoneproject.ui.upload.commercial.di

import android.app.Activity
import android.content.Context
import com.google.android.gms.location.LocationRequest
import com.google.firebase.storage.FirebaseStorage
import com.me.cl.capstoneproject.adapter.recyclerview.PictureUploadAdapter
import com.me.cl.capstoneproject.bean.Photo
import com.me.cl.capstoneproject.di.ActivityScope
import com.me.cl.capstoneproject.di.CommercialUploadQL
import com.me.cl.capstoneproject.ui.upload.commercial.CommercialUploadInteractorImpl
import com.me.cl.capstoneproject.ui.upload.commercial.CommercialUploadPresenterImpl
import com.me.cl.capstoneproject.ui.upload.commercial.mvp.CommercialUploadInteractor
import com.me.cl.capstoneproject.ui.upload.commercial.mvp.CommercialUploadPresenter
import com.me.cl.capstoneproject.ui.upload.commercial.mvp.CommercialUploadView
import dagger.Module
import dagger.Provides
import java.util.*

/**
 * Created by CL on 11/15/17.
 */
@ActivityScope
@Module
class CommercialUploadModule(var activity: Activity) {

    @Provides
    @ActivityScope
    fun provideActivity(): Activity {
        return activity
    }

    @Provides
    @ActivityScope
    @CommercialUploadQL
     fun provideContext(): Context {
        return activity
    }

    @Provides
     fun provideCommercialUploadPresenter(commercialUploadPresenter: CommercialUploadPresenterImpl): CommercialUploadPresenter<CommercialUploadView> {
        return commercialUploadPresenter
    }

    @Provides
     fun provideCommercialUploadInteractor(commercialUploadInteractor: CommercialUploadInteractorImpl): CommercialUploadInteractor {
        return commercialUploadInteractor
    }

    @Provides
     fun providePictureUploadAdapter(@CommercialUploadQL context: Context): PictureUploadAdapter {
        return PictureUploadAdapter(context, ArrayList<Photo>())
    }

    @Provides
    @ActivityScope
    internal fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @ActivityScope
     fun provideLocationRequest(): LocationRequest {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval((5 * 1000).toLong())        // 5 seconds, in milliseconds
                .setFastestInterval((1 * 1000).toLong())
    }

}
