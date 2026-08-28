package com.linkly.link

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.net.URI

@RestController
class LinkController(private val linkService: LinkService) {

    @PostMapping("/api/links")
    @ResponseStatus(HttpStatus.CREATED)
    fun createShortUrl(@Valid @RequestBody request: CreateLinkRequest) : LinkResponse {
        return linkService.create(request.url)
    }

    @GetMapping("/api/links/{code}")
    fun getUrl(@PathVariable code: String) : LinkResponse {
        return linkService.findByCode(code)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown short code")
    }

    @GetMapping("/r/{code}")
    fun redirect(@PathVariable code: String) : ResponseEntity<Void> {
        val link = linkService.findByCode(code)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown short code")

        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create(link.url))
            .build()
    }
}