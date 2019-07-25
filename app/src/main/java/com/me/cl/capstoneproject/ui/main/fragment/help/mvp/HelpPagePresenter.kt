package com.me.cl.capstoneproject.ui.main.fragment.help.mvp

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import com.me.cl.capstoneproject.base.BasePresenter
import com.me.cl.capstoneproject.bean.HelpItem
import com.me.cl.capstoneproject.ui.main.fragment.free.mvp.FreePagePresenter
import com.me.cl.capstoneproject.ui.main.fragment.free.mvp.FreePageView
import com.trello.rxlifecycle2.LifecycleProvider

/**
 * Created by CL on 11/3/17.
 */

interface HelpPagePresenter<V> : BasePresenter<V> {
    fun configHelpList(title:String,position:Int,swipe:Boolean,restore:Boolean)
    fun onTitleDecided(title: String, position: Int)
    fun onTagsForeGroundStateChange(title: String, titlePosition: Int, tagPosition: Int)
    fun onUploadComplete(success: Boolean)
    fun getDependentPresenter(): FreePagePresenter<FreePageView>
    fun saveWidgetListModel(helpItemList: List<HelpItem>)
    fun onSaveInstanceState(outState: Bundle)
    fun init(savedInstanceState: Bundle?, provider: LifecycleProvider<Lifecycle.Event>?)
}
