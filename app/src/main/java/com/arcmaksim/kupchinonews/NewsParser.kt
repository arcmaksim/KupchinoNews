package com.arcmaksim.kupchinonews

import android.graphics.drawable.Drawable
import android.util.Xml

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

class NewsParser {

    private val ns: String? = null
    private val fTitle = "title"
    private val fLink = "link"
    private val fDescription = "content:encoded"
    private val fPubDate = "pubDate"
    private val fCreator = "dc:creator"

    internal val fImageStartTag = "src=\""
    internal val fDesriptionTag = "</div>\r\n\t<p>"
    internal val fDescriptionTitleFinishTag = "\" alt"
    //internal val fTagMask = "\\<[^\\>]*\\>"
    internal val fTagMask = "<[^>]*>"
    internal val fDivTag = "(?s)<div>.*?</div>"

    internal val fOldDateFormat: DateFormat = SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss", Locale.ENGLISH)
    internal val fNewDateFormat: DateFormat = SimpleDateFormat("kk:mm  dd.MM.yyyy", Locale.ENGLISH)

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
        parser.require(XmlPullParser.START_TAG, null, "rss")
        var title: String = ""
        var link: String = ""
        var description: String = ""
        var pubDate: String = ""
        var creator: String = ""
        var image: Drawable? = null
        var lock = true
        val items = ArrayList<NewsItem>()
        var isEnd = false
        while (parser.next() != XmlPullParser.END_DOCUMENT) {

            if(parser.eventType == XmlPullParser.END_TAG && parser.name == "item") {
                if (isEnd) {
                    items.add(NewsItem(title, link, description, pubDate, creator, image))
                    image = null
                }
                isEnd = !isEnd
            }

            if (!lock) {
                when (parser.name) {

                    fTitle -> title = readTag(parser, fTitle)

                    fLink -> link = readTag(parser, fLink)

                    fDescription -> {
                        description = readTag(parser, fDescription)

                        /*val asd = description.indexOf(fImageStartTag)
                        if (asd != -1) {
                            val url = description.substring(description.indexOf(fImageStartTag) + fImageStartTag.length,
                                    description.indexOf(fDescriptionTitleFinishTag))
                            //image = LoadImageFromWebOperations(url)
                        } else {
                            //image = null
                        }*/

                        image = null

                        description = description.replace(fDivTag.toRegex(), "").replace(fTagMask.toRegex(), "")
                    }

                    fPubDate -> {
                        pubDate = readTag(parser, fPubDate)

                        var d: Date? = null
                        try {
                            d = fOldDateFormat.parse(pubDate)
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }

                        pubDate = fNewDateFormat.format(d)
                    }

                    fCreator -> creator = readTag(parser, fCreator)
                }
            } else {
                if (parser.name == "item")
                    lock = false
            }
            /*if (title != null && link != null && description != null && pubDate != null && creator != null) {
                items.add(NewsItem(title, link, description, pubDate, creator, image))
                //title = link = description = pubDate = creator = null
                image = null
            }*/
        }
        return items
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readTag(parser: XmlPullParser, tag: String): String {
        parser.require(XmlPullParser.START_TAG, ns, tag)
        val title = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, tag)
        return title
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }
}