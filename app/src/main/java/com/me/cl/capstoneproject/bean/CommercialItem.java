package com.me.cl.capstoneproject.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by CL on 11/1/17.
 */

public class CommercialItem implements Parcelable {

    private Float average_costume;
    private String category;
    private ContactBean contact;
    private LocationBean location;



    private List<PhotoBean> photoBeanList;
//    private PhotoBean photo;

    private float rating;
    private float totalRating;
    private String summary;
    private String title;
    private String commercialId;


    private List<ReviewBean> reviewBeanList;
    private float review_count;
    private String create_time;


//    public PhotoBean getPhoto() {
//        return photo;
//    }
//
//    public void setPhoto(PhotoBean photo) {
//        this.photo = photo;
//    }

    public LocationBean getLocation() {
        return location;
    }

    public void setLocation(LocationBean location) {
        this.location = location;
    }

    public float getReview_count() {
        return review_count;
    }

    public void setReview_count(float review_count) {
        this.review_count = review_count;
    }

    public Float getAverage_costume() {
        return average_costume;
    }

    public void setAverage_costume(Float average_costume) {
        this.average_costume = average_costume;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ContactBean getContact() {
        return contact;
    }

    public void setContact(ContactBean contact) {
        this.contact = contact;
    }

    public List<PhotoBean> getPhotoBeanList() {
        return photoBeanList;
    }

    public void setPhotoBeanList(List<PhotoBean> photoBeanList) {
        this.photoBeanList = photoBeanList;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ReviewBean> getReviewBeanList() {
        return reviewBeanList;
    }

    public void setReviewBeanList(List<ReviewBean> reviewBeanList) {
        this.reviewBeanList = reviewBeanList;
    }

    public String getCommercialId() {
        return commercialId;
    }

    public void setCommercialId(String commercialId) {
        this.commercialId = commercialId;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public float getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(float totalRating) {
        this.totalRating = totalRating;
    }


    public static class ContactBean implements Parcelable {
        /**
         * phone : 1234567890
         * wechat : 098765
         */

        private String phone;
        private String wechat;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getWechat() {
            return wechat;
        }

        public void setWechat(String wechat) {
            this.wechat = wechat;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.phone);
            dest.writeString(this.wechat);
        }

        public ContactBean() {
        }

        protected ContactBean(Parcel in) {
            this.phone = in.readString();
            this.wechat = in.readString();
        }

        public static final Creator<ContactBean> CREATOR = new Creator<ContactBean>() {
            @Override
            public ContactBean createFromParcel(Parcel source) {
                return new ContactBean(source);
            }

            @Override
            public ContactBean[] newArray(int size) {
                return new ContactBean[size];
            }
        };
    }

    public static class PhotoBean implements Parcelable {
        /**
         * photo_last_name : cloud_photo_url
         */

        private String photo_last_name;
        private String uri;
        private String size;

        public String getPhoto_last_name() {
            return photo_last_name;
        }

        public void setPhoto_last_name(String photo_last_name) {
            this.photo_last_name = photo_last_name;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public PhotoBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.photo_last_name);
            dest.writeString(this.uri);
            dest.writeString(this.size);
        }

        protected PhotoBean(Parcel in) {
            this.photo_last_name = in.readString();
            this.uri = in.readString();
            this.size = in.readString();
        }

        public static final Creator<PhotoBean> CREATOR = new Creator<PhotoBean>() {
            @Override
            public PhotoBean createFromParcel(Parcel source) {
                return new PhotoBean(source);
            }

            @Override
            public PhotoBean[] newArray(int size) {
                return new PhotoBean[size];
            }
        };
    }

    public static class ReviewBean implements Parcelable {

        private String avatar;
        private String content;
        private String date;
        private String name;
        private List<PhotoBean> photoBeanList;
        private String rate;
        private String parentId;
        private String reviewId;

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

        public List<PhotoBean> getPhotoBeanList() {
            return photoBeanList;
        }

        public void setPhotoBeanList(List<PhotoBean> photoBeanList) {
            this.photoBeanList = photoBeanList;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getReviewId() {
            return reviewId;
        }

        public void setReviewId(String reviewId) {
            this.reviewId = reviewId;
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
            dest.writeTypedList(this.photoBeanList);
            dest.writeString(this.rate);
            dest.writeString(this.parentId);
            dest.writeString(this.reviewId);
        }

        protected ReviewBean(Parcel in) {
            this.avatar = in.readString();
            this.content = in.readString();
            this.date = in.readString();
            this.name = in.readString();
            this.photoBeanList = in.createTypedArrayList(PhotoBean.CREATOR);
            this.rate = in.readString();
            this.parentId = in.readString();
            this.reviewId = in.readString();
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

    public CommercialItem() {
    }

    public static class LocationBean implements Parcelable {

        /**
         * address : **** street
         * latitude : 200
         * longitude : 150
         */

        private String city;
        private String state;
        private String street;
        private String zip;
        private String latitude;
        private String longitude;

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public LocationBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.city);
            dest.writeString(this.state);
            dest.writeString(this.street);
            dest.writeString(this.zip);
            dest.writeString(this.latitude);
            dest.writeString(this.longitude);
        }

        protected LocationBean(Parcel in) {
            this.city = in.readString();
            this.state = in.readString();
            this.street = in.readString();
            this.zip = in.readString();
            this.latitude = in.readString();
            this.longitude = in.readString();
        }

        public static final Creator<LocationBean> CREATOR = new Creator<LocationBean>() {
            @Override
            public LocationBean createFromParcel(Parcel source) {
                return new LocationBean(source);
            }

            @Override
            public LocationBean[] newArray(int size) {
                return new LocationBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.average_costume);
        dest.writeString(this.category);
        dest.writeParcelable(this.contact, flags);
        dest.writeParcelable(this.location, flags);
        dest.writeTypedList(this.photoBeanList);
        dest.writeFloat(this.rating);
        dest.writeFloat(this.totalRating);
        dest.writeString(this.summary);
        dest.writeString(this.title);
        dest.writeString(this.commercialId);
        dest.writeTypedList(this.reviewBeanList);
        dest.writeFloat(this.review_count);
        dest.writeString(this.create_time);
    }

    protected CommercialItem(Parcel in) {
        this.average_costume = (Float) in.readValue(Float.class.getClassLoader());
        this.category = in.readString();
        this.contact = in.readParcelable(ContactBean.class.getClassLoader());
        this.location = in.readParcelable(LocationBean.class.getClassLoader());
        this.photoBeanList = in.createTypedArrayList(PhotoBean.CREATOR);
        this.rating = in.readFloat();
        this.totalRating = in.readFloat();
        this.summary = in.readString();
        this.title = in.readString();
        this.commercialId = in.readString();
        this.reviewBeanList = in.createTypedArrayList(ReviewBean.CREATOR);
        this.review_count = in.readFloat();
        this.create_time = in.readString();
    }

    public static final Creator<CommercialItem> CREATOR = new Creator<CommercialItem>() {
        @Override
        public CommercialItem createFromParcel(Parcel source) {
            return new CommercialItem(source);
        }

        @Override
        public CommercialItem[] newArray(int size) {
            return new CommercialItem[size];
        }
    };
}


