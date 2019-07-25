package com.me.cl.capstoneproject.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.lzy.imagepicker.bean.ImageItem;

/**
 * Created by CL on 11/16/17.
 */

public class Photo extends ImageItem implements Parcelable{
    private String downloadUri;
    private long Size;
    private boolean isUploaded=false;

    public Photo() {
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }

    public long getSize() {
        return Size;
    }

    public void setSize(long size) {
        Size = size;
    }


    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.downloadUri);
        dest.writeLong(this.Size);
        dest.writeByte(this.isUploaded ? (byte) 1 : (byte) 0);
    }

    protected Photo(Parcel in) {
        super(in);
        this.downloadUri = in.readString();
        this.Size = in.readLong();
        this.isUploaded = in.readByte() != 0;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
