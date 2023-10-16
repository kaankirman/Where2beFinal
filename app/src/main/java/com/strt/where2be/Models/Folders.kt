package com.strt.where2be.Models

import android.os.Parcel
import android.os.Parcelable

data class Folders(
    val name:String="",
    val image:String="",
    var folder_id:String="",
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(folder_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Folders> {
        override fun createFromParcel(parcel: Parcel): Folders {
            return Folders(parcel)
        }

        override fun newArray(size: Int): Array<Folders?> {
            return arrayOfNulls(size)
        }
    }
}