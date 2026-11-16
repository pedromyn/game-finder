package com.gamematch.api.service

import com.gamematch.api.model.Game
import com.gamematch.api.repository.GameRepository
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

// Erros devolvidos pela API. A mensagem vai no campo "message" do JSON e o app a mostra ao usuário.

fun badRequest(message: String) = ResponseStatusException(HttpStatus.BAD_REQUEST, message)

fun notFound(message: String) = ResponseStatusException(HttpStatus.NOT_FOUND, message)

fun unauthorized(message: String) = ResponseStatusException(HttpStatus.UNAUTHORIZED, message)

fun conflict(message: String) = ResponseStatusException(HttpStatus.CONFLICT, message)

/** Busca um jogo ou responde 404. */
fun GameRepository.findGame(id: Long): Game =
    findById(id).orElseThrow { notFound("Jogo $id não encontrado.") }
