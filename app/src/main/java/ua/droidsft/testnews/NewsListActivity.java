package ua.droidsft.testnews;

import android.support.v4.app.Fragment;

public class NewsListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return NewsListFragment.newInstance();
    }

}
