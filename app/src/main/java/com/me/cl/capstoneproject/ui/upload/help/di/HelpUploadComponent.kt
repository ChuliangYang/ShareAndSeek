package com.me.cl.capstoneproject.ui.upload.help.di

import com.me.cl.capstoneproject.di.HelpUploadActivityScope
import com.me.cl.capstoneproject.ui.upload.help.HelpUploadActivity
import dagger.Subcomponent

/**
 * Created by CL on 12/1/17.
 */
@Subcomponent( modules = arrayOf(HelpUploadModule::class))
@HelpUploadActivityScope
interface HelpUploadComponent {
    fun inject(helpUploadActivity: HelpUploadActivity)
}
