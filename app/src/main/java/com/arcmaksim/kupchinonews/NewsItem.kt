package com.arcmaksim.kupchinonews

import android.graphics.drawable.Drawable

data class NewsItem(val title: String = "",
               val link: String = "",
               val description: String = "",
               val pubDate: String = "",
               val creator: String = "",
               val image: Drawable? = null)