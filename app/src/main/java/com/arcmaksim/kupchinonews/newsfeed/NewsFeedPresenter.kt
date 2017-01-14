package com.arcmaksim.kupchinonews.newsfeed

import android.app.Activity
import android.content.Context
import android.util.Log
import com.arcmaksim.kupchinonews.R
import com.arcmaksim.kupchinonews.commons.NewsFeedHtmlCleaner
import com.arcmaksim.kupchinonews.commons.NewsFeedParser
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.concurrent.TimeUnit

class NewsFeedPresenter(view: NewsFeedContract.View, context: Context) : NewsFeedContract.Presenter {

    companion object {
        const val TAG: String = "NewsFeedPresenter"
    }

    private val mNewsFeedView: NewsFeedContract.View = view
    private val mContext: Context = context

    init {
        mNewsFeedView.setPresenter(this)
    }

    override fun start() {
        mNewsFeedView.showLoadingIndicator()
        retrieveNewsFeed()
    }

    override fun retrieveNewsFeed() {
        val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build()
        val request = Request.Builder()
                .url(mContext.resources.getString(R.string.news_feed_url))
                .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) =
                    (mContext as Activity).runOnUiThread {
                        mNewsFeedView.showErrorLabel(R.string.news_feed_failed_reception_error)
                    }

            override fun onResponse(call: Call?, response: Response?) {
                try {
                    if (response?.isSuccessful as Boolean) {
                        var newsFeed = NewsFeedParser().parse(response?.body()?.byteStream() as InputStream)
                        newsFeed = processNewsFeed(newsFeed)
                        (mContext as Activity).runOnUiThread {
                            mNewsFeedView.showNewsFeed(newsFeed)
                        }
                    } else {
                        (mContext as Activity).runOnUiThread {
                            mNewsFeedView.showErrorLabel(R.string.news_feed_unknown_error)
                        }
                    }

                } catch (e: IOException) {
                    Log.e(TAG, "Exception caught:", e)
                }
            }

        })
    }

    override fun processNewsFeed(newsFeed: ArrayList<NewsItem>): ArrayList<NewsItem> {
        newsFeed.forEach {
            if (it.mImageUrl != "") {
                it.mImage = Picasso.with(mContext).load(it.mImageUrl).get()
            }
            it.mDescription = NewsFeedHtmlCleaner.cleanHtml(it.mDescription)
        }
        return newsFeed
    }

}