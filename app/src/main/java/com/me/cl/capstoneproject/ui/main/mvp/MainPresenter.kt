package com.me.cl.capstoneproject.ui.main.mvp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.view.View

import com.me.cl.capstoneproject.base.BasePresenter

/**
 * Created by CL on 11/1/17.
 */

interface MainPresenter<V> : BasePresenter<V> {
    fun configDistrictsSpinner()
    fun configViewPagerWithTab()
    fun onViewPageSelected(position: Int, title: String, titlePosition: Int)
    fun onFabCreateClick(context: Context,view: View,rootView:View)
    fun onStart()
    fun handleActivityResult(requestCode: Int?, resultCode: Int?, data: Intent?, activity: Activity?)
    fun onOptionsItemSelected(item: MenuItem?, activity: Activity?): Boolean
    fun onTitleChange(title: String, position: Int, origin: Int)
}
