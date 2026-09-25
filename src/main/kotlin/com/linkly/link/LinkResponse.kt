package com.linkly.link

data class LinkResponse(
    val code: String,
    val url: String,
    val shortUrl: String,
    var clicks: Int
)