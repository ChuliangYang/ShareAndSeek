package com.me.cl.capstoneproject.ui.upload.free.mvp

import com.me.cl.capstoneproject.bean.Photo
import java.util.*

/**
 * Created by CL on 11/29/17.
 */

interface FreeUploadInteractor{
    fun storeFormInCloud(photos: ArrayList<Photo>, form: HashMap<String, Any>,databaseLocation:String): Boolean?
}
