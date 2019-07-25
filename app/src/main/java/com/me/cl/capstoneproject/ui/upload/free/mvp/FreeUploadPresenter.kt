package com.me.cl.capstoneproject.ui.upload.free.mvp

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.me.cl.capstoneproject.base.BasePresenter
import com.me.cl.capstoneproject.ui.upload.commercial.mvp.CommercialUploadPresenter
import com.me.cl.capstoneproject.ui.upload.commercial.mvp.CommercialUploadView
import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.Single
import java.util.*

/**
 * Created by CL on 11/29/17.
 */

interface FreeUploadPresenter<V> : BasePresenter<V>{
    fun onCurrentLocationChecked(checked: Boolean?, context: Context)
    fun handleStart()
    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    fun uploadForm(activity: Activity)
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray, activity: Activity)
    fun checkInputValid(form: HashMap<String, Any>): Boolean?
    fun saveLatiLontiToData(formData: HashMap<String, Any>): Single<*>
    fun getDependentCommercialUploadPresenter(): CommercialUploadPresenter<CommercialUploadView>?
    fun getCurrentInteractor():FreeUploadInteractor
    fun saveScrollState(scrollY:Int)
    fun init(activity: Activity, savedInstanceState: Bundle?, provider: LifecycleProvider<Lifecycle.Event>?)
}
