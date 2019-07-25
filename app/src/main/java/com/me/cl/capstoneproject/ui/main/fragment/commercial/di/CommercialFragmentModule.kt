package com.me.cl.capstoneproject.ui.main.fragment.commercial.di

import android.content.Context
import android.support.v4.app.Fragment
import com.me.cl.capstoneproject.adapter.recyclerview.CommercialListAdapter
import com.me.cl.capstoneproject.bean.CommercialItem
import com.me.cl.capstoneproject.di.FragmentQL
import com.me.cl.capstoneproject.di.FragmentScope
import com.me.cl.capstoneproject.ui.main.fragment.commercial.CommercialInteractorImpl
import com.me.cl.capstoneproject.ui.main.fragment.commercial.CommercialPresenterImpl
import com.me.cl.capstoneproject.ui.main.fragment.commercial.mvp.CommercialPageInteractor
import com.me.cl.capstoneproject.ui.main.fragment.commercial.mvp.CommercialPagePresenter
import com.me.cl.capstoneproject.ui.main.fragment.commercial.mvp.CommercialPageView
import dagger.Module
import dagger.Provides
import java.util.*

/**
 * Created by CL on 11/5/17.
 */
@Module
@FragmentScope
class CommercialFragmentModule(var fragment: Fragment) {

    @Provides
    @FragmentScope
     fun provideFragment(): Fragment {
        return fragment
    }

    @Provides
    @FragmentScope
//    @Named("Fragment")
    @FragmentQL
     fun provideContext(fragment: Fragment): Context {
        return fragment.context!!
    }

    @Provides
     fun provideCommercialPagePresenter(commercialPresenter: CommercialPresenterImpl): CommercialPagePresenter<CommercialPageView> {
        return commercialPresenter
    }

    @Provides
     fun provideCommercialPageInteractor(commercialInteractor: CommercialInteractorImpl): CommercialPageInteractor {
        return commercialInteractor
    }


    @Provides
    @FragmentQL
     fun provideCommercialListAdapter(fragment: Fragment): CommercialListAdapter {
        return CommercialListAdapter(fragment.context, null)
    }

    @Provides
     fun provideCommercialItems(): List<CommercialItem> {
        return ArrayList()
    }

}
