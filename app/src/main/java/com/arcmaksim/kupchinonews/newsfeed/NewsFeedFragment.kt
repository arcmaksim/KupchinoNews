package com.arcmaksim.kupchinonews.newsfeed

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.arcmaksim.kupchinonews.R
import com.arcmaksim.kupchinonews.activity.MainActivity
import com.arcmaksim.kupchinonews.commons.hide
import com.arcmaksim.kupchinonews.commons.inflate
import com.arcmaksim.kupchinonews.commons.show
import com.arcmaksim.kupchinonews.commons.showToast
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.item_news.view.*
import java.util.*

class NewsFeedFragment : Fragment(), NewsFeedContract.View, PopupMenu.OnMenuItemClickListener,
        AppBarLayout.OnOffsetChangedListener {

    companion object {
        fun newInstance(): NewsFeedFragment = NewsFeedFragment()
    }

    private var mAdapter: NewsFeedAdapter? = null
    private lateinit var mPresenter: NewsFeedContract.Presenter
    private var mCurrentPopupMenu: PopupMenu? = null
    private var mLastPositionInAdapter = -1
    private var mAppBar: AppBarLayout? = null
    private var mIsAppBarCollapsed = false

    interface NewsItemListener {

        fun onHeaderClick(positionInAdapter: Int)

        fun onMenuClick(positionInAdapter: Int)

    }

    private var mItemListener: NewsItemListener = object : NewsItemListener {

        override fun onHeaderClick(positionInAdapter: Int) = toggleExpandableContent(positionInAdapter)

        override fun onMenuClick(positionInAdapter: Int) = showNewsItemMenu(positionInAdapter)

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mAppBar = (context as MainActivity).getAppBar()
        mAppBar?.addOnOffsetChangedListener(this)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        mIsAppBarCollapsed = verticalOffset == 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            container?.inflate(R.layout.fragment_news)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
        swipeRefresh.setColorSchemeColors(typedValue.data)
        swipeRefresh.setOnRefreshListener { mPresenter.fetchNewsFeed() }

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context,
                layoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.setHasFixedSize(true)

        retryButton.setOnClickListener { mPresenter.retryFetching() }
    }

    override fun onResume() {
        super.onResume()
        mPresenter.start()
    }

    override fun setPresenter(presenter: NewsFeedContract.Presenter) {
        mPresenter = presenter
    }

    override fun showLoadingIndicator() {
        errorView.hide()
        recyclerView.hide()
        swipeRefresh.isRefreshing = false
        progressBar.show()
    }

    override fun showErrorPanel(errorResID: Int) {
        recyclerView.hide()
        swipeRefresh.isRefreshing = false
        progressBar.hide()
        errorView.show()
        errorTextView.text = resources.getString(errorResID)
    }

    override fun showErrorToast(errorResID: Int) {
        swipeRefresh.isRefreshing = false
        activity.showToast(resources.getString(errorResID))
    }

    override fun showNewsFeed(newsFeed: ArrayList<NewsItem>?) {
        dismissNewsItemMenu()

        errorView.hide()
        progressBar.hide()
        recyclerView.show()

        newsFeed?.let {
            swipeRefresh.isRefreshing = false
            mAdapter = NewsFeedAdapter(newsFeed, mItemListener, activity)
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

        mAppBar?.setExpanded(mIsAppBarCollapsed)
    }

    override fun dismissNewsItemMenu() {
        mCurrentPopupMenu?.dismiss()
    }

    override fun showRefreshIndicator() {
        swipeRefresh.isRefreshing = true
    }

    override fun toggleExpandableContent(positionInAdapter: Int) {
        val newsItemState = !mAdapter?.mExpandableLayoutsStates?.get(positionInAdapter)!!
        mAdapter?.mExpandableLayoutsStates?.set(positionInAdapter, newsItemState)
        val expandableContent = recyclerView.findViewHolderForAdapterPosition(positionInAdapter).itemView.expandableContent
        expandableContent.visibility = if (newsItemState) View.VISIBLE else View.GONE
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

}