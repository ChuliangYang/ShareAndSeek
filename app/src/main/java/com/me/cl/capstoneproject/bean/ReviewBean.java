package com.me.cl.capstoneproject.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by CL on 11/30/17.
 */

public class ReviewBean implements Parcelable {
    /**
     * avatar : picture_url
     * content : It's great!
     * date : 11/01/2017
     * name : Carlos
     * photo : {"photo_last_name":"cloud_photo_url","photo_uri":"url"}
     * rate : 4
     */

    private String avatar;
    private String content;
    private String date;
    private String name;
    ArrayList<Photo> photos;
    private String rate;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }


    public ReviewBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatar);
        dest.writeString(this.content);
        dest.writeString(this.date);
        dest.writeString(this.name);
        dest.writeTypedList(this.photos);
        dest.writeString(this.rate);
    }

    protected ReviewBean(Parcel in) {
        this.avatar = in.readString();
        this.content = in.readString();
        this.date = in.readString();
        this.name = in.readString();
        this.photos = in.createTypedArrayList(Photo.CREATOR);
        this.rate = in.readString();
    }

    public static final Creator<ReviewBean> CREATOR = new Creator<ReviewBean>() {
        @Override
        public ReviewBean createFromParcel(Parcel source) {
            return new ReviewBean(source);
        }

        @Override
        public ReviewBean[] newArray(int size) {
            return new ReviewBean[size];
        }
    };
}
