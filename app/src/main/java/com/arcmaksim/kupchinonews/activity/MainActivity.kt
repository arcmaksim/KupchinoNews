package com.arcmaksim.kupchinonews.activity

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import com.arcmaksim.kupchinonews.R
import com.arcmaksim.kupchinonews.mvp.BasePresenter
import com.arcmaksim.kupchinonews.newsfeed.NewsFeedFragment
import com.arcmaksim.kupchinonews.newsfeed.NewsFeedPresenter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val mViewPagerAdapter by lazy {
        ViewPagerAdapter(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.DefaultMaterialTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        setupViewPager()
    }

    private fun setupToolbar() {
        toolbar.setTitleTextColor(resources.getColor(R.color.material_color_white, null))
        setSupportActionBar(toolbar)
        setTitle(R.string.app_name)
        toolbar.elevation = 0F
    }

    private fun setupViewPager() {
        viewPager.offscreenPageLimit = 0
        tabLayout.setupWithViewPager(viewPager)

        val fragment = NewsFeedFragment.newInstance()
        val title = resources.getString(R.string.news_feed_tab_title)
        val presenter = NewsFeedPresenter(fragment, this)
        mViewPagerAdapter.addFragment(fragment, title, presenter)

        viewPager.adapter = mViewPagerAdapter
    }

    fun getAppBar(): AppBarLayout = appBarLayout

    inner class ViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {

        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()
        private val mPresenters = ArrayList<BasePresenter>()

        override fun getItem(index: Int) = mFragmentList[index]

        override fun getCount() = mFragmentList.size

        override fun getPageTitle(position: Int) = mFragmentTitleList[position]

        fun addFragment(fragment: Fragment, title: String, presenter: BasePresenter) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
            mPresenters.add(presenter)
        }

    }

}