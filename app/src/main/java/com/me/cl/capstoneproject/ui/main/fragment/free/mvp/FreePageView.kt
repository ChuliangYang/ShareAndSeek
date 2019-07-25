package com.me.cl.capstoneproject.ui.main.fragment.free.mvp

import android.content.Context
import android.content.DialogInterface
import android.os.Parcelable
import com.afollestad.materialdialogs.MaterialDialog
import com.me.cl.capstoneproject.bean.FreeItem

/**
 * Created by CL on 11/3/17.
 */

interface FreePageView {
    fun showAlertDialog(title: String, content: String, button: String, singleButtonCallback: MaterialDialog.SingleButtonCallback, onCancelListener: DialogInterface.OnCancelListener, context: Context)
    fun controlIndeterminateProgress(show: Boolean, title: String?, content: String?, onCancelListener: DialogInterface.OnCancelListener, context: Context?, swipe:Boolean)
    fun getCommercialListModel(): List<FreeItem>?=null
    fun getRvState(): Parcelable?=null
    fun popFreeList(freeItemBeans: List<FreeItem>, restore: Boolean?, rvState: Parcelable?)=Unit
}
