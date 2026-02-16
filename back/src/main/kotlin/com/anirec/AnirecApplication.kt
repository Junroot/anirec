package com.anirec

import com.anirec.global.config.AppProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class)
class AnirecApplication

fun main(args: Array<String>) {
	runApplication<AnirecApplication>(*args)
}
