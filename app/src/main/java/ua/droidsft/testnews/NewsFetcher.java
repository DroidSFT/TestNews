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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Vlad on 18.04.2016.
 */
public class NewsFetcher {
    private static final String TAG = "NewsFetcher";

    private static final String RSS_URL =
            "https://news.google.com.ua/news?cf=all&hl=ru&pz=1&ned=ru_ua&output=rss";

    public List<NewsItem> fetchNews() {
        List<NewsItem> items = new ArrayList<>();

        URL url;
        try {
            url = new URL(RSS_URL);

            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;

            int responseCode = httpConnection.getResponseCode();

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
                        String date = dateElement.getFirstChild().getNodeValue();
                        String id = idElement.getFirstChild().getNodeValue();

                        NewsItem item = new NewsItem(title, link, date, id);

                        items.add(item);
                    }
                }
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, "fetchNews: MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "fetchNews: IOException");
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            Log.d(TAG, "fetchNews: ParserConfigurationException");
            e.printStackTrace();
        } catch (SAXException e) {
            Log.d(TAG, "fetchNews: SAXException");
            e.printStackTrace();
        }

        return items;
    }

}
