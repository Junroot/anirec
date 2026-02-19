package com.anirec.global.exception

class AnimeNotFoundException(
    message: String = "Anime not found",
) : RuntimeException(message)
