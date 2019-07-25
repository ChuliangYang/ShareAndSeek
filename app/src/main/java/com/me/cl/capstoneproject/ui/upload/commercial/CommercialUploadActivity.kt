package com.me.cl.capstoneproject.ui.upload.commercial

import android.animation.Animator
import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TextInputLayout
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView

import com.afollestad.materialdialogs.MaterialDialog
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.me.cl.capstoneproject.MyApplication
import com.me.cl.capstoneproject.R
import com.me.cl.capstoneproject.adapter.recyclerview.PictureUploadAdapter
import com.me.cl.capstoneproject.adapter.spinner.HintAdapter
import com.me.cl.capstoneproject.base.Constant
import com.me.cl.capstoneproject.bean.MyLocation
import com.me.cl.capstoneproject.bean.Photo
import com.me.cl.capstoneproject.event.AnimEvent
import com.me.cl.capstoneproject.event.UIStateEvent
import com.me.cl.capstoneproject.ui.upload.commercial.di.CommercialUploadModule
import com.me.cl.capstoneproject.ui.upload.commercial.di.DaggerCommercialUploadComponent
import com.me.cl.capstoneproject.ui.upload.commercial.mvp.CommercialUploadPresenter
import com.me.cl.capstoneproject.ui.upload.commercial.mvp.CommercialUploadView
import com.me.cl.capstoneproject.widget.RecyclerViewSpacesItemDecoration

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap

import javax.inject.Inject

import butterknife.BindView
import butterknife.ButterKnife

import com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_FAB_X
import com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_FAB_Y
import com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_RADIUS
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle
import com.trello.rxlifecycle2.LifecycleProvider

/**
 * Created by CL on 11/15/17.
 */

class CommercialUploadActivity : AppCompatActivity(), CommercialUploadView {

    @BindView(R.id.tb_upload)
    lateinit var tbUpload: Toolbar
    @BindView(R.id.tvl_service_name)
    lateinit var tvlServiceName: TextInputLayout
    @BindView(R.id.sp_state)
    lateinit var spState: Spinner
    @BindView(R.id.sp_type)
    lateinit var spType: Spinner
    @BindView(R.id.tv_dollar)
    lateinit var tvDollar: TextView
    @BindView(R.id.tvl_address)
    lateinit var tvlAddress: TextInputLayout
    @BindView(R.id.sp_city)
    lateinit var spCity: Spinner
    @BindView(R.id.cb_use_current_location)
    lateinit var cbUseCurrentLocation: CheckBox
    @BindView(R.id.tvl_average)
    lateinit var tvlAverage: TextInputLayout
    @BindView(R.id.tvl_zip_code)
    lateinit var tvlZipCode: TextInputLayout
    @BindView(R.id.tvl_phone)
    lateinit var tvlPhone: TextInputLayout
    @BindView(R.id.tvl_wechat)
    lateinit var tvlWechat: TextInputLayout
    @BindView(R.id.rv_add)
    lateinit var rvAdd: RecyclerView
    @BindView(R.id.et_description)
    lateinit var etDescription: EditText
    @BindView(R.id.adView2)
    lateinit var adView: AdView
    @BindView(R.id.root_view)
    lateinit var rootView: View
    @BindView(R.id.ns_upload)
    lateinit var nsUpload: NestedScrollView
    @BindView(R.id.cl_upload)
    lateinit var clUpload: ConstraintLayout

    @Inject
    lateinit var presenter: CommercialUploadPresenter<CommercialUploadView>
    @Inject
    lateinit var pictureUploadAdapter: PictureUploadAdapter
    @Inject
    lateinit var activity: Activity

    val provider: LifecycleProvider<Lifecycle.Event> = AndroidLifecycle.createLifecycleProvider(this)


    var IndeterminateProgress: MaterialDialog?=null

     var mX: Int = 0
     var mY: Int = 0
     var radius: Int = 0

    override var photoModel: ArrayList<Photo?>?
        get() = pictureUploadAdapter.photoList
        set(photos) {
            var photos = photos
            if (photos == null) {
                photos = ArrayList()
            }
            pictureUploadAdapter.replaceAll(photos)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commercial_upload)
        ButterKnife.bind(this)
        if (savedInstanceState == null) {
            configRevealAnim()
        }
        DaggerCommercialUploadComponent.builder().applicationComponent((application as MyApplication).applicationComponent)
                .commercialUploadModule(CommercialUploadModule(this)).build().inject(this)
        setSupportActionBar(tbUpload)
        tbUpload.setNavigationOnClickListener { v -> onBackPressed() }
        tbUpload.title = getString(R.string.commercial)
        presenter.manage(this)
        presenter.init(this, savedInstanceState,provider)
        initPhotoRecyclerview()
        initCitySpinner()
        initTypeSpinner()
        addListener()
    }

    private fun configRevealAnim() {
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mX = intent.getIntExtra(BUNDLE_KEY_FAB_X, 0)
                mY = intent.getIntExtra(BUNDLE_KEY_FAB_Y, 0)
                radius = intent.getIntExtra(BUNDLE_KEY_RADIUS, 0)
                rootView.visibility = View.INVISIBLE
                val delay = 300
                val animator = createRevealAnimator(false, mX, mY, radius, delay + 50)
                animator.start()
                EventBus.getDefault().postSticky(AnimEvent(delay, R.id.fab_create, false))
            }
        })
    }

    private fun addListener() {
        cbUseCurrentLocation.setOnCheckedChangeListener { buttonView, isChecked -> presenter.onCurrentLocationChecked(isChecked, this) }
    }

    private fun initPhotoRecyclerview() {
        rvAdd.layoutManager = GridLayoutManager(this, 4)
        rvAdd.setHasFixedSize(true)
        rvAdd.adapter = pictureUploadAdapter
        val stringIntegerHashMap = HashMap<String, Int>()
        stringIntegerHashMap[RecyclerViewSpacesItemDecoration.TOP_DECORATION] = 50
        val recyclerViewSpacesItemDecoration = RecyclerViewSpacesItemDecoration(stringIntegerHashMap)
        rvAdd.addItemDecoration(recyclerViewSpacesItemDecoration)
    }

    private fun initTypeSpinner() {
        val typeList = Arrays.asList(*resources.getStringArray(R.array.commercial_categories))
        val typeHintAdapter = HintAdapter(this, android.R.layout.simple_spinner_item, typeList)
        typeHintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spType.adapter = typeHintAdapter
        spType.setSelection(typeList.size - 1)

    }

    private fun initCitySpinner() {
        val cityList = Arrays.asList(*resources.getStringArray(R.array.city_list))
        val cityHintAdapter = HintAdapter(this, android.R.layout.simple_spinner_item, cityList)
        cityHintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCity.adapter = cityHintAdapter
        spCity.setSelection(cityList.size - 1)
    }

    private fun getScrollY(top: Int): Int {
        return if (top - nsUpload.height / 3f >= 0) (top - nsUpload.height / 3f).toInt() else 0
    }

    private fun createRevealAnimator(reversed: Boolean, x: Int, y: Int, initRadius: Int, startDelay: Int): Animator {
        val hypot = Math.hypot(rootView.height.toDouble(), rootView.width.toDouble()).toFloat()
        val startRadius:Float = if (reversed) hypot else initRadius.toFloat()
        val endRadius:Float = if (reversed) radius.toFloat() else hypot

        val animator = ViewAnimationUtils.createCircularReveal(
                rootView, x, y,
                startRadius,
                endRadius )
        animator.duration = 700
        animator.startDelay = startDelay.toLong()
        animator.interpolator = AccelerateDecelerateInterpolator()

        if (reversed) {
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    animator.removeListener(this)
                    EventBus.getDefault().postSticky(AnimEvent(300, R.id.fab_create, true))
                    rootView.visibility = View.INVISIBLE
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
                    rootView.visibility = View.VISIBLE
                    nsUpload.visibility = View.INVISIBLE
                    Handler().postDelayed({ YoYo.with(Techniques.Landing).duration(500).onStart { animator1 -> nsUpload.visibility = View.VISIBLE }.playOn(nsUpload) }, 300)
                    for (i in 0 until tbUpload.childCount) {
                        if (tbUpload.getChildAt(i) is TextView && (tbUpload.getChildAt(i) as TextView).text == tbUpload.title) {
                            YoYo.with(Techniques.RubberBand).duration(500).delay(600).playOn(tbUpload.getChildAt(i))
                            //                        break;
                        }
                        if (tbUpload.getChildAt(i) is ImageView) {
                            YoYo.with(Techniques.RubberBand).duration(400).delay(700).playOn(tbUpload.getChildAt(i))
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
        adView.destroy()
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onBackPressed() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(this@CommercialUploadActivity.currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        val animator = createRevealAnimator(true, mX, mY, radius, 0)
        animator.start()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun displayAdd() {
        //        MobileAds.initialize(this, getString(R.string.ad_test_app_id));
        MobileAds.initialize(this, getString(R.string.ad_app_id))
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSaveScrollState(event: UIStateEvent.SaveScrollState) {
        presenter.saveScrollState(nsUpload.scrollY)
    }

    override fun popMyAddress(myLocation: MyLocation?) {
        tvlAddress.editText?.setText(myLocation?.address)
        tvlZipCode.editText?.setText(myLocation?.zipCode)
        var isSelected = false
        for (i in 0 until spCity.adapter.count) {
            if (!TextUtils.isEmpty(spCity.adapter.getItem(i).toString())) {
                if (spCity.adapter.getItem(i).toString() == myLocation?.city) {
                    spCity.setSelection(i)
                    isSelected = true
                    break
                }
            }
        }
        if (!isSelected) {
            spCity.setSelection(spCity.adapter.count)
        }
    }

    override fun addPhotos(photos: ArrayList<Photo?>?) {
        pictureUploadAdapter.addPhotos(photos)
    }

    override fun replaceAllPhotos(photos: ArrayList<Photo?>?) {
        pictureUploadAdapter.replaceAll(photos)
    }

    override fun collectFormDataFromUI(form: HashMap<String, Any>?) {
        if (form != null) {
            form[Constant.UploadPage.MAP_KEY_AVERAGE] = tvlAverage.editText?.text.toString()
            form[Constant.UploadPage.MAP_KEY_CATEGORY] = spType.selectedItem.toString()
            form[Constant.UploadPage.MAP_KEY_TITLE] = tvlServiceName.editText?.text.toString()
            form[Constant.UploadPage.MAP_KEY_PHONE] = tvlPhone.editText?.text.toString()
            form[Constant.UploadPage.MAP_KEY_WECHAT] = tvlWechat.editText?.text.toString()
            form[Constant.UploadPage.MAP_KEY_SUMMARY] = etDescription.text.toString()
            form[Constant.UploadPage.MAP_KEY_STREET] = tvlAddress.editText?.text.toString()
            form[Constant.UploadPage.MAP_KEY_CITY] = spCity.selectedItem.toString()
            form[Constant.UploadPage.MAP_KEY_STATE] = spState.selectedItem.toString()
            form[Constant.UploadPage.MAP_KEY_ZIP] = tvlZipCode.editText?.text.toString()
            form[Constant.UploadPage.MAP_KEY_PHOTOS] = pictureUploadAdapter.photoList
        }
    }


    override fun showAlertForFormField(textFieldFlag: Int?) {
        when (textFieldFlag) {
            Constant.UploadPage.FLAG_TITLE_TEXT_FIELD -> {
                tvlServiceName.error = getString(R.string.please_input_service_name)
                tvlServiceName.requestFocus()
                nsUpload.scrollTo(0, getScrollY(tvlServiceName.top))
            }
            Constant.UploadPage.FLAG_ADDRESS_TEXT_FIELD -> {
                tvlAddress.error = getString(R.string.invalid_address)
                tvlAddress.requestFocus()

                nsUpload.scrollTo(0, getScrollY(tvlAddress.top))
            }
            Constant.UploadPage.FLAG_AVERAGE_TEXT_FIELD -> {
            }
            Constant.UploadPage.FLAG_PHONE_TEXT_FIELD -> {
                tvlPhone.error = getString(R.string.please_input_a_valid_phone_number)
                tvlPhone.requestFocus()
                nsUpload.scrollTo(0, getScrollY(tvlPhone.top))
            }
            Constant.UploadPage.FLAG_WECHAT_TEXT_FIELD -> {
            }
            Constant.UploadPage.FLAG_ZIP_TEXT_FIELD -> {
                tvlZipCode.error = getString(R.string.invalid)
                tvlZipCode.requestFocus()
                nsUpload.scrollTo(0, getScrollY(tvlZipCode.top))
            }
            Constant.UploadPage.FLAG_SUMMARY_TEXT_FIELD -> {
                MaterialDialog.Builder(this)
                        .title(getString(R.string.input_invalid))
                        .content(getString(R.string.please_write_some_description))
                        .positiveText(getString(R.string.positive))
                        .show()
                etDescription.requestFocus()
                nsUpload.scrollTo(0, getScrollY(etDescription.top))
            }
            Constant.UploadPage.FLAG_TYPE_FIELD -> {
                MaterialDialog.Builder(this)
                        .title(getString(R.string.input_invalid))
                        .content(getString(R.string.please_select_a_type))
                        .positiveText(getString(R.string.positive))
                        .show()
                nsUpload.scrollTo(0, 0)
            }
            Constant.UploadPage.FLAG_CITY_FIELD -> {
                MaterialDialog.Builder(this)
                        .title(getString(R.string.input_invalid))
                        .content(getString(R.string.please_select_a_city))
                        .positiveText(getString(R.string.positive))
                        .show()
                nsUpload.scrollTo(0, getScrollY(spCity.top))
            }
        }
    }

    override fun restoreAllTextInputError() {
        tvlServiceName.isErrorEnabled = false
        tvlAddress.isErrorEnabled = false
        tvlPhone.isErrorEnabled = false
        tvlZipCode.isErrorEnabled = false
    }

    override fun showAlertDialog(title: String?, content: String?, button: String?, singleButtonCallback: MaterialDialog.SingleButtonCallback?) {
        MaterialDialog.Builder(this)
                .title(title?:"")
                .content(content?:"")
                .positiveText(button?:"").apply {
                    singleButtonCallback?.let {
                        onPositive(singleButtonCallback)
                    }
                }.show()
    }

    override fun checkedFetchLocation(checked: Boolean?) {
        checked?.let {
            cbUseCurrentLocation.isChecked = checked
        }
    }

    override fun controlIndeterminateProgress(show: Boolean?, title: String?, content: String?, onCancelListener: DialogInterface.OnCancelListener?) {
        if (show == true) {
            if (IndeterminateProgress == null) {
                IndeterminateProgress = MaterialDialog.Builder(this).title(title?:"").content(content?:"")
                        .progress(true, 0).canceledOnTouchOutside(false).apply {
                            onCancelListener?.let {
                                cancelListener(onCancelListener)
                            }
                        }.show()
            } else {
                IndeterminateProgress?.show()
            }

        } else {
            IndeterminateProgress?.run {
                if (isShowing) {
                    dismiss()
                }
            }

        }
    }

    override fun setCheckBoxState(checked: Boolean?) {
        cbUseCurrentLocation.isChecked = checked?:false
    }

    override fun scrollTo(x: Int?, y: Int?) {
        nsUpload.postDelayed({ nsUpload.scrollTo(x?:0, y?:0) }, 300)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val middle: Int
        middle = mX
        mX = mY
        mY = middle
    }

}
