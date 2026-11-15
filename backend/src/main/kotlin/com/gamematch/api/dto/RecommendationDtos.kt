package com.gamematch.api.dto

import com.gamematch.api.model.Game
import com.gamematch.api.model.GameMode
import com.gamematch.api.model.Recommendation
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Preferências do usuário usadas para calcular o ranking")
data class PreferencesRequest(
    @field:Schema(example = "Pedro")
    val userName: String,
    @field:Schema(example = "PC", description = "PC, PlayStation, Xbox ou Switch")
    val platform: String,
    @field:Schema(example = "[\"RPG\", \"Ação\"]")
    val genres: List<String>,
    @field:Schema(example = "SOLO", description = "SOLO ou ONLINE")
    val mode: GameMode,
    @field:Schema(example = "false", description = "true = só jogos curtos")
    val shortOnly: Boolean = false,
)

@Schema(description = "Campos editáveis de uma recomendação (envie só o que quiser alterar)")
data class UpdateRecommendationRequest(
    @field:Schema(example = "Jogos para as férias")
    val title: String? = null,
    @field:Schema(example = "true")
    val favorite: Boolean? = null,
)

data class RankedGame(
    val game: Game,
    @field:Schema(description = "Compatibilidade de 0 a 100", example = "85")
    val score: Int,
)

data class RecommendationResponse(
    val id: Long,
    val userName: String,
    val title: String,
    val favorite: Boolean,
    val createdAt: LocalDateTime,
    val platform: String,
    val genres: List<String>,
    val mode: GameMode,
    val shortOnly: Boolean,
    val results: List<RankedGame>,
)

fun Recommendation.toResponse() = RecommendationResponse(
    id = id!!,
    userName = userName,
    title = title,
    favorite = favorite,
    createdAt = createdAt,
    platform = platform,
    genres = genres.split(",").filter { it.isNotBlank() },
    mode = mode,
    shortOnly = shortOnly,
    results = items.map { RankedGame(it.game!!, it.score) },
)
