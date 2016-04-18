package ua.droidsft.testnews.database;

/**
 * Created by Vlad on 18.04.2016.
 */
public class NewsDbSchema {
    public static final class NewsTable {
        public static final String NAME = "news";

        public static final class Cols {
            public static final String ID = "id";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String LINK = "link";
        }
    }
}
