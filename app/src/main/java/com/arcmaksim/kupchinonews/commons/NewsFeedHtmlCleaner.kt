package com.arcmaksim.kupchinonews.commons

import org.htmlcleaner.HtmlCleaner
import org.htmlcleaner.TagNode

object NewsFeedHtmlCleaner {

    @JvmStatic
    fun cleanHtml(htmlString: String): String {
        val htmlCleaner = HtmlCleaner()
        // <br> causing empty lines
        htmlCleaner.properties.pruneTags = "iframe,br"

        val root = htmlCleaner.clean(htmlString)

        // Remove first <div> with title and image...
        root.getElementListByName("div", true)[0].removeFromTree()

        // ... hyperlinks...
        root.getElementListHavingAttribute("href", true)
                .forEach { tag -> tag.removeAttribute("href") }

        // ... unused tags...
        root.getElementListByName("span", true)
                .filter { it.hasAttribute("class") || it.hasAttribute("id") }
                .forEach { it.removeFromTree() }

        // ... empty paragraphs...
        root.getElementListByName("p", true)
                .filter { it.isEmpty }
                .forEach { it.removeFromTree() }

        // ... additional images...
        root.evaluateXPath("//div[@class='wp-caption alignnone']")
                .map { it as TagNode }
                .forEach { it.removeFromTree() }
        // ... and spaces
        root.evaluateXPath("//div[@class='page_post_sized_thumbs clear_fix']")
                .map { it as TagNode }
                .forEach { it.removeFromTree() }

        return htmlCleaner.getInnerHtml(root)
    }

}