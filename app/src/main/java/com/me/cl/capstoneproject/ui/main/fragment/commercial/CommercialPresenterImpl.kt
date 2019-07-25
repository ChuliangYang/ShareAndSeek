package com.me.cl.capstoneproject.ui.main.fragment.commercial

import android.arch.lifecycle.Lifecycle
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import com.me.cl.capstoneproject.base.Constant.Dialog.*
import com.me.cl.capstoneproject.base.Constant.ListPage.RX_KEY_COMMERCIAL_LIST
import com.me.cl.capstoneproject.base.Constant.ListPage.SORT_LIST_BY_DEFAULT
import com.me.cl.capstoneproject.base.Constant.MainPage.*
import com.me.cl.capstoneproject.bean.CommercialCategory
import com.me.cl.capstoneproject.bean.CommercialItem
import com.me.cl.capstoneproject.ui.list.mvp.CommercialListInteractor
import com.me.cl.capstoneproject.ui.main.fragment.commercial.mvp.CommercialPageInteractor
import com.me.cl.capstoneproject.ui.main.fragment.commercial.mvp.CommercialPagePresenter
import com.me.cl.capstoneproject.ui.main.fragment.commercial.mvp.CommercialPageView
import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by CL on 11/3/17.
 */

class CommercialPresenterImpl @Inject
constructor(internal var interactor: CommercialPageInteractor,  var commercialListInteractor: CommercialListInteractor) : CommercialPagePresenter<CommercialPageView> {
     lateinit var view: CommercialPageView
    var provider: LifecycleProvider<Lifecycle.Event>?=null

    var disposables: HashMap<String?, Disposable?> = HashMap()

    override val currentDisplayedTitlePosition: Int?
        get() = if (interactor.getFromCache(CACHE_KEY_CURRENT_TITLE_POSITION) != null) {
            interactor.getFromCache(CACHE_KEY_CURRENT_TITLE_POSITION) as Int
        } else {
            -1
        }

    override val currentDisplayedTitle: String?
        get() = if (interactor.getFromCache(CACHE_KEY_CURRENT_TITLE) != null) {
            interactor.getFromCache(CACHE_KEY_CURRENT_TITLE) as String?
        } else {
            ""
        }

    override fun manage(view: CommercialPageView) {
        this.view = view
    }

    override fun destory() {
        disposables.clear()
    }

    override fun init() {

    }

    override fun init(instanceState: Bundle?,provider: LifecycleProvider<Lifecycle.Event>?) {
        this.provider=provider
        configCommercialCatogary()
        if (instanceState != null && instanceState.containsKey(BUNDLE_KEY_LIST)) {
            interactor.saveInstanceState(instanceState)
            configCommercialListFirstPage(instanceState.getString(BUNDLE_KEY_CURRENT_TITLE), instanceState.getInt(BUNDLE_KEY_CURRENT_TITLE_POSITION), false, false, true)
            interactor.saveToCache(STATE_KEY_FORE_GROUND, instanceState.getBoolean(STATE_KEY_FORE_GROUND))
        }
    }


    override fun configCommercialCatogary() {
        Single.create<MutableList<MutableList<CommercialCategory>>> { e -> interactor.divideIntoMutiCommercialCateList(interactor.commercialCateList)?.let { e.onSuccess(it) } }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .compose(provider?.bindToLifecycle())
                .subscribe { multiList -> view.popCommercialCategoryPagers(multiList) }

    }

    override fun configCommercialListFirstPage(title: String?, position: Int?, swipe: Boolean?, animate: Boolean?, isRestore: Boolean?) {
        if (isRestore!!) {
            if (interactor.saveInstanceState!!.getParcelableArrayList<Parcelable>(BUNDLE_KEY_LIST) != null) {
                val commercialItemList = interactor.saveInstanceState!!.getParcelableArrayList<CommercialItem>(BUNDLE_KEY_LIST)
                view.popCommericialList(commercialItemList, true, false, interactor.saveInstanceState!!.getParcelable(BUNDLE_KEY_RVSTATE))
                saveCurrentTitlePosition(position)
                saveCurrentTitle(title)
            }
        } else {
            disposables[RX_KEY_COMMERCIAL_LIST] = commercialListInteractor.fetchSortList(SORT_LIST_BY_DEFAULT, null, title).subscribeOn(Schedulers.io())
                    .doOnSubscribe({
                        view.controlIndeterminateProgress(true, PLEASE_WAIT, FETCHING, DialogInterface.OnCancelListener {
                            if (disposables.containsKey(RX_KEY_COMMERCIAL_LIST)) {
                                disposables[RX_KEY_COMMERCIAL_LIST]?.dispose()
                            }
                        },null, swipe) })
                    .doOnError { throwable -> view.showAlertDialog(SORRY, FETCHING_FAILED, OK, null, null, null) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        view.controlIndeterminateProgress(false, null, null,DialogInterface.OnCancelListener {
                            if (disposables.containsKey(RX_KEY_COMMERCIAL_LIST)) {
                                disposables[RX_KEY_COMMERCIAL_LIST]?.dispose()
                            }
                        }, null, swipe)
                    }
                    .toObservable()
                    .compose(provider?.bindToLifecycle())
                    .subscribe({ o ->
                        view.popCommericialList(o as MutableList<CommercialItem>, false, animate, null)
                        saveCurrentTitlePosition(position)
                        saveCurrentTitle(title)
                        if (o != null) {
                            interactor.saveWidgetListModel(o)
                        }
                    }) { throwable ->
                        Timber.e(throwable.toString())
                        saveCurrentTitlePosition(-1)
                        saveCurrentTitle(title)
                    }
        }
    }

    override fun configCommercialListNextPage() {
        interactor.getCommercialList(interactor.lastKey)!!
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .compose(provider?.bindToLifecycle())
                .subscribe { list -> view.popCommericialList(list as MutableList<CommercialItem>, false, true, null) }
    }

    override fun onTitleDecided(title: String?, position: Int?) {
        if (interactor.getFromCache(STATE_KEY_FORE_GROUND) != null && interactor.getFromCache(STATE_KEY_FORE_GROUND) as Boolean) {
            if (currentDisplayedTitlePosition == -1 || currentDisplayedTitlePosition != position) {
                saveCurrentTitlePosition(position)//防止多次请求
                saveCurrentTitle(title)
                configCommercialListFirstPage(title, position, false, true, false)
            }
        } else {
            interactor.saveToCache(STATE_KEY_SMART_REFRESH_WHEN_FRONT, true)
        }

    }

    override fun onTagsForeGroundStateChange(title: String?, titlePosition: Int?, tagPosition: Int?) {

        interactor.saveToCache(STATE_KEY_FORE_GROUND, tagPosition == 0)

        if (interactor.getFromCache(STATE_KEY_FORE_GROUND) != null && interactor.getFromCache(STATE_KEY_FORE_GROUND) as Boolean) {
            if (interactor.getFromCache(STATE_KEY_FORCE_REFRESH_WHEN_FRONT) != null && interactor.getFromCache(STATE_KEY_FORCE_REFRESH_WHEN_FRONT) as Boolean) {
                configCommercialListFirstPage(title, titlePosition, false, true, false)
                interactor.saveToCache(STATE_KEY_FORCE_REFRESH_WHEN_FRONT, false)
            }
            if (interactor.getFromCache(STATE_KEY_SMART_REFRESH_WHEN_FRONT) != null && interactor.getFromCache(STATE_KEY_SMART_REFRESH_WHEN_FRONT) as Boolean) {
                if (currentDisplayedTitlePosition == -1 || currentDisplayedTitlePosition != titlePosition) {
                    saveCurrentTitlePosition(titlePosition)//防止多次请求
                    saveCurrentTitle(title)
                    configCommercialListFirstPage(title, titlePosition, false, true, false)
                }
                interactor.saveToCache(STATE_KEY_SMART_REFRESH_WHEN_FRONT, false)
            }
        }


    }

    override fun onRefreshList(success: Boolean?, animate: Boolean?) {
        if (success!!) {
            if (interactor.getFromCache(STATE_KEY_FORE_GROUND) != null && interactor.getFromCache(STATE_KEY_FORE_GROUND) as Boolean) {
                configCommercialListFirstPage(interactor.getTitleFromTitlePosition(currentDisplayedTitlePosition), currentDisplayedTitlePosition, false, animate, false)
            } else {
                interactor.saveToCache(STATE_KEY_FORCE_REFRESH_WHEN_FRONT, true)
            }
        }
    }

    override fun saveCurrentTitlePosition(position: Int?) {
        interactor.saveToCache(CACHE_KEY_CURRENT_TITLE_POSITION, position)
    }

    override fun saveCurrentTitle(title: String?) {
        interactor.saveToCache(CACHE_KEY_CURRENT_TITLE, title)
    }

    override fun saveWidgetListModel(commercialItemList: MutableList<CommercialItem>?) {
        if (commercialItemList != null) {
            interactor.saveWidgetListModel(commercialItemList)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable(BUNDLE_KEY_RVSTATE, view.rvState)
        if (view.commercialListModel != null) {
            outState?.putParcelableArrayList(BUNDLE_KEY_LIST, view.commercialListModel as ArrayList<out Parcelable>?)
        }
        currentDisplayedTitlePosition?.let { outState?.putInt(BUNDLE_KEY_CURRENT_TITLE_POSITION, it) }
        outState?.putString(BUNDLE_KEY_CURRENT_TITLE, currentDisplayedTitle)
        if (interactor.getFromCache(STATE_KEY_FORE_GROUND) is Boolean) {
            outState?.putBoolean(STATE_KEY_FORE_GROUND, (interactor.getFromCache(STATE_KEY_FORE_GROUND) as Boolean?)!!)
        }
    }


}
