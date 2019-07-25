package com.me.cl.capstoneproject.ui.upload.commercial.di

import com.me.cl.capstoneproject.ApplicationComponent
import com.me.cl.capstoneproject.di.ActivityScope
import com.me.cl.capstoneproject.ui.upload.commercial.CommercialUploadActivity

import dagger.Component

/**
 * Created by CL on 11/15/17.
 */
@ActivityScope
@Component(dependencies = arrayOf(ApplicationComponent::class), modules = arrayOf(CommercialUploadModule::class))
interface CommercialUploadComponent {
    fun inject(commercialUploadActivity: CommercialUploadActivity?)
}
