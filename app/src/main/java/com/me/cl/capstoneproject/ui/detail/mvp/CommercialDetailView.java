package com.me.cl.capstoneproject.ui.detail.mvp;

import com.me.cl.capstoneproject.bean.CommercialItem;
import com.me.cl.capstoneproject.bean.ReviewBeanWrapper;

import java.util.List;

/**
 * Created by CL on 11/22/17.
 */

public interface CommercialDetailView {
    void popBaseInfo(CommercialItem commercialItem);
    void popReviewList(List<ReviewBeanWrapper> reviewBeanList);
    void showSnackBar(String info);
    void setRating(Float score,int count);
}
