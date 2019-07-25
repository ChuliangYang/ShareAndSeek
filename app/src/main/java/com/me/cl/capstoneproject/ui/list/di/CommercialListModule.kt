package com.me.cl.capstoneproject.ui.list.di

import android.app.Activity
import android.content.Context
import com.me.cl.capstoneproject.adapter.recyclerview.CommercialListAdapter
import com.me.cl.capstoneproject.bean.CommercialItem
import com.me.cl.capstoneproject.di.ActivityScope
import com.me.cl.capstoneproject.ui.list.CommercialListInteractorImpl
import com.me.cl.capstoneproject.ui.list.CommercialListPresenterImpl
import com.me.cl.capstoneproject.ui.list.mvp.CommercialListInteractor
import com.me.cl.capstoneproject.ui.list.mvp.CommercialListPresenter
import com.me.cl.capstoneproject.ui.list.mvp.CommercialListView
import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Named

/**
 * Created by CL on 11/29/17.
 */
@Module
@ActivityScope
class CommercialListModule(private val activity: android.app.Activity) {

    @Provides
    @ActivityScope
    @Named("CommercialList")
     fun provideContext(): Context {
        return activity
    }

    @Provides
    @ActivityScope
    @Named("CommercialList")
     fun provideActivity(): Activity {
        return activity
    }

    @Provides
     fun provideCommercialListInteractor(commercialListInteractor: CommercialListInteractorImpl): CommercialListInteractor {
        return commercialListInteractor
    }

    @Provides
     fun provideCommercialListPresenter(commercialListPresenter: CommercialListPresenterImpl): CommercialListPresenter<CommercialListView> {
        return commercialListPresenter
    }

    @Provides
    @Named("CommercialList")
     fun provideCommercialListAdapter(@Named("CommercialList")
                                          context: Context): CommercialListAdapter {
        return CommercialListAdapter(context, ArrayList<CommercialItem>())
    }
}
