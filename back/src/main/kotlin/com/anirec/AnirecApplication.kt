package com.anirec

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AnirecApplication

fun main(args: Array<String>) {
	runApplication<AnirecApplication>(*args)
}
