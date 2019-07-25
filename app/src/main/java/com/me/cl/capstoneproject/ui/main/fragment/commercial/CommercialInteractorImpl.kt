package com.me.cl.capstoneproject.ui.main.fragment.commercial

import android.content.Context
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.me.cl.capstoneproject.R
import com.me.cl.capstoneproject.base.Constant
import com.me.cl.capstoneproject.base.Constant.MAP_LAST_KEY
import com.me.cl.capstoneproject.bean.CommercialCategory
import com.me.cl.capstoneproject.bean.CommercialItem
import com.me.cl.capstoneproject.di.FragmentQL
import com.me.cl.capstoneproject.ui.main.fragment.commercial.mvp.CommercialPageInteractor
import com.me.cl.capstoneproject.util.BaseUtils
import com.me.cl.capstoneproject.util.SharedPreferencesHelper
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

/**
 * Created by CL on 11/3/17.
 */

class CommercialInteractorImpl @Inject
constructor(private val databaseReference: DatabaseReference, @param:FragmentQL private val context: Context) : CommercialPageInteractor {

    private var eachPageItemCount = Constant.MainPage.EACH_PAGE_COUNT
    private var savedDate = mutableMapOf<Any?,Any?>()

    override var saveInstanceState: Bundle? = Bundle()

    private val cato_drawables = intArrayOf(R.drawable.ic_restaurant, R.drawable.ic_bars, R.drawable.ic_salon, R.drawable.ic_take_out, R.drawable.ic_shopping, R.drawable.ic_hotel, R.drawable.ic_skin_care, R.drawable.ic_coffe, R.drawable.ic_drug, R.drawable.ic_pet, R.drawable.ic_theater, R.drawable.ic_other)

    override val commercialCateList: MutableList<CommercialCategory>
        get() {
            val commercialCategoryList = ArrayList<CommercialCategory>()
            for (i in 0 until context.resources.getStringArray(R.array.commercial_categories).size - 1) {
                val commercialCategory = CommercialCategory(context.resources.getStringArray(R.array.commercial_categories)[i], cato_drawables[i])
                commercialCategoryList.add(commercialCategory)
            }

            return commercialCategoryList
        }

    override val lastKey: String?
        get() = BaseUtils.checkStringEmpty(savedDate.get(MAP_LAST_KEY))

    override fun divideIntoMutiCommercialCateList(CommercialCateList: MutableList<CommercialCategory>?): MutableList<MutableList<CommercialCategory>>? {
        val muti_List = ArrayList<MutableList<CommercialCategory>>()
        var commercialCategoryList: MutableList<CommercialCategory> = ArrayList()
        for (i in CommercialCateList!!.indices) {
            commercialCategoryList.add(CommercialCateList[i])
            if (i != 0 && (i % (eachPageItemCount - 1) == 0 || i == CommercialCateList.size - 1)) {
                if (!commercialCategoryList.isEmpty()) {
                    muti_List.add(commercialCategoryList)
                }
                commercialCategoryList = ArrayList()
            }
        }
        return muti_List
    }


    override fun getCommercialList(startKey: String?): Single<*>? {
       return Single.create<List<CommercialItem>> { e ->
               databaseReference.child("commercial").child("base_list")
                       .orderByKey().limitToFirst(Constant.MainPage.LIST_PAGE_SIZE)
                       .addListenerForSingleValueEvent(object : ValueEventListener {
                           override fun onDataChange(dataSnapshot: DataSnapshot) {
                               val commercialItems = ArrayList<CommercialItem>()
                               for (dataSnap in dataSnapshot.children) {
                                   val commercialItem = dataSnap.getValue(CommercialItem::class.java)
                                   commercialItem!!.commercialId = dataSnap.key
                                   commercialItems.add(commercialItem)
                               }
                               if (commercialItems.size > 0) {
                                   saveLastKey(commercialItems[commercialItems.size - 1].commercialId)
                               }
                               e.onSuccess(commercialItems)
                           }

                           override fun onCancelled(databaseError: DatabaseError) {
                               e.onError(InterruptedException(databaseError.toString()))
                           }
                       })
       }
    }

    override fun saveLastKey(key: String?) {
        savedDate.put(MAP_LAST_KEY, key)
    }

    override fun saveToCache(key: String?, value: Any?) {
        savedDate.put(key, value)
    }

    override fun getFromCache(key: String?): Any? {
        return if (savedDate.containsKey(key)) {
            savedDate.get(key)
        } else {
            null
        }
    }

    override fun getTitleFromTitlePosition(position: Int?): String? {
        position?.let {
            return context.resources.getStringArray(R.array.district_list)[position]
        }?:let { return null }
    }

    override fun saveWidgetListModel(commercialItemList: MutableList<CommercialItem>?) {
        SharedPreferencesHelper.clear(context.applicationContext, Constant.SharedPreference.SHARED_PREFERENCES_NAME)
        SharedPreferencesHelper.saveKeyValue(context.applicationContext, Constant.SharedPreference.SHARED_PREFERENCES_NAME, Constant.SharedPreference.KEY_CATOGERY, Constant.SharedPreference.CATOGERY_COMMERCIAL)
        val name = ArrayList<String>()
        val reviews = ArrayList<String>()
        if (commercialItemList != null) {
            for (commercialItem in commercialItemList) {
                name.add(commercialItem.title)
                if (commercialItem.review_count == 0f) {
                    reviews.add(0.toString())
                } else {
                    reviews.add(commercialItem.review_count.toInt().toString())
                }
            }

            try {
                SharedPreferencesHelper.saveObject(context.applicationContext, Constant.SharedPreference.SHARED_PREFERENCES_NAME, Constant.SharedPreference.KEY_COMMERCIAL_NAME, name)
                SharedPreferencesHelper.saveObject(context.applicationContext, Constant.SharedPreference.SHARED_PREFERENCES_NAME, Constant.SharedPreference.KEY_COMMERCIAL_REVIEWS, reviews)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    override fun saveInstanceState(outState: Bundle?) {
        if (outState != null) {
            saveInstanceState = outState
        } else {
            saveInstanceState!!.clear()
        }
    }
}
