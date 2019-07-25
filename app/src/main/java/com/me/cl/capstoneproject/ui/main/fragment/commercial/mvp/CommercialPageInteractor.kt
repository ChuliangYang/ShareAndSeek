package com.me.cl.capstoneproject.ui.main.fragment.commercial.mvp

import android.os.Bundle

import com.me.cl.capstoneproject.bean.CommercialCategory
import com.me.cl.capstoneproject.bean.CommercialItem

import io.reactivex.Single

/**
 * Created by CL on 11/3/17.
 */

interface CommercialPageInteractor {
    val commercialCateList: MutableList<CommercialCategory>?
    val lastKey: String?
    val saveInstanceState: Bundle?
    fun divideIntoMutiCommercialCateList(CommercialCateList: MutableList<CommercialCategory>?): MutableList<MutableList<CommercialCategory>>?
    fun getCommercialList(StartKey: String?): Single<*>?
    fun saveLastKey(key: String?)
    fun saveToCache(key: String?, value: Any?)
    fun getFromCache(key: String?): Any?
    fun getTitleFromTitlePosition(position: Int?): String?
    fun saveWidgetListModel(commercialItemList: MutableList<CommercialItem>?)
    fun saveInstanceState(outState: Bundle?)
}
