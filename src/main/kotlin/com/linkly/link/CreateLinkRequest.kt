package com.linkly.link

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL

data class CreateLinkRequest(
    @field:NotBlank(message = "Url must not be blank!")
    @field:URL(message = "Url must be a valid URL!")
    val url: String
)
