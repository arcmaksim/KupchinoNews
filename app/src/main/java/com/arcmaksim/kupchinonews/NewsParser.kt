package com.arcmaksim.kupchinonews

import android.content.Context
import android.graphics.Bitmap
import android.util.Xml
import com.arcmaksim.kupchinonews.commons.Cleaner
import com.squareup.picasso.Picasso
import org.joda.time.format.DateTimeFormat
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.util.*

class NewsParser(private var mContext: Context) {

    private val mTitleTag = "title"
    private val mLinkTag = "link"
    private val mDescriptionTag = "content:encoded"
    private val mPublicationDateTag = "pubDate"
    private val mCreatorTag = "dc:creator"

    internal val mImageOpeningTag = "src=\""
    internal val mImageClosingTag = "\" alt"

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): ArrayList<NewsItem> {
        inputStream.use { inputStream ->
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
        var title: String = ""
        var link: String = ""
        var description: String = ""
        var pubDate: String = ""
        var creator: String = ""
        var image: Bitmap? = null
        val items = ArrayList<NewsItem>()

        while (parser.next() != XmlPullParser.END_DOCUMENT) {

            if(parser.eventType == XmlPullParser.END_TAG && parser.name == "item") {
                items.add(NewsItem(title, link, description, pubDate, creator, image))
                image = null
            }

            when(parser.name) {
                mTitleTag -> title = readTag(parser, mTitleTag)
                mLinkTag -> link = readTag(parser, mLinkTag)
                mDescriptionTag -> {
                    description = readTag(parser, mDescriptionTag)

                    val imageUrlPosition = description.indexOf(mImageOpeningTag)
                    if(imageUrlPosition != -1) {
                        val url = description.substring(imageUrlPosition + mImageOpeningTag.length,
                                description.indexOf(mImageClosingTag))
                        image = Picasso.with(mContext).load(url).get()
                    }

                    description = Cleaner.cleanHtml(description)
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

}