package com.me.cl.capstoneproject.ui.main.fragment.help.di

import com.me.cl.capstoneproject.di.SubFragmentScope
import com.me.cl.capstoneproject.ui.main.fragment.help.HelpFragment
import dagger.Subcomponent

/**
 * Created by CL on 11/8/17.
 */
@Subcomponent(modules = arrayOf(HelpFragmentModule::class))
@SubFragmentScope
interface HelpFragmentComponent {
    fun inject(helpFragment: HelpFragment)
}
