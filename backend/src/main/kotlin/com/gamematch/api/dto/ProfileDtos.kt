package com.gamematch.api.dto

import com.gamematch.api.model.Game
import com.gamematch.api.model.Profile
import io.swagger.v3.oas.annotations.media.Schema

data class ProfileResponse(
    val userName: String,
    val favoriteGenres: List<String>,
    val likedGames: List<Game>,
    @field:Schema(description = "Versão da foto (null = sem foto). A foto fica em GET /profiles/{userName}/photo")
    val photoVersion: Long?,
)

@Schema(description = "Gêneros favoritos do perfil (substitui a lista atual)")
data class UpdateProfileRequest(
    @field:Schema(example = "[\"RPG\", \"Aventura\"]")
    val favoriteGenres: List<String>,
)

class ProfilePhoto(val bytes: ByteArray, val contentType: String)

fun Profile.toResponse() = ProfileResponse(
    userName = userName,
    favoriteGenres = favoriteGenres.sorted(),
    likedGames = likedGames.sortedBy { it.name },
    photoVersion = photoVersion.takeIf { photo != null },
)
