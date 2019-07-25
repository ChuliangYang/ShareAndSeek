package com.me.cl.capstoneproject.adapter.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.me.cl.capstoneproject.ui.main.fragment.commercial.CommercialFragment;
import com.me.cl.capstoneproject.ui.main.fragment.free.FreeFragment;
import com.me.cl.capstoneproject.ui.main.fragment.help.HelpFragment;

/**
 * Created by CL on 11/3/17.
 */

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    private String[] titles;

    public MainPagerAdapter(FragmentManager fm,String[] titles) {
        super(fm);
        this.titles=titles;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
               return CommercialFragment.Companion.newInstance();
            case 1:
                return FreeFragment.Companion.newInstance();
            case 2:
                return HelpFragment.Companion.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return titles!=null?titles.length:0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
