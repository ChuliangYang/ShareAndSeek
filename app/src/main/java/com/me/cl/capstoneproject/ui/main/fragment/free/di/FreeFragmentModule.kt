package com.me.cl.capstoneproject.ui.main.fragment.free.di

import android.content.Context
import android.support.v4.app.Fragment
import com.me.cl.capstoneproject.adapter.recyclerview.FreeListAdapter
import com.me.cl.capstoneproject.di.FragmentQL
import com.me.cl.capstoneproject.di.FragmentScope
import com.me.cl.capstoneproject.ui.main.fragment.free.FreeInteractorImpl
import com.me.cl.capstoneproject.ui.main.fragment.free.FreePresenterImpl
import com.me.cl.capstoneproject.ui.main.fragment.free.mvp.FreePageInteractor
import com.me.cl.capstoneproject.ui.main.fragment.free.mvp.FreePagePresenter
import com.me.cl.capstoneproject.ui.main.fragment.free.mvp.FreePageView
import dagger.Module
import dagger.Provides

/**
 * Created by CL on 11/8/17.
 */

@Module
@FragmentScope
class FreeFragmentModule(var fragment: Fragment) {

    @Provides
    @FragmentScope
    @FragmentQL
    fun provideFragment(): Fragment {
        return fragment
    }

    @Provides
    @FragmentScope
    fun provideContext(@FragmentQL fragment: Fragment): Context {
        return fragment.context!!
    }

    @Provides
    @FragmentScope
    internal fun getFreePageInteractor(freeInteractor: FreeInteractorImpl): FreePageInteractor {
        return freeInteractor
    }

    @Provides
    @FragmentScope
    internal fun getFreePagePresenter(freePresenter: FreePresenterImpl): FreePagePresenter<FreePageView> {
        return freePresenter
    }

    @Provides
    @FragmentScope
    internal fun provideFreeListAdapter(@FragmentQL fragment: Fragment): FreeListAdapter {
        return FreeListAdapter(fragment.activity!!, mutableListOf())
    }



}
