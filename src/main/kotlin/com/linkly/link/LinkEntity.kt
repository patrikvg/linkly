package com.linkly.link

import jakarta.persistence.Entity

@Entity
data class LinkEntity(
    val code: String,
    val url: String
)
