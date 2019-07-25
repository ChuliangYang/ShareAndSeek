package com.me.cl.capstoneproject.ui.main.fragment.help

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.afollestad.materialdialogs.MaterialDialog
import com.me.cl.capstoneproject.MyApplication
import com.me.cl.capstoneproject.R
import com.me.cl.capstoneproject.adapter.recyclerview.HelpListAdapter
import com.me.cl.capstoneproject.anim.ScaleFadeInAnimator
import com.me.cl.capstoneproject.base.BaseFragment
import com.me.cl.capstoneproject.base.Constant
import com.me.cl.capstoneproject.bean.HelpItem
import com.me.cl.capstoneproject.event.ForeGroundEvent
import com.me.cl.capstoneproject.event.PermissionCheckEvent
import com.me.cl.capstoneproject.event.TitleChangeEvent
import com.me.cl.capstoneproject.event.UploadCompleteEvent
import com.me.cl.capstoneproject.ui.main.fragment.free.di.DaggerFreeFragmentComponent
import com.me.cl.capstoneproject.ui.main.fragment.free.di.FreeFragmentModule
import com.me.cl.capstoneproject.ui.main.fragment.help.di.HelpFragmentModule
import com.me.cl.capstoneproject.ui.main.fragment.help.mvp.HelpPagePresenter
import com.me.cl.capstoneproject.ui.main.fragment.help.mvp.HelpPageView
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle
import com.trello.rxlifecycle2.LifecycleProvider
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.onRefresh
import javax.inject.Inject

/**
 * Created by CL on 11/3/17.
 */

class HelpFragment : BaseFragment(), HelpPageView {
//    override fun getRvState(): Parcelable? {
//    }

    @Inject
     lateinit var helpPagePresenter: HelpPagePresenter<HelpPageView>

    @Inject
    lateinit var helpListAdapter: HelpListAdapter
    @BindView(R.id.rv_help)
     lateinit var rvHelp: RecyclerView
    @BindView(R.id.sf_list)
    lateinit var sfList: SwipeRefreshLayout

    val provider: LifecycleProvider<Lifecycle.Event> = AndroidLifecycle.createLifecycleProvider(this)


    lateinit var unbinder: Unbinder

    var isRestore = false
    var passTitleEvent = false
    var passForegroundEvent = false
    var initView=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerFreeFragmentComponent.builder()
                .applicationComponent((activity?.application as MyApplication).applicationComponent)
                .freeFragmentModule(FreeFragmentModule(this)).build().plus(HelpFragmentModule(this))
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_help, container, false)
        unbinder = ButterKnife.bind(this, view)
        initView=true
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerview()
        helpPagePresenter.manage(this)
        helpPagePresenter.init(savedInstanceState,provider)
        if (savedInstanceState != null && savedInstanceState.containsKey(Constant.MainPage.BUNDLE_KEY_SAVE_STATE)) {
            isRestore = true
            passTitleEvent = true
            passForegroundEvent = true
        } else {
            isRestore = false
            passTitleEvent = false
            passForegroundEvent = false
        }
        sfList.apply {
            setProgressViewOffset(true, 0, 100)
            setSize(SwipeRefreshLayout.LARGE)
            setColorSchemeResources(
                    R.color.colorAccent,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light)
            onRefresh {
                helpPagePresenter.configHelpList(helpPagePresenter.getDependentPresenter().getCurrentDisplayedTitle(),helpPagePresenter.getDependentPresenter().getCurrentDisplayedTitlePosition(),true,false)
            }
        }
    }

    private fun initRecyclerview() {
        rvHelp.apply {
            setHasFixedSize(false)
            itemAnimator = ScaleFadeInAnimator()
            itemAnimator.addDuration = 500
            itemAnimator.removeDuration = 150
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = helpListAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)

    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
        initView=false
    }

    override fun onDestroy() {
        super.onDestroy()
        helpPagePresenter.destory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        helpPagePresenter.onSaveInstanceState(outState)
        outState.putBoolean(Constant.MainPage.BUNDLE_KEY_SAVE_STATE, true)

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onPermissionCheckEvent(event: PermissionCheckEvent.PhoneCall.MainActivityToHelpListFragment) {
        helpListAdapter.makePhoneCall(event.number)
        EventBus.getDefault().removeStickyEvent(event)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN,priority = 1)
    fun onTitleDecided(event: TitleChangeEvent.ToMainPageTags) {
        if (passTitleEvent && helpPagePresenter.getDependentPresenter().getCurrentDisplayedTitlePosition() == event.CurrentPosition) {
            EventBus.getDefault().removeStickyEvent(event)
            passTitleEvent = false
            return
        }
        helpPagePresenter.onTitleDecided(event.CurrentTitle, event.CurrentPosition)
        EventBus.getDefault().removeStickyEvent(event)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onBringToForeGround(event: ForeGroundEvent.MainPageTAG) {
        if (passForegroundEvent) {
            if (event.CurrentTagPosition == 2) {
                EventBus.getDefault().removeStickyEvent(event)
            }
            passForegroundEvent = false
            return
        }
        helpPagePresenter.onTagsForeGroundStateChange(event.CurrentTitle, event.CurrentTitlePosition, event.CurrentTagPosition)
        EventBus.getDefault().removeStickyEvent(event)
        if (event.CurrentTagPosition==2) {
            helpPagePresenter.saveWidgetListModel(helpListAdapter.getDataSet()!!)
            EventBus.getDefault().removeStickyEvent(event)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onUploadComplete(event: UploadCompleteEvent.HelpService) {
        helpPagePresenter.onUploadComplete(event.success)
        EventBus.getDefault().removeStickyEvent(event)
    }
    companion object {

        fun newInstance(): HelpFragment {
            val args = Bundle()
            val fragment = HelpFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun popHelpList(helpItems: List<HelpItem>,restore:Boolean?,rvState: Parcelable?) {
        if (restore == true) {
            helpListAdapter.helpItemBeans = helpItems.toMutableList()
            if (rvState != null) {
                rvHelp.layoutManager.onRestoreInstanceState(rvState)
            }
        } else {
            helpListAdapter.helpItemBeans.size.let {
                helpListAdapter.helpItemBeans.clear()
                helpListAdapter.notifyItemRangeRemoved(0,it)
                helpListAdapter.notifyItemRangeChanged(0,it)
            }

            helpListAdapter.helpItemBeans=helpItems.toMutableList()
            helpListAdapter.notifyItemRangeInserted(0,helpItems.size)
        }

 }

    override fun showAlertDialog(title: String, content: String, button: String, singleButtonCallback: MaterialDialog.SingleButtonCallback, onCancelListener: DialogInterface.OnCancelListener, context: Context) {
        super.showAlertDialog(title, content, button, singleButtonCallback, onCancelListener)
    }

    override fun controlIndeterminateProgress(show: Boolean, title: String?, content: String?, onCancelListener: DialogInterface.OnCancelListener, context: Context?,swipe:Boolean) {
        if (swipe) {
            if (!show&&initView) {
                if (sfList.isRefreshing) {
                    sfList.isRefreshing = false
                }
            }
        } else {
            super.controlIndeterminateProgress(show, title, content, onCancelListener)
        }
    }

    override fun getRvState(): Parcelable? {
        return rvHelp.layoutManager.onSaveInstanceState()
    }
}
