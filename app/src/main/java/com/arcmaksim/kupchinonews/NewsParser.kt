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

    private val ns: String? = null
    private val fTitle = "title"
    private val fLink = "link"
    private val fDescription = "content:encoded"
    private val fPubDate = "pubDate"
    private val fCreator = "dc:creator"

    internal val fImageStartTag = "src=\""
    internal val fDesriptionTag = "</div>\r\n\t<p>"
    internal val fDescriptionTitleFinishTag = "\" alt"

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): ArrayList<NewsItem> {
        try {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readFeed(parser)
        } finally {
            inputStream.close()
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): ArrayList<NewsItem> {

        val formatIn = DateTimeFormat.forPattern("EEE, dd MMM yyy HH:mm:ss Z").withLocale(Locale("EN"))
        val formatOut = DateTimeFormat.forPattern("d MMMM kk:mm").withLocale(Locale("RU"))

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
                fTitle -> title = readTag(parser, fTitle)
                fLink -> link = readTag(parser, fLink)
                fDescription -> {
                    description = readTag(parser, fDescription)

                    val asd = description.indexOf(fImageStartTag)
                    if(asd != -1) {
                        val url = description.substring(description.indexOf(fImageStartTag) + fImageStartTag.length,
                                description.indexOf(fDescriptionTitleFinishTag))
                        image = Picasso.with(mContext).load(url).get()
                    }

                    description = Cleaner.cleanHtml(description)
                }
                fPubDate -> {
                    val tag = readTag(parser, fPubDate)
                    val date = formatIn.parseDateTime(tag)
                    pubDate = formatOut.print(date)
                }
                fCreator -> creator = readTag(parser, fCreator)
            }
        }

        return items
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readTag(parser: XmlPullParser, tag: String): String {
        parser.require(XmlPullParser.START_TAG, ns, tag)
        val content = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, tag)
        return content
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var text = ""
        if (parser.next() == XmlPullParser.TEXT) {
            text = parser.text
            parser.nextTag()
        }
        return text
    }

}