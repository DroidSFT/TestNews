package ua.droidsft.testnews.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import ua.droidsft.testnews.NewsItem;
import ua.droidsft.testnews.database.NewsDbSchema.NewsTable;

/**
 * Created by Vlad on 18.04.2016.
 */
public class NewsCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public NewsCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public NewsItem getNewsItem() {
        String id = getString(getColumnIndex(NewsTable.Cols.ID));
        String title = getString(getColumnIndex(NewsTable.Cols.TITLE));
        String date = getString(getColumnIndex(NewsTable.Cols.DATE));
        String link = getString(getColumnIndex(NewsTable.Cols.LINK));

        return new NewsItem(title, link, date, id);
    }
}
