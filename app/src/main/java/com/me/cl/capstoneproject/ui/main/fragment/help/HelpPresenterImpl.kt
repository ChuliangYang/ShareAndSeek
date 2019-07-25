package com.me.cl.capstoneproject.ui.main.fragment.help

import android.arch.lifecycle.Lifecycle
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import com.me.cl.capstoneproject.base.Constant
import com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_CURRENT_TITLE_POSITION
import com.me.cl.capstoneproject.bean.HelpItem
import com.me.cl.capstoneproject.ui.main.fragment.free.mvp.FreePagePresenter
import com.me.cl.capstoneproject.ui.main.fragment.free.mvp.FreePageView
import com.me.cl.capstoneproject.ui.main.fragment.help.mvp.HelpPageInteractor
import com.me.cl.capstoneproject.ui.main.fragment.help.mvp.HelpPagePresenter
import com.me.cl.capstoneproject.ui.main.fragment.help.mvp.HelpPageView
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

class HelpPresenterImpl @Inject
constructor(var helpInteractor: HelpPageInteractor, var freePresenter:FreePagePresenter<FreePageView>) : HelpPagePresenter<HelpPageView> {
    lateinit var view: HelpPageView
    var disposables: HashMap<String, Disposable> = HashMap()
    val rxKey="configHelpList"
    var provider: LifecycleProvider<Lifecycle.Event>?=null

    override fun manage(view: HelpPageView) {
        this.view = view
        freePresenter.manage(view)
    }

    override fun destory() {
        disposables.clear()
    }

    override fun init() {
    }

    override fun init(savedInstanceState: Bundle?,provider: LifecycleProvider<Lifecycle.Event>?) {
        this.provider=provider
        if (savedInstanceState!=null) {
            helpInteractor.saveInstanceState(savedInstanceState)
            var instanceState=helpInteractor.getSaveInstanceState()
            helpInteractor.saveListModel(instanceState.get(Constant.MainPage.BUNDLE_KEY_LIST) as ArrayList<HelpItem>?)
            helpInteractor.saveRvState(instanceState.get(Constant.MainPage.BUNDLE_KEY_RVSTATE) as Parcelable?)

            if (instanceState.get(Constant.MainPage.BUNDLE_KEY_FORCE_REFRESH)!=null) {
                helpInteractor.saveToCache(Constant.MainPage.STATE_KEY_FORCE_REFRESH_WHEN_FRONT,instanceState.get(Constant.MainPage.BUNDLE_KEY_FORCE_REFRESH))
            }
            if (instanceState.get(Constant.MainPage.BUNDLE_KEY_SMART_REFRESH)!=null) {
                helpInteractor.saveToCache(Constant.MainPage.STATE_KEY_SMART_REFRESH_WHEN_FRONT,instanceState.get(Constant.MainPage.BUNDLE_KEY_SMART_REFRESH))
            }
            freePresenter.saveCurrentTitlePosition(instanceState.getInt(Constant.MainPage.BUNDLE_KEY_CURRENT_TITLE_POSITION))
            freePresenter.saveCurrentTitle(instanceState.getString(Constant.MainPage.BUNDLE_KEY_CURRENT_TITLE))
            if (helpInteractor.getSaveInstanceState().getParcelableArrayList<HelpItem>(Constant.MainPage.BUNDLE_KEY_LIST) != null) {
                configHelpList(freePresenter.getCurrentDisplayedTitle(),freePresenter.getCurrentDisplayedTitlePosition(),false,true)
            }
        }
    }

    override fun configHelpList(title:String,position:Int,swipe:Boolean,restore:Boolean) {
        if (restore) {
            if (helpInteractor.getSaveInstanceState().getParcelableArrayList<HelpItem>(Constant.MainPage.BUNDLE_KEY_LIST) != null&&helpInteractor.getSaveInstanceState().getInt(BUNDLE_KEY_CURRENT_TITLE_POSITION)==position) {
                val helpItemList = helpInteractor.getSaveInstanceState().getParcelableArrayList<HelpItem>(Constant.MainPage.BUNDLE_KEY_LIST)
                view.popHelpList(helpItemList, true, helpInteractor.getSaveInstanceState().getParcelable(Constant.MainPage.BUNDLE_KEY_RVSTATE))
                freePresenter.saveCurrentTitlePosition(position)
                freePresenter.saveCurrentTitle(title)
                return
            }
        }
            disposables.put(rxKey,helpInteractor.getHelpList(0,title).toObservable().subscribeOn(Schedulers.io())
                    .doOnSubscribe({ disposable ->
                        view.controlIndeterminateProgress(true, Constant.Dialog.PLEASE_WAIT, Constant.Dialog.FETCHING, DialogInterface.OnCancelListener {
                            dialog ->
                            if (disposables.containsKey(rxKey)) {
                                disposables.get(rxKey)?.dispose()
                            }

                        }, null,swipe)
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally({
                        view.controlIndeterminateProgress(false, null, null, DialogInterface.OnCancelListener { dialog ->
                            if (disposables.containsKey(rxKey)) {
                                disposables.get(rxKey)?.dispose()
                            }
                        }, null,swipe)
                    })
                    .compose(provider?.bindToLifecycle())
                    .subscribe({ list -> view.popHelpList(list as ArrayList<HelpItem>,false,null)
                        freePresenter.saveCurrentTitlePosition(position)
                        freePresenter.saveCurrentTitle(title)
                        helpInteractor.saveWidgetListModel(list)
                        helpInteractor.saveListModel(list)
                    }) { throwable -> Timber.e(throwable)
                        freePresenter.saveCurrentTitlePosition(-1)
                        freePresenter.saveCurrentTitle("")
                        helpInteractor.saveListModel(null)
                    })
    }

    override fun onTitleDecided(title: String,position:Int){
        helpInteractor.getFreeTagInteractor().apply {
            if (getFromCache(Constant.MainPage.STATE_KEY_FORE_GROUND) != null && getFromCache(Constant.MainPage.STATE_KEY_FORE_GROUND) as Boolean) {
                if (freePresenter.getCurrentDisplayedTitlePosition() == -1 || freePresenter.getCurrentDisplayedTitlePosition() != position) {
                    freePresenter.saveCurrentTitlePosition(position)//防止多次请求
                    freePresenter.saveCurrentTitle(title)
                    configHelpList(title,position,false,false)
                }
            } else {
                saveToCache(Constant.MainPage.STATE_KEY_SMART_REFRESH_WHEN_FRONT, true)
            }
        }


    }

    override fun onTagsForeGroundStateChange(title: String, titlePosition: Int, tagPosition: Int) {
        helpInteractor.getFreeTagInteractor().apply {
           saveToCache(Constant.MainPage.STATE_KEY_FORE_GROUND, tagPosition == 2)

            if (getFromCache(Constant.MainPage.STATE_KEY_FORE_GROUND) != null && getFromCache(Constant.MainPage.STATE_KEY_FORE_GROUND) as Boolean) {
                if (getFromCache(Constant.MainPage.STATE_KEY_FORCE_REFRESH_WHEN_FRONT) != null && getFromCache(Constant.MainPage.STATE_KEY_FORCE_REFRESH_WHEN_FRONT) as Boolean) {
                    configHelpList(title,titlePosition,false,false)
                    saveToCache(Constant.MainPage.STATE_KEY_FORCE_REFRESH_WHEN_FRONT, false)
                }
                if (getFromCache(Constant.MainPage.STATE_KEY_SMART_REFRESH_WHEN_FRONT) != null && getFromCache(Constant.MainPage.STATE_KEY_SMART_REFRESH_WHEN_FRONT) as Boolean) {
                    if (freePresenter.getCurrentDisplayedTitlePosition() == -1 || freePresenter.getCurrentDisplayedTitlePosition() != titlePosition) {
                        freePresenter.saveCurrentTitlePosition(titlePosition)//防止多次请求
                        freePresenter.saveCurrentTitle(title)
                        configHelpList(title,titlePosition,false,false)
                    }
                    saveToCache(Constant.MainPage.STATE_KEY_SMART_REFRESH_WHEN_FRONT, false)
                }

                helpInteractor.saveRvState(null)
            }
        }
    }

    override fun onUploadComplete(success: Boolean) {
        if (success) {
            helpInteractor.getFreeTagInteractor().apply {
                if (getFromCache(Constant.MainPage.STATE_KEY_FORE_GROUND) != null && getFromCache(Constant.MainPage.STATE_KEY_FORE_GROUND) as Boolean) {
                    configHelpList(getTitleFromTitlePosition(freePresenter.getCurrentDisplayedTitlePosition()), freePresenter.getCurrentDisplayedTitlePosition(),false,false)
                } else {
                    saveToCache(Constant.MainPage.STATE_KEY_FORCE_REFRESH_WHEN_FRONT, true)
                }
            }

        }
    }

    override fun getDependentPresenter():FreePagePresenter<FreePageView>{
        return freePresenter
    }

    override fun saveWidgetListModel(helpItemList: List<HelpItem>) {
        helpInteractor.saveWidgetListModel(helpItemList)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (helpInteractor.getRvState() != null) {
            outState.putParcelable(Constant.MainPage.BUNDLE_KEY_RVSTATE, helpInteractor.getRvState())
        } else {
            outState.putParcelable(Constant.MainPage.BUNDLE_KEY_RVSTATE, view.getRvState())
        }
        if (helpInteractor.getListModel() != null) {
            outState.putParcelableArrayList(Constant.MainPage.BUNDLE_KEY_LIST,helpInteractor.getListModel())
        }

        if (helpInteractor.getFromCache(Constant.MainPage.STATE_KEY_FORCE_REFRESH_WHEN_FRONT)!=null) {
            outState.putBoolean(Constant.MainPage.BUNDLE_KEY_FORCE_REFRESH, helpInteractor.getFromCache(Constant.MainPage.STATE_KEY_FORCE_REFRESH_WHEN_FRONT) as Boolean)
        }
        if (helpInteractor.getFromCache(Constant.MainPage.STATE_KEY_SMART_REFRESH_WHEN_FRONT)!=null) {
            outState.putBoolean(Constant.MainPage.BUNDLE_KEY_SMART_REFRESH, helpInteractor.getFromCache(Constant.MainPage.STATE_KEY_SMART_REFRESH_WHEN_FRONT) as Boolean)
        }
        outState.putInt(BUNDLE_KEY_CURRENT_TITLE_POSITION, freePresenter.getCurrentDisplayedTitlePosition())
        outState.putString(Constant.MainPage.BUNDLE_KEY_CURRENT_TITLE,freePresenter.getCurrentDisplayedTitle())
        if (helpInteractor.getFreeTagInteractor().getFromCache(Constant.MainPage.STATE_KEY_FORE_GROUND) is Boolean) {
            outState.putBoolean(Constant.MainPage.STATE_KEY_FORE_GROUND, helpInteractor.getFreeTagInteractor().getFromCache(Constant.MainPage.STATE_KEY_FORE_GROUND) as Boolean)
        }
    }
}
