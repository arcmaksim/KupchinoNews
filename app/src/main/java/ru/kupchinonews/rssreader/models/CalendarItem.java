package ru.kupchinonews.rssreader.models;

import java.util.ArrayList;
import java.util.Date;

public class CalendarItem {

    private String mCategories;
    private String mContact;
    private String mDescription;
    private String mLocation;
    private String mSummary;
    private String mURL;
    private String mXTags;
    private Date mDTStart;
    private Date mDTEnd;
    private byte mRRule;
    private ArrayList<Date> mRDate;

    public CalendarItem(String categories, String description, String location, String summary, String xtags) {
        mCategories = categories;
        mDescription = description;
        mLocation = location;
        mSummary = summary;
        mXTags = xtags;
    }

    public CalendarItem() {
        mRDate = new ArrayList<Date>();
    }

    public String getCategories() {
        return mCategories;
    }

    public void setCategories(String mCategories) {
        this.mCategories = mCategories;
    }

    public String getContact() {
        return mContact;
    }

    public void setContact(String mContact) {
        this.mContact = mContact;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getXTags() {
        return mXTags;
    }

    public void setXTags(String mXTags) {
        this.mXTags = mXTags;
    }

    public String getURL() {
        return mURL;
    }

    public void setURL(String mURL) {
        this.mURL = mURL;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String mSummary) {
        this.mSummary = mSummary;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public byte getRRule() {
        return mRRule;
    }

    public byte getRRuleBit(int position) {
        return (byte)((mRRule >> position) & 1);
    }

    public void setRRule(byte days) {
        mRRule = days;
    }

    public Date getDTEnd() {
        return mDTEnd;
    }

    public void setDTEnd(Date mDTEnd) {
        this.mDTEnd = mDTEnd;
    }

    public Date getDTStart() {
        return mDTStart;
    }

    public void setDTStart(Date mDTStart) {
        this.mDTStart = mDTStart;
    }

    public void addRDate(Date date) {
        mRDate.add(date);
    }

    public ArrayList<Date> getRDate() {
        return mRDate;
    }
}
