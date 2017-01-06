package com.arcmaksim.kupchinonews.newsfeed

import com.arcmaksim.kupchinonews.mvp.BasePresenter
import com.arcmaksim.kupchinonews.mvp.BaseView
import java.util.*

class NewsFeedContract {

    interface View : BaseView<Presenter> {

        fun showLoadingIndicator()

        fun showErrorLabel(errorResID: Int)

        fun showNewsFeed(newsFeed: ArrayList<NewsItem>? = null)

        fun showRefreshIndicator(visibility: Boolean)

        fun toggleExpandableContent(positionInAdapter: Int)

        fun showNewsItemMenu(positionInAdapter: Int)

    }

    interface Presenter : BasePresenter {

        fun retrieveNewsFeed()

        fun processNewsFeed(newsFeed: ArrayList<NewsItem>): ArrayList<NewsItem>

    }

}