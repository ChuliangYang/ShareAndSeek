package com.me.cl.capstoneproject.ui.upload.help.mvp

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.me.cl.capstoneproject.base.BasePresenter
import com.trello.rxlifecycle2.LifecycleProvider

/**
 * Created by CL on 12/1/17.
 */

interface HelpUploadPresenter<V> : BasePresenter<V>{
    fun onCurrentLocationChecked(checked: Boolean?, context: Context)
    fun handleStart()
    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    fun uploadForm(activity: Activity)
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray, activity: Activity)
    fun saveScrollState(scrollY:Int)
    fun init(activity: Activity, savedInstanceState: Bundle?, provider: LifecycleProvider<Lifecycle.Event>?)
}
