package com.me.cl.capstoneproject.ui.main

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.view.MenuItem
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.me.cl.capstoneproject.R
import com.me.cl.capstoneproject.base.Constant
import com.me.cl.capstoneproject.base.Constant.MainPage.*
import com.me.cl.capstoneproject.event.*
import com.me.cl.capstoneproject.ui.main.mvp.MainInteractor
import com.me.cl.capstoneproject.ui.main.mvp.MainPresenter
import com.me.cl.capstoneproject.ui.main.mvp.MainView
import com.me.cl.capstoneproject.ui.upload.commercial.CommercialUploadActivity
import com.me.cl.capstoneproject.ui.upload.free.FreeUploadActivity
import com.me.cl.capstoneproject.ui.upload.help.HelpUploadActivity
import com.me.cl.capstoneproject.util.BaseUtils
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


/**
 * Created by CL on 11/1/17.
 */

class MainPresenterImpl @Inject
constructor(var mainInteractor: MainInteractor) : MainPresenter<MainView> {
    var mainView: MainView?=null
    lateinit var eventBus:EventBus

    override fun manage(mainView: MainView) {
        this.mainView = mainView
    }

    override fun destory() {
        mainView = null
    }

    override fun init() {
        eventBus=EventBus.getDefault()
        configDistrictsSpinner()
        configViewPagerWithTab()
    }

    override fun configDistrictsSpinner() {
        mainView?.popSpinnerWithDistricts(mainInteractor.allDistricts)
    }

    override fun configViewPagerWithTab() {
        mainView?.initViewPagerWithTabLayout(mainInteractor.allTags)
        mainInteractor.apply {
           saveCurrentState(STATE_KEY_CURRENT_TAG, 0)
           saveCurrentState(STATE_KEY_CURRENT_TAG_TITLE, NEW_YORK)
           saveCurrentState(STATE_KEY_CURRENT_TAG_TITLE_POSITION, 0)
        }

    }
    override fun onViewPageSelected(position: Int, title: String, titlePosition: Int) {
        eventBus.apply {
            when (position) {
                0 -> postSticky(ForeGroundEvent.MainPageTAG(title, titlePosition, position))
                1 -> postSticky(ForeGroundEvent.MainPageTAG(title, titlePosition, position))
                2 -> postSticky(ForeGroundEvent.MainPageTAG(title, titlePosition, position))
            }
        }

        mainInteractor.apply {
            saveCurrentState(STATE_KEY_CURRENT_TAG, position)
            saveCurrentState(STATE_KEY_CURRENT_TAG_TITLE, title)
            saveCurrentState(STATE_KEY_CURRENT_TAG_TITLE_POSITION, titlePosition)
        }

    }

    override fun onFabCreateClick(context: Context,view: View,rootView: View) {
        val currentTag = mainInteractor.getCurrentState(STATE_KEY_CURRENT_TAG) as Int
        when (currentTag) {
            0 -> if (BaseUtils.System.loginCheck(context as Activity, REQUEST_CODE_COMMERCIAL_CREATE_SIGN_IN)) {
                mainView?.startUploadActivity(CommercialUploadActivity::class.java,rootView.width/2,rootView.height/2,view.width/2,REQUEST_CODE_CREATE_COMMERCIAL)
            }
            1 -> if (BaseUtils.System.loginCheck(context as Activity, REQUEST_CODE_FREE_CREATE_SIGN_IN)) {
                mainView?.startUploadActivity(FreeUploadActivity::class.java,rootView.width/2,rootView.height/2,view.width/2,REQUEST_CODE_CREATE_FREE)
            }
            2 -> if (BaseUtils.System.loginCheck(context as Activity, REQUEST_CODE_HELP_CREATE_SIGN_IN)) {
                mainView?.startUploadActivity(HelpUploadActivity::class.java,rootView.width/2,rootView.height/2,view.width/2,REQUEST_CODE_CREATE_HELP)
            }
        }
    }

    override fun onStart() {
        mainInteractor.run {
            if (getCurrentState(STATE_KEY_CURRENT_TAG) != null) {
                eventBus.let {
                    when (getCurrentState(STATE_KEY_CURRENT_TAG) as Int) {
                        0 -> it.postSticky(ForeGroundEvent.MainPageTAG(getCurrentState(STATE_KEY_CURRENT_TAG_TITLE) as String,getCurrentState(STATE_KEY_CURRENT_TAG_TITLE_POSITION) as Int, 0))
                        1 -> it.postSticky(ForeGroundEvent.MainPageTAG(getCurrentState(STATE_KEY_CURRENT_TAG_TITLE) as String,getCurrentState(STATE_KEY_CURRENT_TAG_TITLE_POSITION) as Int, 1))
                        2 -> it.postSticky(ForeGroundEvent.MainPageTAG(getCurrentState(STATE_KEY_CURRENT_TAG_TITLE) as String,getCurrentState(STATE_KEY_CURRENT_TAG_TITLE_POSITION) as Int, 2))
                    }
                }
            }
        }


    }

    override fun onTitleChange(title: String, position: Int,origin:Int) {
        eventBus.apply {
            when(origin){
                Constant.TITLE_CHANGE_FROM_MAIN-> postSticky(TitleChangeEvent.FromMainActivity(title, position))
            }
            postSticky(TitleChangeEvent.ToMainPageTags(title, position))
        }

        mainInteractor.apply {
            saveCurrentState(STATE_KEY_CURRENT_TAG_TITLE, title)
            saveCurrentState(STATE_KEY_CURRENT_TAG_TITLE_POSITION, position)
        }


    }

    override fun handleActivityResult(requestCode: Int?, resultCode: Int?, data: Intent?,activity: Activity?) {
        if (when (requestCode) {
            REQUEST_CODE_COMMERCIAL_CREATE_SIGN_IN -> true
            REQUEST_CODE_FREE_CREATE_SIGN_IN -> true
            REQUEST_CODE_HELP_CREATE_SIGN_IN -> true
            REQUEST_CODE_SIGN_IN_MENU -> true
            else -> false
        }) {
            val response = IdpResponse.fromResultIntent(data)
            // Successfully signed in
            if (resultCode === RESULT_OK) {
                when (requestCode) {
                    REQUEST_CODE_COMMERCIAL_CREATE_SIGN_IN
                    ,REQUEST_CODE_FREE_CREATE_SIGN_IN
                    ,REQUEST_CODE_HELP_CREATE_SIGN_IN ->mainView?.performFabClick()
            }
                eventBus.postSticky(LoginEvent.LogIn())
                return
            } else {
                eventBus.postSticky(LoginEvent.Failed(""))
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    mainView?.showSnackBar(activity?.getString(R.string.sign_in_cancelled))
                    return
                }

                if (response.errorCode == ErrorCodes.NO_NETWORK) {
                    mainView?.showSnackBar(activity?.getString(R.string.no_internet_connection))
                    return
                }

                if (response.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    mainView?.showSnackBar(activity?.getString(R.string.unknown_error))
                    return
                }
            }
            mainView?.showSnackBar(activity?.getString(R.string.unknown_sign_in_response))
        }
        if (requestCode==REQUEST_CODE_CREATE_COMMERCIAL&&resultCode== RESULT_OK) {
            eventBus.postSticky(UploadCompleteEvent.CommercialService(true))
            return
        }
        if (requestCode==REQUEST_CODE_DETAIL&&resultCode== RESULT_OK) {
            eventBus.postSticky(CommandEvent.RefreshCommercialList(false))
            return
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?,activity: Activity?): Boolean {
        return when(item?.itemId){
            R.id.sign ->{
                if (BaseUtils.System.OnlyCheckIfLogin()) {
                    //log out
                    AuthUI.getInstance()
                            .signOut(activity as FragmentActivity)
                            .addOnCompleteListener({
                                eventBus.postSticky(LoginEvent.LogOut())
                                mainView?.showSnackBar(activity.getString(R.string.sign_out_success))
                            })
                } else {
                    //log in
                    BaseUtils.System.loginCheck(activity, REQUEST_CODE_SIGN_IN_MENU)
                }
                true
            }
            else->{
                false
            }
        }
    }
}
