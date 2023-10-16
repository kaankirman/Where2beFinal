package com.strt.where2be.Models

import android.os.Parcel
import android.os.Parcelable

data class Locations (
    var location_id:String="",
    val name:String="",
    val lat:String="",
    val lon:String="",
    val image:String="",
    val descriptionText:String=""
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
        parcel.writeString(location_id)
        parcel.writeString(name)
        parcel.writeString(lat)
        parcel.writeString(lon)
        parcel.writeString(image)
        parcel.writeString(descriptionText)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Locations> {
        override fun createFromParcel(parcel: Parcel): Locations {
            return Locations(parcel)
        }

        override fun newArray(size: Int): Array<Locations?> {
            return arrayOfNulls(size)
        }
    }
}