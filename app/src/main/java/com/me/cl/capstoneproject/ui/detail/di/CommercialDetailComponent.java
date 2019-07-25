package com.me.cl.capstoneproject.ui.detail.di;

import com.me.cl.capstoneproject.ApplicationComponent;
import com.me.cl.capstoneproject.di.ActivityScope;
import com.me.cl.capstoneproject.ui.detail.CommercialDetailActivity;

import dagger.Component;

/**
 * Created by CL on 11/22/17.
 */

@ActivityScope
@Component(dependencies = ApplicationComponent.class,modules = CommercialDetailModule.class)
public interface CommercialDetailComponent {
        void inject(CommercialDetailActivity commercialDetailActivity);
}
