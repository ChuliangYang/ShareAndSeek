package com.me.cl.capstoneproject.ui.main.fragment.commercial

import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.view.PagerAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.afollestad.materialdialogs.MaterialDialog
import com.me.cl.capstoneproject.MyApplication
import com.me.cl.capstoneproject.R
import com.me.cl.capstoneproject.adapter.recyclerview.CommercialCateAdapter
import com.me.cl.capstoneproject.adapter.recyclerview.CommercialListAdapter
import com.me.cl.capstoneproject.anim.ScaleFadeInAnimator
import com.me.cl.capstoneproject.base.BaseFragment
import com.me.cl.capstoneproject.base.Constant
import com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_SAVE_STATE
import com.me.cl.capstoneproject.bean.CommercialCategory
import com.me.cl.capstoneproject.bean.CommercialItem
import com.me.cl.capstoneproject.di.FragmentQL
import com.me.cl.capstoneproject.event.CommandEvent
import com.me.cl.capstoneproject.event.ForeGroundEvent
import com.me.cl.capstoneproject.event.TitleChangeEvent
import com.me.cl.capstoneproject.event.UploadCompleteEvent
import com.me.cl.capstoneproject.ui.list.di.CommercialListModule
import com.me.cl.capstoneproject.ui.main.fragment.commercial.di.CommercialFragmentModule
import com.me.cl.capstoneproject.ui.main.fragment.commercial.di.DaggerCommercialFragmentComponent
import com.me.cl.capstoneproject.ui.main.fragment.commercial.mvp.CommercialPagePresenter
import com.me.cl.capstoneproject.ui.main.fragment.commercial.mvp.CommercialPageView
import com.me.cl.capstoneproject.widget.CustomNestedScrollView
import com.me.cl.capstoneproject.widget.InkPageIndicator
import com.me.cl.capstoneproject.widget.WrapContentViewPager
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle
import com.trello.rxlifecycle2.LifecycleProvider
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * Created by CL on 11/3/17.
 */

class CommercialFragment : BaseFragment(), CommercialPageView {

    @Inject
    lateinit var commercialPagePresenter: CommercialPagePresenter<CommercialPageView>

    @Inject @field:FragmentQL
    lateinit var commercialListAdapter: CommercialListAdapter
    @Inject
    lateinit var commercialItems: MutableList<CommercialItem>

    @BindView(R.id.vp_commercial_cate)
    lateinit var vpCommercialCate: WrapContentViewPager
    @BindView(R.id.indicator)
    lateinit var indicator: InkPageIndicator
    @BindView(R.id.rv_commercial_item)
    lateinit var rvCommercialItem: RecyclerView
    @BindView(R.id.sf_commercial)
    lateinit var sf_commercial: SwipeRefreshLayout
    @BindView(R.id.nsv_root)
    lateinit var nsv_root: CustomNestedScrollView
    @BindView(R.id.ll_cate)
    lateinit var ll_cate: LinearLayout

    val provider: LifecycleProvider<Lifecycle.Event> = AndroidLifecycle.createLifecycleProvider(this)


    lateinit var unbinder: Unbinder

     var isRestore = false
     var passTitleEvent = false
     var passForegroundEvent = false

     var initView=false

    override val commercialListModel: MutableList<CommercialItem>?
        get() = commercialListAdapter!!.data

    override val rvState: Parcelable
        get() = rvCommercialItem!!.layoutManager.onSaveInstanceState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerCommercialFragmentComponent.builder()
                .applicationComponent((activity!!.application as MyApplication).applicationComponent)
                .commercialListModule(CommercialListModule(activity!!))
                .commercialFragmentModule(CommercialFragmentModule(this)).build().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_commercial, container, false)
        unbinder = ButterKnife.bind(this, root)

        rvCommercialItem?.apply {
            setHasFixedSize(false)
            itemAnimator = ScaleFadeInAnimator()
            itemAnimator.addDuration = 400
            itemAnimator.removeDuration = 100
            val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.divider_item))
            addItemDecoration(dividerItemDecoration)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = commercialListAdapter
        }


        sf_commercial?.apply {
            setProgressViewOffset(true, 0, 100)

            setSize(SwipeRefreshLayout.LARGE)

            setColorSchemeResources(
                    R.color.colorAccent,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light)
        }

        nsv_root?.includeSwipeRefreshLayout = true
        initView=true
        return root
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        commercialPagePresenter?.manage(this)
        commercialPagePresenter?.init(savedInstanceState,provider)
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_KEY_SAVE_STATE)) {
            isRestore = true
            passTitleEvent = true
            passForegroundEvent = true
        } else {
            isRestore = false
            passTitleEvent = false
            passForegroundEvent = false
        }

        sf_commercial?.setOnRefreshListener { commercialPagePresenter?.configCommercialListFirstPage(commercialPagePresenter?.currentDisplayedTitle, commercialPagePresenter?.currentDisplayedTitlePosition, true, true, false) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
        initView=false
    }

    override fun onDestroy() {
        super.onDestroy()
        commercialPagePresenter.destory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        commercialPagePresenter.onSaveInstanceState(outState)
        outState.putBoolean(BUNDLE_KEY_SAVE_STATE, true)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN, priority = 1)
    fun onTitleDecided(event: TitleChangeEvent.ToMainPageTags) {
        if (passTitleEvent && commercialPagePresenter?.currentDisplayedTitlePosition == event.CurrentPosition) {
            EventBus.getDefault().removeStickyEvent(event)
            passTitleEvent = false
            return
        }
        commercialPagePresenter?.onTitleDecided(event.CurrentTitle, event.CurrentPosition)
        EventBus.getDefault().removeStickyEvent(event)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onBringToForeGround(event: ForeGroundEvent.MainPageTAG) {
        if (passForegroundEvent) {
            if (event.CurrentTagPosition == 0) {
                EventBus.getDefault().removeStickyEvent(event)
            }
            passForegroundEvent = false
            return
        }
        commercialPagePresenter?.onTagsForeGroundStateChange(event.CurrentTitle, event.CurrentTitlePosition, event.CurrentTagPosition)
        if (event.CurrentTagPosition == 0 && commercialListAdapter?.data != null) {
            commercialPagePresenter?.saveWidgetListModel(commercialListAdapter?.data)
        }
        if (event.CurrentTagPosition == 0) {
            EventBus.getDefault().removeStickyEvent(event)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onUploadComplete(event: UploadCompleteEvent.CommercialService) {
        commercialPagePresenter!!.onRefreshList(event.success, false)
        EventBus.getDefault().removeStickyEvent(event)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onRefreshList(event: CommandEvent.RefreshCommercialList) {
        commercialPagePresenter!!.onRefreshList(true, event.animate)
        EventBus.getDefault().removeStickyEvent(event)
    }


    //------------------------------------------------------------View_interface----------------------------------------------------------------------

    override fun popCommercialCategoryPagers(multiCommercialCategoryList: MutableList<MutableList<CommercialCategory>>?) {
        if (vpCommercialCate != null) {
            vpCommercialCate!!.adapter = object : PagerAdapter() {
                override fun getCount(): Int {
                    return multiCommercialCategoryList?.size ?: 0
                }

                override fun instantiateItem(container: ViewGroup, position: Int): Any {
                    val root = (LayoutInflater.from(activity).inflate(R.layout.recyclerview, container, false) as RecyclerView).apply {
                        setHasFixedSize(true)
                        layoutManager = GridLayoutManager(context, Constant.MainPage.COLUM_COUNT)
                        adapter = CommercialCateAdapter(context, multiCommercialCategoryList?.run { multiCommercialCategoryList[position] })
                    }.also { container.addView(it) }
                    return root
                }

                override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                    container.removeView(`object` as View)
                }

                override fun isViewFromObject(view: View, `object`: Any): Boolean {
                    return view === `object`
                }
            }
            indicator!!.setViewPager(vpCommercialCate)
            vpCommercialCate!!.currentItem = 0
            vpCommercialCate!!.measure(0, 0)
        }
    }

    override fun popCommericialList(commercialItems: MutableList<CommercialItem>?, restore: Boolean?, animate: Boolean?, rvState: Parcelable?) {
        commercialListAdapter.run {
            var commercialItems=commercialItems
            if (data == null) {
                data = ArrayList()
            }
            if (commercialItems == null) {
                commercialItems = ArrayList()
            }
            if (restore==true) {
                commercialItems?.let { data.addAll(it) }
                this@CommercialFragment.commercialItems!!.clear()
                commercialItems?.let {
                    this@CommercialFragment.commercialItems!!.addAll(it)
                }
                if (rvState != null) {
                    rvCommercialItem!!.layoutManager.onRestoreInstanceState(rvState)
                }else{}
            } else {
                if (animate==true) {
                    data.clear()
                    notifyItemRangeRemoved(0, this@CommercialFragment.commercialItems!!.size)
                    notifyItemRangeChanged(0, this@CommercialFragment.commercialItems!!.size)
                    this@CommercialFragment.commercialItems!!.clear()
                    if ((commercialItems as MutableList<CommercialItem>?)?.isEmpty() != true) {
                        (commercialItems as MutableList<CommercialItem>?)?.let {
                            data.addAll(it)
                        }
                        notifyItemRangeInserted(0, commercialItems.size)
                        notifyItemRangeChanged(0, commercialItems.size)
                        commercialItems?.let {
                            this@CommercialFragment.commercialItems!!.addAll(it)
                        }
                    }else{}
                } else {
                    data.clear()
                    this@CommercialFragment.commercialItems!!.clear()
                    data.addAll(commercialItems)
                    notifyDataSetChanged()
                    commercialItems?.let {
                        this@CommercialFragment.commercialItems!!.addAll(it)
                    }
                }
            }
        }
    }

    override fun showAlertDialog(title: String?, content: String?, button: String?, singleButtonCallback: MaterialDialog.SingleButtonCallback?, onCancelListener: DialogInterface.OnCancelListener?, context: Context?) {
        super.showAlertDialog(title, content, button, singleButtonCallback, onCancelListener)
    }

    override fun controlIndeterminateProgress(show: Boolean?, title: String?, content: String?, onCancelListener: DialogInterface.OnCancelListener?, context: Context?, swipe: Boolean?) {
        if (swipe==true) {
            if (show!=true&&initView) {
                if (sf_commercial.isRefreshing) {
                    sf_commercial!!.isRefreshing = false
                }
            }
        } else {
            super.controlIndeterminateProgress(show!!, title, content, onCancelListener)
        }
    }

    companion object {

        fun newInstance(): CommercialFragment {
            val args = Bundle()
            val fragment = CommercialFragment()
            fragment.arguments = args
            return fragment
        }
    }
    //------------------------------------------------------------View_interface----------------------------------------------------------------------

}
