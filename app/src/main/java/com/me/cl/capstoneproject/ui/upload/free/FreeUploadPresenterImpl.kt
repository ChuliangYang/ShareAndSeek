package com.me.cl.capstoneproject.ui.upload.free

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.me.cl.capstoneproject.base.Constant
import com.me.cl.capstoneproject.base.Constant.UploadPage.MAP_KEY_PHOTOS
import com.me.cl.capstoneproject.bean.Photo
import com.me.cl.capstoneproject.event.UploadCompleteEvent
import com.me.cl.capstoneproject.ui.upload.commercial.mvp.CommercialUploadPresenter
import com.me.cl.capstoneproject.ui.upload.commercial.mvp.CommercialUploadView
import com.me.cl.capstoneproject.ui.upload.free.mvp.FreeUploadInteractor
import com.me.cl.capstoneproject.ui.upload.free.mvp.FreeUploadPresenter
import com.me.cl.capstoneproject.ui.upload.free.mvp.FreeUploadView
import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by CL on 11/29/17.
 */

class FreeUploadPresenterImpl @Inject
constructor(
        var interactor: FreeUploadInteractor, var commercialUploadPresenter: CommercialUploadPresenter<CommercialUploadView>) : FreeUploadPresenter<FreeUploadView> {


    lateinit var view: FreeUploadView
    var provider: LifecycleProvider<Lifecycle.Event>?=null


    override fun manage(view: FreeUploadView) {
        this.view = view
        commercialUploadPresenter.manage(view)
    }

    override fun destory() {

    }

    override fun init() {
    }

    override fun init(activity: Activity,savedInstanceState:Bundle?,provider: LifecycleProvider<Lifecycle.Event>?) {
        this.provider=provider
        commercialUploadPresenter.init(activity,savedInstanceState,provider)
    }


    override fun onCurrentLocationChecked(checked: Boolean?, context: Context) {
        commercialUploadPresenter.onCurrentLocationChecked(checked,context)
    }

    override fun handleStart() {
        commercialUploadPresenter.handleStart()
    }

    override fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        commercialUploadPresenter.handleActivityResult(requestCode,resultCode,data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray, activity: Activity) {
        commercialUploadPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults, activity)
    }

    override fun uploadForm(activity: Activity) {
        val formData = HashMap<String, Any>()
        view.collectFormDataFromUI(formData)
        view.restoreAllTextInputError()
        if (checkInputValid(formData)== true) {
            saveLatiLontiToData(formData).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).doOnSubscribe({ o -> view.controlIndeterminateProgress(true, "Please wait", "uploading...", null) }).doOnError({ throwable ->
                if (throwable is IllegalArgumentException) {
                    view.showAlertDialog("Sorry", "Please input a valid address", "OK", null)
                }
            }).observeOn(Schedulers.io()).toObservable().flatMap({ o -> commercialUploadPresenter.interactor?.storePhotosInCloud(formData[MAP_KEY_PHOTOS] as ArrayList<Photo?>) })
                    .doOnError({ throwable -> }).toList()
                    .observeOn(Schedulers.io()).map({ o -> interactor.storeFormInCloud(o as ArrayList<Photo>,formData , Constant.Database.FREE_LIST_LOCATION) })
                    .observeOn(AndroidSchedulers.mainThread()).doFinally({ view.controlIndeterminateProgress(false, null, null, null) })
                    .compose(provider?.bindToLifecycle())
                    .subscribe({ success ->
                        view.showAlertDialog("Success", "Upload Success!", "OK", MaterialDialog.SingleButtonCallback { dialog, which ->
                            dialog.dismiss()
                            EventBus.getDefault().postSticky(UploadCompleteEvent.FreeService(true))
                            activity.onBackPressed()
                        })
                    }, { throwable -> Timber.e(throwable.toString()) })
        }
    }

    override fun checkInputValid(form: HashMap<String, Any>): Boolean? {
        for ((key,value) in form){
            when(key){
                Constant.UploadPage.MAP_KEY_TITLE->
                    if((value as? String).isNullOrEmpty()){
                        view.showAlertForFormField(Constant.UploadPage.FLAG_TITLE_TEXT_FIELD)
                        form.clear()
                        return@checkInputValid false
                    }
                Constant.UploadPage.MAP_KEY_STREET->
                    if((value as? String).isNullOrEmpty()){
                        view.showAlertForFormField(Constant.UploadPage.FLAG_ADDRESS_TEXT_FIELD)
                        form.clear()
                        return@checkInputValid false
                    }
                Constant.UploadPage.MAP_KEY_ZIP->
                    if((value as? String).isNullOrEmpty()){
                        view.showAlertForFormField(Constant.UploadPage.FLAG_ZIP_TEXT_FIELD)
                        form.clear()
                        return@checkInputValid false
                    }
                Constant.UploadPage.MAP_KEY_WECHAT->
                    if((value as? String).isNullOrEmpty()){

                    }
                Constant.UploadPage.MAP_KEY_PHONE->
                    if((value as? String).isNullOrEmpty()){
                        view.showAlertForFormField(Constant.UploadPage.FLAG_PHONE_TEXT_FIELD)
                        form.clear()
                        return@checkInputValid false
                    }
                Constant.UploadPage.MAP_KEY_CITY->
                    if((value as? String).isNullOrEmpty()||(value as? String).equals("City")){
                        view.showAlertForFormField(Constant.UploadPage.FLAG_CITY_FIELD)
                        form.clear()
                        return@checkInputValid false
                    }
                Constant.UploadPage.MAP_KEY_SUMMARY->
                    if((value as? String).isNullOrEmpty()){
                        view.showAlertForFormField(Constant.UploadPage.FLAG_SUMMARY_TEXT_FIELD)
                        form.clear()
                        return@checkInputValid false
                    }
                Constant.UploadPage.MAP_KEY_END->
                    if((value as? String).isNullOrEmpty()||(value as? String).equals("Expire Time")){
                        view.showAlertForFormField(Constant.UploadPage.FLAG_END_TIME)
                        form.clear()
                        return@checkInputValid false
                    }
            }
        }

        return true

    }

    override fun saveLatiLontiToData(formData: HashMap<String, Any>): Single<*> {
        return commercialUploadPresenter.saveLatiLontiToData(formData)!!
    }

    override fun getDependentCommercialUploadPresenter(): CommercialUploadPresenter<CommercialUploadView>? {
        return commercialUploadPresenter
    }

    override fun getCurrentInteractor(): FreeUploadInteractor {
        return interactor
    }

    override fun saveScrollState(scrollY: Int) {
        commercialUploadPresenter.saveScrollState(scrollY)
    }

}
