package com.me.cl.capstoneproject.ui.main.fragment.free

import android.arch.lifecycle.Lifecycle
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import com.me.cl.capstoneproject.base.Constant.Dialog.FETCHING
import com.me.cl.capstoneproject.base.Constant.Dialog.PLEASE_WAIT
import com.me.cl.capstoneproject.base.Constant.MainPage.*
import com.me.cl.capstoneproject.bean.FreeItem
import com.me.cl.capstoneproject.ui.main.fragment.free.mvp.FreePageInteractor
import com.me.cl.capstoneproject.ui.main.fragment.free.mvp.FreePagePresenter
import com.me.cl.capstoneproject.ui.main.fragment.free.mvp.FreePageView
import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by CL on 11/8/17.
 */

class FreePresenterImpl @Inject
constructor(var freePageInteractor: FreePageInteractor) : FreePagePresenter<FreePageView> {
    lateinit var view: FreePageView
    var disposables: HashMap<String, Disposable> = HashMap()
    val rxKey="configFreeList"
    var provider: LifecycleProvider<Lifecycle.Event>?=null


    override fun manage(view: FreePageView) {
        this.view = view
    }

    override fun destory() {
        disposables.clear()
    }

    override fun init() {
    }

    override fun init(savedInstanceState: Bundle?,provider: LifecycleProvider<Lifecycle.Event>?) {
        this.provider=provider
        if (savedInstanceState!=null) {
            freePageInteractor.saveInstanceState(savedInstanceState)
            var instanceState=freePageInteractor.getSaveInstanceState()
            freePageInteractor.saveListModel(instanceState.get(BUNDLE_KEY_LIST) as ArrayList<FreeItem>?)
            freePageInteractor.saveRvState(instanceState.get(BUNDLE_KEY_RVSTATE) as Parcelable?)
            if (instanceState.get(BUNDLE_KEY_FORCE_REFRESH)!=null) {
                freePageInteractor.saveToCache(STATE_KEY_FORCE_REFRESH_WHEN_FRONT,instanceState.get(BUNDLE_KEY_FORCE_REFRESH))
            }
            if (instanceState.get(BUNDLE_KEY_SMART_REFRESH)!=null) {
                freePageInteractor.saveToCache(STATE_KEY_SMART_REFRESH_WHEN_FRONT,instanceState.get(BUNDLE_KEY_SMART_REFRESH))
            }
            saveCurrentTitlePosition(instanceState.getInt(BUNDLE_KEY_CURRENT_TITLE_POSITION))
            saveCurrentTitle(instanceState.getString(BUNDLE_KEY_CURRENT_TITLE))
            if (freePageInteractor.getSaveInstanceState().getParcelableArrayList<FreeItem>(BUNDLE_KEY_LIST) != null) {
                configFreeList(getCurrentDisplayedTitle(),getCurrentDisplayedTitlePosition(),false,true)
            }
            freePageInteractor.saveToCache(STATE_KEY_FORE_GROUND, instanceState.getBoolean(STATE_KEY_FORE_GROUND))
        }
    }


    override fun configFreeList(title:String,position:Int,swipe:Boolean,restore:Boolean) {
        if (restore) {
            if (freePageInteractor.getSaveInstanceState().getParcelableArrayList<FreeItem>(BUNDLE_KEY_LIST) != null&&freePageInteractor.getSaveInstanceState().getInt(BUNDLE_KEY_CURRENT_TITLE_POSITION)==position) {
                val freeItemList = freePageInteractor.getSaveInstanceState().getParcelableArrayList<FreeItem>(BUNDLE_KEY_LIST)
                view.popFreeList(freeItemList,true,freePageInteractor.getSaveInstanceState().getParcelable(BUNDLE_KEY_RVSTATE))
                saveCurrentTitlePosition(position)
                saveCurrentTitle(title)
                return
            }
        }
            disposables.put(rxKey,freePageInteractor.getFreeList(0,title).toObservable().subscribeOn(Schedulers.io())
                    .doOnSubscribe({ disposable ->
                        view.controlIndeterminateProgress(true, PLEASE_WAIT, FETCHING, DialogInterface.OnCancelListener {
                            dialog ->
                            if (disposables.containsKey(rxKey)) {
                                disposables.get(rxKey)?.dispose()
                            }

                        }, null,swipe)
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally({
                        view.controlIndeterminateProgress(false, null, null,DialogInterface.OnCancelListener { dialog ->
                            if (disposables.containsKey(rxKey)) {
                                disposables.get(rxKey)?.dispose()
                            }
                        }, null,swipe)
                    })
                    .compose(provider?.bindToLifecycle())
                    .subscribe({ list -> view.popFreeList(list as ArrayList<FreeItem>,false,null)
                        freePageInteractor.saveListModel(list)
                        saveCurrentTitlePosition(position)
                        saveCurrentTitle(title)
                        freePageInteractor.saveWidgetListModel(list)
                    }) { throwable -> Timber.e(throwable)
                        freePageInteractor.saveListModel(null)
                        saveCurrentTitlePosition(-1)
                        saveCurrentTitle("")
                    })
    }

    override fun onTitleDecided(title: String,position:Int){

        if (freePageInteractor.getFromCache(STATE_KEY_FORE_GROUND) != null && freePageInteractor.getFromCache(STATE_KEY_FORE_GROUND) as Boolean) {
            if (getCurrentDisplayedTitlePosition() == -1 || getCurrentDisplayedTitlePosition() != position) {
                saveCurrentTitlePosition(position)//防止多次请求
                saveCurrentTitle(title)
                configFreeList(title,position,false,false)
            }
        } else {
            freePageInteractor.saveToCache(STATE_KEY_SMART_REFRESH_WHEN_FRONT, true)
        }

    }

    override fun onTagsForeGroundStateChange(title: String, titlePosition: Int, tagPosition: Int) {
        freePageInteractor.saveToCache(STATE_KEY_FORE_GROUND, tagPosition == 1)

        if (freePageInteractor.getFromCache(STATE_KEY_FORE_GROUND) != null && freePageInteractor.getFromCache(STATE_KEY_FORE_GROUND) as Boolean) {
            if (freePageInteractor.getFromCache(STATE_KEY_FORCE_REFRESH_WHEN_FRONT) != null && freePageInteractor.getFromCache(STATE_KEY_FORCE_REFRESH_WHEN_FRONT) as Boolean) {
                configFreeList(title,titlePosition,false,false)
                freePageInteractor.saveToCache(STATE_KEY_FORCE_REFRESH_WHEN_FRONT, false)
            }
            if (freePageInteractor.getFromCache(STATE_KEY_SMART_REFRESH_WHEN_FRONT) != null && freePageInteractor.getFromCache(STATE_KEY_SMART_REFRESH_WHEN_FRONT) as Boolean) {
                if (getCurrentDisplayedTitlePosition() == -1 || getCurrentDisplayedTitlePosition() != titlePosition) {
                    saveCurrentTitlePosition(titlePosition)//防止多次请求
                    saveCurrentTitle(title)
                    configFreeList(title,titlePosition,false,false)
                }
                freePageInteractor.saveToCache(STATE_KEY_SMART_REFRESH_WHEN_FRONT, false)
            }
        }


    }

    override  fun saveCurrentTitlePosition(position: Int) {
         freePageInteractor.saveToCache(CACHE_KEY_CURRENT_TITLE_POSITION, position)
    }

    override fun getCurrentDisplayedTitlePosition(): Int {
        return if (freePageInteractor.getFromCache(CACHE_KEY_CURRENT_TITLE_POSITION) != null) {
            freePageInteractor.getFromCache(CACHE_KEY_CURRENT_TITLE_POSITION) as Int
        } else {
            -1
        }
    }

    override fun saveCurrentTitle(title: String) {
        freePageInteractor.saveToCache(CACHE_KEY_CURRENT_TITLE, title)
    }

    override fun getCurrentDisplayedTitle(): String {
        return if (freePageInteractor.getFromCache(CACHE_KEY_CURRENT_TITLE) != null) {
            return freePageInteractor.getFromCache(CACHE_KEY_CURRENT_TITLE) as String
        } else {
            return ""
        }
    }

    override fun onUploadComplete(success: Boolean) {
        if (success) {
            if (freePageInteractor.getFromCache(STATE_KEY_FORE_GROUND) != null && freePageInteractor.getFromCache(STATE_KEY_FORE_GROUND) as Boolean) {
                configFreeList(freePageInteractor.getTitleFromTitlePosition(getCurrentDisplayedTitlePosition()), getCurrentDisplayedTitlePosition(),false,false)
            } else {
                freePageInteractor.saveToCache(STATE_KEY_FORCE_REFRESH_WHEN_FRONT, true)
            }
        }
    }

    override fun saveWidgetListModel(freeItemList: List<FreeItem>) {
        freePageInteractor.saveWidgetListModel(freeItemList)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (freePageInteractor.getRvState() != null) {
            outState.putParcelable(BUNDLE_KEY_RVSTATE, freePageInteractor.getRvState())
        } else {
            outState.putParcelable(BUNDLE_KEY_RVSTATE, view.getRvState())
        }
        if (freePageInteractor.getListModel() != null) {
            outState.putParcelableArrayList(BUNDLE_KEY_LIST,freePageInteractor.getListModel())
        }
        if (freePageInteractor.getFromCache(STATE_KEY_FORCE_REFRESH_WHEN_FRONT)!=null) {
            outState.putBoolean(BUNDLE_KEY_FORCE_REFRESH, freePageInteractor.getFromCache(STATE_KEY_FORCE_REFRESH_WHEN_FRONT) as Boolean)
        }
        if (freePageInteractor.getFromCache(STATE_KEY_SMART_REFRESH_WHEN_FRONT)!=null) {
            outState.putBoolean(BUNDLE_KEY_SMART_REFRESH, freePageInteractor.getFromCache(STATE_KEY_SMART_REFRESH_WHEN_FRONT) as Boolean)
        }
        outState.putInt(BUNDLE_KEY_CURRENT_TITLE_POSITION, getCurrentDisplayedTitlePosition())
        outState.putString(BUNDLE_KEY_CURRENT_TITLE, getCurrentDisplayedTitle())
        if (freePageInteractor.getFromCache(STATE_KEY_FORE_GROUND) is Boolean) {
            outState.putBoolean(STATE_KEY_FORE_GROUND, freePageInteractor.getFromCache(STATE_KEY_FORE_GROUND) as Boolean)
        }

    }
}
