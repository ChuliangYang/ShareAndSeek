package com.me.cl.capstoneproject.ui.main.fragment.help.mvp

import android.os.Bundle
import android.os.Parcelable
import com.me.cl.capstoneproject.bean.HelpItem
import com.me.cl.capstoneproject.ui.main.fragment.free.mvp.FreePageInteractor
import io.reactivex.Single
import java.util.ArrayList

/**
 * Created by CL on 11/3/17.
 */

interface HelpPageInteractor {
    fun getHelpList(page: Int,district:String): Single<*>
    fun saveToCache(key: String, value: Any)
    fun getFromCache(key: String): Any?
    fun getFreeTagInteractor(): FreePageInteractor
    fun saveWidgetListModel(helpItemList: List<HelpItem>)
    fun saveListModel(freeList: ArrayList<HelpItem>?)
    fun getListModel(): ArrayList<HelpItem>?
    fun saveRvState(rvState: Parcelable?)
    fun getRvState(): Parcelable?
    fun getSaveInstanceState(): Bundle
    fun saveInstanceState(outState: Bundle?)
}
