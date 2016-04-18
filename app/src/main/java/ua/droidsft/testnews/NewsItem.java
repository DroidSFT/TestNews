package ua.droidsft.testnews;

/**
 * Created by Vlad on 18.04.2016.
 */
public class NewsItem {
    private String mTitle;
    private String mLink;
    private String mDate;
    private String mId;

    public NewsItem(String title, String link, String date, String id) {
        mTitle = title;
        mLink = link;
        mDate = date;
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getLink() {
        return mLink;
    }

    public String getDate() {
        return mDate;
    }

    public String getId() {
        return mId;
    }

    public String getUrl() {
        //TODO: parse link and return url
        return null;
    }

}
