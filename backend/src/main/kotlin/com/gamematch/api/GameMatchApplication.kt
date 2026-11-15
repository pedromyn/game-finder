package com.gamematch.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GameMatchApplication

fun main(args: Array<String>) {
    runApplication<GameMatchApplication>(*args)
}
