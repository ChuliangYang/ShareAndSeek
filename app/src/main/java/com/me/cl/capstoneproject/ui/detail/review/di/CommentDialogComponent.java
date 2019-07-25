package com.me.cl.capstoneproject.ui.detail.review.di;

import com.me.cl.capstoneproject.ApplicationComponent;
import com.me.cl.capstoneproject.di.FragmentScope;
import com.me.cl.capstoneproject.ui.detail.review.CommentDialogFragment;

import dagger.Component;

/**
 * Created by CL on 11/30/17.
 */
@Component(dependencies = ApplicationComponent.class,modules = CommentDialogModule.class)
@FragmentScope
public interface CommentDialogComponent {
    void inject(CommentDialogFragment commentDialogFragment);
}
