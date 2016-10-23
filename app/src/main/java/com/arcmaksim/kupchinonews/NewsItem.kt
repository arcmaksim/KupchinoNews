package com.arcmaksim.kupchinonews

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable

data class NewsItem(var mTitle: String = "",
                    var mLink: String = "",
                    var mDescription: String = "",
                    var mPubDate: String = "",
                    var mCreator: String = "",
                    var mImage: Bitmap? = null) : Parcelable {


    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<NewsItem> = object : Parcelable.Creator<NewsItem> {
            override fun createFromParcel(p0: Parcel?): NewsItem {
                return NewsItem(p0)
            }

            override fun newArray(size: Int): Array<out NewsItem> {
                return Array(size) {NewsItem()}
            }

        }
    }

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(mTitle)
        parcel?.writeString(mLink)
        parcel?.writeString(mDescription)
        parcel?.writeString(mPubDate)
        parcel?.writeString(mCreator)
        parcel?.writeParcelable(mImage, flags)
    }

    private constructor(parcel: Parcel?): this() {
        if(parcel != null) {
            mTitle = parcel.readString()
            mLink = parcel.readString()
            mDescription = parcel.readString()
            mPubDate = parcel.readString()
            mCreator = parcel.readString()
            mImage = parcel.readParcelable<Bitmap>(ClassLoader.getSystemClassLoader())
        }
    }

    override fun describeContents() = 0

}