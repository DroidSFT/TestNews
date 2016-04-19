package ua.droidsft.testnews;

import android.support.v4.app.Fragment;

/**
 * Activity for NewsListFragment.
 * Created by Vlad on 18.04.2016.
 */
public class NewsListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return NewsListFragment.newInstance();
    }

}
