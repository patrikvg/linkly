package com.linkly

import com.linkly.link.LinkController
import com.linkly.link.LinkResponse
import com.linkly.link.LinkService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(LinkController::class)
class LinkControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc // simuliert HTTP

    // ersetzt meinen echten LinkService durch einen Mock (kopie)
    @MockitoBean
    private lateinit var linkService: LinkService

    @Test fun createLink_return201() {
        Mockito.`when`(linkService.create("https://google.com"))
            .thenReturn(LinkResponse("abc1234", "https://google.com", "/r/abc1234", 0))

        mockMvc.perform(
            post("/api/links")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"url": "https://google.com"}""")
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code").value("abc1234"))
    }

    @Test fun createLink_emptyURL_return400() {
        mockMvc.perform(
            post("/api/links")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"url": ""}""")
        )
            .andExpect(status().isBadRequest())
    }
}