package com.me.cl.capstoneproject.bean;

/**
 * Created by CL on 11/4/17.
 */

public class CommercialCategory {
    private String category;
    private int imgId;

    public CommercialCategory(String category, int imgId) {
        this.category = category;
        this.imgId = imgId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}
