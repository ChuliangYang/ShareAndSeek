package com.me.cl.capstoneproject.ui.upload.commercial.mvp

import android.location.Location
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.me.cl.capstoneproject.bean.MyLocation
import com.me.cl.capstoneproject.bean.Photo
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

/**
 * Created by CL on 11/15/17.
 */

interface CommercialUploadInteractor {
    val locationRequest: LocationRequest?
    val locationListener: LocationListener?
    fun getCurrentLocation(mGoogleApiClient: GoogleApiClient?): Single<*>?
    fun parseLocation(location: Location?): MyLocation?
    fun convertIntoPhotoData(vararg data: Any?): Photo?
    fun storePhotosInCloud(photos: ArrayList<Photo?>?): Observable<*>?
    fun storeFormInCloud(photos: ArrayList<Photo>?, form: HashMap<String, Any>?): Boolean?
    fun saveLocationListener(locationListener: LocationListener?)
    fun getGoogleLocationApiClient(connectionCallbacks: GoogleApiClient.ConnectionCallbacks?, onConnectionFailedListener: GoogleApiClient.OnConnectionFailedListener?): GoogleApiClient?
    fun storeAddressLocation(wholeAddress: String?, location: MyLocation?)
    fun getLocationFromAddress(wholeAddress: String?): MyLocation?
    fun getWholeAddressFromLocationBean(myLocation: MyLocation?): String?
    fun parseAddress(street: String?, city: String?, state: String?): MyLocation?
    fun saveToCache(key: String?, value: Any?)
    fun getFromCache(key: String?): Any?
}
