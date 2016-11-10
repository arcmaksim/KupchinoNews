package com.arcmaksim.kupchinonews.ui.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.arcmaksim.kupchinonews.NewsItem
import com.arcmaksim.kupchinonews.NewsParser
import com.arcmaksim.kupchinonews.R
import com.arcmaksim.kupchinonews.commons.*
import com.arcmaksim.kupchinonews.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_news.*
import okhttp3.*
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.concurrent.TimeUnit

class NewsFragment : Fragment() {

    private var mAdapter: NewsAdapter? = null
    private var mIsNewsLoading = false

    private fun onRefresh() {
        swipeRefresh.isRefreshing = true
        if(activity.isNetworkAvailable()) {
            getNews()
        } else {
            showError(R.string.no_internet_error)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            container?.inflate(R.layout.fragment_news)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        swipeRefresh.setColorSchemeColors(R.attr.colorAccent)
        swipeRefresh.setOnRefreshListener { onRefresh() }
        swipeRefresh.isEnabled = true

        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context,
                layoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.setHasFixedSize(true)

        if(mAdapter != null) {
            showNews()
            swipeRefresh.isRefreshing = mIsNewsLoading
        } else if(activity.isNetworkAvailable()) {
            showProgressBar()
            getNews()
        }
    }

    private fun getNews() {
        mIsNewsLoading = true

        val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build()
        val request = Request.Builder()
                .url(resources.getString(R.string.feed_url))
                .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                mIsNewsLoading = false
                activity.runOnUiThread { showError(R.string.failed_reception_error) }
            }

            override fun onResponse(call: Call?, response: Response?) {
                try {
                    mIsNewsLoading = false
                    if(response?.isSuccessful as Boolean) {
                        val news: ArrayList<NewsItem> = NewsParser(activity).parse(response?.body()?.byteStream() as InputStream)
                        activity.runOnUiThread {
                            mAdapter = NewsAdapter(news, activity)
                            showNews()
                        }
                    } else {
                        activity.runOnUiThread { showError(R.string.unknown_error) }
                    }

                } catch (e: IOException) {
                    Log.e(MainActivity.TAG, "Exception caught:", e)
                }
            }

        })
    }

    private fun showNews() {
        errorTextView.remove()
        progressBar.remove()
        recyclerView.show()
        recyclerView.adapter = mAdapter
        swipeRefresh.isEnabled = true
        swipeRefresh.isRefreshing = false
    }

    private fun showError(errorID: Int) {
        if(mAdapter != null) {
            activity.showToast(resources.getString(errorID))
        } else {
            progressBar.remove()
            recyclerView.remove()
            errorTextView.show()
            errorTextView.text = resources.getString(errorID)
            swipeRefresh.isEnabled = true
        }
        swipeRefresh.isRefreshing = false
    }

    private fun showProgressBar() {
        swipeRefresh.isEnabled = false
        recyclerView.remove()
        errorTextView.remove()
        progressBar.show()
    }
}