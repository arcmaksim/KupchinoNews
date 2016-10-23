package com.arcmaksim.kupchinonews

import android.support.transition.Scene
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_detailed_news.view.*
import kotlinx.android.synthetic.main.item_news.view.*
import java.util.*

class NewsAdapter(internal var mNews: ArrayList<NewsItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        @JvmStatic private val TAG: String = NewsAdapter::class.java.simpleName

        const private val news_simple = 0
        const private val news_detailed = 1
    }

    private val mViewStates by lazy {
        BooleanArray(mNews.size, { false })
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when(getItemViewType(position)) {
            news_simple -> (holder as SimpleViewHolder).bindView(position)
            else -> (holder as DetailedViewHolder).bindView(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(mViewStates[position]) news_detailed else news_simple
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            news_simple -> return SimpleViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_news, parent, false))
            else -> return DetailedViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_detailed_news, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return mNews.size
    }

    inner class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var itemPosition: Int = 0

        fun bindView(position: Int) {
            itemView.newsHeader.text = mNews[position].mTitle
            itemView.setOnClickListener { switch(itemPosition) }
            itemPosition = position
        }

    }

    inner class DetailedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var itemPosition: Int = 0

        fun bindView(position: Int) {
            itemView.newsTitle.text = mNews[position].mTitle
            //itemImage.setImageResource(Recipes.resourceIds[position])
            itemView.newsDescription.text = mNews[position].mDescription
            itemView.setOnClickListener { switch(itemPosition) }
            itemPosition = position
        }

    }

    private fun switch(position: Int) {
        mViewStates[position] = !mViewStates[position]
        //val transitionRoot = get
        //val newScene = Scene.getSceneForLayout(,R.layout.item_detailed_news, )
        notifyDataSetChanged()
    }

}