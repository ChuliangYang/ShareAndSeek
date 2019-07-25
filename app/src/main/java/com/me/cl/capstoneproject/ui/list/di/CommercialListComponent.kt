package com.me.cl.capstoneproject.ui.list.di

import com.me.cl.capstoneproject.ApplicationComponent
import com.me.cl.capstoneproject.di.ActivityScope
import com.me.cl.capstoneproject.ui.list.CommercialListActivity

import dagger.Component

/**
 * Created by CL on 11/29/17.
 */
@Component(dependencies = arrayOf(ApplicationComponent::class), modules = arrayOf(CommercialListModule::class))
@ActivityScope
interface CommercialListComponent {
    fun inject(commercialListActivity: CommercialListActivity)
}
