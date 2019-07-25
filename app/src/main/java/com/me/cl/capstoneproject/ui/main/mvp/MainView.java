package com.me.cl.capstoneproject.ui.main.mvp;

/**
 * Created by CL on 11/1/17.
 */

public interface MainView{
    void popSpinnerWithDistricts(String[] districts);
    void initViewPagerWithTabLayout(String[] titles);
    void showSnackBar(String info);
    void performFabClick();
    void startUploadActivity(Class target,int x,int y,int radius,int requestCode);
}
