package com.arcmaksim.kupchinonews.newsfeed

import android.content.Context
import android.content.res.Configuration
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arcmaksim.kupchinonews.R
import com.arcmaksim.kupchinonews.commons.tint
import kotlinx.android.synthetic.main.item_news.view.*
import java.util.*

class NewsFeedAdapter(val mNews: ArrayList<NewsItem>,
                      val mNewsItemListener: NewsFeedFragment.NewsItemListener,
                      val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val mExpandableLayoutsStates: BooleanArray

    init {
        mExpandableLayoutsStates = BooleanArray(mNews.size, { i -> i == 0 })
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) =
            (holder as ViewHolder).bindView(mNews[position])

    override fun getItemViewType(position: Int) = 0

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_news, parent, false))

    override fun getItemCount() = mNews.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(news: NewsItem) {

            with(news) {
                itemView.newsTitleView.text = mTitle
                itemView.newsDateView.text = mPublicationDate

                mImage?.let {
                    if (mContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        itemView.newsTitleImage.setImageBitmap(it)
                    } else {
                        itemView.newsDescriptionImage.setImageBitmap(it)
                    }
                }
                itemView.newsDescriptionView.text = Html.fromHtml(mDescription)
            }

            itemView.newsPopupMenuButton.drawable.tint(mContext, R.color.material_color_grey_600)

            itemView.expandableContent.visibility =
                    if (mExpandableLayoutsStates[adapterPosition]) View.VISIBLE else View.GONE

            itemView.newsHeader.setOnClickListener {
                mNewsItemListener.onHeaderClick(adapterPosition)
            }
            itemView.newsPopupMenuButton.setOnClickListener {
                mNewsItemListener.onMenuClick(adapterPosition)
            }
        }

    }

}