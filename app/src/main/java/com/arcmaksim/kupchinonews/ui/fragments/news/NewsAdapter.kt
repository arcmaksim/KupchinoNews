package com.arcmaksim.kupchinonews.ui.fragments.news

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arcmaksim.kupchinonews.NewsItem
import com.arcmaksim.kupchinonews.R
import kotlinx.android.synthetic.main.item_news.view.*
import java.util.*

class NewsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    companion object {
        @JvmStatic private val TAG: String = NewsAdapter::class.java.simpleName
    }

    val mNews: ArrayList<NewsItem>
    var mExpandableLayoutsStates: BooleanArray

    constructor(news: ArrayList<NewsItem>, expandableLayoutsStates: BooleanArray) : super() {
        mNews = news
        mExpandableLayoutsStates = expandableLayoutsStates
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) = (holder as ViewHolder).bindView(position)

    override fun getItemViewType(position: Int) = 0

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_news, parent, false))

    override fun getItemCount() = mNews.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemPosition: Int = 0

        fun bindView(position: Int) {
            itemView.newsTitle.text = mNews[position].mTitle
            itemView.newsTitle.setOnClickListener { switch(position, itemView.expandableLayout) }
            if(mNews[position].mImage != null) {
                itemView.newsImage.setImageBitmap(mNews[position].mImage)
            }
            itemView.newsDescription.text = mNews[position].mDescription
            itemView.expandableLayout.visibility = if (mExpandableLayoutsStates[position]) View.VISIBLE else View.GONE
            itemPosition = position
        }
    }

    private fun switch(position: Int, itemView: View) {
        mExpandableLayoutsStates[position] = !mExpandableLayoutsStates[position]
        itemView.visibility = if(mExpandableLayoutsStates[position]) View.VISIBLE else View.GONE
    }

}