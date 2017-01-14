package com.arcmaksim.kupchinonews.newsfeed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.arcmaksim.kupchinonews.R
import com.arcmaksim.kupchinonews.commons.*
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.item_news.view.*
import java.util.*

class NewsFeedFragment : Fragment(), NewsFeedContract.View, PopupMenu.OnMenuItemClickListener {

    private var mAdapter: NewsFeedAdapter? = null
    private var mIsRefreshingActive = false
    private lateinit var mPresenter: NewsFeedContract.Presenter
    private var mCurrentPopupMenu: PopupMenu? = null
    private var mLastPositionInAdapter = -1

    private var mItemListener: NewsItemListener = object : NewsItemListener {

        override fun onHeaderClick(positionInAdapter: Int) = toggleExpandableContent(positionInAdapter)

        override fun onMenuClick(positionInAdapter: Int) = showNewsItemMenu(positionInAdapter)

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
        showRefreshIndicator(mIsRefreshingActive)

        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context,
                layoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.setHasFixedSize(true)

        mAdapter?.let {
            showNewsFeed(null)
        } ?: if (activity.isNetworkAvailable()) {
            mPresenter.start()
        }
    }

    override fun setPresenter(presenter: NewsFeedContract.Presenter) {
        mPresenter = presenter
    }

    override fun showLoadingIndicator() {
        swipeRefresh.isEnabled = false
        showRefreshIndicator(false)

        recyclerView.hide()
        errorTextView.hide()
        progressBar.show()
    }

    override fun showErrorLabel(errorResID: Int) {
        swipeRefresh.isEnabled = true
        showRefreshIndicator(false)

        mAdapter?.let {
            activity.showToast(resources.getString(errorResID))
        } ?: let {
            progressBar.hide()
            recyclerView.hide()
            errorTextView.show()
            errorTextView.text = resources.getString(errorResID)
        }
    }

    override fun showNewsFeed(newsFeed: ArrayList<NewsItem>?) {
        swipeRefresh.isEnabled = true

        errorTextView.hide()
        progressBar.hide()
        recyclerView.show()

        newsFeed?.let {
            showRefreshIndicator(false)
            mAdapter = NewsFeedAdapter(newsFeed, mItemListener, activity)
            recyclerView.adapter = mAdapter
        }

        recyclerView.adapter = mAdapter
    }

    override fun showNewsItemMenu(positionInAdapter: Int) {
        mLastPositionInAdapter = positionInAdapter

        mCurrentPopupMenu = PopupMenu(context,
                recyclerView.findViewHolderForAdapterPosition(positionInAdapter).itemView.newsPopupMenuButton)
        mCurrentPopupMenu?.setOnMenuItemClickListener(this)
        mCurrentPopupMenu?.inflate(R.menu.popup_news)
        if (mAdapter?.mExpandableLayoutsStates?.get(positionInAdapter)!!) {
            mCurrentPopupMenu?.menu?.getItem(0)?.setTitle(R.string.news_feed_hide_content_layout_popup)
        }
        mCurrentPopupMenu?.show()
    }

    override fun showRefreshIndicator(visibility: Boolean) {
        mIsRefreshingActive = visibility
        swipeRefresh.isRefreshing = mIsRefreshingActive
    }

    override fun toggleExpandableContent(positionInAdapter: Int) {
        val newsItemState = !mAdapter?.mExpandableLayoutsStates?.get(positionInAdapter)!!
        mAdapter?.mExpandableLayoutsStates?.set(positionInAdapter, newsItemState)
        val expandableContent = recyclerView.findViewHolderForAdapterPosition(positionInAdapter).itemView.expandableContent
        expandableContent.visibility =
                if (newsItemState) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_toggle_content_layout -> toggleExpandableContent(mLastPositionInAdapter)
            R.id.item_go_to_site -> {
                val viewIntent = Intent("android.intent.action.VIEW",
                        Uri.parse(mAdapter?.mNews?.get(mLastPositionInAdapter)?.mLink))
                context.startActivity(viewIntent)
                return true
            }
        }
        return false
    }

    private fun onRefresh() {
        if (activity.isNetworkAvailable()) {
            showRefreshIndicator(true)
            mPresenter.retrieveNewsFeed()
        } else {
            showErrorLabel(R.string.news_feed_no_internet_error)
        }
    }

    interface NewsItemListener {

        fun onHeaderClick(positionInAdapter: Int)

        fun onMenuClick(positionInAdapter: Int)

    }

}