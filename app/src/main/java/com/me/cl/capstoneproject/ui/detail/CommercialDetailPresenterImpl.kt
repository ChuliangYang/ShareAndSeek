package com.me.cl.capstoneproject.ui.detail

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Lifecycle
import android.content.Intent
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.lzy.imagepicker.ImagePicker
import com.lzy.imagepicker.bean.ImageItem
import com.me.cl.capstoneproject.R
import com.me.cl.capstoneproject.base.Constant.DetailPage.MAP_KEY_COMMENT_DIALOG_FRAGMENT
import com.me.cl.capstoneproject.base.Constant.MainPage.REQUEST_CODE_COMMENT_PUBLISH_SIGN_IN
import com.me.cl.capstoneproject.base.Constant.UploadPage.PICK_UP_PHOTO_REQUEST_CODE
import com.me.cl.capstoneproject.base.Constant.UploadPage.REQUEST_CODE_PREVIEW
import com.me.cl.capstoneproject.bean.Photo
import com.me.cl.capstoneproject.bean.ReviewBeanWrapper
import com.me.cl.capstoneproject.event.LoginEvent
import com.me.cl.capstoneproject.ui.detail.mvp.CommercialDetailInteractor
import com.me.cl.capstoneproject.ui.detail.mvp.CommercialDetailPresenter
import com.me.cl.capstoneproject.ui.detail.mvp.CommercialDetailView
import com.me.cl.capstoneproject.ui.detail.review.CommentDialogFragment
import com.me.cl.capstoneproject.util.BaseUtils
import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

/**
 * Created by CL on 11/22/17.
 */

class CommercialDetailPresenterImpl @Inject
constructor(val interactor: CommercialDetailInteractor) : CommercialDetailPresenter<CommercialDetailView> {


    lateinit var view: CommercialDetailView
    var provider: LifecycleProvider<Lifecycle.Event>?=null


    override fun manage(view: CommercialDetailView) {
        this.view = view
    }

    override fun destory() {

    }
    override fun init() {

    }
    override fun init(provider: LifecycleProvider<Lifecycle.Event>?) {
        this.provider=provider
        configBaseInfo()
        configReviewList()
    }

    override fun configBaseInfo() {
        view.popBaseInfo(interactor.commercialBaseInfo)
    }

    override fun configReviewList() {
        interactor.getReviews(interactor.lastKey)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .compose<Any?>(provider?.bindUntilEvent<Lifecycle.Event>(Lifecycle.Event.ON_DESTROY))
                .subscribe({ o -> view.popReviewList(o as List<ReviewBeanWrapper>) }) { throwable -> Timber.e(throwable.toString()) }

    }

    override fun configMap(googleMap: GoogleMap) {
        if (interactor.commercialBaseInfo != null && interactor.commercialBaseInfo.location != null) {
            val target = LatLng(java.lang.Double.parseDouble(interactor.commercialBaseInfo.location.latitude), java.lang.Double.parseDouble(interactor.commercialBaseInfo.location.longitude))
            googleMap.addMarker(MarkerOptions().position(target).title("Target"))
            val cameraPosition = CameraPosition.Builder().target(target).tilt(0f).zoom(16f).build()
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        }
    }

    override fun onCommentClick(fragmentManager: FragmentManager, activity: Activity) {
        if (BaseUtils.System.loginCheck(activity, REQUEST_CODE_COMMENT_PUBLISH_SIGN_IN)) {
            val commentDialogFragment = CommentDialogFragment.newInstance(interactor.commercialId)
            val commentDialogFragmentWeak = WeakReference(commentDialogFragment)
            commentDialogFragment.show(fragmentManager, "comment")
            interactor.saveToCache(MAP_KEY_COMMENT_DIALOG_FRAGMENT, commentDialogFragmentWeak)
        }

    }

    override fun handleActivityResult(requestCode: Int?, resultCode: Int?, data: Intent?, activity: AppCompatActivity?) {
        var commentDialogFragment: CommentDialogFragment? = null

        if (requestCode == PICK_UP_PHOTO_REQUEST_CODE || requestCode == REQUEST_CODE_PREVIEW) {
            if (interactor.getFromCache(MAP_KEY_COMMENT_DIALOG_FRAGMENT) != null) {
                commentDialogFragment = (interactor.getFromCache(MAP_KEY_COMMENT_DIALOG_FRAGMENT) as WeakReference<CommentDialogFragment>).get()
            }
        }

        if (requestCode == PICK_UP_PHOTO_REQUEST_CODE && resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == PICK_UP_PHOTO_REQUEST_CODE) {
                val imageItems = data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) as ArrayList<ImageItem>
                val photos = ArrayList<Photo>()
                for (imageItem in imageItems) {
                    val photo = Photo()
                    photo.downloadUri = imageItem.path
                    photo.setSize(imageItem.size)
                    photo.addTime = imageItem.addTime
                    photo.height = imageItem.height
                    photo.width = imageItem.width
                    photo.name = imageItem.name
                    photo.path = imageItem.path
                    photo.mimeType = imageItem.mimeType
                    photo.size = imageItem.size
                    photos.add(photo)
                }

                if (photos != null && commentDialogFragment != null) {
                    commentDialogFragment.notifyPhotosAdd(photos)
                }
            }
            return
        } else if (requestCode == REQUEST_CODE_PREVIEW && resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                val images = data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS) as ArrayList<ImageItem>

                val photos = ArrayList<Photo>()
                for (imageItem in images) {
                    val photo = Photo()
                    photo.downloadUri = imageItem.path
                    photo.setSize(imageItem.size)
                    photo.addTime = imageItem.addTime
                    photo.height = imageItem.height
                    photo.width = imageItem.width
                    photo.name = imageItem.name
                    photo.path = imageItem.path
                    photo.mimeType = imageItem.mimeType
                    photo.size = imageItem.size
                    photos.add(photo)
                }
                if (photos != null && commentDialogFragment != null) {
                    commentDialogFragment.notifyPhotosReplace(photos)
                }
            }
            return
        }

        if (requestCode == REQUEST_CODE_COMMENT_PUBLISH_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                EventBus.getDefault().postSticky(LoginEvent.LogIn())
                commentDialogFragment = CommentDialogFragment.newInstance(interactor.commercialId)
                val commentDialogFragmentWeak = WeakReference(commentDialogFragment)
                commentDialogFragment?.show(activity?.supportFragmentManager, "comment")
                interactor.saveToCache(MAP_KEY_COMMENT_DIALOG_FRAGMENT, commentDialogFragmentWeak)
            } else {
                // Sign in failed
                EventBus.getDefault().postSticky(LoginEvent.Failed(""))
                if (response == null) {
                    // User pressed back button
                    view.showSnackBar(activity?.getString(R.string.sign_in_cancelled))
                    return
                }

                if (response.errorCode == ErrorCodes.NO_NETWORK) {
                    view.showSnackBar(activity?.getString(R.string.no_internet_connection))
                    return
                }

                if (response.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    view.showSnackBar(activity?.getString(R.string.unknown_error))
                    return
                }
            }
            view.showSnackBar(activity?.getString(R.string.unknown_sign_in_response))
            return
        }

    }
    override fun onNewCommentPublish(addScore:Float) {
        configReviewList()
        Single.create<Any> {
            it.onSuccess(interactor.caculateRating(addScore,1))
        }.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .compose(provider?.bindToLifecycle())
                .subscribe {
            t ->  view.setRating((t as android.util.Pair<Any,Any>).first as Float, (t.second as Float).toInt())
        }
    }
}
