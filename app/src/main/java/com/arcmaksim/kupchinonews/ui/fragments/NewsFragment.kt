package com.arcmaksim.kupchinonews.ui.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arcmaksim.kupchinonews.NewsAdapter
import com.arcmaksim.kupchinonews.NewsItem
import com.arcmaksim.kupchinonews.NewsParser
import com.arcmaksim.kupchinonews.R
import com.arcmaksim.kupchinonews.commons.inflate
import com.arcmaksim.kupchinonews.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_news.*
import okhttp3.*
import java.io.IOException
import java.util.*

class NewsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        @JvmStatic val NEWS_LIST = "NEWS_LIST"
    }

    private lateinit var mNews: ArrayList<NewsItem>

    override fun onRefresh() {
        if(progressBar.visibility == View.GONE) {
            if(isNetworkAvailable()) {
                swipeRefresh.isRefreshing = true
                recyclerView.visibility = View.GONE
                getNews()
            } else {
                errorTextView.text = "No internet"
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_news)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        swipeRefresh.setColorSchemeResources(R.color.colorAccent)
        swipeRefresh.isEnabled = false

        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context,
                layoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        if(savedInstanceState != null && savedInstanceState.containsKey(NEWS_LIST)) {
            mNews = savedInstanceState.getParcelableArrayList(NEWS_LIST)
            updateDisplay(mNews)
        } else if (isNetworkAvailable()) getNews() else errorTextView.visibility = View.VISIBLE
    }

    private fun getNews() {
        errorTextView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        val client = OkHttpClient()
        val request = Request.Builder()
                .url("http://kupchinonews.ru/feed")
                .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                errorTextView.text = "Failed to receive news"
                errorTextView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call?, response: Response?) {
                try {
                    val rawNews = response?.body()?.byteStream()
                    mNews = NewsParser(activity).parse(rawNews!!)

                    if(response?.isSuccessful!!) {
                        activity.runOnUiThread { updateDisplay(mNews) }
                    } else {
                        errorTextView.text = "Something wrong with receiving news"
                        errorTextView.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    }

                } catch (e: IOException) {
                    Log.e(MainActivity.TAG, "Exception caught:", e)
                }
            }

        })
    }

    private fun isNetworkAvailable(): Boolean {
        val manager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return networkInfo?.isConnected ?: false
    }

    private fun updateDisplay(news: ArrayList<NewsItem>) {
        recyclerView.adapter = NewsAdapter(news)
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        swipeRefresh.isEnabled = true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelableArrayList(NEWS_LIST, mNews)
        super.onSaveInstanceState(outState)
    }
}

