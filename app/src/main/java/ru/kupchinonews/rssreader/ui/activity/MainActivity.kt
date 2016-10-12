package ru.kupchinonews.rssreader.ui.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import java.util.ArrayList

import ru.kupchinonews.rssreader.R
import ru.kupchinonews.rssreader.ui.fragments.CalendarFragment
import ru.kupchinonews.rssreader.ui.fragments.NewsFragment
import ru.kupchinonews.rssreader.ui.fragments.SendNewsFragment

class MainActivity : AppCompatActivity(), LocationListener {

    private var mToolbar: Toolbar? = null
    private var mTabLayout: TabLayout? = null
    private var mViewPager: ViewPager? = null
    private var mViewPagerAdapter: ViewPagerAdapter? = null

    private var flags: BooleanArray? = null

    private var mNewsFragment: NewsFragment? = null
    private var mCalendarFragment: CalendarFragment? = null
    private var mSendNewsFragment: SendNewsFragment? = null

    private var hasData = false
    private var hasInternet = false

    private var mLocationManager: LocationManager? = null
    private var mProvider: String? = null

    var lat: Double = 0.toDouble()
        private set
    var lng: Double = 0.toDouble()
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.registerReceiver(this.mConnReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        setContentView(R.layout.main_layout)
        window.setBackgroundDrawable(null)

        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        mProvider = mLocationManager!!.getBestProvider(criteria, false)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val location = mLocationManager!!.getLastKnownLocation(mProvider)

        if (location != null)
            onLocationChanged(location)

        mToolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(mToolbar)

        mViewPager = findViewById(R.id.viewpager) as ViewPager?
        mViewPager!!.offscreenPageLimit = 2
        setupViewPager(mViewPager)

        mTabLayout = findViewById(R.id.tabs) as TabLayout?
        mTabLayout!!.setupWithViewPager(mViewPager)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        mViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        mNewsFragment = NewsFragment.getInstance()
        mCalendarFragment = CalendarFragment.getInstance()
        mSendNewsFragment = SendNewsFragment()
        mViewPagerAdapter!!.addFragment(mNewsFragment, "Новостная\nлента")
        mViewPagerAdapter!!.addFragment(mCalendarFragment, "Календарь событий")
        mViewPagerAdapter!!.addFragment(mSendNewsFragment, "Отправить новость")
        viewPager.adapter = mViewPagerAdapter
    }

    override fun onLocationChanged(location: Location) {
        lat = location.latitude
        lng = location.longitude
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

    }

    override fun onProviderEnabled(provider: String) {
        //Toast.makeText(this, "Enabled new provider " + mProvider, Toast.LENGTH_SHORT).show();
    }

    override fun onProviderDisabled(provider: String) {
        //Toast.makeText(this, "Disabled provider " + mProvider, Toast.LENGTH_SHORT).show();
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(index: Int): Fragment {
            return mFragmentList[index]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        fun replaceFragment(fragment: Fragment, index: Int) {
            mFragmentList.removeAt(index)
            mFragmentList.add(index, fragment)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mLocationManager!!.requestLocationUpdates(mProvider, 400, 1f, this)
    }

    override fun onPause() {
        super.onPause()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mLocationManager!!.removeUpdates(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mConnReceiver)
    }

    private val mConnReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            if (netInfo != null && netInfo.isConnected) {
                NewsFragment.getInstance().mReady = true
                CalendarFragment.getInstance().mReady = true
                if (!hasData) {
                    if (CalendarFragment.getInstance().mDoneLoading)
                        CalendarFragment.getInstance().startCalendarService()
                    if (NewsFragment.getInstance().mDoneLoading)
                        NewsFragment.getInstance().startService()
                }
                hasData = true
                hasInternet = true
            } else {
                hasInternet = false
            }
        }
    }

    fun hasInternet(): Boolean {
        return hasInternet
    }

    fun hasData(): Boolean {
        return hasData
    }

    fun initFlags(size: Int) {
        flags = BooleanArray(size)
        flags[0] = true
    }

    fun switchFlag(pos: Int) {
        flags[pos] = !flags!![pos]
    }

    fun getFlag(pos: Int): Boolean {
        return flags!![pos]
    }

}
