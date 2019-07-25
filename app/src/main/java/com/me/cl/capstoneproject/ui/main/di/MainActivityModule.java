package com.me.cl.capstoneproject.ui.main.di;

import android.content.Context;

import com.me.cl.capstoneproject.di.ActivityQL;
import com.me.cl.capstoneproject.di.ActivityScope;
import com.me.cl.capstoneproject.ui.main.MainInteractorImpl;
import com.me.cl.capstoneproject.ui.main.MainPresenterImpl;
import com.me.cl.capstoneproject.ui.main.mvp.MainInteractor;
import com.me.cl.capstoneproject.ui.main.mvp.MainPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by CL on 11/3/17.
 */
@ActivityScope
@Module
public class MainActivityModule {
    private Context context;

    public MainActivityModule(Context context) {
        this.context=context;
    }

    @Provides
    @ActivityScope
    @ActivityQL
    Context provideActivityContext(){return context;}

    @Provides
    MainInteractor provideMainInteractor(MainInteractorImpl mainInteractorImpl){return mainInteractorImpl;}

    @Provides
    MainPresenter provideMainPresenter(MainPresenterImpl mainPresenterImpl){return mainPresenterImpl;}
}
