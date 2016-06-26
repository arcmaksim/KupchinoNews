package ru.kupchinonews.rssreader.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.kupchinonews.rssreader.News;
import ru.kupchinonews.rssreader.NewsAdapter;
import ru.kupchinonews.rssreader.NewsItem;
import ru.kupchinonews.rssreader.R;
import ru.kupchinonews.rssreader.activity.MainActivity;
import ru.kupchinonews.rssreader.services.RssService;

public class NewsFragment extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView listView;
    private RecyclerView mRecyclerView;
    private TextView mNoInternetTextView;
    private View view;
    private MainActivity act;
    private NewsAdapter mAdapter;
    private static NewsFragment mInstance;
    public static boolean mReady = false;
    public static boolean mDoneLoading = false;

    public NewsFragment() {}

    public static NewsFragment getInstance() {
        if(mInstance == null)
            mInstance = new NewsFragment();
        return  mInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_news, container, false);

            mNoInternetTextView = (TextView) view.findViewById(R.id.no_internet_text_view);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

            if(!act.hasInternet()) {
                mNoInternetTextView.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
            mSwipeRefreshLayout.setOnRefreshListener(this);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
            //mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            //mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            //mRecyclerView.setOnScrollChangeListener();
            listView = (ListView) view.findViewById(R.id.listView);
            listView.setOnItemClickListener(this);
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    boolean enable = false;
                    if (listView != null && listView.getChildCount() > 0) {
                        boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                        boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                        enable = firstItemVisible && topOfFirstItemVisible;
                    }
                    mSwipeRefreshLayout.setEnabled(enable);
                }
            });

            mDoneLoading = true;
            if(mReady)
                startService();
        } else {
            if (Build.VERSION.SDK_INT <= 10) {
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.removeView(view);
            }
        }
        return view;
    }

    @Override
    public void onAttach(Activity myActivity) {
        super.onAttach(myActivity);
        this.act = (MainActivity) myActivity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        act.switchFlag(position);
        mAdapter.notifyDataSetChanged();
    }

    public void startService() {
        mNoInternetTextView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getActivity(), RssService.class);
        intent.putExtra(RssService.RECEIVER, resultReceiver);
        getActivity().startService(intent);
    }

    private final ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
        @SuppressWarnings("unchecked")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            ArrayList<NewsItem> items = (ArrayList<NewsItem>) resultData.getSerializable(RssService.NEWS);

            if (items != null) {
                News.get().setNews(items);
                mAdapter = new NewsAdapter(getActivity(), items, act);
                listView.setAdapter(mAdapter);
                //mRecyclerView.setAdapter(new NewsAdapter2(items));
            } else {
                Toast.makeText(getActivity(), getString(R.string.rss_error), Toast.LENGTH_LONG).show();
            }

            act.initFlags(items.size());

            mProgressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            //mRecyclerView.setVisibility(View.VISIBLE);
            getActivity().stopService(new Intent(getActivity(), RssService.class));
            if(mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);
        }
    };

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        if (!act.hasInternet()) {
            Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            //mRecyclerView.setVisibility(View.GONE);
            startService();
        }
    }
}
