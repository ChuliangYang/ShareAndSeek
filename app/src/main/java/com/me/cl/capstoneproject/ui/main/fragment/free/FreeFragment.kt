package com.me.cl.capstoneproject.ui.main.fragment.free

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
import com.me.cl.capstoneproject.adapter.recyclerview.FreeListAdapter
import com.me.cl.capstoneproject.anim.ScaleFadeInAnimator
import com.me.cl.capstoneproject.base.BaseFragment
import com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_SAVE_STATE
import com.me.cl.capstoneproject.bean.FreeItem
import com.me.cl.capstoneproject.event.ForeGroundEvent
import com.me.cl.capstoneproject.event.PermissionCheckEvent
import com.me.cl.capstoneproject.event.TitleChangeEvent
import com.me.cl.capstoneproject.event.UploadCompleteEvent
import com.me.cl.capstoneproject.ui.main.fragment.free.di.DaggerFreeFragmentComponent
import com.me.cl.capstoneproject.ui.main.fragment.free.di.FreeFragmentModule
import com.me.cl.capstoneproject.ui.main.fragment.free.mvp.FreePagePresenter
import com.me.cl.capstoneproject.ui.main.fragment.free.mvp.FreePageView
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

class FreeFragment : BaseFragment(), FreePageView {

    @BindView(R.id.rv_list)
    lateinit var rvList: RecyclerView

    @BindView(R.id.sf_list)
    lateinit var sfList: SwipeRefreshLayout

    @Inject
     lateinit var freePagePresenter: FreePagePresenter<FreePageView>
    @Inject
    lateinit var freeListAdapter: FreeListAdapter
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
                .freeFragmentModule(FreeFragmentModule(this)).build().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.main_recyclerview, container, false)
        unbinder = ButterKnife.bind(this, view)
        initView=true
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        freePagePresenter.manage(this)
        freePagePresenter.init(savedInstanceState,provider)
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_KEY_SAVE_STATE)) {
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
                freePagePresenter.configFreeList(freePagePresenter.getCurrentDisplayedTitle(),freePagePresenter.getCurrentDisplayedTitlePosition(),true,false)
            }
        }
    }

    private fun initRecyclerView() {
        rvList.apply {
            setHasFixedSize(false)
            itemAnimator = ScaleFadeInAnimator()
            itemAnimator.addDuration = 500
            itemAnimator.removeDuration = 150
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = freeListAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
        initView=false
    }

    override fun onDestroy() {
        super.onDestroy()
        freePagePresenter.destory()
    }

     override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)

    }

     override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        freePagePresenter.onSaveInstanceState(outState)
        outState.putBoolean(BUNDLE_KEY_SAVE_STATE, true)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onPermissionCheckEvent(event: PermissionCheckEvent.PhoneCall.MainActivityToFreeListFragment) {
        freeListAdapter.makePhoneCall(event.number)
        EventBus.getDefault().removeStickyEvent(event)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN,priority = 1)
    fun onTitleDecided(event: TitleChangeEvent.ToMainPageTags) {
        if (passTitleEvent && freePagePresenter.getCurrentDisplayedTitlePosition() == event.CurrentPosition) {
            EventBus.getDefault().removeStickyEvent(event)
            passTitleEvent = false
            return
        }
        freePagePresenter.onTitleDecided(event.CurrentTitle, event.CurrentPosition)
        EventBus.getDefault().removeStickyEvent(event)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onBringToForeGround(event: ForeGroundEvent.MainPageTAG) {
        if (passForegroundEvent) {
            if (event.CurrentTagPosition == 1) {
                EventBus.getDefault().removeStickyEvent(event)
            }
            passForegroundEvent = false
            return
        }
        freePagePresenter.onTagsForeGroundStateChange(event.CurrentTitle, event.CurrentTitlePosition, event.CurrentTagPosition)
        if (event.CurrentTagPosition==1) {
            freePagePresenter.saveWidgetListModel(freeListAdapter.getDataSet()!!)
            EventBus.getDefault().removeStickyEvent(event)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onUploadComplete(event: UploadCompleteEvent.FreeService) {
        freePagePresenter.onUploadComplete(event.success)
        EventBus.getDefault().removeStickyEvent(event)
    }
    //------------------------------------------------------------View_interface----------------------------------------------------------------------

    override fun popFreeList(freeItemBeans: List<FreeItem>,restore:Boolean?,rvState:Parcelable?) {
        if (restore == true) {
            freeListAdapter.freeItemBeans=freeItemBeans.toMutableList()
            if (rvState != null) {
                rvList.layoutManager.onRestoreInstanceState(rvState)
            }
        } else {
            freeListAdapter.freeItemBeans.size.let {
                freeListAdapter.freeItemBeans.clear()
                freeListAdapter.notifyItemRangeRemoved(0,it)
                freeListAdapter.notifyItemRangeChanged(0,it)
            }

            freeListAdapter.freeItemBeans=freeItemBeans.toMutableList()
            freeListAdapter.notifyItemRangeInserted(0,freeItemBeans.size)
        }
    }

    companion object {
        fun newInstance(): FreeFragment {
            val args = Bundle()
            val fragment = FreeFragment()
            fragment.arguments = args
            return fragment
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

    override fun getCommercialListModel(): List<FreeItem>? {
        return freeListAdapter.getDataSet()
    }

    override fun getRvState(): Parcelable? {
        return rvList.layoutManager.onSaveInstanceState()
    }

    //------------------------------------------------------------View_interface----------------------------------------------------------------------

}
