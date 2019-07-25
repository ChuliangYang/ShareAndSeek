package com.me.cl.capstoneproject.adapter.recyclerview

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.lzy.imagepicker.ImagePicker
import com.lzy.imagepicker.bean.ImageItem
import com.lzy.imagepicker.ui.ImagePreviewDelActivity
import com.me.cl.capstoneproject.R
import com.me.cl.capstoneproject.base.Constant.UploadPage.REQUEST_CODE_PREVIEW
import com.me.cl.capstoneproject.bean.FreeItem
import com.me.cl.capstoneproject.bean.HelpItem
import org.jetbrains.anko.intentFor
//import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

/**
 * Created by CL on 12/22/17.
 */
class PhotoAlbumAdapter<P>(var context:Context,var photoList: List<P>?):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return photoList?.size?:0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PhotoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PhotoViewHolder) {
            var photo=photoList?.get(position)
            if (photo is FreeItem.PhotoBean){
                Glide.with(context).load(photo.uri).into(holder.ivPhoto)
            }
            if (photo is HelpItem.PhotoBean) {
                Glide.with(context).load(photo.uri).into(holder.ivPhoto)
            }

            holder.ivPhoto.setOnClickListener {
                if (this@PhotoAlbumAdapter.context is Activity) {
                    val imageItems = ArrayList<ImageItem>()
                    if (photoList?.get(position) is FreeItem.PhotoBean) {
                        photoList?.forEach {
                            var photoBean=it as FreeItem.PhotoBean
                            ImageItem().apply {
                                name = photoBean.photo_last_name
                                path = photoBean.uri
                            }.let {
                                imageItems.add(it)
                            }
                        }

                        (this@PhotoAlbumAdapter.context as Activity).apply {
                            startActivityForResult(intentFor<ImagePreviewDelActivity>(
                                    ImagePicker.EXTRA_IMAGE_ITEMS to imageItems,
                                    ImagePicker.EXTRA_SELECTED_IMAGE_POSITION to position,
                                    ImagePicker.EXTRA_EXTEND_SHOW_DELETE to false,
                                    ImagePicker.EXTRA_FROM_ITEMS to true
                            ),REQUEST_CODE_PREVIEW)
                        }

                    }

                    if (photoList?.get(position) is HelpItem.PhotoBean) {
                        photoList?.forEach {
                            var photoBean=it as HelpItem.PhotoBean
                            ImageItem().apply {
                                name = photoBean.photo_last_name
                                path = photoBean.uri
                            }.let {
                                imageItems.add(it)
                            }
                        }

                        (this@PhotoAlbumAdapter.context as Activity).apply {
                            startActivityForResult(intentFor<ImagePreviewDelActivity>(
                                    ImagePicker.EXTRA_IMAGE_ITEMS to imageItems,
                                    ImagePicker.EXTRA_SELECTED_IMAGE_POSITION to position,
                                    ImagePicker.EXTRA_EXTEND_SHOW_DELETE to false,
                                    ImagePicker.EXTRA_FROM_ITEMS to true
                                    ),REQUEST_CODE_PREVIEW)
                        }
                    }
                }
            }
        }

    }



      class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.iv_photo)
        lateinit var ivPhoto: ImageView

        init {
            ButterKnife.bind(this, itemView)
        }
    }

}