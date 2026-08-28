package com.linkly.hello

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Erster HTTP-Einstiegspunkt.
 *
 * @RestController = diese Klasse beantwortet HTTP-Requests und gibt den
 * Rückgabewert direkt als JSON (bzw. Text) zurück — kein HTML-Template.
 */
@RestController
@RequestMapping("/api")
class HelloController {

	@GetMapping("/hello")
	fun hello(
		@RequestParam(defaultValue = "World") name: String,
	): Map<String, String> {
		return mapOf("message" to "Hello, $name!")
	}
}
