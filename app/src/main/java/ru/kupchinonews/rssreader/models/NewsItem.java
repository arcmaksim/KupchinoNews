package ru.kupchinonews.rssreader.models;

import android.graphics.drawable.Drawable;

/**
 * Created by MeatBoy on 29.07.2015.
 */
public class NewsItem {

    private String mTitle;
    private String mLink;
    private String mDescription;
    private String mPubDate;
    private String mCreator;
    private Drawable mImage;

    public NewsItem(String title, String link, String description, String pubDate, String creator, Drawable image) {
        mTitle = title;
        mLink = link;
        mDescription = description;
        mPubDate = pubDate;
        mCreator = creator;
        mImage = image;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getLink() {
        return mLink;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getPubDate() {
        return mPubDate;
    }

    public String getCreator() {
        return mCreator;
    }

    public Drawable getImage() {
        return mImage;
    }
}
