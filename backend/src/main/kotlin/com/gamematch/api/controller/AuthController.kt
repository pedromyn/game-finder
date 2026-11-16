package com.gamematch.api.controller

import com.gamematch.api.dto.AuthRequest
import com.gamematch.api.dto.ProfileResponse
import com.gamematch.api.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Criar perfil e entrar")
class AuthController(private val service: AuthService) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria um perfil com nome e senha (409 se o nome já existir)")
    fun register(@RequestBody request: AuthRequest): ProfileResponse = service.register(request)

    @PostMapping("/login")
    @Operation(summary = "Confere nome e senha e devolve o perfil (401 se estiverem errados)")
    fun login(@RequestBody request: AuthRequest): ProfileResponse = service.login(request)
}
