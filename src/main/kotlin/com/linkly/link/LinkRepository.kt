package com.linkly.link

import org.springframework.data.jpa.repository.JpaRepository

interface LinkRepository : JpaRepository<LinkEntity, String> {
    fun findByCode(code: String): LinkEntity?
}