package ua.droidsft.testnews;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Fetcher for getting and parsing news from RSS feed.
 * Created by Vlad on 18.04.2016.
 */
public class NewsFetcher {
    private static final String TAG = "NewsFetcher";

    // Number of news items is adjustable by adding &num=XX to Google News RSS URL
    private static final String RSS_URL =
            "https://news.google.com.ua/news?cf=all&hl=ru&pz=1&ned=ru_ua&output=rss&num=20";

    private static final int TIMEOUT = 15000;

    public List<NewsItem> fetchNews() {
        List<NewsItem> items = new ArrayList<>();

        URL url;
        try {
            url = new URL(RSS_URL);

            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);

            HttpURLConnection httpConnection = (HttpURLConnection) connection;

            int responseCode = httpConnection.getResponseCode();

            // Try to parse news items
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "fetchNews: HTTP_OK");

                InputStream in = httpConnection.getInputStream();

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                Document dom = db.parse(in);

                Element docElement = dom.getDocumentElement();

                NodeList nl = docElement.getElementsByTagName("item");

                if (nl != null && nl.getLength() > 0) {
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element entry = (Element) nl.item(i);
                        Element titleElement = (Element) entry.getElementsByTagName("title").item(0);
                        Element linkElement = (Element) entry.getElementsByTagName("link").item(0);
                        Element dateElement = (Element) entry.getElementsByTagName("pubDate").item(0);
                        Element idElement = (Element) entry.getElementsByTagName("guid").item(0);

                        String title = titleElement.getFirstChild().getNodeValue();
                        String link = linkElement.getFirstChild().getNodeValue();
                        String dateString = dateElement.getFirstChild().getNodeValue();
                        String id = idElement.getFirstChild().getNodeValue();

                        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
                        Date date = new Date(); // Current date/time will be used if date parsing fails
                        try {
                            date = sdf.parse(dateString);
                        } catch (ParseException e) {
                            Log.d(TAG, "fetchNews: ParseException while parsing date, current date will be used");
                        }

                        NewsItem item = new NewsItem(title, link, date, id);

                        items.add(item);
                    }
                }
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "fetchNews: MalformedURLException", e);
        } catch (IOException e) {
            Log.e(TAG, "fetchNews: IOException", e);
        } catch (ParserConfigurationException e) {
            Log.e(TAG, "fetchNews: ParserConfigurationException", e);
        } catch (SAXException e) {
            Log.e(TAG, "fetchNews: SAXException", e);
        }

        return items;
    }

}
