package com.me.cl.capstoneproject.ui.upload.commercial.mvp

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.common.api.GoogleApiClient
import com.me.cl.capstoneproject.base.BasePresenter
import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.Single
import java.util.*

/**
 * Created by CL on 11/15/17.
 */

interface CommercialUploadPresenter<V> : BasePresenter<V> {
    val interactor: CommercialUploadInteractor?
    fun onCurrentLocationChecked(checked: Boolean?, context: Context?)
    fun fetchMyLocation(mGoogleApiClient: GoogleApiClient?)
    fun handleActivityResult(requestCode: Int?, resultCode: Int?, data: Intent?)
    fun uploadForm(activity: Activity?)
    fun onRequestPermissionsResult(requestCode: Int?, permissions: Array<String>?, grantResults: IntArray?, activity: Activity?)
    fun configGoogleClient(activity: Activity?)
    fun handleStart()
    fun saveLatiLontiToData(formData: HashMap<String, Any>?): Single<*>?
    fun saveScrollState(scrollY: Int?)
    fun onSaveInstanceState(outState: Bundle?)
    fun init(activity: Activity?, savedInstanceState: Bundle?, provider: LifecycleProvider<Lifecycle.Event>?)
}
