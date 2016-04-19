package ua.droidsft.testnews;

import android.net.Uri;

import java.util.Date;

/**
 * Model of item of news
 * Created by Vlad on 18.04.2016.
 */
public class NewsItem {
    private String mTitle;
    private String mLink;
    private Date mDate;
    private String mId;

    public NewsItem(String title, String link, Date date, String id) {
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

    public Date getDate() {
        return mDate;
    }

    public String getId() {
        return mId;
    }

    // Returns the news item's source Uri by splitting link field
    public Uri getPageUri() {
        String[] linkSplit = mLink.split("url=");
        return Uri.parse(linkSplit[linkSplit.length - 1]);
    }

}
