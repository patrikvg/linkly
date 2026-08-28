package com.linkly

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LinklyApplication

fun main(args: Array<String>) {
	runApplication<LinklyApplication>(*args)
}
