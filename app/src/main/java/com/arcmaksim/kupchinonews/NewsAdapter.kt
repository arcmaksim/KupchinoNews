package com.arcmaksim.kupchinonews

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_news.view.*
import java.util.*

class NewsAdapter(internal var mNews: ArrayList<NewsItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        @JvmStatic private val TAG: String = NewsAdapter::class.java.simpleName
    }

    private val mViewStates by lazy {
        BooleanArray(mNews.size, { false })
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
            itemView.newsDescription.text = mNews[position].mDescription
            itemPosition = position
        }
    }

    private fun switch(position: Int, itemView: View) {
        mViewStates[position] = !mViewStates[position]
        itemView.visibility = if(mViewStates[position]) View.VISIBLE else View.GONE
    }

}