package com.me.cl.capstoneproject.ui.detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.me.cl.capstoneproject.R;
import com.me.cl.capstoneproject.bean.CommercialItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.me.cl.capstoneproject.base.Constant.DetailPage.BUNDLE_KEY_PHOTO_LIST;
import static com.me.cl.capstoneproject.base.Constant.DetailPage.INT_KEY_CURRENT_POSITION;
import static com.me.cl.capstoneproject.base.Constant.UploadPage.REQUEST_CODE_PREVIEW;

/**
 * Created by CL on 11/25/17.
 */

public class PhotoFragment extends Fragment {

    private List<CommercialItem.PhotoBean> photoBeanList;
    private int position;
    @BindView(R.id.iv_album_item)
    ImageView ivAlbumItem;
    Unbinder unbinder;
    onImageCompleteOnceListener onImageCompleteOnceListener;

    public PhotoFragment() {
    }

    public static PhotoFragment newInstance(List<CommercialItem.PhotoBean> photoBeanList,int position) {
        Bundle args = new Bundle();
        PhotoFragment fragment = new PhotoFragment();
        args.putInt(INT_KEY_CURRENT_POSITION,position);
        args.putParcelableArrayList(BUNDLE_KEY_PHOTO_LIST, (ArrayList<? extends Parcelable>) photoBeanList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoBeanList = getArguments().getParcelableArrayList(BUNDLE_KEY_PHOTO_LIST);
        position=getArguments().getInt(INT_KEY_CURRENT_POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_album_banner, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (photoBeanList.get(position).getUri()!=null) {
            Glide.with(getContext()).load(photoBeanList.get(position).getUri()).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                    ivAlbumItem.setImageDrawable(resource);
                    if (onImageCompleteOnceListener!=null) {
                        onImageCompleteOnceListener.onComplete();
                        onImageCompleteOnceListener=null;
                    }
                }
            });
        }
        ivAlbumItem.setOnClickListener(v -> {
                Activity activity=getActivity();
                Intent intentPreview = new Intent(activity, ImagePreviewDelActivity.class);
                ArrayList<ImageItem> imageItems=new ArrayList<>();
                for (CommercialItem.PhotoBean photo: photoBeanList) {
                    ImageItem imageItem=new ImageItem();
//                    imageItem.addTime=photo.get;
//                    imageItem.height=photo.height;
//                    imageItem.width=photo.width;
                    imageItem.name=photo.getPhoto_last_name();
                    imageItem.path=photo.getUri();
//                    imageItem.mimeType=photo.mimeType;
//                    imageItem.size=photo.size;
                    imageItems.add(imageItem);
                }
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imageItems);
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_EXTEND_SHOW_DELETE, false);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                activity.startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);

        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setOnImageCompleteOnceListener(PhotoFragment.onImageCompleteOnceListener onImageCompleteOnceListener) {
        this.onImageCompleteOnceListener = onImageCompleteOnceListener;
    }

    public interface onImageCompleteOnceListener{
            void onComplete();
    }


}
