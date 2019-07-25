package com.me.cl.capstoneproject.ui.upload.help

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.me.cl.capstoneproject.base.Constant
import com.me.cl.capstoneproject.bean.Photo
import com.me.cl.capstoneproject.event.UploadCompleteEvent
import com.me.cl.capstoneproject.ui.upload.free.mvp.FreeUploadPresenter
import com.me.cl.capstoneproject.ui.upload.free.mvp.FreeUploadView
import com.me.cl.capstoneproject.ui.upload.help.mvp.HelpUploadInteractor
import com.me.cl.capstoneproject.ui.upload.help.mvp.HelpUploadPresenter
import com.me.cl.capstoneproject.ui.upload.help.mvp.HelpUploadView
import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by CL on 12/1/17.
 */

class HelpUploadPresenterImpl @Inject
constructor(var helpUploadInteractor: HelpUploadInteractor,var freeUploadPresenter: FreeUploadPresenter<FreeUploadView>) : HelpUploadPresenter<HelpUploadView> {
    private lateinit var view: HelpUploadView
    var provider: LifecycleProvider<Lifecycle.Event>?=null


    override fun manage(view: HelpUploadView) {
        this.view = view
        freeUploadPresenter.manage(view)
    }

    override fun destory() {

    }

    override fun init() {

    }

    override fun init(activity: Activity,savedInstanceState: Bundle?,provider: LifecycleProvider<Lifecycle.Event>?) {
        this.provider=provider
        freeUploadPresenter.init(activity,savedInstanceState,provider)
    }


    override fun onCurrentLocationChecked(checked: Boolean?, context: Context) {
        freeUploadPresenter.onCurrentLocationChecked(checked, context)
    }

    override fun handleStart() {
        freeUploadPresenter.handleStart()
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        freeUploadPresenter.handleActivityResult(requestCode, resultCode, data)
    }

    override fun uploadForm(activity: Activity) {
        val formData = HashMap<String, Any>()
        view.collectFormDataFromUI(formData)
        view.restoreAllTextInputError()
        if (freeUploadPresenter.checkInputValid(formData) == true) {
            freeUploadPresenter.saveLatiLontiToData(formData).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).doOnSubscribe({ o -> view.controlIndeterminateProgress(true, "Please wait", "uploading...", null) }).doOnError({ throwable ->
                if (throwable is IllegalArgumentException) {
                    view.showAlertDialog("Sorry", "Please input a valid address", "OK", null)
                }
            }).observeOn(Schedulers.io()).toObservable().flatMap({ o -> freeUploadPresenter.getDependentCommercialUploadPresenter()?.interactor?.storePhotosInCloud(formData[Constant.UploadPage.MAP_KEY_PHOTOS] as ArrayList<Photo?>) })
                    .doOnError({ throwable -> }).toList()
                    .observeOn(Schedulers.io()).map({ o -> freeUploadPresenter.getCurrentInteractor().storeFormInCloud(o as ArrayList<Photo>,formData , Constant.Database.HELP_LIST_LOCATION) })
                    .observeOn(AndroidSchedulers.mainThread()).doFinally({ view.controlIndeterminateProgress(false, null, null, null) })
                    .compose(provider?.bindToLifecycle())
                    .subscribe({ success ->
                        view.showAlertDialog("Success", "Upload Success!", "OK", MaterialDialog.SingleButtonCallback{ dialog, which ->
                            dialog.dismiss()
                            EventBus.getDefault().postSticky(UploadCompleteEvent.HelpService(true))
                            activity.onBackPressed()
                        })
                    }, { throwable -> Timber.e(throwable.toString()) })
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray, activity: Activity) {
        freeUploadPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults, activity)
    }
    override fun saveScrollState(scrollY: Int) {
        freeUploadPresenter.saveScrollState(scrollY)
    }
}
