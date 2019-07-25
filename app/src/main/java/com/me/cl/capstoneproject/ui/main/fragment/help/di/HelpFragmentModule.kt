package com.me.cl.capstoneproject.ui.main.fragment.help.di

import android.support.v4.app.Fragment
import com.me.cl.capstoneproject.adapter.recyclerview.HelpListAdapter
import com.me.cl.capstoneproject.di.SubFragmentQL
import com.me.cl.capstoneproject.di.SubFragmentScope
import com.me.cl.capstoneproject.ui.main.fragment.help.HelpInteractorImpl
import com.me.cl.capstoneproject.ui.main.fragment.help.HelpPresenterImpl
import com.me.cl.capstoneproject.ui.main.fragment.help.mvp.HelpPageInteractor
import com.me.cl.capstoneproject.ui.main.fragment.help.mvp.HelpPagePresenter
import com.me.cl.capstoneproject.ui.main.fragment.help.mvp.HelpPageView
import dagger.Module
import dagger.Provides

/**
 * Created by CL on 11/8/17.
 */
@Module
@SubFragmentScope
class HelpFragmentModule(var fragment: Fragment) {

    @Provides
    @SubFragmentScope
    @SubFragmentQL
     fun provideFragment(): Fragment {
        return fragment
    }

    @Provides
     fun provideHelpPageInteractor(helpInteractor: HelpInteractorImpl): HelpPageInteractor {
        return helpInteractor
    }

    @Provides
     fun provideHelpPagePresenter(helpPresenter: HelpPresenterImpl): HelpPagePresenter<HelpPageView>{
        return helpPresenter
    }

    @Provides
     fun provideHelpListAdapter(@SubFragmentQL fragment: Fragment): HelpListAdapter {
        return HelpListAdapter(fragment.activity!!, mutableListOf())
    }
}
