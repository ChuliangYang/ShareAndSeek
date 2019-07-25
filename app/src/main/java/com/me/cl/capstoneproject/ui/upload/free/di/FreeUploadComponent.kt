package com.me.cl.capstoneproject.ui.upload.free.di

import com.me.cl.capstoneproject.ApplicationComponent
import com.me.cl.capstoneproject.di.ActivityScope
import com.me.cl.capstoneproject.ui.upload.commercial.di.CommercialUploadModule
import com.me.cl.capstoneproject.ui.upload.free.FreeUploadActivity
import com.me.cl.capstoneproject.ui.upload.help.di.HelpUploadComponent
import com.me.cl.capstoneproject.ui.upload.help.di.HelpUploadModule
import dagger.Component

/**
 * Created by CL on 11/29/17.
 */
@Component(dependencies = [ApplicationComponent::class], modules = [FreeUploadModule::class, CommercialUploadModule::class])
@ActivityScope
interface FreeUploadComponent {
    fun inject(freeUploadActivity: FreeUploadActivity)
    fun HelpUploadComponent(helpUploadModule: HelpUploadModule): HelpUploadComponent
}
