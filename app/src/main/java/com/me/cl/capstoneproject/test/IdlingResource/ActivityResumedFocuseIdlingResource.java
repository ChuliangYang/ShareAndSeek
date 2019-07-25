package com.me.cl.capstoneproject.test.IdlingResource;

import android.arch.lifecycle.Lifecycle;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;

import javax.annotation.Nullable;

/**
 * Created by CL on 1/28/18.
 */

public class ActivityResumedFocuseIdlingResource implements IdlingResource {
    Long continueFocusTime=100L;//ms

    AppCompatActivity activity;
    ResourceCallback resourceCallback;
    Long firstFocusTime;
    Long currentFoucusTime;

    public ActivityResumedFocuseIdlingResource(AppCompatActivity activity, @Nullable Long continueFocusTime) {
        this.activity = activity;
        if (continueFocusTime!=null) {
            this.continueFocusTime=continueFocusTime;
        }
    }

    @Override
    public String getName() {
        return ActivityResumedFocuseIdlingResource.class.getName()+System.currentTimeMillis();
    }

    @Override
    public boolean isIdleNow() {
        boolean isidle;
        if (activity==null) {
            return false;
        }

        if (activity.getLifecycle().getCurrentState()!= Lifecycle.State.RESUMED) {
            return false;
        }

        if (activity.hasWindowFocus()) {
            currentFoucusTime=System.currentTimeMillis();
            if (firstFocusTime != null) {
                if (currentFoucusTime - firstFocusTime >= continueFocusTime) {
                    resourceCallback.onTransitionToIdle();
                    isidle= true;
                } else {
                    isidle= false;
                }
            } else {
                firstFocusTime=currentFoucusTime;
                isidle= false;
            }
        } else {
            firstFocusTime=null;
            currentFoucusTime=null;
            isidle= false;
        }
        return isidle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
            resourceCallback=callback;
    }
}
