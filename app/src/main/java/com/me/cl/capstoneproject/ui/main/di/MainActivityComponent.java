package com.me.cl.capstoneproject.ui.main.di;

import com.me.cl.capstoneproject.ApplicationComponent;
import com.me.cl.capstoneproject.di.ActivityScope;
import com.me.cl.capstoneproject.ui.main.MainActivity;

import dagger.Component;

/**
 * Created by CL on 11/3/17.
 */
@ActivityScope
@Component(dependencies = ApplicationComponent.class ,modules = MainActivityModule.class)
public interface MainActivityComponent {
    void inject(MainActivity mainActivity);
}
