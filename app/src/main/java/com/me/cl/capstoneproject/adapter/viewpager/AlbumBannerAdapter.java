package com.me.cl.capstoneproject.adapter.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.me.cl.capstoneproject.bean.CommercialItem;
import com.me.cl.capstoneproject.ui.detail.PhotoFragment;

import java.util.List;
import java.util.WeakHashMap;

/**
 * Created by CL on 11/25/17.
 */

public class AlbumBannerAdapter extends FragmentStatePagerAdapter {

    private List<CommercialItem.PhotoBean> photoBeanList;


    private WeakHashMap<Integer,Object> weakCache=new WeakHashMap();


    private View mCurrentView;


    public AlbumBannerAdapter(FragmentManager fm, List<CommercialItem.PhotoBean> photoBeanList) {
        super(fm);
        this.photoBeanList = photoBeanList;
    }

    @Override
    public Fragment getItem(int position) {
        PhotoFragment photoFragment=PhotoFragment.newInstance(photoBeanList,position);
        weakCache.put(position,photoFragment);
        return photoFragment;
    }

    @Override
    public int getCount() {
        return photoBeanList!=null?photoBeanList.size():0;
    }

    public void addPhotoList(List<CommercialItem.PhotoBean> photoBeanList){
        if (photoBeanList != null) {
            this.photoBeanList.addAll(photoBeanList);
        } else {
            this.photoBeanList=photoBeanList;
        }
    }

    public WeakHashMap<Integer, Object> getWeakCache() {
        return weakCache;
    }


    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }
//
//    public View getmCurrentView() {
//        return mCurrentView;
//    }
}
