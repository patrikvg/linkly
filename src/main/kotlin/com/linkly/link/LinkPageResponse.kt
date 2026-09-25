package com.linkly.link

data class LinkPageResponse(
    val content: List<LinkResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)
