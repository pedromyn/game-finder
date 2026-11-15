package com.gamematch.api.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import java.time.LocalDateTime

/** Uma recomendação gerada: as preferências informadas + o ranking calculado. */
@Entity
class Recommendation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var userName: String = "",
    var title: String = "",
    var favorite: Boolean = false,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var platform: String = "",
    /** Gêneros escolhidos, separados por vírgula. */
    var genres: String = "",
    @Enumerated(EnumType.STRING)
    var mode: GameMode = GameMode.SOLO,
    var shortOnly: Boolean = false,
    @OneToMany(mappedBy = "recommendation", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("score DESC, id ASC")
    var items: MutableList<RecommendationItem> = mutableListOf(),
)

/** Uma linha do ranking: um jogo e sua compatibilidade (0 a 100). */
@Entity
class RecommendationItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(optional = false)
    var recommendation: Recommendation? = null,
    @ManyToOne(optional = false)
    var game: Game? = null,
    var score: Int = 0,
)
