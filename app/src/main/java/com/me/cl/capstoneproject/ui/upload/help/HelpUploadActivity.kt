package com.me.cl.capstoneproject.ui.upload.help

import android.animation.Animator
import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.GridLayoutManager
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.me.cl.capstoneproject.MyApplication
import com.me.cl.capstoneproject.R
import com.me.cl.capstoneproject.adapter.recyclerview.PictureUploadAdapter
import com.me.cl.capstoneproject.adapter.spinner.HintAdapter
import com.me.cl.capstoneproject.base.BaseActivity
import com.me.cl.capstoneproject.base.Constant
import com.me.cl.capstoneproject.base.Constant.MainPage.*
import com.me.cl.capstoneproject.bean.MyLocation
import com.me.cl.capstoneproject.bean.Photo
import com.me.cl.capstoneproject.event.AnimEvent
import com.me.cl.capstoneproject.event.UIStateEvent
import com.me.cl.capstoneproject.ui.upload.commercial.di.CommercialUploadModule
import com.me.cl.capstoneproject.ui.upload.free.di.DaggerFreeUploadComponent
import com.me.cl.capstoneproject.ui.upload.free.di.FreeUploadModule
import com.me.cl.capstoneproject.ui.upload.help.di.HelpUploadModule
import com.me.cl.capstoneproject.ui.upload.help.mvp.HelpUploadPresenter
import com.me.cl.capstoneproject.ui.upload.help.mvp.HelpUploadView
import com.me.cl.capstoneproject.widget.RecyclerViewSpacesItemDecoration
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle
import com.trello.rxlifecycle2.LifecycleProvider
import kotlinx.android.synthetic.main.activity_help_upload.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject



class HelpUploadActivity : BaseActivity(), HelpUploadView {


    override var photoModel: ArrayList<Photo?>?
        get() =  pictureUploadAdapter.photoList
        set(photos) {
            if (photos == null) {
            pictureUploadAdapter.replaceAll(ArrayList<Photo>())
        } else {
            pictureUploadAdapter.replaceAll(photos)
        }}


    @Inject
     lateinit var presenter: HelpUploadPresenter<HelpUploadView>

    @Inject
     lateinit var pictureUploadAdapter: PictureUploadAdapter

    val provider: LifecycleProvider<Lifecycle.Event> = AndroidLifecycle.createLifecycleProvider(this)

    internal var mX: Int = 0
    internal var mY: Int = 0
    internal var radius: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_upload)
        DaggerFreeUploadComponent.builder().applicationComponent((application as MyApplication).applicationComponent)
                .commercialUploadModule(CommercialUploadModule(this))
                .freeUploadModule(FreeUploadModule(this)).build()
                .HelpUploadComponent(HelpUploadModule(this)).inject(this)
        setSupportActionBar(tb_upload)
        tb_upload.setNavigationOnClickListener { onBackPressed() }
        tb_upload.title = getString(R.string.help)
        configRevealAnim()
        presenter.manage(this)
        presenter.init(this,savedInstanceState,provider)
        initPhotoRecyclerview()
        initCitySpinner()
        initEndSpinner()
        addListener()
    }

    private fun addListener() {
        cb_use_current_location.setOnCheckedChangeListener({ buttonView, isChecked -> presenter.onCurrentLocationChecked(isChecked, this) })
    }

    private fun configRevealAnim() {
        root_view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                root_view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mX = intent.getIntExtra(BUNDLE_KEY_FAB_X, 0)
                mY = intent.getIntExtra(BUNDLE_KEY_FAB_Y, 0)
                radius = intent.getIntExtra(BUNDLE_KEY_RADIUS, 0)
                root_view.visibility = View.INVISIBLE
                val delay = 300
                val animator = createRevealAnimator(false, mX, mY, radius, delay + 50)
                animator.start()
                EventBus.getDefault().postSticky(AnimEvent(delay, R.id.fab_create, false))
            }
        })
    }

    private fun createRevealAnimator(reversed: Boolean, x: Int, y: Int, initRadius: Int, startDelay: Int): Animator {
        val hypot = Math.hypot(root_view.height.toDouble(), root_view.width.toDouble()).toFloat()
        val startRadius = if (reversed) hypot else initRadius.toFloat()
        val endRadius = if (reversed) radius.toFloat() else hypot

        val animator = ViewAnimationUtils.createCircularReveal(
                root_view, x, y,
                startRadius,
                endRadius
        )
        animator.duration = 700
        animator.startDelay = startDelay.toLong()
        animator.interpolator = AccelerateDecelerateInterpolator()

        if (reversed) {
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    animator.removeListener(this)
                    EventBus.getDefault().postSticky(AnimEvent(300, R.id.fab_create, true))
                    root_view.visibility = View.INVISIBLE
                    finish()
                    overridePendingTransition(0, 0)
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
        } else {
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    root_view.visibility = View.VISIBLE
                    ns_upload.visibility = View.INVISIBLE
                    Handler().postDelayed({ YoYo.with(Techniques.Landing).duration(500).onStart { animator1 -> ns_upload.visibility = View.VISIBLE }.playOn(ns_upload) }, 300)
                    for (i in 0 until tb_upload.childCount) {
                        if (tb_upload.getChildAt(i) is TextView && (tb_upload.getChildAt(i) as TextView).text == tb_upload.title) {
                            YoYo.with(Techniques.RubberBand).duration(500).delay(600).playOn(tb_upload.getChildAt(i))
                            //                        break;
                        }
                        if (tb_upload.getChildAt(i) is ImageView) {
                            YoYo.with(Techniques.RubberBand).duration(400).delay(700).playOn(tb_upload.getChildAt(i))
                        }
                    }
                }

                override fun onAnimationEnd(animation: Animator) {
                    animator.removeListener(this)
                    displayAdd()
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
        }
        return animator
    }

    private fun initPhotoRecyclerview() {
        rv_add.apply {
            layoutManager = GridLayoutManager(this@HelpUploadActivity, 4)
            setHasFixedSize(true)
            adapter = pictureUploadAdapter
            val stringIntegerHashMap = HashMap<String, Int>()
            stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION, 50)
            val recyclerViewSpacesItemDecoration = RecyclerViewSpacesItemDecoration(stringIntegerHashMap)
            addItemDecoration(recyclerViewSpacesItemDecoration)
        }
    }

    private fun initCitySpinner() {
        val cityList = listOf(*resources.getStringArray(R.array.city_list))
        sp_city.adapter = HintAdapter(this, android.R.layout.simple_spinner_item, cityList).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        sp_city.setSelection(cityList.size - 1)
    }

    private fun initEndSpinner() {
        resources.getStringArray(R.array.end_time).toList().let {
            sp_end_time.adapter = HintAdapter(this, android.R.layout.simple_spinner_item, it).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            sp_end_time.setSelection(it.size - 1)
        }

    }

    private fun getScrollY(top: Int): Int {
        return if (top - ns_upload.height / 3f >= 0) (top - ns_upload.height / 3f).toInt() else 0
    }

    override fun onStart() {
        super.onStart()
        presenter.handleStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        adView2.destroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.handleActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.upload_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.upload -> {
                presenter.uploadForm(this)
                return true
            }
            else -> return false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onBackPressed() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(this.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        val animator = createRevealAnimator(true, mX, mY, radius, 0)
        animator.start()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSaveScrollState(event: UIStateEvent.SaveScrollState) {
        presenter.saveScrollState(ns_upload.scrollY)
    }


    private fun displayAdd() {
//        MobileAds.initialize(this, getString(R.string.ad_test_app_id))
        MobileAds.initialize(this, getString(R.string.ad_app_id))
        val adRequest = AdRequest.Builder().build()
        adView2.loadAd(adRequest)
    }


    override fun popMyAddress(myLocation: MyLocation?) {
        tvl_address.editText?.setText(myLocation?.address)
        tvl_zip_code.editText?.setText(myLocation?.zipCode)
        var isSelected=false

        for (i in 0 until sp_city.adapter.count) {
            if (!sp_city.adapter.getItem(i).toString().isEmpty()) {
                if (sp_city.adapter.getItem(i).toString() == myLocation?.city) {
                    sp_city.setSelection(i)
                    isSelected = true
                    break
                }
            }
        }
        if (!isSelected) {
            sp_city.setSelection(sp_city.adapter.count)
        }
    }

    override fun addPhotos(photos: ArrayList<Photo?>?) {
        pictureUploadAdapter.addPhotos(photos)
    }

    override fun replaceAllPhotos(photos: ArrayList<Photo?>?) {
        pictureUploadAdapter.replaceAll(photos)
    }

    override fun collectFormDataFromUI(form: HashMap<String, Any>?) {
        form?.run {
            put(Constant.UploadPage.MAP_KEY_TITLE, tvl_service_name.editText?.text.toString())
            put(Constant.UploadPage.MAP_KEY_PHONE, tvl_phone.editText?.text.toString())
            put(Constant.UploadPage.MAP_KEY_WECHAT, tvl_wechat.editText?.text.toString())
            put(Constant.UploadPage.MAP_KEY_SUMMARY, et_description.text.toString())
            put(Constant.UploadPage.MAP_KEY_STREET, tvl_address.editText?.text.toString())
            put(Constant.UploadPage.MAP_KEY_CITY, sp_city.selectedItem.toString())
            put(Constant.UploadPage.MAP_KEY_END, sp_end_time.selectedItem.toString())
            put(Constant.UploadPage.MAP_KEY_STATE, sp_state.selectedItem.toString())
            put(Constant.UploadPage.MAP_KEY_ZIP, tvl_zip_code.editText?.text.toString())
            put(Constant.UploadPage.MAP_KEY_PHOTOS, pictureUploadAdapter.photoList)
        }

    }

    override fun showAlertForFormField(textFieldFlag: Int?) {
        when (textFieldFlag) {
            Constant.UploadPage.FLAG_TITLE_TEXT_FIELD ->
            {
                tvl_service_name.error = getString(R.string.please_input_service_name)
                tvl_service_name.requestFocus()
                ns_upload.scrollTo(0,getScrollY(tvl_service_name.top))
            }
            Constant.UploadPage.FLAG_ADDRESS_TEXT_FIELD ->
            {
                tvl_address.error = getString(R.string.invalid_address)
                tvl_address.requestFocus()
                ns_upload.scrollTo(0,getScrollY(tvl_address.top))
            }
            Constant.UploadPage.FLAG_AVERAGE_TEXT_FIELD -> {
            }
            Constant.UploadPage.FLAG_PHONE_TEXT_FIELD ->
            {
                tvl_phone.error = getString(R.string.please_input_a_valid_phone_number)

                tvl_phone.requestFocus()
                ns_upload.scrollTo(0,getScrollY(tvl_phone.top))
            }
            Constant.UploadPage.FLAG_WECHAT_TEXT_FIELD -> {
            }
            Constant.UploadPage.FLAG_ZIP_TEXT_FIELD ->
            {
                tvl_zip_code.error = getString(R.string.invalid)

                tvl_zip_code.requestFocus()
                ns_upload.scrollTo(0,getScrollY(tvl_zip_code.top))
            }
            Constant.UploadPage.FLAG_SUMMARY_TEXT_FIELD -> {
                MaterialDialog.Builder(this)
                        .title(getString(R.string.input_invalid))
                        .content(getString(R.string.please_write_some_description))
                        .positiveText(getString(R.string.positive))
                        .show()
                et_description.requestFocus()
                ns_upload.scrollTo(0,getScrollY(et_description.top))
            }
            Constant.UploadPage.FLAG_TYPE_FIELD -> MaterialDialog.Builder(this)
                    .title(getString(R.string.input_invalid))
                    .content(getString(R.string.please_select_a_type))
                    .positiveText(getString(R.string.positive))
                    .show()
            Constant.UploadPage.FLAG_CITY_FIELD -> {MaterialDialog.Builder(this)
                    .title(getString(R.string.input_invalid))
                    .content(getString(R.string.please_select_a_city))
                    .positiveText(getString(R.string.positive))
                    .show()
                    ns_upload.scrollTo(0,getScrollY(sp_city.top))}
            Constant.UploadPage.FLAG_END_TIME ->{
                MaterialDialog.Builder(this)
                        .title(getString(R.string.input_invalid))
                        .content(getString(R.string.please_select_end_time))
                        .positiveText(getString(R.string.positive))
                        .show()
                ns_upload.scrollTo(0,getScrollY(sp_end_time.top))
            }

        }
    }

    override fun restoreAllTextInputError() {
        tvl_service_name.isErrorEnabled = false
        tvl_address.isErrorEnabled = false
        tvl_zip_code.isErrorEnabled = false
        tvl_phone.isErrorEnabled=false

    }

    override fun showAlertDialog(title: String?, content: String?, button: String?, singleButtonCallback: MaterialDialog.SingleButtonCallback?) {
        super.showAlertDialog(title,content,button,singleButtonCallback,null)
    }

    override fun checkedFetchLocation(checked: Boolean?) {
        checked?.let { cb_use_current_location.isChecked = it }
    }

    override fun setCheckBoxState(checked: Boolean?) {
        cb_use_current_location.isChecked=checked?:false
    }
    override fun scrollTo(x: Int?, y: Int?) {
        ns_upload.postDelayed({ ns_upload.scrollTo(x?:0,y?:0) },300 )
    }

    override fun controlIndeterminateProgress(show: Boolean?, title: String?, content: String?, onCancelListener: DialogInterface.OnCancelListener?) {
        super.controlIndeterminateProgress(show?:false,title,content, onCancelListener)
    }
}
