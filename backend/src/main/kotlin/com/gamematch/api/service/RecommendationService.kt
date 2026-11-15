package com.gamematch.api.service

import com.gamematch.api.dto.PreferencesRequest
import com.gamematch.api.dto.RecommendationResponse
import com.gamematch.api.dto.UpdateRecommendationRequest
import com.gamematch.api.dto.toResponse
import com.gamematch.api.model.Game
import com.gamematch.api.model.GameMode
import com.gamematch.api.model.Recommendation
import com.gamematch.api.model.RecommendationItem
import com.gamematch.api.repository.GameRepository
import com.gamematch.api.repository.RecommendationRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import kotlin.math.roundToInt

@Service
class RecommendationService(
    private val games: GameRepository,
    private val recommendations: RecommendationRepository,
) {

    @Transactional
    fun create(request: PreferencesRequest): RecommendationResponse {
        val userName = request.userName.trim()
        val genres = request.genres.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
        if (userName.isEmpty()) throw badRequest("Informe o nome do usuário.")
        if (genres.isEmpty()) throw badRequest("Escolha pelo menos um gênero.")

        val recommendation = Recommendation(
            userName = userName,
            title = "Recomendação de $userName",
            platform = request.platform.trim(),
            genres = genres.joinToString(","),
            mode = request.mode,
            shortOnly = request.shortOnly,
        )
        games.findAll()
            .map { it to score(it, recommendation.platform, genres, request.mode, request.shortOnly) }
            .sortedWith(compareByDescending<Pair<Game, Int>> { it.second }.thenBy { it.first.name })
            .take(RANKING_SIZE)
            .forEach { (game, score) ->
                recommendation.items += RecommendationItem(recommendation = recommendation, game = game, score = score)
            }
        return recommendations.save(recommendation).toResponse()
    }

    @Transactional(readOnly = true)
    fun findAll(): List<RecommendationResponse> =
        recommendations.findAllByOrderByCreatedAtDesc().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun findById(id: Long): RecommendationResponse = load(id).toResponse()

    @Transactional
    fun update(id: Long, request: UpdateRecommendationRequest): RecommendationResponse {
        val recommendation = load(id)
        request.title?.let {
            if (it.isBlank()) throw badRequest("O título não pode ficar vazio.")
            recommendation.title = it.trim()
        }
        request.favorite?.let { recommendation.favorite = it }
        return recommendation.toResponse()
    }

    @Transactional
    fun delete(id: Long) = recommendations.delete(load(id))

    private fun load(id: Long): Recommendation = recommendations.findById(id)
        .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Recomendação $id não encontrada.") }

    private fun badRequest(message: String) = ResponseStatusException(HttpStatus.BAD_REQUEST, message)

    companion object {
        const val RANKING_SIZE = 10

        // Pesos de cada critério (somam 100).
        private const val GENRE_WEIGHT = 50.0
        private const val PLATFORM_WEIGHT = 25
        private const val MODE_WEIGHT = 15
        private const val LENGTH_WEIGHT = 10

        /** Compatibilidade de 0 a 100 entre um jogo e as preferências. */
        fun score(game: Game, platform: String, genres: List<String>, mode: GameMode, shortOnly: Boolean): Int {
            val wanted = genres.map { it.lowercase() }.toSet()
            val matches = game.genres.count { it.lowercase() in wanted }
            // Média entre "quanto do que eu pedi o jogo tem" e "quanto do jogo é o que eu pedi".
            val coverage = matches.toDouble() / wanted.size.coerceAtLeast(1)
            val focus = matches.toDouble() / game.genres.size.coerceAtLeast(1)
            val genrePoints = GENRE_WEIGHT * (coverage + focus) / 2

            val platformPoints = if (game.platforms.any { it.equals(platform, ignoreCase = true) }) PLATFORM_WEIGHT else 0
            val modePoints = if (game.mode == mode || game.mode == GameMode.AMBOS || mode == GameMode.AMBOS) MODE_WEIGHT else 0
            val lengthPoints = if (!shortOnly || game.shortGame) LENGTH_WEIGHT else 0

            return genrePoints.roundToInt() + platformPoints + modePoints + lengthPoints
        }
    }
}
