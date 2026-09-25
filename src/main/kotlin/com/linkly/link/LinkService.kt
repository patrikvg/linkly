package com.linkly.link

import org.hibernate.annotations.NotFound
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.awt.print.Pageable

@Service
class LinkService(private val repository: LinkRepository) {

    private fun generateCode(): String {
        val allowedChar = ('A'..'Z') + ('a'..'z') + ('0'..'9')

        val code = (1..7)
            .map { allowedChar.random() }
            .joinToString("")

        return code
    }

    fun create(url: String): LinkResponse {
        val urlAlreadyExits = repository.findByUrl(url)
        
        if (urlAlreadyExits != null) {
            return LinkResponse(urlAlreadyExits.code, url, "/r/${urlAlreadyExits.code}", urlAlreadyExits.clicks)
        }
        
        
        var count = 0
        var code = generateCode()

        // if code exists new one will be created but a maximum of 5 times
        while (repository.findByCode(code) != null && count <= 5) {
            code = generateCode()
            count++
        }

        // if the code still exists, error will be thrown
        if (repository.findByCode(code) == null) {
            repository.save(LinkEntity(code = code, url = url))
        }
        else {
            throw IllegalStateException("The code already exists!")
        }

        val shortUrl = "/r/$code"

        return LinkResponse(code, url, shortUrl, 0)
    }

    fun findByCode(code: String) : LinkResponse? {
        val url = repository.findByCode(code) ?: return null

        val shortUrl = "/r/${url.code}"
        return LinkResponse(url.code, url.url, shortUrl, url.clicks)
    }

    fun registerClick(code: String): LinkResponse? {
        val url = repository.findByCode(code) ?: return null

        val shortUrl = "/r/${url.code}"

        // increment the clicks on the url and save it into the DB
        url.clicks++
        repository.save(url)

        return LinkResponse(url.code, url.url, shortUrl, url.clicks)
    }
    
    fun delete(code: String) : Boolean {
        val shortUrl = repository.findByCode(code) ?: return false
        repository.delete(shortUrl)
        return true
    }
    
    fun findAllShortUrl() : List<LinkResponse> {
        // findAll ist eine eingebaute JpaRepository Mehode (LinkRepository.kt)
        val allShortUrl = repository.findAll()
            .map { url -> LinkResponse(url.code, url.url, "/r/${url.code}", url.clicks)}
            
        return allShortUrl
    }
    
    fun findAllShortUrlWithPageable(page: Int, size: Int) : LinkPageResponse {
        val pageable = PageRequest.of(page, size)
        val result = repository.findAll(pageable)
        
        // comverting the Page<LinkEntity> into a LinkResponse
        val links = result.content.map { entity ->
            LinkResponse(entity.code, entity.url, "/r/${entity.code}", entity.clicks)
        }
        
        return LinkPageResponse(
            content = links,
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages
        )
    }
}