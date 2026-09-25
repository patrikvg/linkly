package com.linkly.link

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "links")
data class LinkEntity(
    @Id
    val code: String,
    val url: String,
    var clicks: Int = 0
)
