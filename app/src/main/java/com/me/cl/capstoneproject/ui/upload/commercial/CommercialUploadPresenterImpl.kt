package com.me.cl.capstoneproject.ui.upload.commercial

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.lzy.imagepicker.ImagePicker
import com.lzy.imagepicker.bean.ImageItem
import com.me.cl.capstoneproject.base.Constant
import com.me.cl.capstoneproject.base.Constant.UploadPage.*
import com.me.cl.capstoneproject.bean.MyLocation
import com.me.cl.capstoneproject.bean.Photo
import com.me.cl.capstoneproject.ui.upload.commercial.mvp.CommercialUploadInteractor
import com.me.cl.capstoneproject.ui.upload.commercial.mvp.CommercialUploadPresenter
import com.me.cl.capstoneproject.ui.upload.commercial.mvp.CommercialUploadView
import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Created by CL on 11/15/17.
 */

class CommercialUploadPresenterImpl @Inject
constructor(override var interactor: CommercialUploadInteractor) : CommercialUploadPresenter<CommercialUploadView> {

    lateinit var view: CommercialUploadView
//    override var interactor: CommercialUploadInteractor? = null
//         set(value: CommercialUploadInteractor?) {
//            super.interactor = value
//        }

//    init {
//        this.interactor = interactor
//    }
    var provider: LifecycleProvider<Lifecycle.Event>?=null


    override fun manage(view: CommercialUploadView) {
        this.view = view
    }

    override fun destory() {

    }

    override fun init() {}

    override fun init(activity: Activity?, savedInstanceState: Bundle?,provider: LifecycleProvider<Lifecycle.Event>?) {
        this.provider=provider
        configGoogleClient(activity)
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_KEY_PHOTO_LIST)) {
            view.photoModel = savedInstanceState.getParcelableArrayList<Photo>(BUNDLE_KEY_PHOTO_LIST)
        }
    }

    override fun onCurrentLocationChecked(checked: Boolean?, context: Context?) {
        if (checked == true) {
            context?.let {
                if (ActivityCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((it as Activity),
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                REQUEST_CODE_ACCESS_FINE_LOCATION)
                    }
                    return
                }
            }
            fetchMyLocation(interactor.getGoogleLocationApiClient(null, null))
        } else {
            view.popMyAddress(MyLocation())
            LocationServices.FusedLocationApi.removeLocationUpdates(interactor.getGoogleLocationApiClient(null, null), interactor.locationListener)
        }
    }

    @SuppressLint("MissingPermission")
    override fun fetchMyLocation(mGoogleApiClient: GoogleApiClient?) {
        Single.create<Any> { e ->
            val locationListener = LocationListener{ location -> e.onSuccess(true) }
            interactor.saveLocationListener(locationListener)
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, interactor.locationRequest, locationListener)
        }.subscribeOn(AndroidSchedulers.mainThread())

                .observeOn(Schedulers.io()).flatMap<Any?> { o ->
                    interactor.getCurrentLocation(mGoogleApiClient)?.timeout(30, TimeUnit.SECONDS)
                            ?.doOnError { throwable ->
                                view.popMyAddress(MyLocation())
                                view.showAlertDialog("Sorry", "We can't fetch your location at this time,please try it later!", "OK", null)
                                view.checkedFetchLocation(false)
                                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, interactor.locationListener)
                            }
                }
                .observeOn(Schedulers.io()).map<MyLocation> { o -> if (interactor.parseLocation(o as Location) != null) interactor.parseLocation(o as Location) else MyLocation() }
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .compose(provider?.bindToLifecycle())
                .subscribe { o ->
                    if ("New York" == (o as MyLocation).state) {
                        view.popMyAddress(o)
                        interactor.storeAddressLocation(interactor.getWholeAddressFromLocationBean(o), o)
                    } else {
                        view.popMyAddress(MyLocation())
                        view.showAlertDialog("Sorry", "Currently only support New York State", "OK", null)
                        view.checkedFetchLocation(false)
                    }
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, interactor.locationListener)

                }
    }

    override fun handleActivityResult(requestCode: Int?, resultCode: Int?, data: Intent?) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == PICK_UP_PHOTO_REQUEST_CODE) {
                val imageItems = data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) as ArrayList<ImageItem>
                var photos = ArrayList<Photo?>()
                for (imageItem in imageItems) {
                    val photo = Photo()
                    photo.downloadUri = imageItem.path
                    photo.setSize(imageItem.size)
                    photo.addTime = imageItem.addTime
                    photo.height = imageItem.height
                    photo.width = imageItem.width
                    photo.name = imageItem.name
                    photo.path = imageItem.path
                    photo.mimeType = imageItem.mimeType
                    photo.size = imageItem.size
                    photos.add(photo)
                }

                if (photos != null) {
                    view.addPhotos(photos)
                    if (interactor.getFromCache("ScrollState") != null) {
                        view.scrollTo(0, interactor.getFromCache("ScrollState") as Int)
                    }
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                val images = data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS) as ArrayList<ImageItem>
                val photos = ArrayList<Photo?>()
                for (imageItem in images) {
                    val photo = Photo()
                    photo.downloadUri = imageItem.path
                    photo.setSize(imageItem.size)
                    photo.addTime = imageItem.addTime
                    photo.height = imageItem.height
                    photo.width = imageItem.width
                    photo.name = imageItem.name
                    photo.path = imageItem.path
                    photo.mimeType = imageItem.mimeType
                    photo.size = imageItem.size
                    photos.add(photo)
                }
                if (photos != null) {
                    view.replaceAllPhotos(photos)
                }
            }
        }
    }

    override fun uploadForm(activity: Activity?) {
        val formData = HashMap<String, Any>()
        view.collectFormDataFromUI(formData)
        view.restoreAllTextInputError()

        if (checkInputValid(formData)== true) {
            saveLatiLontiToData(formData)?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())?.doOnSubscribe { o -> view.controlIndeterminateProgress(true, "Please wait", "uploading...", null) }?.doOnError { throwable ->
                        if (throwable is IllegalArgumentException) {
                            view.showAlertDialog("Sorry", "Please input a valid address", "OK", null)
                        }
                    }?.observeOn(Schedulers.io())?.toObservable()?.flatMap { o -> interactor.storePhotosInCloud(formData[MAP_KEY_PHOTOS] as ArrayList<Photo?>) }
                    ?.doOnError { throwable -> }?.toList()
                    ?.observeOn(Schedulers.io())?.map { o -> interactor.storeFormInCloud(o as ArrayList<Photo>, formData) }
                    ?.observeOn(AndroidSchedulers.mainThread())?.doFinally { view.controlIndeterminateProgress(false, null, null, null) }
                    ?.subscribe({ success ->
                        view.showAlertDialog("Success", "Upload Success!", "OK", MaterialDialog.SingleButtonCallback { dialog, which ->
                            dialog.dismiss()
                            activity?.setResult(Activity.RESULT_OK)
                            activity?.onBackPressed()
                        })
                        //                        EventBus.getDefault().postSticky(new UploadCompleteEvent.CommercialService(true));
                    }) { throwable -> Timber.e(throwable.toString()) }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int?, permissions: Array<String>?, grantResults: IntArray?, activity: Activity?) {
        when (requestCode) {
            REQUEST_CODE_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults?.size?:0 > 0 && grantResults?.get(0) ?: "" == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    fetchMyLocation(interactor.getGoogleLocationApiClient(null, null))
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    view.setCheckBoxState(false)
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    override fun configGoogleClient(activity: Activity?) {
        interactor.getGoogleLocationApiClient(object : GoogleApiClient.ConnectionCallbacks {
            override fun onConnected(bundle: Bundle?) {}

            override fun onConnectionSuspended(i: Int) {
                interactor.getGoogleLocationApiClient(null, null)?.connect()

            }
        }, GoogleApiClient.OnConnectionFailedListener { connectionResult -> })
    }

    override fun handleStart() {
        if (interactor.getGoogleLocationApiClient(null, null) != null) {
            interactor.getGoogleLocationApiClient(null, null)?.connect()
        }
    }

    fun checkInputValid(form: HashMap<String, Any>): Boolean? {

        if (TextUtils.isEmpty(form[Constant.UploadPage.MAP_KEY_TITLE] as String)) {
            view.showAlertForFormField(Constant.UploadPage.FLAG_TITLE_TEXT_FIELD)
            form.clear()
            return false
        }

        if (TextUtils.isEmpty(form[Constant.UploadPage.MAP_KEY_AVERAGE] as String)) {

        }

        if (TextUtils.isEmpty(form[Constant.UploadPage.MAP_KEY_STREET] as String)) {
            view.showAlertForFormField(Constant.UploadPage.FLAG_ADDRESS_TEXT_FIELD)
            form.clear()
            return false
        }

        if (TextUtils.isEmpty(form[Constant.UploadPage.MAP_KEY_ZIP] as String)) {
            view.showAlertForFormField(Constant.UploadPage.FLAG_ZIP_TEXT_FIELD)
            form.clear()
            return false
        }

        if (TextUtils.isEmpty(form[Constant.UploadPage.MAP_KEY_WECHAT] as String)) {
        }

        if (TextUtils.isEmpty(form[Constant.UploadPage.MAP_KEY_PHONE] as String) || !Pattern.matches(VAILD_PHONE_PATTERN, form[Constant.UploadPage.MAP_KEY_PHONE] as String)) {
            view.showAlertForFormField(Constant.UploadPage.FLAG_PHONE_TEXT_FIELD)
            form.clear()
            return false
        }

        if (TextUtils.isEmpty(form[Constant.UploadPage.MAP_KEY_CATEGORY] as CharSequence) || "Type" == form[Constant.UploadPage.MAP_KEY_CATEGORY]) {
            view.showAlertForFormField(Constant.UploadPage.FLAG_TYPE_FIELD)
            form.clear()
            return false
        }

        if (TextUtils.isEmpty(form[Constant.UploadPage.MAP_KEY_CITY] as CharSequence) || "City" == form[Constant.UploadPage.MAP_KEY_CITY]) {
            view.showAlertForFormField(Constant.UploadPage.FLAG_CITY_FIELD)
            form.clear()
            return false
        }

        if (TextUtils.isEmpty(form[Constant.UploadPage.MAP_KEY_SUMMARY] as String)) {
            view.showAlertForFormField(Constant.UploadPage.FLAG_SUMMARY_TEXT_FIELD)
            form.clear()
            return false
        }

        return true
    }

    override fun saveLatiLontiToData(formData: HashMap<String, Any>?): Single<*>? {
        return Single.create<Any> { e ->
            formData?.let {
                val street = it[MAP_KEY_STREET] as String?
                val city = it[MAP_KEY_CITY] as String?
                val state = it[MAP_KEY_STATE] as String?

                val wholeAddress = String.format("%s,%s,%s", street, city, state)

                interactor.getLocationFromAddress(wholeAddress)?.apply {
                    it[MAP_KEY_LATITUDE] = latitude
                    it[MAP_KEY_LONGTITUDE] = longitude
                    e.onSuccess(true)
                }?:apply {
                    if (interactor.parseAddress(street, city, state) != null) {
                        it[MAP_KEY_LATITUDE] = interactor.parseAddress(street, city, state)!!.latitude
                        it[MAP_KEY_LONGTITUDE] = interactor.parseAddress(street, city, state)!!.longitude
                        e.onSuccess(true)
                    } else {
                        e.onError(IllegalArgumentException())
                    }
                }
            }
        }
    }

    override fun saveScrollState(scrollY: Int?) {
        interactor.saveToCache("ScrollState", scrollY)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelableArrayList(BUNDLE_KEY_PHOTO_LIST, view.photoModel)
    }
}
