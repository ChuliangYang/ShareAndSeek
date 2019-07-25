package com.me.cl.capstoneproject.adapter.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.me.cl.capstoneproject.R;
import com.me.cl.capstoneproject.base.Constant;
import com.me.cl.capstoneproject.bean.Photo;
import com.me.cl.capstoneproject.event.UIStateEvent;
import com.me.cl.capstoneproject.widget.SelectDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.me.cl.capstoneproject.base.Constant.UploadPage.PICK_UP_PHOTO_REQUEST_CODE;
import static com.me.cl.capstoneproject.base.Constant.UploadPage.REQUEST_CODE_PREVIEW;

/**
 * Created by CL on 11/16/17.
 */

public class PictureUploadAdapter extends RecyclerView.Adapter {

    public static final int ITEM_TYPE_ADD = 1;
    public static final int ITEM_TYPE_PHOTO = 2;
    public  int MAX_PHOTO_NUM = 8;

    private Context activityContext;
    private ArrayList<Photo> photoList;

    public PictureUploadAdapter(Context activityContext, ArrayList<Photo> photoList) {
        this.activityContext = activityContext;
        if (photoList == null) {
            this.photoList = new ArrayList<>();
        } else {
            this.photoList =photoList;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_PHOTO:
                return new PhotoViewHolder(LayoutInflater.from(activityContext).inflate(R.layout.item_photo, parent, false));
            case ITEM_TYPE_ADD:
                return new AddViewHolder(LayoutInflater.from(activityContext).inflate(R.layout.item_add, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PhotoViewHolder) {
            PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
            Glide.with(activityContext).load(Uri.fromFile(new File(photoList.get(position).getDownloadUri()))).into(photoViewHolder.ivPhoto);
        } else if (holder instanceof AddViewHolder) {
            AddViewHolder addViewHolder= (AddViewHolder) holder;
            if (position >= MAX_PHOTO_NUM) {
                addViewHolder.itemView.setVisibility(View.GONE);
            } else {
                addViewHolder.itemView.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        if (photoList == null) {
            return 1;
        } else {
            if (photoList.size() >= MAX_PHOTO_NUM) {
                return MAX_PHOTO_NUM;
            } else {
                return photoList.size()+1;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (photoList.size() >= MAX_PHOTO_NUM) {
            return ITEM_TYPE_PHOTO;
        } else {
            if (position == getItemCount() - 1) {
                return ITEM_TYPE_ADD;
            } else {
                return ITEM_TYPE_PHOTO;
            }
        }

    }

    public void addPhotos(ArrayList<Photo> photos){
        photoList.addAll(photos);
        notifyDataSetChanged();
    }

    public void replaceAll(ArrayList<Photo> photos){
        if (photoList!=null) {
            photoList.clear();
            photoList.addAll(photos);
            notifyDataSetChanged();
        }
    }

    public ArrayList<Photo> getPhotoList(){
        return photoList;
    }


    public class AddViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_add)
        ImageView ivAdd;

        public AddViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ivAdd.setOnClickListener(v -> {
                if (activityContext instanceof Activity) {
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_PICK);
//                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    ((ActivityQL)context).startActivityForResult(intent, Constant.UploadPage.PICK_UP_PHOTO_REQUEST_CODE);

                    List<String> names = new ArrayList<>();
                    names.add("take photo");
                    names.add("album");
                    showDialog((parent, view, position, id) -> {
                        switch (position) {
                            case 0: // 直接调起相机
                                /**
                                 * 0.4.7 目前直接调起相机不支持裁剪，如果开启裁剪后不会返回图片，请注意，后续版本会解决
                                 *
                                 * 但是当前直接依赖的版本已经解决，考虑到版本改动很少，所以这次没有上传到远程仓库
                                 *
                                 * 如果实在有所需要，请直接下载源码引用。
                                 */
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(MAX_PHOTO_NUM-photoList.size());
                                Intent intent = new Intent(activityContext, ImageGridActivity.class);
                                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                                ((Activity)activityContext).startActivityForResult(intent,PICK_UP_PHOTO_REQUEST_CODE);
                                EventBus.getDefault().post(new UIStateEvent.SaveScrollState());
                                break;
                            case 1:
                                //打开选择,本次允许选择的数量
                                ImagePicker.getInstance().setSelectLimit(MAX_PHOTO_NUM-photoList.size());
                                Intent intent1 = new Intent(activityContext, ImageGridActivity.class);
                            /* 如果需要进入选择的时候显示已经选中的图片，
                             * 详情请查看ImagePickerActivity
                             * */
//                                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
                                ((Activity)activityContext).startActivityForResult(intent1, Constant.UploadPage.PICK_UP_PHOTO_REQUEST_CODE);
                                EventBus.getDefault().post(new UIStateEvent.SaveScrollState());
                                break;
                            default:
                                break;
                        }

                    }, names);
                }
            });
        }
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_photo)
        ImageView ivPhoto;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ivPhoto.setOnClickListener(v -> {
                if (activityContext instanceof Activity) {
                    Activity activity=(Activity)activityContext;
                    Intent intentPreview = new Intent(activity, ImagePreviewDelActivity.class);
                    ArrayList<ImageItem> imageItems=new ArrayList<>();
                    for (Photo photo: photoList) {
                        ImageItem imageItem=new ImageItem();
                        imageItem.addTime=photo.addTime;
                        imageItem.height=photo.height;
                        imageItem.width=photo.width;
                        imageItem.name=photo.name;
                        imageItem.path=photo.path;
                        imageItem.mimeType=photo.mimeType;
                        imageItem.size=photo.size;
                        imageItems.add(imageItem);
                    }
                    intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imageItems);
                    intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, getAdapterPosition());
                    intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                    activity.startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                }
            });
        }
    }

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog((Activity) activityContext, R.style
                .transparentFrameWindowStyle,
                listener, names);
        dialog.show();

        return dialog;
    }
}
