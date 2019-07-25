package com.me.cl.capstoneproject.ui.detail.mvp

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.content.Intent
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.me.cl.capstoneproject.base.BasePresenter
import com.trello.rxlifecycle2.LifecycleProvider

/**
 * Created by CL on 11/22/17.
 */

interface CommercialDetailPresenter<V> : BasePresenter<V> {
    fun configBaseInfo()
    fun configReviewList()
    fun configMap(googleMap: GoogleMap)
    fun onCommentClick(fragmentManager: FragmentManager, activity: Activity)
    fun handleActivityResult(requestCode: Int?, resultCode: Int?, data: Intent?, activity: AppCompatActivity?)
    fun onNewCommentPublish(addScore:Float)
    fun init(provider: LifecycleProvider<Lifecycle.Event>?)
}
