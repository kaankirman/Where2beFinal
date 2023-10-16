package com.strt.where2be.Models

import android.os.Parcel
import android.os.Parcelable

data class User (
    val id:String="",
    val name: String="",
    val email:String="",
    var image:String="",
    val fcmToken:String="",
    val imageUri:String=""
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(image)
        parcel.writeString(fcmToken)
        parcel.writeString(imageUri)
    }

    override fun describeContents()=0

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
