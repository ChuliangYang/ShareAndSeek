package com.me.cl.capstoneproject.ui.upload.commercial.mvp

import android.content.DialogInterface
import com.afollestad.materialdialogs.MaterialDialog
import com.me.cl.capstoneproject.bean.MyLocation
import com.me.cl.capstoneproject.bean.Photo
import java.util.*

/**
 * Created by CL on 11/15/17.
 */

interface CommercialUploadView {
    var photoModel: ArrayList<Photo?>?
    fun popMyAddress(myLocation: MyLocation?)
    fun addPhotos(photos: ArrayList<Photo?>?)
    fun replaceAllPhotos(photos: ArrayList<Photo?>?)
    fun collectFormDataFromUI(form: HashMap<String, Any>?)
    fun showAlertForFormField(textFieldFlag: Int?)
    fun restoreAllTextInputError()
    fun showAlertDialog(title: String?, content: String?, button: String?, singleButtonCallback: MaterialDialog.SingleButtonCallback?)
    fun checkedFetchLocation(checked: Boolean?)
    fun controlIndeterminateProgress(show: Boolean?, title: String?, content: String?, onCancelListener: DialogInterface.OnCancelListener?)
    fun setCheckBoxState(checked: Boolean?)
    fun scrollTo(x: Int?, y: Int?)
}
