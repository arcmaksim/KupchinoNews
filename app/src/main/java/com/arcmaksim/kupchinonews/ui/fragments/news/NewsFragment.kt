package com.arcmaksim.kupchinonews.ui.fragments.news

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
import com.arcmaksim.kupchinonews.ui.fragments.news.NewsAdapter
import com.arcmaksim.kupchinonews.NewsItem
import com.arcmaksim.kupchinonews.NewsParser
import com.arcmaksim.kupchinonews.R
import com.arcmaksim.kupchinonews.commons.inflate
import com.arcmaksim.kupchinonews.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_news.*
import okhttp3.*
import java.io.IOException
import java.io.InputStream
import java.util.*

class NewsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        const val NEWS_LIST = "NEWS_LIST"
        const val EXPANDABLE_LAYOUTS_STATES = "EXPANDABLE_LAYOUTS_STATES"
    }

    private var mAdapter: NewsAdapter? = null

    override fun onRefresh() {
        if(progressBar.visibility == View.GONE) {
            if(isNetworkAvailable()) {
                swipeRefresh.isRefreshing = true
                recyclerView.visibility = View.GONE
                getNews()
            } else {
                errorTextView.text = resources.getString(R.string.no_internet_error)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            container?.inflate(R.layout.fragment_news)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        swipeRefresh.setColorSchemeResources(R.color.colorAccent)
        swipeRefresh.isEnabled = false

        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context,
                layoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(NEWS_LIST)) {
                val news: ArrayList<NewsItem> = savedInstanceState.getParcelableArrayList(NEWS_LIST)
                val layoutStates: BooleanArray = savedInstanceState.getBooleanArray(EXPANDABLE_LAYOUTS_STATES)
                updateDisplay(NewsAdapter(news, layoutStates))
            }
        } else if (isNetworkAvailable()) getNews() else errorTextView.visibility = View.VISIBLE
    }

    private fun getNews() {
        errorTextView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        val client = OkHttpClient()
        val request = Request.Builder()
                .url(resources.getString(R.string.feed_url))
                .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                activity.runOnUiThread {
                    errorTextView.text = resources.getString(R.string.unknown_error)
                    errorTextView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }

            override fun onResponse(call: Call?, response: Response?) {
                try {
                    if(response?.isSuccessful as Boolean) {
                        val news: ArrayList<NewsItem> = NewsParser(activity).parse(response?.body()?.byteStream() as InputStream)
                        activity.runOnUiThread { updateDisplay(NewsAdapter(news)) }
                    } else {
                        activity.runOnUiThread {
                            errorTextView.text = resources.getString(R.string.failed_reception_error)
                            errorTextView.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                        }
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

    private fun updateDisplay(adapter: NewsAdapter) {
        mAdapter = adapter
        recyclerView.adapter = mAdapter
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        swipeRefresh.isEnabled = true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        if(mAdapter != null) {
            outState?.putBooleanArray(EXPANDABLE_LAYOUTS_STATES, (mAdapter as NewsAdapter).mExpandableLayoutsStates)
            outState?.putParcelableArrayList(NEWS_LIST, (mAdapter as NewsAdapter).mNews)
        }
        super.onSaveInstanceState(outState)
    }
}