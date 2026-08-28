package com.linkly.link

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class LinkService(private val repository: LinkRepository) {

    fun create(url: String): LinkResponse {
        val allowedChar = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        val code = (1..7)
            .map { allowedChar.random() }
            .joinToString("")

        repository.save(LinkEntity(code = code, url = url))
        val shortUrl = "/r/$code"

        return LinkResponse(code, url, shortUrl)
    }

    fun findByCode(code: String) : LinkResponse? {
        val url = repository.findByCode(code) ?: return null

        val shortUrl = "/r/${url.code}"
        return LinkResponse(url.code, url.url, shortUrl)
    }
}