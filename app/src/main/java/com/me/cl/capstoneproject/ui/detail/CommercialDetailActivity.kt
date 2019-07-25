package com.me.cl.capstoneproject.ui.detail

import android.Manifest
import android.animation.Animator
import android.animation.ArgbEvaluator
import android.annotation.TargetApi
import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.*
import android.support.v4.widget.NestedScrollView
import android.support.v7.graphics.Palette
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.transition.Transition
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.drawablecolorchange.DrawableColorChange
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.me.cl.capstoneproject.MyApplication
import com.me.cl.capstoneproject.R
import com.me.cl.capstoneproject.adapter.recyclerview.ReviewListAdapter
import com.me.cl.capstoneproject.adapter.viewpager.AlbumBannerAdapter
import com.me.cl.capstoneproject.base.BaseActivity
import com.me.cl.capstoneproject.base.Constant.DetailPage.*
import com.me.cl.capstoneproject.bean.CommercialItem
import com.me.cl.capstoneproject.bean.ReviewBeanWrapper
import com.me.cl.capstoneproject.event.UploadCompleteEvent
import com.me.cl.capstoneproject.ui.detail.di.CommercialDetailModule
import com.me.cl.capstoneproject.ui.detail.di.DaggerCommercialDetailComponent
import com.me.cl.capstoneproject.ui.detail.mvp.CommercialDetailPresenter
import com.me.cl.capstoneproject.ui.detail.mvp.CommercialDetailView
import com.me.cl.capstoneproject.util.BaseUtils
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle
import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_commercial_detail.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar
//import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by CL on 11/22/17.
 */

class CommercialDetailActivity : BaseActivity(), CommercialDetailView, OnMapReadyCallback {
    @BindView(R.id.vp_album)
    lateinit var vpAlbum: ViewPager

    @BindView(R.id.app_bar)
    lateinit var appBar: Toolbar
    @BindView(R.id.appbar_banner)
    lateinit var appbarBanner:AppBarLayout
    @BindView(R.id.collapsing_toolbar_layout)
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    @BindView(R.id.rv_reviews)
    lateinit var rvReviews: RecyclerView
    @BindView(R.id.iv_address)
    lateinit var ivAddress: ImageView
    @BindView(R.id.tv_address)
    lateinit var tvAddress: TextView
    @BindView(R.id.rl_address)
    lateinit var rlAddress: RelativeLayout
    @BindView(R.id.iv_phone)
    lateinit var ivPhone: ImageView
    @BindView(R.id.tv_phone)
    lateinit var tvPhone: TextView
    @BindView(R.id.rl_phone)
    lateinit var rlPhone: RelativeLayout
    @BindView(R.id.iv_wechat)
    lateinit var ivWechat: ImageView
    @BindView(R.id.tv_wechat)
    lateinit var tvWechat: TextView
    @BindView(R.id.rl_wechat)
    lateinit var rlWechat: RelativeLayout
    @BindView(R.id.rb_reviews_bottom)
    lateinit var rbReviewsBottom: RatingBar
    @BindView(R.id.tv_reviews_bottom)
    lateinit var tvReviewsBottom: TextView
    @BindView(R.id.relativeLayout)
    lateinit var relativeLayout: RelativeLayout
    @BindView(R.id.background)
    lateinit var background: View
    @BindView(R.id.tv_title)
    lateinit var tvTitle: TextView
    @BindView(R.id.rb_reviews)
    lateinit var rbReviews: RatingBar
    @BindView(R.id.tv_reviews)
    lateinit var tvReviews: TextView
    @BindView(R.id.tv_average)
    lateinit var tvAverage: TextView
    @BindView(R.id.tv_describtion)
    lateinit var tvDescribtion: TextView
    @BindView(R.id.ad_banner)
    lateinit var adBanner: AdView
    @BindView(R.id.sv_background)
    lateinit var svBackground: NestedScrollView
    @BindView(R.id.ll_deitail)
    lateinit var llDeitail: LinearLayout
    @Inject
    lateinit var commercialDetailPresenter: CommercialDetailPresenter<CommercialDetailView>

    @Inject
    lateinit var reviewListAdapter: ReviewListAdapter
    @Inject
    lateinit var albumBannerAdapter: AlbumBannerAdapter

    val provider: LifecycleProvider<Lifecycle.Event> = AndroidLifecycle.createLifecycleProvider(this)


    var textColor:Int?=null
    var vibrantColor:Int?=null
    var autoChangeBanner=true
    var autoChangeBannerCache=true
    var appbarState=-1
    var hasPhoto=true
    var cachePhone:String?=null
    var currentColor:Int=Color.WHITE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition()
        }

        setContentView(R.layout.activity_commercial_detail)
        ButterKnife.bind(this)
        DaggerCommercialDetailComponent.builder().applicationComponent((application as MyApplication).applicationComponent)
                .commercialDetailModule(CommercialDetailModule(this)).build().inject(this)
        setSupportActionBar(appBar)
        appBar.setNavigationOnClickListener { v -> finishAfterTransition() }
        supportActionBar?.setHomeButtonEnabled(true)
        initMap()
        initReviewsRecyclerview()

        commercialDetailPresenter.manage(this)
        commercialDetailPresenter.init(provider)
        initAppbar()
        configEnterAnimator()
        displayAdd()

        if (savedInstanceState?.containsKey(BUNDLE_KEY_CURRENT_COLOR) == true) {
            currentColor= savedInstanceState.get(BUNDLE_KEY_CURRENT_COLOR) as Int
            if (currentColor!= Color.WHITE) {
                appBar.post {
                    if (appBar.childCount>1 ) {
                        val drawableColorChange = DrawableColorChange(this@CommercialDetailActivity)
                        drawableColorChange.setDrawable((appBar.getChildAt(1) as ImageView).drawable)
                        drawableColorChange.setColor(currentColor)
                        (appBar.getChildAt(1) as ImageButton).setImageDrawable(drawableColorChange.colorChangedDrawable)
                        for (i in 0 until appBar.menu.size()) {
                            appBar.menu.getItem(i).icon = drawableColorChange.changeColorByColor(appBar.menu.getItem(i).icon, currentColor)
                        }
                    }
                }

            }
        }
        if (savedInstanceState?.containsKey(BUNDLE_KEY_TEXT_COLOR) == true) {
            textColor= savedInstanceState.get(BUNDLE_KEY_TEXT_COLOR) as Int
            collapsingToolbarLayout.setExpandedTitleColor(textColor
                    ?: android.R.color.white)
        }
        if (savedInstanceState?.containsKey(BUNDLE_KEY_VIBRANT_COLOR) == true) {
            vibrantColor= savedInstanceState.get(BUNDLE_KEY_VIBRANT_COLOR) as Int
        }
        if (savedInstanceState?.containsKey(BUNDLE_KEY_APP_BAR_STATE) == true) {
            appbarState= savedInstanceState.get(BUNDLE_KEY_APP_BAR_STATE) as Int
        }
        if (savedInstanceState?.containsKey(BUNDLE_KEY_AUTO_CHANGE) == true) {
            autoChangeBanner= savedInstanceState.get(BUNDLE_KEY_AUTO_CHANGE) as Boolean
        }
        if (savedInstanceState?.containsKey(BUNDLE_KEY_HAS_PHOTO) == true) {
            hasPhoto= savedInstanceState.get(BUNDLE_KEY_HAS_PHOTO) as Boolean
        }
        if (savedInstanceState?.get(BUNDLE_KEY_CACHE_PHONE)!=null) {
            cachePhone= savedInstanceState.get(BUNDLE_KEY_CACHE_PHONE) as String
        }

    }

    private fun initAppbar() {
        if (hasPhoto) {
            var oldOffSet = -1
            appbarBanner.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
                override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                    if (oldOffSet != verticalOffset) {
                        if (verticalOffset == 0) {
                            autoChangeBanner = true
                            appbarState = 1
                            if (textColor != null) {
                                collapsingToolbarLayout.setExpandedTitleColor(textColor
                                        ?: android.R.color.white)
                            }
                            if (vibrantColor != null) {
                                val drawableColorChange = DrawableColorChange(this@CommercialDetailActivity)
                                drawableColorChange.setDrawable((appBar.getChildAt(1) as ImageView).drawable)
                                drawableColorChange.setColor(vibrantColor ?: android.R.color.white)
                                (appBar.getChildAt(1) as ImageButton).setImageDrawable(drawableColorChange.colorChangedDrawable)
                                for (i in 0 until appBar.menu.size()) {
                                    appBar.menu.getItem(i).icon = drawableColorChange.changeColorByColor(appBar.menu.getItem(i).icon, vibrantColor)
                                }
                                currentColor= vibrantColor?: android.R.color.white
                            }
                            //展开
                        } else if (Math.abs(verticalOffset) >= appBarLayout?.totalScrollRange?:0) {
                            //折叠
                            autoChangeBanner = false
                            appbarState = 2
                            val drawableColorChange = DrawableColorChange(this@CommercialDetailActivity)
                            drawableColorChange.setDrawable((appBar.getChildAt(1) as ImageView).drawable)
                            drawableColorChange.setColorResId(android.R.color.white)
                            (appBar.getChildAt(1) as ImageButton).setImageDrawable(drawableColorChange.colorChangedDrawable)
                            for (i in 0 until appBar.menu.size()) {
                                appBar.menu.getItem(i).icon = drawableColorChange.changeColorById(appBar.menu.getItem(i).icon, android.R.color.white)
                            }
                            currentColor=Color.WHITE
                        } else {
                            autoChangeBanner = false
                            appbarState = 3
                            if (verticalOffset != oldOffSet && vibrantColor != null) {
                                val animColor = ArgbEvaluator().evaluate((Math.abs(verticalOffset).toFloat() / (appBarLayout?.totalScrollRange?.toFloat()?:0f)), vibrantColor, Color.WHITE)
                                val drawableColorChange = DrawableColorChange(this@CommercialDetailActivity)
                                drawableColorChange.setDrawable((appBar.getChildAt(1) as ImageView).drawable)
                                drawableColorChange.setColor(animColor as Int)
                                (appBar.getChildAt(1) as ImageButton).setImageDrawable(drawableColorChange.colorChangedDrawable)
                                for (i in 0 until appBar.menu.size()) {
                                    appBar.menu.getItem(i).icon = drawableColorChange.changeColorByColor(appBar.menu.getItem(i).icon, animColor)
                                }
                                currentColor=animColor
                            }
                        }
                        oldOffSet = verticalOffset
                    }
                }
            })
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun configEnterAnimator() {
        window.sharedElementEnterTransition.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition?) {
                transition?.removeListener(this)
                appbarBanner.visibility=View.VISIBLE
            }

            override fun onTransitionResume(transition: Transition?) {
            }

            override fun onTransitionPause(transition: Transition?) {
            }

            override fun onTransitionCancel(transition: Transition?) {
            }

            override fun onTransitionStart(transition: Transition?) {
                if (hasPhoto) {
                    appbarBanner.visibility = View.INVISIBLE
                    svBackground.translationY = rl_content.measuredHeight.toFloat()-appbarBanner.measuredHeight

                } else {
                    appbarBanner.translationY=-(collapsingToolbarLayout.minimumHeight.toFloat())
                    appbarBanner.animate().translationY(0f).setStartDelay(350).duration = 300
                    svBackground.translationY = rl_content.measuredHeight.toFloat()-collapsingToolbarLayout.minimumHeight
                }
                svBackground.scrollX=0
                svBackground.scrollY=0
                svBackground.animate().setStartDelay(350).setDuration(200).translationY(0f)
                        .setInterpolator(LinearInterpolator()).setListener(object: Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        svBackground.scrollX=0
                        svBackground.scrollY=0
                    }
                })

            }
        })
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initReviewsRecyclerview() {
        rvReviews.setHasFixedSize(false)
        val dividerItemDecoration = DividerItemDecoration(baseContext, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.divider_review_item))
        rvReviews.addItemDecoration(dividerItemDecoration)
        rvReviews.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        rvReviews.adapter = reviewListAdapter

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        autoChangeBanner=autoChangeBannerCache

    }

    override fun onPause() {
        super.onPause()
        autoChangeBannerCache=autoChangeBanner
        autoChangeBanner=false
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.detail_menu, menu)
        if (!hasPhoto) {
            val animColor = Color.WHITE
            val drawableColorChange = DrawableColorChange(this@CommercialDetailActivity)
            drawableColorChange.setDrawable((appBar.getChildAt(1) as ImageView).drawable)
            drawableColorChange.setColor(animColor)
            (appBar.getChildAt(1) as ImageButton).setImageDrawable(drawableColorChange.colorChangedDrawable)
            for (i in 0 until appBar.menu.size()) {
                appBar.menu.getItem(i).icon = drawableColorChange.changeColorById(appBar.menu.getItem(i).icon, android.R.color.white)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_share -> {
                share("${tvTitle.text}   address: ${tvAddress.text}  phone:${tvPhone.text}", "${tvTitle.text}")
            }
            R.id.menu_write_comment -> commercialDetailPresenter.onCommentClick(supportFragmentManager, this)
            R.id.menu_album->(albumBannerAdapter.weakCache.get(vpAlbum.currentItem) as Fragment).view?.performClick()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSION_CHECK_PHONE_CALL -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cachePhone?.run {
                    cachePhone?.let {
                        makeCall(it)
                    }
                }
                cachePhone=null
            } else {
                cachePhone=null
                toast(getString(R.string.permission_denied))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(BUNDLE_KEY_CURRENT_COLOR,currentColor)
        outState?.putInt(BUNDLE_KEY_VIBRANT_COLOR,vibrantColor?:Color.WHITE)
        outState?.putInt(BUNDLE_KEY_TEXT_COLOR,textColor?:Color.WHITE)
        outState?.putInt(BUNDLE_KEY_APP_BAR_STATE,appbarState)
        outState?.putBoolean(BUNDLE_KEY_AUTO_CHANGE,autoChangeBanner)
        outState?.putBoolean(BUNDLE_KEY_HAS_PHOTO,hasPhoto)
        outState?.putString(BUNDLE_KEY_CACHE_PHONE,cachePhone)
    }

    override fun onDestroy() {
        super.onDestroy()
        adBanner.destroy()
    }

    private fun displayAdd() {
//        MobileAds.initialize(this, getString(R.string.ad_test_app_id))
        MobileAds.initialize(this, getString(R.string.ad_app_id))
        val adRequest = AdRequest.Builder().build()
        adBanner.loadAd(adRequest)
    }

    override fun popBaseInfo(commercialItem: CommercialItem) {
        //        if (commercialItem.getRating()!=null) {
        rbReviews.rating = commercialItem.rating
        rbReviewsBottom.rating = commercialItem.rating
        //        }
        tvTitle.text = commercialItem.title
        appBar.title=tvTitle.text
                //        if (commercialItem.getReview_count()!=null) {
        if (commercialItem.review_count != 0f) {
            tvReviews.text = commercialItem.review_count.toInt().toString() + if (commercialItem.review_count == 1f) " "+getString(R.string.review) else " "+getString(R.string.reviews)
            tvReviewsBottom.text = tvReviews.text
        } else {
            tvReviewsBottom.text="0 "+getString(R.string.review)
        }

        //        }

        if (commercialItem.average_costume != 0f) {
            tvAverage.text = getString(R.string.monetary_unit)+" " + commercialItem.average_costume.toInt().toString()

        }
        tvDescribtion.text = commercialItem.summary
        if (commercialItem.location != null) {
            tvAddress.text = commercialItem.location.street + " , " + commercialItem.location.city + " , " + commercialItem.location.state
        }
        if (commercialItem.contact != null) {
            if (!TextUtils.isEmpty(commercialItem.contact.phone)) {
                rlPhone.visibility=View.VISIBLE
                tvPhone.text = commercialItem.contact.phone
                rlPhone.setOnClickListener {
                    if (BaseUtils.System.runtimePermissionCheck(this@CommercialDetailActivity, Manifest.permission.CALL_PHONE, REQUEST_CODE_PERMISSION_CHECK_PHONE_CALL)) {
                        makeCall(commercialItem.contact.phone)
                    } else {
                        cachePhone= commercialItem.contact.phone
                    }
                }
            } else {
                rlPhone.visibility=View.GONE
            }
            if (!TextUtils.isEmpty(commercialItem.contact.wechat)) {
                rlWechat.visibility=View.VISIBLE
                tvWechat.text = commercialItem.contact.wechat
            } else {
                rlWechat.visibility=View.GONE
            }
        }
        if (commercialItem.photoBeanList!=null&&commercialItem.photoBeanList.size > 0) {
            hasPhoto=true
            albumBannerAdapter.addPhotoList(commercialItem.photoBeanList)
            vpAlbum.adapter = albumBannerAdapter
            vpAlbum.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    vpAlbum.viewTreeObserver.removeOnPreDrawListener(this)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startPostponedEnterTransition()
                    }

                    Observable.interval(3, 6, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).compose(provider?.bindUntilEvent(Lifecycle.Event.ON_DESTROY)).subscribe {
                        if (autoChangeBanner) {
                            if (vpAlbum.currentItem + 1 >= vpAlbum.adapter?.count?:0) {
                                vpAlbum.setCurrentItem(0, false)
                            } else {
                                vpAlbum.arrowScroll(View.FOCUS_RIGHT)
                            }
                        }
                    }
                    return true
                }
            })
            vpAlbum.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                    when (state) {
                        SCROLL_STATE_IDLE -> autoChangeBanner = appbarState == 1
                        SCROLL_STATE_DRAGGING -> autoChangeBanner = false
                        SCROLL_STATE_SETTLING -> autoChangeBanner = false
                    }
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    if (albumBannerAdapter.weakCache.get(position) is Fragment) {
                        if ((albumBannerAdapter.weakCache.get(position) as Fragment).view?.find<ImageView>(R.id.iv_album_item)?.image != null) {
                            Palette.generateAsync(((albumBannerAdapter.weakCache.get(position) as Fragment).view?.find<ImageView>(R.id.iv_album_item)?.image as BitmapDrawable).bitmap
                                    , Palette.PaletteAsyncListener({ palette ->
                                textColor = findProperColor(palette,0)
                                vibrantColor = findProperColor(palette,1)
                                collapsingToolbarLayout.setExpandedTitleColor(textColor
                                        ?: android.R.color.white)
                                currentColor= vibrantColor?:Color.WHITE
                                val drawableColorChange = DrawableColorChange(this@CommercialDetailActivity)
                                drawableColorChange.setDrawable((appBar.getChildAt(1) as ImageView).drawable)
                                drawableColorChange.setColor(vibrantColor ?: android.R.color.white)
                                (appBar.getChildAt(1) as ImageButton).setImageDrawable(drawableColorChange.colorChangedDrawable)
                                for (i in 0 until appBar.menu.size()) {
                                    appBar.menu.getItem(i).icon = drawableColorChange.changeColorByColor(appBar.menu.getItem(i).icon, vibrantColor)
                                }
                            }))
                        } else {
                            (albumBannerAdapter.weakCache.get(position) as PhotoFragment).onImageCompleteOnceListener = PhotoFragment.onImageCompleteOnceListener {
                                Palette.generateAsync(((albumBannerAdapter.weakCache.get(position) as PhotoFragment).view?.find<ImageView>(R.id.iv_album_item)?.image as BitmapDrawable).bitmap
                                        , { palette ->
                                    textColor = findProperColor(palette,0)
                                    vibrantColor = findProperColor(palette,1)
                                    collapsingToolbarLayout.setExpandedTitleColor(textColor
                                            ?: android.R.color.white)
                                    currentColor= vibrantColor?:Color.WHITE
                                    val drawableColorChange = DrawableColorChange(this@CommercialDetailActivity)
                                    drawableColorChange.setDrawable((appBar.getChildAt(1) as ImageView).drawable)
                                    drawableColorChange.setColor(vibrantColor
                                            ?: android.R.color.white)
                                    (appBar.getChildAt(1) as ImageButton).setImageDrawable(drawableColorChange.colorChangedDrawable)
                                    for (i in 0 until appBar.menu.size()) {
                                        appBar.menu.getItem(i).icon = drawableColorChange.changeColorByColor(appBar.menu.getItem(i).icon, vibrantColor)
                                    }
                                })
                            }
                        }
                    }
                }
            })

            vpAlbum.post {
                if (albumBannerAdapter.weakCache.get(0) is PhotoFragment) {
                    (albumBannerAdapter.weakCache.get(0) as PhotoFragment).onImageCompleteOnceListener = PhotoFragment.onImageCompleteOnceListener {
                        Palette.generateAsync(((albumBannerAdapter.weakCache.get(0) as PhotoFragment).view?.find<ImageView>(R.id.iv_album_item)?.image as BitmapDrawable).bitmap
                                , { palette ->
                            textColor = findProperColor(palette,0)
                            vibrantColor = findProperColor(palette,1)
                            currentColor= vibrantColor?:Color.WHITE
                            collapsingToolbarLayout.setExpandedTitleColor(textColor
                                    ?: android.R.color.white)
                            val drawableColorChange = DrawableColorChange(this@CommercialDetailActivity)
                            drawableColorChange.setDrawable((appBar.getChildAt(1) as ImageView).drawable)
                            drawableColorChange.setColor(vibrantColor ?: android.R.color.white)
                            (appBar.getChildAt(1) as ImageButton).setImageDrawable(drawableColorChange.colorChangedDrawable)
                            for (i in 0 until appBar.menu.size()) {
                                appBar.menu.getItem(i).icon = drawableColorChange.changeColorByColor(appBar.menu.getItem(i).icon, vibrantColor)
                            }
                        })
                    }
                }
            }
        } else {
            hasPhoto=false
            appbarBanner.post{
                startPostponedEnterTransition()
            }
            appbarBanner.setExpanded(false)
        }


    }

    override fun popReviewList(reviewBeanList: List<ReviewBeanWrapper>) {
        reviewListAdapter.addNewItems(reviewBeanList)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        commercialDetailPresenter.configMap(googleMap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        commercialDetailPresenter.handleActivityResult(requestCode, resultCode, data, this)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onNewCommentPublish(event: UploadCompleteEvent.CommentPublish) {
        if (event.success) {
            commercialDetailPresenter.onNewCommentPublish(event.addScore)
            setResult(Activity.RESULT_OK)
        }
        EventBus.getDefault().removeStickyEvent(event)
    }

    override fun showSnackBar(info: String?) {
        info?.let {
            contentView?.let {
                snackbar(it, info)
            }
        }
    }

    override fun setRating(score:Float,count:Int){
        rbReviews.rating = score
        rbReviewsBottom.rating = score
        if (count != 0) {
            tvReviews.text = count.toString() + if (count == 1) " "+getString(R.string.review) else " "+getString(R.string.reviews)
            tvReviewsBottom.text = tvReviews.text
        } else {
            tvReviewsBottom.text="0 "+getString(R.string.review)
        }
    }

    private fun findProperColor(palette:Palette,type:Int):Int{
        if (palette.vibrantSwatch!=null) {
            if (type == 0) {
                return palette.vibrantSwatch?.bodyTextColor?:0
            } else {
                return palette.vibrantSwatch?.rgb?:0
            }
        }
        if (palette.lightVibrantSwatch!=null) {
            if (type == 0) {
                return palette.lightVibrantSwatch?.bodyTextColor?:0
            } else {
                return palette.lightVibrantSwatch?.rgb?:0
            }
        }
        if (palette.darkVibrantSwatch!=null) {
            if (type == 0) {
                return palette.darkVibrantSwatch?.bodyTextColor?:0
            } else {
                return palette.darkVibrantSwatch?.rgb?:0
            }
        }
        return Color.WHITE
    }
}
