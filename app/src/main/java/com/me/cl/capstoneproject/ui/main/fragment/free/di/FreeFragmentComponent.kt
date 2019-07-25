package com.me.cl.capstoneproject.ui.main.fragment.free.di

import com.me.cl.capstoneproject.ApplicationComponent
import com.me.cl.capstoneproject.di.FragmentScope
import com.me.cl.capstoneproject.ui.main.fragment.free.FreeFragment
import com.me.cl.capstoneproject.ui.main.fragment.help.di.HelpFragmentComponent
import com.me.cl.capstoneproject.ui.main.fragment.help.di.HelpFragmentModule

import dagger.Component

/**
 * Created by CL on 11/8/17.
 */
@FragmentScope
@Component(dependencies = arrayOf(ApplicationComponent::class), modules = arrayOf(FreeFragmentModule::class))
interface FreeFragmentComponent {
    fun inject(freeFragment: FreeFragment)

    fun plus(helpFragmentModule: HelpFragmentModule): HelpFragmentComponent
}
