package com.me.cl.capstoneproject.ui.main.fragment.help

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import com.google.firebase.database.DatabaseReference
import com.me.cl.capstoneproject.base.Constant
import com.me.cl.capstoneproject.bean.HelpItem
import com.me.cl.capstoneproject.ui.list.FirebaseDatabaseService
import com.me.cl.capstoneproject.ui.main.fragment.free.mvp.FreePageInteractor
import com.me.cl.capstoneproject.ui.main.fragment.help.mvp.HelpPageInteractor
import com.me.cl.capstoneproject.util.SharedPreferencesHelper
import io.reactivex.Single
import retrofit2.Retrofit
import java.util.ArrayList
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * Created by CL on 11/8/17.
 */

class HelpInteractorImpl @Inject
constructor(val databaseReference: DatabaseReference,var retrofit: Retrofit,var freePageInteractor: FreePageInteractor,var context: Context) : HelpPageInteractor {
    var savedData=HashMap<String,Any?>()
    var bundle = Bundle()
    val KEY_LIST_MODEL="list"
    val KEY_RV_STATE="rv_state"

    override fun getHelpList(page: Int,district:String): Single<*> {
        if ("New York" == district) {
            return retrofit.create(FirebaseDatabaseService::class.java).fetchHelpList(null)
        } else {
            return retrofit.create(FirebaseDatabaseService::class.java).fetchHelpList(district)
        }
    }

    override fun saveToCache(key: String, value: Any) {
        savedData.put(key, value)
    }

    override fun getFromCache(key: String): Any? {
        return if (savedData.containsKey(key)) {
            savedData.get(key)
        } else {
            null
        }
    }


    override fun getFreeTagInteractor(): FreePageInteractor {
            return freePageInteractor
    }


    override fun saveWidgetListModel(helpItemList: List<HelpItem>) {
        SharedPreferencesHelper.clear(context.applicationContext, Constant.SharedPreference.SHARED_PREFERENCES_NAME)
        SharedPreferencesHelper.saveKeyValue(context.applicationContext, Constant.SharedPreference.SHARED_PREFERENCES_NAME, Constant.SharedPreference.KEY_CATOGERY, Constant.SharedPreference.CATOGERY_HELP)
        val name = ArrayList<String>()
        for (helpItem in helpItemList) {
            name.add(helpItem.title!!)
        }

        try {
            SharedPreferencesHelper.saveObject(context.applicationContext, Constant.SharedPreference.SHARED_PREFERENCES_NAME, Constant.SharedPreference.KEY_HELP_NAME, name)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun saveListModel(freeList: ArrayList<HelpItem>?){
        if (freeList != null) {
            savedData.put(KEY_LIST_MODEL, freeList)
        } else {
            savedData.put(KEY_LIST_MODEL, ArrayList<HelpItem>())
        }
    }

    override fun getListModel():ArrayList<HelpItem>?{
        return savedData.get(KEY_LIST_MODEL) as ArrayList<HelpItem>?
    }
    override fun saveRvState(rvState: Parcelable?){
        savedData.put(KEY_RV_STATE, rvState)
    }

    override fun getRvState(): Parcelable?{
        return savedData.get(KEY_RV_STATE) as Parcelable?
    }



    override fun getSaveInstanceState(): Bundle {
        return bundle
    }

    override fun saveInstanceState(outState: Bundle?) {
        if (outState != null) {
            bundle = outState
        } else {
            bundle.clear()
        }
    }

}

