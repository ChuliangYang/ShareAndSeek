package com.me.cl.capstoneproject.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by CL on 12/20/17.
 */
data class FreeItem(
        var title: String? = null,
        var contact: ContactBean? = null,
        var location: LocationBean? = null,
        var photoBeanList: List<PhotoBean>? = null,
        var summary: String? = null,
        var freeId: String? = null,
        var create_time: String? = null,
        var expiredTime:Long?=null,
        var expand:Boolean?=null,
        var head_avator:String?=null,
        var person_name:String?=null,
        var expandState:Int?=null
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readParcelable(ContactBean::class.java.classLoader),
            parcel.readParcelable(LocationBean::class.java.classLoader),
            parcel.createTypedArrayList(PhotoBean),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int)

    data class ContactBean(var phone: String? = null, var wechat: String? = null) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(phone)
            parcel.writeString(wechat)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ContactBean> {
            override fun createFromParcel(parcel: Parcel): ContactBean {
                return ContactBean(parcel)
            }

            override fun newArray(size: Int): Array<ContactBean?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class LocationBean(var city: String? = null, var state: String? = null, var street: String? = null, var zip: String? = null, var latitude: String? = null, var longitude: String? = null) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(city)
            parcel.writeString(state)
            parcel.writeString(street)
            parcel.writeString(zip)
            parcel.writeString(latitude)
            parcel.writeString(longitude)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<LocationBean> {
            override fun createFromParcel(parcel: Parcel): LocationBean {
                return LocationBean(parcel)
            }

            override fun newArray(size: Int): Array<LocationBean?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class PhotoBean(var photo_last_name: String? = null, var uri: String? = null, var size: String? = null) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readString())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(photo_last_name)
            parcel.writeString(uri)
            parcel.writeString(size)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<PhotoBean> {
            override fun createFromParcel(parcel: Parcel): PhotoBean {
                return PhotoBean(parcel)
            }

            override fun newArray(size: Int): Array<PhotoBean?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeParcelable(contact, flags)
        parcel.writeParcelable(location, flags)
        parcel.writeTypedList(photoBeanList)
        parcel.writeString(summary)
        parcel.writeString(freeId)
        parcel.writeString(create_time)
        parcel.writeValue(expiredTime)
        parcel.writeValue(expand)
        parcel.writeString(head_avator)
        parcel.writeString(person_name)
        parcel.writeValue(expandState)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FreeItem> {
        override fun createFromParcel(parcel: Parcel): FreeItem {
            return FreeItem(parcel)
        }

        override fun newArray(size: Int): Array<FreeItem?> {
            return arrayOfNulls(size)
        }
    }

}