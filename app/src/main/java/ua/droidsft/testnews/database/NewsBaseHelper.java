package ua.droidsft.testnews.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ua.droidsft.testnews.database.NewsDbSchema.NewsTable;

/**
 * Created by Vlad on 18.04.2016.
 */
public class NewsBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    public static final String DATABASE_NAME = "newsBase.db";

    public NewsBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + NewsTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                NewsTable.Cols.ID + ", " +
                NewsTable.Cols.TITLE + ", " +
                NewsTable.Cols.DATE + ", " +
                NewsTable.Cols.LINK +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Database is used only for caching, so just drop it on upgrade
        db.execSQL("drop table if exist " + NewsTable.NAME);
        onCreate(db);
    }
}
