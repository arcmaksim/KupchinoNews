package com.arcmaksim.kupchinonews.activity

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.arcmaksim.kupchinonews.mvp.BasePresenter
import java.util.*

class ViewPagerAdapter(manager: FragmentManager, context: Context) : FragmentStatePagerAdapter(manager) {

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