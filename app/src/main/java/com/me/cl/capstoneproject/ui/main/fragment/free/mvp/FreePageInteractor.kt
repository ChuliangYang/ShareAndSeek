package com.me.cl.capstoneproject.ui.main.fragment.free.mvp

import android.os.Bundle
import android.os.Parcelable
import com.me.cl.capstoneproject.bean.FreeItem
import io.reactivex.Single

/**
 * Created by CL on 11/3/17.
 */

interface FreePageInteractor {
    fun getFreeList(page: Int,district:String): Single<*>
    fun saveToCache(key: String, value: Any)
    fun getFromCache(key: String): Any?
    fun getTitleFromTitlePosition(position: Int): String
    fun saveWidgetListModel(freeList: List<FreeItem>)
    fun getSaveInstanceState(): Bundle
    fun saveInstanceState(outState: Bundle?)
    fun saveListModel(freeList: ArrayList<FreeItem>?)
    fun getListModel(): ArrayList<FreeItem>?
    fun saveRvState(rvState: Parcelable?)
    fun getRvState(): Parcelable?
}
