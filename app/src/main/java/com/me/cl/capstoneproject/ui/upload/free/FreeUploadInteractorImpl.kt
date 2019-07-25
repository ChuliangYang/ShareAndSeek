package com.me.cl.capstoneproject.ui.upload.free

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.me.cl.capstoneproject.base.Constant.UploadPage.*
import com.me.cl.capstoneproject.bean.FreeItem
import com.me.cl.capstoneproject.bean.Photo
import com.me.cl.capstoneproject.di.CommercialUploadQL
import com.me.cl.capstoneproject.ui.upload.free.mvp.FreeUploadInteractor
import java.util.*
import javax.inject.Inject

/**
 * Created by CL on 11/29/17.
 */

class FreeUploadInteractorImpl @Inject
constructor(@CommercialUploadQL val context: Context,val firebaseDatabase: FirebaseDatabase) : FreeUploadInteractor {

    override fun storeFormInCloud(photos: ArrayList<Photo>, form: HashMap<String, Any>,databaseLocation:String): Boolean? {
        val freeList=firebaseDatabase.getReference(databaseLocation)
        val key=freeList.push().key

        FreeItem().apply {
            location = FreeItem.LocationBean().apply {
                street = form[MAP_KEY_STREET] as? String
                city = form[MAP_KEY_CITY] as? String
                state = form[MAP_KEY_STATE] as? String
                zip = form[MAP_KEY_ZIP] as? String
                latitude = form[MAP_KEY_LATITUDE].toString()
                longitude = form[MAP_KEY_LONGTITUDE].toString()
            }

            contact = FreeItem.ContactBean().apply {
                phone = form[MAP_KEY_PHONE] as? String
                wechat = form[MAP_KEY_WECHAT] as? String
            }

            summary = form[MAP_KEY_SUMMARY] as? String
            title = form[MAP_KEY_TITLE] as? String

            FirebaseAuth.getInstance().currentUser?.let {
                head_avator= it.photoUrl?.toString()
                person_name=it.displayName
            }?:run {
                head_avator="None"
                person_name="Charles"
            }


            photoBeanList=ArrayList<FreeItem.PhotoBean>().apply {
                photos.forEach{
                    FreeItem.PhotoBean().apply {
                        uri = it.downloadUri.toString()
                        size = it.getSize().toString()
                        photo_last_name = it.name
                    }.let {
                        add(it)
                    }
                }

            }
            expiredTime=caculateExpireTime(form[MAP_KEY_END] as String)

            freeId=key

            create_time = System.currentTimeMillis().toString()

        }.let {
            freeList.child(key).setValue(it)
        }
        return true
    }

    fun caculateExpireTime(expireTime:String):Long?{
        val calendar=Calendar.getInstance()
        expireTime.split(" ").let {
            when(it[1]){
                "hour"->
                    calendar.apply {
                        add(Calendar.HOUR_OF_DAY,it[0].toInt())
                    }.let {
                        return@caculateExpireTime it.timeInMillis
                    }

                "day"->
                    calendar.apply {
                        add(Calendar.DAY_OF_YEAR,it[0].toInt())
                    }.let {
                        return@caculateExpireTime it.timeInMillis
                    }
                "week"->
                    calendar.apply {
                        add(Calendar.WEEK_OF_YEAR,it[0].toInt())
                    }.let {
                        return@caculateExpireTime it.timeInMillis
                    }
                "month"->
                    calendar.apply {
                        add(Calendar.MONTH,it[0].toInt())
                    }.let {
                        return@caculateExpireTime it.timeInMillis
                    }
                "year"->
                    calendar.apply {
                        add(Calendar.YEAR,it[0].toInt())
                    }.let {
                        return@caculateExpireTime it.timeInMillis
                    }
                else -> return@caculateExpireTime null
            }
        }
    }
}
