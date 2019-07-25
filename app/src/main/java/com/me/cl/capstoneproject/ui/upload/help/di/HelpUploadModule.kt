package com.me.cl.capstoneproject.ui.upload.help.di

import android.app.Activity
import android.content.Context
import com.me.cl.capstoneproject.di.HelpUploadActivityScope
import com.me.cl.capstoneproject.di.HelpUploadQL
import com.me.cl.capstoneproject.ui.upload.help.HelpUploadInteractorImpl
import com.me.cl.capstoneproject.ui.upload.help.HelpUploadPresenterImpl
import com.me.cl.capstoneproject.ui.upload.help.mvp.HelpUploadInteractor
import com.me.cl.capstoneproject.ui.upload.help.mvp.HelpUploadPresenter
import com.me.cl.capstoneproject.ui.upload.help.mvp.HelpUploadView
import dagger.Module
import dagger.Provides

/**
 * Created by CL on 12/1/17.
 */
@Module
@HelpUploadActivityScope
class HelpUploadModule(private val activity: Activity) {

    @Provides
    @HelpUploadActivityScope
    @HelpUploadQL
    fun provideContext(activity: Activity): Context {
        return activity
    }

    @Provides
    fun provideHelpUploadPresenter(helpUploadPresenterImpl: HelpUploadPresenterImpl): HelpUploadPresenter<HelpUploadView>{
        return helpUploadPresenterImpl
    }

    @Provides
    fun provideHelpUploadInteractor(helpUploadInteractorImpl: HelpUploadInteractorImpl):HelpUploadInteractor{
        return helpUploadInteractorImpl
    }

}
