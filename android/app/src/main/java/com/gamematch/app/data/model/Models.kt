package com.gamematch.app.data.model

import java.io.Serializable

data class Game(
    val id: Long,
    val name: String,
    val description: String,
    val genres: List<String>,
    val platforms: List<String>,
    /** SOLO, ONLINE ou AMBOS. */
    val mode: String,
    val shortGame: Boolean,
    val imageUrl: String,
    val storeUrl: String,
)

/** Um jogo do ranking com sua compatibilidade (0 a 100). */
data class RankedGame(
    val game: Game,
    val score: Int,
)

data class Recommendation(
    val id: Long,
    val userName: String,
    val title: String,
    val favorite: Boolean,
    val createdAt: String,
    val platform: String,
    val genres: List<String>,
    val mode: String,
    val shortOnly: Boolean,
    val results: List<RankedGame>,
)

/** Corpo do POST /recommendations. Serializable para viajar entre telas via Intent. */
data class PreferencesRequest(
    val userName: String,
    val platform: String,
    val genres: List<String>,
    val mode: String,
    val shortOnly: Boolean,
) : Serializable

/** Corpo do PUT /recommendations/{id}. Campos nulos não são enviados (ficam como estão). */
data class UpdateRecommendationRequest(
    val title: String? = null,
    val favorite: Boolean? = null,
)
