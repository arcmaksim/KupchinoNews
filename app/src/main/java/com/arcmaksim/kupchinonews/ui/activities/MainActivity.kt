package com.arcmaksim.kupchinonews.ui.activities

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.arcmaksim.kupchinonews.R
import com.arcmaksim.kupchinonews.ViewPagerAdapter
import com.arcmaksim.kupchinonews.ui.fragments.CalendarFragment
import com.arcmaksim.kupchinonews.ui.fragments.news.NewsFragment
import com.arcmaksim.kupchinonews.ui.fragments.SendNewsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    companion object {
        @JvmStatic val TAG: String = MainActivity::class.java.simpleName
    }

    private val mViewPagerAdapter by lazy {
        ViewPagerAdapter(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.DefaultMaterialTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.offscreenPageLimit = 2
        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)

        supportActionBar?.elevation = 0F
    }

    private fun setupViewPager(viewPager: ViewPager) {
        mViewPagerAdapter.addFragment(NewsFragment(), resources.getString(R.string.news_tab_title))
        mViewPagerAdapter.addFragment(CalendarFragment(), resources.getString(R.string.calendar_tab_title))
        mViewPagerAdapter.addFragment(SendNewsFragment(), resources.getString(R.string.send_news_tab_title))
        viewPager.adapter = mViewPagerAdapter
    }

}
