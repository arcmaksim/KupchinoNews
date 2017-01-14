package com.arcmaksim.kupchinonews.activity

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.arcmaksim.kupchinonews.R
import com.arcmaksim.kupchinonews.newsfeed.NewsFeedFragment
import com.arcmaksim.kupchinonews.newsfeed.NewsFeedPresenter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mViewPagerAdapter by lazy {
        ViewPagerAdapter(supportFragmentManager, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.DefaultMaterialTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.offscreenPageLimit = 0
        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)

        supportActionBar?.elevation = 0F
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val fragment = NewsFeedFragment()
        val title = resources.getString(R.string.news_feed_tab_title)
        val presenter = NewsFeedPresenter(fragment, this)
        mViewPagerAdapter.addFragment(fragment, title, presenter)

        viewPager.adapter = mViewPagerAdapter
    }

}