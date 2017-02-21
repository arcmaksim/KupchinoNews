package com.arcmaksim.kupchinonews.newsfeed

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class NewsItem(var mTitle: String = "",
                    var mLink: String = "",
                    var mDescription: String = "",
                    var mPublicationDate: String = "",
                    var mCreator: String = "",
                    var mImageUrl: String = "") : Parcelable {

    var mImage: Bitmap? = null

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<NewsItem> = object : Parcelable.Creator<NewsItem> {

            override fun createFromParcel(parcel: Parcel): NewsItem = NewsItem(parcel)

            override fun newArray(size: Int): Array<NewsItem> = Array(size) { NewsItem() }

        }
    }

    private constructor(parcel: Parcel) : this() {
        with(parcel) {
            mTitle = readString()
            mLink = readString()
            mDescription = readString()
            mPublicationDate = readString()
            mCreator = readString()
            mImage = readParcelable<Bitmap>(ClassLoader.getSystemClassLoader())
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(mTitle)
            writeString(mLink)
            writeString(mDescription)
            writeString(mPublicationDate)
            writeString(mCreator)
            writeParcelable(mImage, flags)
        }
    }

    override fun describeContents() = 0

}