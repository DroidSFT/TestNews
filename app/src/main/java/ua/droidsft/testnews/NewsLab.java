package ua.droidsft.testnews;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ua.droidsft.testnews.database.NewsBaseHelper;
import ua.droidsft.testnews.database.NewsCursorWrapper;
import ua.droidsft.testnews.database.NewsDbSchema.NewsTable;

/**
 * Created by Vlad on 18.04.2016.
 */
public class NewsLab {
    private static final String TAG = "NewsLab";

    private static NewsLab sNewsLab;

    private SQLiteDatabase mDatabase;

    private NewsLab(Context context) {
        mDatabase = new NewsBaseHelper(context.getApplicationContext()).getWritableDatabase();
    }

    public static NewsLab get(Context context) {
        if (sNewsLab == null) {
            sNewsLab = new NewsLab(context);
        }
        return sNewsLab;
    }

    public List<NewsItem> getNewsItems(boolean useCache) {
        if (useCache) {
            Cursor cursor = mDatabase.rawQuery("select count(*) from " + NewsTable.NAME, null);
            cursor.moveToFirst();
            int itemsInDb = cursor.getInt(0);
            Log.i(TAG, "getNewsItems: itemsInDb = " + itemsInDb);
            cursor.close();
            if (itemsInDb > 0) {
                return getNewsItemsFromDb();
            } else {
                return getNewsItemsFromNet();
            }
        } else {
            return getNewsItemsFromNet();
        }
    }

    public void addNewsItemToDb(NewsItem item) {
        ContentValues values = getContentValues(item);
        mDatabase.insert(NewsTable.NAME, null, values);
    }

    public List<NewsItem> getNewsItemsFromDb() {
        List<NewsItem> items = new ArrayList<>();
        NewsCursorWrapper cursorWrapper = queryNews(null, null);

        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                items.add(cursorWrapper.getNewsItem());
                cursorWrapper.moveToNext();
            }
        } finally {
            cursorWrapper.close();
        }

        return items;
    }

    public List<NewsItem> getNewsItemsFromNet() {
        List<NewsItem> items = new NewsFetcher().fetchNews();
        if (items.size() > 0) {
            clearDb();
            for (NewsItem item : items) {
                addNewsItemToDb(item);
            }
        }
        return items;
    }

    public void clearDb() {
        mDatabase.execSQL("delete from " + NewsTable.NAME);
        mDatabase.execSQL("vacuum");
    }

    private static ContentValues getContentValues(NewsItem item) {
        ContentValues values = new ContentValues();
        values.put(NewsTable.Cols.ID, item.getId());
        values.put(NewsTable.Cols.TITLE, item.getTitle());
        values.put(NewsTable.Cols.DATE, item.getDate());
        values.put(NewsTable.Cols.LINK, item.getLink());
        return values;
    }

    private NewsCursorWrapper queryNews(String whereClause, String[] whereArgs) {
        //Cursor will be freed up in a method, which will use it
        @SuppressLint("Recycle")
        Cursor cursor = mDatabase.query(
                NewsTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new NewsCursorWrapper(cursor);
    }
}
