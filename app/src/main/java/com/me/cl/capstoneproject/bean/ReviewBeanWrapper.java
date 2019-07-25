package com.me.cl.capstoneproject.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CL on 12/13/17.
 */

public class ReviewBeanWrapper implements Parcelable {

    private ReviewBean reviewBean;

    private String reviewId;

    private String parentId;

    private int expandState=0;

    private Boolean expand;


    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    public ReviewBean getReviewBean() {
        return reviewBean;
    }

    public void setReviewBean(ReviewBean reviewBean) {
        this.reviewBean = reviewBean;
    }

    public ReviewBeanWrapper() {
    }

    public int getExpandState() {
        return expandState;
    }

    public void setExpandState(int expandState) {
        this.expandState = expandState;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.reviewBean, flags);
        dest.writeString(this.reviewId);
        dest.writeString(this.parentId);
        dest.writeInt(this.expandState);
    }

    protected ReviewBeanWrapper(Parcel in) {
        this.reviewBean = in.readParcelable(ReviewBean.class.getClassLoader());
        this.reviewId = in.readString();
        this.parentId = in.readString();
        this.expandState = in.readInt();
    }

    public static final Creator<ReviewBeanWrapper> CREATOR = new Creator<ReviewBeanWrapper>() {
        @Override
        public ReviewBeanWrapper createFromParcel(Parcel source) {
            return new ReviewBeanWrapper(source);
        }

        @Override
        public ReviewBeanWrapper[] newArray(int size) {
            return new ReviewBeanWrapper[size];
        }
    };

    public Boolean getExpand() {
        return expand;
    }

    public void setExpand(Boolean expand) {
        this.expand = expand;
    }
}
