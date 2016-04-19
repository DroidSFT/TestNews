package ua.droidsft.testnews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.webkit.WebView;

/**
 * Created by Vlad on 19.04.2016.
 */
public class NewsPageActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context, Uri newsPageUri) {
        Intent intent = new Intent(context, NewsPageActivity.class);
        intent.setData(newsPageUri);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return NewsPageFragment.newInstance(getIntent().getData());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, NewsListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        NewsPageFragment fragment = (NewsPageFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            super.onBackPressed();
            return;
        }

        WebView webView = fragment.getWebView();
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
