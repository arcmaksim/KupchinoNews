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

            override fun createFromParcel(parcel: Parcel?): NewsItem = NewsItem(parcel)

            override fun newArray(size: Int): Array<out NewsItem> = Array(size) { NewsItem() }

        }
    }

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(mTitle)
        parcel?.writeString(mLink)
        parcel?.writeString(mDescription)
        parcel?.writeString(mPublicationDate)
        parcel?.writeString(mCreator)
        parcel?.writeParcelable(mImage, flags)
    }

    private constructor(parcel: Parcel?): this() {
        parcel?.let {
            mTitle = it.readString()
            mLink = it.readString()
            mDescription = it.readString()
            mPublicationDate = it.readString()
            mCreator = it.readString()
            mImage = it.readParcelable<Bitmap>(ClassLoader.getSystemClassLoader())
        }
    }

    override fun describeContents() = 0

}