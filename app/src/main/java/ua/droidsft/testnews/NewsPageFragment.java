package ua.droidsft.testnews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Fragment shows the source website of selected news item.
 * Created by Vlad on 19.04.2016.
 */
public class NewsPageFragment extends Fragment {
    private static final String NEWS_URI = "news_page_url";

    private Uri mUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static NewsPageFragment newInstance(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(NEWS_URI, uri);
        NewsPageFragment fragment = new NewsPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUri = getArguments().getParcelable(NEWS_URI);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();

        // Use source site as actionbar title
        String title = mUri.toString().split("/")[2];

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news_page, container, false);
        mProgressBar = (ProgressBar) v.findViewById(R.id.news_page_progressbar);
        mProgressBar.setMax(100);
        mWebView = (WebView) v.findViewById(R.id.news_page_web_view);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true); // Needed to be able to open some sites
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        mWebView.setWebChromeClient(new WebChromeClient() {
            // Show site loading progress bar and hide it when loading finish
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            // Use news page title as actionbar subtitle
            @Override
            public void onReceivedTitle(WebView view, String title) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                ActionBar actionBar = activity.getSupportActionBar();
                if (actionBar == null) return;
                actionBar.setSubtitle(title);
            }
        });

        // Handle non http(s) links by allowing user to choose an app for it
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String[] schema = url.split(":");
                if (schema[0].equals("http") || schema[0].equals("https")) {
                    return false;
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(Intent.createChooser(i, getString(R.string.chooser_title)));
                    return true;
                }
            }
        });

        mWebView.loadUrl(mUri.toString());

        return v;
    }

    public WebView getWebView() {
        return mWebView;
    }
}
