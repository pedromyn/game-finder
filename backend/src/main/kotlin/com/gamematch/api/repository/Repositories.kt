package com.gamematch.api.repository

import com.gamematch.api.model.Game
import com.gamematch.api.model.Recommendation
import org.springframework.data.jpa.repository.JpaRepository

interface GameRepository : JpaRepository<Game, Long>

interface RecommendationRepository : JpaRepository<Recommendation, Long> {
    fun findAllByOrderByCreatedAtDesc(): List<Recommendation>
}
