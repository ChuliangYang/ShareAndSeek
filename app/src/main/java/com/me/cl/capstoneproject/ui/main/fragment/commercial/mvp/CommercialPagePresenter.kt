package com.me.cl.capstoneproject.ui.main.fragment.commercial.mvp

import android.arch.lifecycle.Lifecycle
import android.os.Bundle

import com.me.cl.capstoneproject.base.BasePresenter
import com.me.cl.capstoneproject.bean.CommercialItem
import com.trello.rxlifecycle2.LifecycleProvider

/**
 * Created by CL on 11/3/17.
 */

interface CommercialPagePresenter<V> : BasePresenter<V> {
    val currentDisplayedTitlePosition: Int?
    val currentDisplayedTitle: String?
    fun configCommercialCatogary()
    fun configCommercialListFirstPage(title: String?, position: Int?, swipe: Boolean?, animate: Boolean?, isRestore: Boolean?)
    fun configCommercialListNextPage()
    fun onTitleDecided(title: String?, position: Int?)
    fun saveCurrentTitlePosition(position: Int?)
    fun onTagsForeGroundStateChange(title: String?, titlePosition: Int?, tagPosition: Int?)
    fun onRefreshList(success: Boolean?, animate: Boolean?)
    fun saveCurrentTitle(title: String?)
    fun saveWidgetListModel(commercialItemList: MutableList<CommercialItem>?)

    fun onSaveInstanceState(outState: Bundle?)
    fun init(instanceState: Bundle?, provider: LifecycleProvider<Lifecycle.Event>?)
}
