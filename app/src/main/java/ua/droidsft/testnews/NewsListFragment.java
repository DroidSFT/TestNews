package ua.droidsft.testnews;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fragment represents the list of news.
 * Created by Vlad on 18.04.2016.
 */
public class NewsListFragment extends Fragment {
    private RecyclerView mNewsRecyclerView;
    private TextView mNoNewsTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<NewsItem> mItems = new ArrayList<>();
    private NewsAdapter mAdapter;

    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // No need to re-create fragment on rotates, etc.
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fagment_list_news, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateItems(false);
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark);

        mNewsRecyclerView = (RecyclerView) v.findViewById(R.id.news_list_recycler);
        mNewsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNoNewsTextView = (TextView) v.findViewById(R.id.no_news_text);
        // Setup adapter immediately if already got items (ex. after rotate).
        if (!mItems.isEmpty()) {
            setupAdapter();
        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        // If have not got any items (ex. first launch),
        // try to load news from cache DB (or from net if DB is empty).
        if (mItems.isEmpty()) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                    updateItems(true);
                }
            });
        }
    }

    private void setupAdapter() {
        if (isAdded()) { // No need to setup adapter if fragment is not added
            if (mAdapter == null) {
                mAdapter = new NewsAdapter(mItems);
                mNewsRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setNews(mItems);
                mAdapter.notifyDataSetChanged();
                if (mNewsRecyclerView.getAdapter() == null) {
                    mNewsRecyclerView.setAdapter(mAdapter);
                }
            }
            mNewsRecyclerView.setVisibility(View.VISIBLE);
            mNoNewsTextView.setVisibility(View.GONE);
        }
    }

    private class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private NewsItem mNewsItem;

        public NewsHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.item_title_text);
            mDateTextView = (TextView) itemView.findViewById(R.id.item_subtitle_text);
            itemView.setOnClickListener(this);
        }

        public void bindNewsItem(NewsItem newsItem) {
            mNewsItem = newsItem;
            mTitleTextView.setText(mNewsItem.getTitle());
            DateFormat sdf = DateFormat.getDateTimeInstance();
            mDateTextView.setText(sdf.format(mNewsItem.getDate()));
        }

        @Override
        public void onClick(View v) {
            Intent i = NewsPageActivity.newIntent(getActivity(), mNewsItem.getPageUri());
            startActivity(i);
        }
    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {
        private List<NewsItem> mNewsItems;

        public NewsAdapter(List<NewsItem> newsItems) {
            mNewsItems = newsItems;
        }

        @Override
        public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item, parent, false);
            return new NewsHolder(view);
        }

        @Override
        public void onBindViewHolder(NewsHolder holder, int position) {
            NewsItem item = mNewsItems.get(position);
            holder.bindNewsItem(item);
        }

        public void setNews(List<NewsItem> news) {
            mNewsItems = news;
        }

        @Override
        public int getItemCount() {
            return mNewsItems.size();
        }
    }

    private void updateItems(boolean useCache) {
        new FetchNews(useCache).execute();
    }

    // AsyncTask for retrieving news in background thread.
    // As Fragment is retained during rotates, there will be no problems with using AsyncTask.
    // In future, it can be replaced with another threading primitive,
    // which will be more suitable for more complex tasks.
    private class FetchNews extends AsyncTask<Void, Void, List<NewsItem>> {
        boolean mUseCache;

        public FetchNews(boolean useCache) {
            mUseCache = useCache;
        }

        @Override
        protected List<NewsItem> doInBackground(Void... params) {
            List<NewsItem> items = NewsLab.get(getActivity()).getNewsItems(mUseCache);
            // Perform sorting in background thread
            Collections.sort(items, new Comparator<NewsItem>() {
                @Override
                public int compare(NewsItem item1, NewsItem item2) {
                    return item2.getDate().compareTo(item1.getDate());
                }
            });
            return items;
        }

        @Override
        protected void onPostExecute(List<NewsItem> newsItems) {
            if (!newsItems.isEmpty()) {
                mItems = newsItems;
                setupAdapter();
            } else {
                Toast.makeText(getActivity(), R.string.no_news_toast, Toast.LENGTH_LONG).show();
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
