package com.arcmaksim.kupchinonews.ui.fragments.news

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.arcmaksim.kupchinonews.NewsItem
import com.arcmaksim.kupchinonews.NewsParser
import com.arcmaksim.kupchinonews.R
import com.arcmaksim.kupchinonews.commons.inflate
import com.arcmaksim.kupchinonews.commons.remove
import com.arcmaksim.kupchinonews.commons.show
import com.arcmaksim.kupchinonews.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_news.*
import okhttp3.*
import java.io.IOException
import java.io.InputStream
import java.util.*

class NewsFragment : Fragment() {

    companion object {
        private const val NEWS_LIST = "NEWS_LIST"
        private const val EXPANDABLE_LAYOUTS_STATES = "EXPANDABLE_LAYOUTS_STATES"
    }

    private var mAdapter: NewsAdapter? = null

    private fun onRefresh() {
        if(isNetworkAvailable()) {
            swipeRefresh.isRefreshing = true
            getNews()
        } else {
            showError(R.string.no_internet_error)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            container?.inflate(R.layout.fragment_news)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        swipeRefresh.setColorSchemeColors(R.attr.colorAccent)
        swipeRefresh.setOnRefreshListener { onRefresh() }
        swipeRefresh.isEnabled = false

        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context,
                layoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.setHasFixedSize(true)

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(NEWS_LIST)) {
                val news: ArrayList<NewsItem> = savedInstanceState.getParcelableArrayList(NEWS_LIST)
                val layoutStates: BooleanArray = savedInstanceState.getBooleanArray(EXPANDABLE_LAYOUTS_STATES)
                showNews(news, layoutStates)
            }
        } else if(isNetworkAvailable()) {
            showProgressBar()
            getNews()
        }
    }

    private fun getNews() {
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(resources.getString(R.string.feed_url))
                .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) =
                    activity.runOnUiThread { showError(R.string.unknown_error) }

            override fun onResponse(call: Call?, response: Response?) {
                try {
                    if(response?.isSuccessful as Boolean) {
                        val news: ArrayList<NewsItem> = NewsParser(activity).parse(response?.body()?.byteStream() as InputStream)
                        activity.runOnUiThread { showNews(news) }
                    } else {
                        activity.runOnUiThread { showError(R.string.failed_reception_error) }
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

    private fun showNews(news: ArrayList<NewsItem>, layoutStates: BooleanArray = BooleanArray(news.size, { false })) {
        mAdapter = NewsAdapter(news, layoutStates)
        errorTextView.remove()
        progressBar.remove()
        recyclerView.show()
        recyclerView.adapter = mAdapter
        swipeRefresh.isEnabled = true
        swipeRefresh.isRefreshing = false
    }

    private fun showError(errorID: Int) {
        progressBar.remove()
        recyclerView.remove()
        errorTextView.show()
        errorTextView.text = resources.getString(errorID)
        swipeRefresh.isEnabled = true
        swipeRefresh.isRefreshing = false
    }

    private fun showProgressBar() {
        recyclerView.remove()
        errorTextView.remove()
        progressBar.show()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if(mAdapter != null) {
            outState?.putBooleanArray(EXPANDABLE_LAYOUTS_STATES, (mAdapter as NewsAdapter).mExpandableLayoutsStates)
            outState?.putParcelableArrayList(NEWS_LIST, (mAdapter as NewsAdapter).mNews)
        }
    }
}