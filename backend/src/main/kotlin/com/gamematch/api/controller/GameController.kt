package com.gamematch.api.controller

import com.gamematch.api.model.Game
import com.gamematch.api.repository.GameRepository
import com.gamematch.api.service.findGame
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/games")
@Tag(name = "Jogos", description = "Catálogo de jogos")
class GameController(private val games: GameRepository) {

    @GetMapping
    @Operation(summary = "Lista todos os jogos do catálogo")
    fun findAll(): List<Game> = games.findAll(Sort.by("name"))

    @GetMapping("/{id}")
    @Operation(summary = "Busca um jogo pelo id")
    fun findById(@PathVariable id: Long): Game = games.findGame(id)
}
