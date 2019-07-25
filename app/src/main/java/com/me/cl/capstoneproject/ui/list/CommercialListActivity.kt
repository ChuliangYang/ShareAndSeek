package com.me.cl.capstoneproject.ui.list

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.Parcelable
import android.support.constraint.ConstraintLayout
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.*
import android.support.v7.widget.Toolbar
import android.transition.Transition
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.me.cl.capstoneproject.MyApplication
import com.me.cl.capstoneproject.R
import com.me.cl.capstoneproject.adapter.recyclerview.CommercialListAdapter
import com.me.cl.capstoneproject.anim.ScaleFadeInAnimator
import com.me.cl.capstoneproject.base.BaseActivity
import com.me.cl.capstoneproject.base.Constant.ListPage.*
import com.me.cl.capstoneproject.base.Constant.MainPage.*
import com.me.cl.capstoneproject.bean.CommercialItem
import com.me.cl.capstoneproject.event.TitleChangeEvent
import com.me.cl.capstoneproject.ui.list.di.CommercialListModule
import com.me.cl.capstoneproject.ui.list.di.DaggerCommercialListComponent
import com.me.cl.capstoneproject.ui.list.mvp.CommercialListPresenter
import com.me.cl.capstoneproject.ui.list.mvp.CommercialListView
import com.me.cl.capstoneproject.util.BitmapUtil
import com.me.cl.capstoneproject.widget.CustomSpinner
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle
import com.trello.rxlifecycle2.LifecycleProvider
import kotlinx.android.synthetic.main.activity_commercial_list.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.support.v4.onRefresh
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList

/**
 * Created by CL on 11/29/17.
 */

class CommercialListActivity : BaseActivity(), CommercialListView {
    @BindView(R.id.toolbar2)
    lateinit var toolbar2: Toolbar
    @BindView(R.id.tv_service)
    internal lateinit var tvService: TextView
    @BindView(R.id.tv_sort)
    internal lateinit var tvSort: TextView
    @BindView(R.id.sp_sort)
    internal lateinit var spSort: Spinner
    @BindView(R.id.sp_title)
    internal lateinit var sp_title: CustomSpinner
    @BindView(R.id.rv_list)
    internal lateinit var rvList: RecyclerView
    @BindView(R.id.view_backgroud)
    internal lateinit var backgroud: View
    @BindView(R.id.fl_root)
    internal lateinit var root: FrameLayout
    @BindView(R.id.sr_list)
    internal lateinit var srlist: SwipeRefreshLayout
    @BindView(R.id.rl_fake)
    internal lateinit var rlFake: RelativeLayout
    @BindView(R.id.cl_content)
    internal lateinit var clContent: ConstraintLayout

    @field:[Inject Named("CommercialList")]
    lateinit var commercialListAdapter: CommercialListAdapter

    @Inject
     lateinit var presenter: CommercialListPresenter<CommercialListView>

    @field:[Inject Named("CommercialList")]
    lateinit var activity: Activity

    val provider: LifecycleProvider<Lifecycle.Event> = AndroidLifecycle.createLifecycleProvider(this)

    var list: MutableList<CommercialItem> = ArrayList()

    var titleSpinnerListnerEnable = true

    var iv1:ImageView?=null
    var iv2:ImageView?=null
    var bindView=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        setContentView(R.layout.activity_commercial_list)
        ButterKnife.bind(this)
        bindView=true
        DaggerCommercialListComponent.builder().applicationComponent((application as MyApplication).applicationComponent)
                .commercialListModule(CommercialListModule(this)).build().inject(this)
//        var iv:ImageView?=null


        rlFake.apply {
            addView(ImageView(this@CommercialListActivity).apply {
                id=R.id.district_share
                setImageBitmap(BitmapUtil.fetchBitmapFromIntent(intent))
                iv1=this
                transitionName=TRANSITION_DISTRICT
            },RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(intent.getIntExtra(BUNDLE_KEY_SP_LEFT,0),intent.getIntExtra(BUNDLE_KEY_SP_TOP,0),0,0)
            })

            addView(ImageView(this@CommercialListActivity).apply {
                id=R.id.toolbar_share
                setImageBitmap(BitmapUtil.fetchBitmapFromIntent(intent))
                iv2=this
                transitionName=TRANSITION_TOOLBAR
            },RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0,0,0,0)
            })

//             addView(ImageView(this@CommercialListActivity).apply {
//                 id=R.id.fab_share
//                 imageBitmap= BitmapUtil.fetchBitmapFromIntent(intent)
//                 iv=this
//                 transitionName="fab"
//             },RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
//                 setMargins(intent.getIntExtra("left",0),intent.getIntExtra("top",0),0,0)
//             })

        }

        initView()
        addListener()
        presenter.manage(this)
        presenter.init(intent,savedInstanceState,provider)

        window.sharedElementEnterTransition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
//                iv!!.animate().y(rlFake.bottom.toFloat()).setDuration(800).setInterpolator(FastOutSlowInInterpolator())
                iv1?.animate()?.y((0-iv1!!.height).toFloat())?.setDuration(600)?.interpolator = FastOutSlowInInterpolator()
                iv2?.animate()?.y((0-iv2!!.height).toFloat())?.setDuration(600)?.interpolator = FastOutSlowInInterpolator()

            }

            override fun onTransitionEnd(transition: Transition) {
                window.sharedElementEnterTransition.removeListener(this)
                rlFake.visibility=View.GONE
//                iv!!.transitionName=null
                iv1?.transitionName=null
                iv2?.transitionName=null
            }

            override fun onTransitionCancel(transition: Transition) {

            }

            override fun onTransitionPause(transition: Transition) {

            }

            override fun onTransitionResume(transition: Transition) {

            }
        })

        window.enterTransition.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {

            }

            override fun onTransitionEnd(transition: Transition) {
                window.enterTransition.removeListener(this)

                presenter.sortList(SORT_LIST_BY_DEFAULT, presenter.getCurrentTypeFromIntent(intent), sp_title.selectedItem.toString(), activity,false,true)
            }

            override fun onTransitionCancel(transition: Transition) {

            }

            override fun onTransitionPause(transition: Transition) {

            }

            override fun onTransitionResume(transition: Transition) {

            }
        })
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            presenter.handleActivityResult(requestCode,resultCode, data,this)
    }

    override fun onBackPressed() {
        toolbar2.isTransitionGroup=false
//                window.sharedElementReturnTransition.removeTarget(view_backgroud)
        toolbar2.transitionName=TRANSITION_TOOLBAR
        sp_title.transitionName=TRANSITION_DISTRICT
        finishAfterTransition()

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destory()
        bindView=false
    }

    private fun initView() {
        setSupportActionBar(toolbar2)
        toolbar2.setNavigationOnClickListener { onBackPressed() }
        supportActionBar?.setDisplayShowTitleEnabled(false)
        initList()
        tvService.text = presenter.getCurrentTypeFromIntent(intent)
        tvSort.text = getString(R.string.sort)
        initSortSpinner()
        sp_title.tag = true// Flag of init state
        spSort.tag = true
        if (sp_title is CustomSpinner) {
            sp_title.onDropDownVisibleChangeListener = object : CustomSpinner.onDropDownVisibleChangeListener {
                override fun onPopUp(v: View) {
                    (sp_title.findViewById(R.id.btn_arrow) as ImageButton).setImageDrawable(getDrawable(R.drawable.vector_arrow_down_to_up))
                    ((sp_title.findViewById(R.id.btn_arrow) as ImageButton).drawable as Animatable).start()
                    (sp_title.findViewById(R.id.btn_arrow) as View).tag = TAG_UP
                }

                override fun onDismiss(v: View) {
                    (sp_title.findViewById(R.id.btn_arrow) as ImageButton).setImageDrawable(getDrawable(R.drawable.vector_arrow_up_to_down))
                    ((sp_title.findViewById(R.id.btn_arrow) as ImageButton).drawable as Animatable).start()
                    (sp_title.findViewById(R.id.btn_arrow) as View).tag = TAG_DOWN
                }
            }
        }

        val adapter = ArrayAdapter<String>(this, R.layout.spinner_area, R.id.item_title, baseContext.resources.getStringArray(R.array.district_list))
        adapter.setDropDownViewResource(R.layout.spinner_drop)
        sp_title.adapter = adapter
        srlist.apply {
            setProgressViewOffset(true, 0, 100)
            setSize(SwipeRefreshLayout.LARGE)
            setColorSchemeResources(
                    R.color.colorAccent,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light)
            setOnRefreshListener {
               presenter.sortList(SORT_LIST_FROM_CACHE, presenter.getCurrentTypeFromIntent(intent), sp_title.selectedItem.toString(), activity,true,true)
            }
        }

        sp_title.setSelection(intent.getIntExtra(BUNDLE_KEY_SP_TITLE_POSITION,0))
//        if (sp_title.selectedItemPosition!=0) {
//            titleSpinnerListnerEnable=false
//        }
        startPostponedEnterTransition()
    }

    private fun addListener() {
        sp_title.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (sp_title.tag as Boolean) {
                    sp_title.tag = false
                } else {
                    if (titleSpinnerListnerEnable) {
                        presenter.sortList(SORT_LIST_FROM_CACHE, presenter.getCurrentTypeFromIntent(intent), sp_title.selectedItem.toString(), activity,false,true)
                        EventBus.getDefault().postSticky(TitleChangeEvent.FromCommercialListActivity(sp_title.selectedItem.toString(), position))
                    } else {
                        titleSpinnerListnerEnable=true
                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        spSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (spSort.tag as Boolean) {
                    spSort.tag = false
                } else {
                    when (position) {
                        0 -> presenter.sortList(SORT_LIST_BY_DEFAULT, presenter.getCurrentTypeFromIntent(intent), sp_title.selectedItem.toString(), activity,false,true)
                        1 -> presenter.sortList(SORT_LIST_BY_RATE, presenter.getCurrentTypeFromIntent(intent), sp_title.selectedItem.toString(), activity,false,true)
                        2 -> presenter.sortList(SORT_LIST_BY_COSTUME, presenter.getCurrentTypeFromIntent(intent), sp_title.selectedItem.toString(), activity,false,true)
                        3 -> presenter.sortList(SORT_LIST_BY_DISTANCE, presenter.getCurrentTypeFromIntent(intent), sp_title.selectedItem.toString(), activity,false,true)
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun initSortSpinner() {
        val sortTypes = Arrays.asList(*resources.getStringArray(R.array.sort_type))
        val sortAdapter = ArrayAdapter(this, R.layout.spinner_sort_item, sortTypes)
        sortAdapter.setDropDownViewResource(R.layout.spinner_sort_item)
        spSort.adapter = sortAdapter

        //        spSort.setSelection(0);
    }

    private fun initList() {
        rvList.apply {
            layoutManager = LinearLayoutManager(this@CommercialListActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(false)
            itemAnimator= DefaultItemAnimator()
            adapter = commercialListAdapter
            itemAnimator = ScaleFadeInAnimator()
            itemAnimator.addDuration = 400
            itemAnimator.removeDuration = 100
            val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.divider_item))
            addItemDecoration(dividerItemDecoration)
        }

    }

    override fun showList(list: List<CommercialItem>,animate:Boolean,restore:Boolean,states:Array<Parcelable?>?) {
        if (commercialListAdapter.data == null) {
            commercialListAdapter.data = ArrayList()
        }
        if (restore) {
            commercialListAdapter.data.addAll(list)
            this.list.clear()
            this.list.addAll(list)
            if (states != null) {
                rvList.layoutManager.onRestoreInstanceState(states[0])
                    sp_title.onRestoreInstanceState(states[1])
                    sp_sort.onRestoreInstanceState(states[2])
            }
        } else {
            if (animate) {
                commercialListAdapter.data.clear()
                commercialListAdapter.notifyItemRangeRemoved(0, this.list.size)
                commercialListAdapter.notifyItemRangeChanged(0, this.list.size)
                this.list.clear()
                if (!list.isEmpty()) {
                    commercialListAdapter.data.addAll(list)
                    commercialListAdapter.notifyItemRangeInserted(0, list.size)
                    this.list.addAll(list)
                }
            } else {
                commercialListAdapter.data.clear()
                this.list.clear()
                commercialListAdapter.data.addAll(list)
                commercialListAdapter.notifyDataSetChanged()
                this.list.addAll(list)
            }
            YoYo.with(Techniques.RubberBand).delay(500).duration(800).playOn(tvService)
        }
    }

    override fun showAlertDialog(title: String, content: String, button: String, singleButtonCallback: MaterialDialog.SingleButtonCallback?, onCancelListener: DialogInterface.OnCancelListener?, context: Context) {
        super.showAlertDialog(title, content, button, singleButtonCallback, onCancelListener)
    }

    override fun showSnackBar(info: String) {
        snackbar(clContent,info)
//        Snackbar.make(viewRoot, info, Snackbar.LENGTH_SHORT)
//                .show()
    }

    override fun controlIndeterminateProgress(show: Boolean, title: String?, content: String?, onCancelListener: DialogInterface.OnCancelListener?, context: Context?,swipe:Boolean) {
        if (swipe) {
            if (!show&&bindView) {
                if (srlist.isRefreshing) {
                    srlist.isRefreshing = false
                }
            }
        } else {
            super.controlIndeterminateProgress(show, title, content, onCancelListener)
        }
    }

    override fun restoreLastSortType(sortType: Int) {
        when (sortType) {
            SORT_LIST_BY_DEFAULT -> spSort.setSelection(0)
            SORT_LIST_BY_RATE -> spSort.setSelection(1)
            SORT_LIST_BY_COSTUME -> spSort.setSelection(2)
            SORT_LIST_BY_DISTANCE -> spSort.setSelection(3)
        }

    }

    override fun getSpinnerTitle():String{
        return sp_title.selectedItem.toString()
    }

    override fun getListModel(): java.util.ArrayList<CommercialItem>? {
        return commercialListAdapter.data
    }

    override fun getRvState():Parcelable?{
        return rvList.layoutManager.onSaveInstanceState()
    }

    override fun getSpinnerTitleState():Parcelable?{
        return sp_title.onSaveInstanceState()
    }

    override fun getSpinnerTitlePosition():Int?{
        return sp_title.selectedItemPosition
    }

    override fun getSpinnerSortState():Parcelable?{
        return spSort.onSaveInstanceState()
    }

    override fun restoreSpinner(states:Array<Parcelable?>?){
        if (states!=null) {
            sp_title.onRestoreInstanceState(states[0])
            sp_sort.onRestoreInstanceState(states[1])
        }
    }
    override fun setTitleSpinnerInitEnable(titleSpinnerListnerEnable: Boolean?) {
        titleSpinnerListnerEnable?.let {
            this.titleSpinnerListnerEnable=it
        }

    }
}
