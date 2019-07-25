package com.me.cl.capstoneproject.ui.main.fragment.free.mvp

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import com.me.cl.capstoneproject.base.BasePresenter
import com.me.cl.capstoneproject.bean.FreeItem
import com.trello.rxlifecycle2.LifecycleProvider

/**
 * Created by CL on 11/3/17.
 */

interface FreePagePresenter<V> : BasePresenter<V> {
    fun configFreeList(title:String,position:Int,swipe:Boolean,restore:Boolean)
    fun onTitleDecided(title: String,position:Int)
    fun saveCurrentTitlePosition(position: Int)
    fun getCurrentDisplayedTitlePosition(): Int
    fun onTagsForeGroundStateChange(title: String, titlePosition: Int, tagPosition: Int)
    fun onUploadComplete(success: Boolean)
    fun saveCurrentTitle(title: String)
    fun getCurrentDisplayedTitle(): String
    fun saveWidgetListModel(freeItemList: List<FreeItem>)
    fun onSaveInstanceState(outState: Bundle)
    fun init(savedInstanceState: Bundle?, provider: LifecycleProvider<Lifecycle.Event>?)
}
