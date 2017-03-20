package com.arcmaksim.kupchinonews.commons

import android.util.Xml
import com.arcmaksim.kupchinonews.newsfeed.NewsItem
import org.joda.time.format.DateTimeFormat
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.util.*

class NewsFeedParser {

    private val mTitleTag = "title"
    private val mLinkTag = "link"
    private val mDescriptionTag = "content:encoded"
    private val mPublicationDateTag = "pubDate"
    private val mCreatorTag = "dc:creator"

    private val mImageOpeningTag = "src=\""
    private val mImageClosingTag = "\" alt"

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): ArrayList<NewsItem> {
        inputStream.use {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): ArrayList<NewsItem> {

        val givenDateFormat = DateTimeFormat.forPattern("EEE, dd MMM yyy HH:mm:ss Z").withLocale(Locale("EN"))
        val targetDateFormat = DateTimeFormat.forPattern("d MMMM kk:mm").withLocale(Locale("RU"))

        parser.require(XmlPullParser.START_TAG, null, "rss")
        var title = ""
        var link = ""
        var description = ""
        var pubDate = ""
        var creator = ""
        var imageUrl = ""
        val items = ArrayList<NewsItem>()

        while (parser.next() != XmlPullParser.END_DOCUMENT) {

            if (parser.eventType == XmlPullParser.END_TAG && parser.name == "item") {
                items.add(NewsItem(title, link, description, pubDate, creator, imageUrl))
            }

            when (parser.name) {
                mTitleTag -> title = readTag(parser, mTitleTag)
                mLinkTag -> link = readTag(parser, mLinkTag)
                mDescriptionTag -> {
                    description = readTag(parser, mDescriptionTag)
                    imageUrl = ""
                    //imageUrl = extractImageUrl(description)
                }
                mPublicationDateTag -> {
                    val tagContent = readTag(parser, mPublicationDateTag)
                    val date = givenDateFormat.parseDateTime(tagContent)
                    pubDate = targetDateFormat.print(date)
                }
                mCreatorTag -> creator = readTag(parser, mCreatorTag)
            }
        }

        return items
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readTag(parser: XmlPullParser, tag: String): String {
        parser.require(XmlPullParser.START_TAG, null, tag)
        val tagContent = readText(parser)
        parser.require(XmlPullParser.END_TAG, null, tag)
        return tagContent
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var tagText = ""
        if (parser.next() == XmlPullParser.TEXT) {
            tagText = parser.text
            parser.nextTag()
        }
        return tagText
    }

    private fun extractImageUrl(description: String): String {
        var imageUrl = ""

        if (description.indexOf(mImageOpeningTag) != -1) {
            imageUrl = description.substring(description.indexOf(mImageOpeningTag)
                    + mImageOpeningTag.length, description.indexOf(mImageClosingTag))
        }

        return imageUrl
    }

}