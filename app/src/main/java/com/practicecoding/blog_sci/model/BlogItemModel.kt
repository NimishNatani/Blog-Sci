package com.practicecoding.blog_sci.model

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class BlogItemModel constructor(
    var heading: String?="null",
    val userName: String?="null",
    val date: String?="null",
    var post: String?="null",
    var likeCount: Int=0,
    val profileImage:String?="null",
    val time:String? = "null",
    var userId:String?="null",
    var likeUser: List<String> ?= null
    ):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList()

    ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(heading)
        parcel.writeString(userName)
        parcel.writeString(date)
        parcel.writeString(post)
        parcel.writeInt(likeCount)
        parcel.writeString(profileImage)
        parcel.writeString(time)
        parcel.writeString(userId)
        parcel.writeStringList(likeUser)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BlogItemModel> {
        override fun createFromParcel(parcel: Parcel): BlogItemModel {
            return BlogItemModel(parcel)
        }

        override fun newArray(size: Int): Array<BlogItemModel?> {
            return arrayOfNulls(size)
        }
    }

}
