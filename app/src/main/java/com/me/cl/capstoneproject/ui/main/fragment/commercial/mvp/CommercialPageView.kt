package com.me.cl.capstoneproject.ui.main.fragment.commercial.mvp

import android.content.Context
import android.content.DialogInterface
import android.os.Parcelable

import com.afollestad.materialdialogs.MaterialDialog
import com.me.cl.capstoneproject.bean.CommercialCategory
import com.me.cl.capstoneproject.bean.CommercialItem

/**
 * Created by CL on 11/3/17.
 */

interface CommercialPageView {

    val commercialListModel: MutableList<CommercialItem>?

    val rvState: Parcelable?
    fun popCommercialCategoryPagers(multiCommercialCategoryList: MutableList<MutableList<CommercialCategory>>?)
    fun popCommericialList(commercialItems:MutableList<CommercialItem>?, restore: Boolean?, animate: Boolean?, rvState: Parcelable?)
    fun showAlertDialog(title: String?, content: String?, button: String?, singleButtonCallback: MaterialDialog.SingleButtonCallback?, onCancelListener: DialogInterface.OnCancelListener?, context: Context?)
    fun controlIndeterminateProgress(show: Boolean?, title: String?, content: String?, onCancelListener: DialogInterface.OnCancelListener?, context: Context?, swipe: Boolean?)
}
