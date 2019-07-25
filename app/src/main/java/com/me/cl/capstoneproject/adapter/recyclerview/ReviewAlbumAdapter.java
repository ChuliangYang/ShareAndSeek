package com.me.cl.capstoneproject.adapter.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.me.cl.capstoneproject.R;
import com.me.cl.capstoneproject.bean.Photo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by CL on 11/24/17.
 */

public class ReviewAlbumAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<Photo> photoBeanList;

    public ReviewAlbumAdapter(Context context, ArrayList<Photo>  photoBeanList) {
        this.context = context;
        this.photoBeanList = photoBeanList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PhotoViewHolder) {
            PhotoViewHolder viewHolder = (PhotoViewHolder) holder;
            Glide.with(context).load(photoBeanList.get(position).getDownloadUri()).into(viewHolder.ivPhoto);
            viewHolder.ivPhoto.setOnClickListener(v -> {
                ArrayList<ImageItem> imageItemList=new ArrayList<>();
                for (Photo photo: photoBeanList) {
                    ImageItem imageItem=new ImageItem();
                    imageItem.name=photo.name;
                    imageItem.path=photo.getDownloadUri();
                    imageItemList.add(imageItem);
                }
                Intent intentPreview = new Intent(context, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imageItemList);
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_EXTEND_SHOW_DELETE, false);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                intentPreview.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentPreview);
            });
        }
    }

    @Override
    public int getItemCount() {
        return photoBeanList != null ? photoBeanList.size() : 0;
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_photo)
        ImageView ivPhoto;
        public PhotoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
