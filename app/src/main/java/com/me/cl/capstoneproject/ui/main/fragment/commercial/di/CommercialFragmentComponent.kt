package com.me.cl.capstoneproject.ui.main.fragment.commercial.di

import com.me.cl.capstoneproject.ApplicationComponent
import com.me.cl.capstoneproject.di.ActivityScope
import com.me.cl.capstoneproject.di.FragmentScope
import com.me.cl.capstoneproject.ui.list.di.CommercialListModule
import com.me.cl.capstoneproject.ui.main.fragment.commercial.CommercialFragment

import dagger.Component

/**
 * Created by CL on 11/5/17.
 */
@FragmentScope
@ActivityScope
@Component(dependencies = arrayOf(ApplicationComponent::class), modules = arrayOf(CommercialFragmentModule::class, CommercialListModule::class))
interface CommercialFragmentComponent {
    fun inject(commercialFragment: CommercialFragment)
}
