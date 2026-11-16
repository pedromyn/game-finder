package com.gamematch.api.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Nome e senha, usados para criar o perfil ou entrar")
data class AuthRequest(
    @field:Schema(example = "Pedro")
    val userName: String,
    @field:Schema(example = "1234", description = "Mínimo de 4 caracteres")
    val password: String,
)
